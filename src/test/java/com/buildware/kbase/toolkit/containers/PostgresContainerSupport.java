package com.buildware.kbase.toolkit.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class PostgresContainerSupport {

    private static final String DEFAULT_IMAGE = "arshavirh/postgres-pgvector:1.2.0";

    @Container
    @SuppressWarnings("resource")
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
        DockerImageName
            .parse(DEFAULT_IMAGE)
            .asCompatibleSubstituteFor("postgres")
    )
        .withDatabaseName("kbase")
        .withUsername("postgres")
        .withPassword("user123");

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }
}
