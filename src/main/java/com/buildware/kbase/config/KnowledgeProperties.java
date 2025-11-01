package com.buildware.kbase.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mcp.knowledge")
public record KnowledgeProperties(String docsPath) {}
