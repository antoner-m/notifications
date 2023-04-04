package com.sitecenter.notification;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    private static final MariaDBContainer mariadb;
//    private static final GenericContainer redis;

    static {
        mariadb = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.19"));
        mariadb.start();
//        redis = new GenericContainer<>(DockerImageName.parse("redis:7.0.9"))
//                .withExposedPorts(6379);
//        redis.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
//        registry.add("spring.redis.host", redis::getContainerIpAddress);
//        registry.add("spring.redis.port",  () -> redis.getMappedPort(6379));
    }
}
