package org.sitecenter.notification.client.tests;

import org.sitecenter.notification.client.INotifUserClient;
import org.sitecenter.notification.client.INotificationClient;
import org.sitecenter.notification.client.dto.NotificationDTOPageableResponse;
import org.sitecenter.notification.client.dto.UserDTOPageableResponse;
import org.sitecenter.notification.dto.NotificationDTO;
import org.sitecenter.notification.dto.NotificationShortDTO;
import org.sitecenter.notification.dto.UserDTO;
import org.sitecenter.notification.dto.UserGroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Slf4j

class ClientApplicationTests {
	@Autowired
	private WebClient notifWebClient;
	@Autowired
	private INotificationClient notificationClient;
	@Autowired
	private INotifUserClient notifUserClient;

	@Value("${org.sitecenter.tests.USER_ID}")
	private String USER_ID;

	@Value("${org.sitecenter.NOTIFICATION_API_URL}")
	private String NOTIFICATION_API_URL;

	@Test
	void contextLoads() {
	}

	@Test
	void notificationCreateAndDelete() {
		log.info("creating new notification for user");
		NotificationShortDTO dto = new NotificationShortDTO("test from client","body of test","").setUser_id(USER_ID);
		NotificationDTO notification = notificationClient.createNotification(dto);
		assert(notification != null);
		assert(notification.getUuid() != null);
		log.info("Notification created {}", notification.toString());
		String notif_created_uuid = notification.getUuid();

		NotificationDTOPageableResponse user_notifs_active = notificationClient.getActiveByUser(USER_ID);
		assert(user_notifs_active != null);
		assert(user_notifs_active.getContent().size() > 0);

		boolean foundCreated = user_notifs_active.getContent().stream().anyMatch(un -> notif_created_uuid.equals(un.getUuid() ));
		assert (foundCreated);

		notificationClient.markRead(USER_ID, notif_created_uuid);

		user_notifs_active = notificationClient.getActiveByUser(USER_ID);
		assert(user_notifs_active == null || !(user_notifs_active.getContent().stream().anyMatch(un -> un.getUuid().equals(notif_created_uuid))));

		NotificationDTOPageableResponse user_notifs_all = notificationClient.getAllByUser(USER_ID, PageRequest.of(0, 100));
		assert(user_notifs_all != null);
		assert(user_notifs_all.getContent().size() > 0);

		Optional<NotificationDTO> foundCreatedInactive = user_notifs_all.getContent().stream().filter(c -> notif_created_uuid.equals(c.getUuid())).findAny();
		assert (foundCreatedInactive != null);
		assert (foundCreatedInactive.isPresent());

		List <NotificationDTO> markedReadList = notificationClient.markReadByUser(USER_ID);

		user_notifs_active = notificationClient.getActiveByUser(USER_ID);
		assert(user_notifs_active == null || user_notifs_active.getContent().size() == 0);
	}

	@Test
	void userCreateAndDelete() {
		log.info("creating new user");
		String user_uuid = UUID.randomUUID().toString();
		String group_uuid = UUID.randomUUID().toString();

		UserDTO user = new UserDTO(user_uuid);
		user.getGroups().add(new UserGroupDTO(group_uuid));
		UserDTO createdUser = notifUserClient.createUser(user);
		assert (createdUser != null);
		assert (createdUser.getUuid().equals(user_uuid));
		assert (createdUser.getGroups().size() == 1);
		assert (createdUser.getGroups().stream().anyMatch(ug -> ug.getUuid().equals(group_uuid)));

		UserDTOPageableResponse userDTOS = notifUserClient.listUsersInGroup(group_uuid, PageRequest.of(0, 1000));
		assert (userDTOS.getContent().size() == 1);
		assert (userDTOS.getContent().stream().anyMatch(u -> u.getUuid().equals(user_uuid)));


	}
	@Test
	void userNotificationsDeleteAfterUser() {
		log.info("creating new user");
	}
}
