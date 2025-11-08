package com.buildware.kbase.knowledge.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.buildware.kbase.knowledge.service.KnowledgeQueryService;
import com.buildware.kbase.knowledge.service.KnowledgeSyncService;
import com.buildware.kbase.knowledge.service.model.KnowledgeHit;
import com.buildware.kbase.knowledge.web.dto.KnowledgeChunkResponse;
import com.buildware.kbase.knowledge.web.mapper.KnowledgeMapperImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = KnowledgeController.class)
@Import(KnowledgeMapperImpl.class)
class KnowledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KnowledgeQueryService knowledgeQueryService;

    @MockitoBean
    private KnowledgeSyncService knowledgeSyncService;

    @Test
    void should_returnOkAndResults_when_validRequest() throws Exception {
        // GIVEN
        KnowledgeHit h = new KnowledgeHit();
        h.setText("hello");
        h.setScore(0.1);
        h.setDocPath("/doc.md");
        h.setTitle("Doc");
        h.setChunkIndex(0);
        when(knowledgeQueryService.query(Mockito.eq("proj"), Mockito.eq("q"), Mockito.eq(5)))
            .thenReturn(List.of(h));

        // WHEN
        MvcResult res = mockMvc.perform(post("/mcp/knowledge/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectCode\":\"proj\",\"query\":\"q\"}"))
            .andExpect(status().isOk())
            .andReturn();

        // THEN
        String json = res.getResponse().getContentAsString();
        assertThat(json).contains("hello");
    }
}
