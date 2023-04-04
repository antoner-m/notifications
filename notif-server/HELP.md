# Read Me First
It's a sample project realizing simple notification rest server.

# What is notification?
Notification is a message to a user or group of user with title, text and url.
You can create notification for group of users and in result server will create a bunch of Notifications, personal to every User of that group.
When such notifications will be created original notification will be the same for every user, so there won't be any data duplication.

User can read or delete their notification. 
A group of users is any subset of users.

Application uses only uuid of users and groups, any other information about them should be stored in other place.

# Techonologies
Application created with Spring Boot framework (2.7), for MariaDB (10.x) as database and Keycloak as security provider.
You can change storage and security as you wish.

This project shows all major steps of developing rest-api server:
- Storing data in MariaDB using Spring JPA;
- Creating Rest API Server using Spring Web;
- Mapping of PJO to DTO using MapStruct;
- Securing server with Spring Security using OAuth2 and KeyCloak as provider;
- Using Spring ApplicationEvent as provider for events inside application (next task is to use Kaffka);
- Create docker image for local deployment;
- Deploy docker image to k8s;
- Creating Rest API client using WebClient;
- Authorization of client using JWT token with ability to refresh it;
- Testing server logic via JUnit 5 and TestContainer;
- Testing rest api client logic via JUnit 5.

I glad to receive feedback to this project. For any questions please email me: me@antoner.me

# How to start
To start server you should pass as java parameter or modify parameters in application.properties:
NOTIFICATION_DB_URL=jdbc:mariadb://[example.com]:3306/[database_name]?connectionTimeZone=UTC
NOTIFICATION_DB_USER=[db_user]
NOTIFICATION_DB_PASS=[password]
JWT_ISSUER_URI=https://cloak.example.org/auth/realms/[yourrealmname]

#Keycloak
Rest api client user should have:
1. Client configured with unique Client ID.
2. Access type: confidential
3. Service Accounts Enabled: ON
4. Service account roles: REST-API-CLIENT (That role should be created inside realm in "Roles", role name uppercase)
5. Secret from "Credentials" page.

Configuration for rest api client tests is inside client/src/test/resources/application.properties

Server just uses JWT token from client and don't need a authentication credentials for itself.

#Docker
Server exposes port 8091 as HTTP endpoint for rest api application.


### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.4/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.4/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.4/reference/htmlsingle/#web)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.0.4/reference/htmlsingle/#web.security)
* [OAuth2 Client](https://docs.spring.io/spring-boot/docs/3.0.4/reference/htmlsingle/#web.security.oauth2.client)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

