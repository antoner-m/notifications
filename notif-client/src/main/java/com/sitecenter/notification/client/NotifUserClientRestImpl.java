package com.sitecenter.notification.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitecenter.notification.client.dto.NotificationDTOPageableResponse;
import com.sitecenter.notification.client.dto.UserDTOPageableResponse;
import com.sitecenter.notification.dto.*;
import com.sitecenter.notification.dto.UserDTO;
import com.sitecenter.notification.dto.UserDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotifUserClientRestImpl implements INotifUserClient {
    @Value("${org.sitecenter.NOTIFICATION_API_URL}")
    private String NOTIFICATION_API_URL;

    private final WebClient webClient;

    public NotifUserClientRestImpl(@Qualifier("notifWebClient") WebClient webClient) {
        this.webClient = webClient;
    }
    public UserDTO createUser(UserDTO userDTO){
        UserDTO result = webClient.post().uri(NOTIFICATION_API_URL+"/api/notif/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userDTO), UserDTO.class)
                .retrieve().bodyToMono(UserDTO.class).block();
        return result;
    }
    public UserDTO getUser(String user_uuid){
        UserDTO result = webClient.get().uri(NOTIFICATION_API_URL+"/api/notif/users/"+user_uuid)
                .retrieve().bodyToMono(UserDTO.class).block();
        return result;
    }
    public UserDTO updateUser(UserDTO userDTO){
        UserDTO result = webClient.put().uri(NOTIFICATION_API_URL+"/api/notif/users/"+userDTO.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userDTO), UserDTO.class)
                .retrieve().bodyToMono(UserDTO.class).block();
        return result;
    }

    @Override
    public void deleteUser(String user_uuid) {
        String deleted_uuid = webClient.delete().uri(NOTIFICATION_API_URL+"/api/notif/users/"+user_uuid)
                .retrieve().bodyToMono(String.class).block();
    }

    @Override
    public UserDTOPageableResponse listUsersInGroup(String group_uuid, Pageable pageable) {
        return getPageFromUrl(NOTIFICATION_API_URL + "/api/notif/groups/" + group_uuid + "/users");
    }

    public UserDTO addUserToGroup(String user_uid, String group_uid){
        UserDTO result = webClient.post().uri(NOTIFICATION_API_URL+"/api/notif/users/"+user_uid+"/groups/"+group_uid+"/add")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(UserDTO.class).block();
        return result;
    }
    public UserDTO removeUserFromGroup(String user_uid, String group_uid){
        UserDTO result = webClient.post().uri(NOTIFICATION_API_URL+"/api/notif/users/"+user_uid+"/groups/"+group_uid+"/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(UserDTO.class).block();
        return result;
    }

    // =================================================================================================================
    // Private methods

    private List<UserDTO> getListFromUrl(String url) {
        Object[] objects = webClient.get().uri(url)
                .retrieve().bodyToMono(Object[].class).block();
        List<UserDTO> result = mapObjectsToUserDTOList(objects);
        return result;
    }

    private UserDTOPageableResponse getPageFromUrl(String url) {
        UserDTOPageableResponse result = webClient.get().uri(url)
                .retrieve().bodyToMono(UserDTOPageableResponse.class).block();
        return result;
    }

    private List<UserDTO> mapObjectsToUserDTOList(Object[] objects) {
        ObjectMapper mapper = new ObjectMapper();
        List<UserDTO> result = Arrays.stream(objects)
                .map(object -> mapper.convertValue(object, UserDTO.class))
                .collect(Collectors.toList());
        return result;
    }

    private List<UserDTO> postUrl(String url, MultiValueMap<String, String> bodyValues) {
        Object[] objects = webClient.post().uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body( BodyInserters.fromFormData(bodyValues))
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        clientResponse -> Mono.empty()
                )
                .bodyToMono(Object[].class).block();
        List<UserDTO> result = mapObjectsToUserDTOList(objects);
        return result;
    }
}
