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

    private static final DockerImageName DEFAULT_IMAGE = DockerImageName
        .parse("arshavirh/postgres-pgvector:1.2.0")
        .asCompatibleSubstituteFor("postgres");

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresContainerSupport.class);

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = getPostgres();

    private static PostgreSQLContainer<?> getPostgres() {
        try (var container = new PostgreSQLContainer<>(DEFAULT_IMAGE)) {
            return container.withDatabaseName("kbase")
                .withUsername("postgres")
                .withPassword("user123")
                .withLogConsumer(new Slf4jLogConsumer(LOGGER).withSeparateOutputStreams())
                .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(120)));
        }
    }

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }
}
