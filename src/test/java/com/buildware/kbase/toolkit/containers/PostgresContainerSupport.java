package com.buildware.kbase.toolkit.containers;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class PostgresContainerSupport {

    private static final String DEFAULT_IMAGE = "arshavirh/postgres-pgvector:1.2.0";
    private static final String IMAGE_ENV = "TEST_POSTGRES_IMAGE";

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresContainerSupport.class);

    @Container
    @SuppressWarnings("resource")
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
        DockerImageName
            .parse(System.getenv().getOrDefault(IMAGE_ENV, DEFAULT_IMAGE))
            .asCompatibleSubstituteFor("postgres")
    )
        .withDatabaseName("kbase")
        .withUsername("postgres")
        .withPassword("user123")
        .withLogConsumer(new Slf4jLogConsumer(LOGGER).withSeparateOutputStreams())
        .waitingFor(Wait.forListeningPort()
            .withStartupTimeout(Duration.ofSeconds(120)));

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }
}
