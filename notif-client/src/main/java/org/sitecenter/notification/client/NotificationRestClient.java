package org.sitecenter.notification.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sitecenter.notification.client.dto.NotificationDTOPageableResponse;
import org.sitecenter.notification.dto.NotificationDTO;
import org.sitecenter.notification.dto.NotificationShortDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NotificationRestClient implements INotificationClient{

    @Value("${org.sitecenter.NOTIFICATION_API_URL}")
    private String NOTIFICATION_API_URL;

    private final WebClient webClient;

    public NotificationRestClient(@Qualifier("notifWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public NotificationDTO createNotification(NotificationShortDTO notification) {
        NotificationDTO result = webClient.post().uri(NOTIFICATION_API_URL+"/api/notif/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(notification), NotificationShortDTO.class)
                .retrieve().bodyToMono(NotificationDTO.class).block();
        return result;
    }

    @Override
    public NotificationDTO getNotification(String uid) {
        NotificationDTO result = webClient.get().uri(NOTIFICATION_API_URL+"/api/notif/notifications/"+uid)
                .retrieve().bodyToMono(NotificationDTO.class).block();
        return result;
    }

    @Override
    public NotificationDTOPageableResponse getActiveByUser(String user_uid) {
        return getPageFromUrl(NOTIFICATION_API_URL+"/api/notif/users/"+user_uid+"/notifications?active=1");
    }

    @Override
    public NotificationDTOPageableResponse getAllByUser(String user_uid, Pageable pageable) {
        String url = NOTIFICATION_API_URL+"/api/notif/users/"+user_uid+"/notifications";
        if (pageable != null) {
            url += "?from="+pageable.getPageNumber()+"&size="+pageable.getPageSize();
        }

        return getPageFromUrl(url);
    }

    @Override
    public Optional<NotificationDTO> markRead(String user_uid, String notification_uuid) {
        NotificationDTO result = webClient.post().uri(NOTIFICATION_API_URL+"/api/notif/users/"+user_uid+"/notifications/"+notification_uuid+"/markRead")
                .retrieve().bodyToMono(NotificationDTO.class).block();
        Optional<NotificationDTO> result2 = Optional.of(result);
        return result2;
    }

    @Override
    public List<NotificationDTO> markReadList(String user_uid, List<String> notification_uuid) {
        MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
        notification_uuid.forEach(uid -> bodyValues.add("notification_uuid", uid));

        List<NotificationDTO> result = postUrl(NOTIFICATION_API_URL+"/api/notif/users/"+user_uid+"/notifications/markRead", bodyValues);
        return result;
    }

    @Override
    public List<NotificationDTO> markReadByUser(String user_uid) {
        MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
        List<NotificationDTO> result = postUrl(NOTIFICATION_API_URL+"/api/notif/users/"+user_uid+"/notifications/markAllRead", bodyValues);
        return result;
    }

    // =================================================================================================================
    // Private methods

    private List<NotificationDTO> getListFromUrl(String url) {
        Object[] objects = webClient.get().uri(url)
                .retrieve().bodyToMono(Object[].class).block();
        List<NotificationDTO> result = mapObjectsToNotificationDTOList(objects);
        return result;
    }

    private NotificationDTOPageableResponse getPageFromUrl(String url) {
        NotificationDTOPageableResponse result = webClient.get().uri(url)
                .retrieve().bodyToMono(NotificationDTOPageableResponse.class).block();
        return result;
    }

    private List<NotificationDTO> mapObjectsToNotificationDTOList(Object[] objects) {
        ObjectMapper mapper = new ObjectMapper();
        List<NotificationDTO> result = Arrays.stream(objects)
                .map(object -> mapper.convertValue(object, NotificationDTO.class))
                .collect(Collectors.toList());
        return result;
    }

    private List<NotificationDTO> postUrl(String url, MultiValueMap<String, String> bodyValues) {
        String objects = webClient.post().uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body( BodyInserters.fromFormData(bodyValues))
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        clientResponse -> Mono.empty()
                )
                .bodyToMono(String.class).block();
        List<NotificationDTO> result = null;//mapObjectsToNotificationDTOList(objects);
        return result;
    }
    private NotificationDTOPageableResponse postUrlPageable(String url, MultiValueMap<String, String> bodyValues) {
        NotificationDTOPageableResponse result = webClient.post().uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body( BodyInserters.fromFormData(bodyValues))
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        clientResponse -> Mono.empty()
                )
                .bodyToMono(NotificationDTOPageableResponse.class).block();
        return result;
    }
}