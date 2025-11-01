package com.buildware.kbase.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI kbaseOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("KBase API")
                .description("MCP Knowledge Base APIs")
                .version("v1")
                .license(new License().name("Apache-2.0"))
                .contact(new Contact().name("ArshavirH")
                    .email("ahunanyan@buildwaresystems.com")
                    .url("https://buildwaresystems.com/")))
            .externalDocs(new ExternalDocumentation()
                .description("Project")
                .url("https://github.com/ArshavirH/k_base"));
    }
}

