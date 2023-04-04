FROM eclipse-temurin:17
EXPOSE 8091
ADD target/notif-server-0.0.1-SNAPSHOT.jar sitecenter-notification-service.jar
ENTRYPOINT ["java","-jar","/sitecenter-notification-service.jar"]