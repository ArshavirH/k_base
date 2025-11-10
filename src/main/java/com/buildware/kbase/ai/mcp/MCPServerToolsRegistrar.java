package com.buildware.kbase.ai.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MCPServerToolsRegistrar {

    private final KnowledgeMcpTool knowledgeMcpTool;

    @Bean
    public ToolCallbackProvider userTools() {
        return MethodToolCallbackProvider
            .builder()
            .toolObjects(knowledgeMcpTool)
            .build();
    }

}


