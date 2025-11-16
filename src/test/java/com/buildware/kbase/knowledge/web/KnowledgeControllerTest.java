package com.buildware.kbase.knowledge.web;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import com.buildware.kbase.knowledge.service.KnowledgePersistenceService;
import com.buildware.kbase.knowledge.service.KnowledgeQueryService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = KnowledgeController.class)
@Import(KnowledgeApiMapperImpl.class)
class KnowledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KnowledgeQueryService knowledgeQueryService;

    @MockitoBean
    private KnowledgePersistenceService knowledgePersistenceService;

    @Test
    void should_returnOkAndResults_when_validRequest() throws Exception {
        // GIVEN
        KnowledgeHit h = random(KnowledgeHit.class);
        when(knowledgeQueryService.query(eq("proj"), eq("q"), eq(5), eq(null)))
            .thenReturn(List.of(h));

        // WHEN
        MvcResult res = mockMvc.perform(get("/knowledge/search")
                .param("projectCode", "proj")
                .param("query", "q")
            )
            .andExpect(status().isOk())
            .andReturn();

        // THEN
        String json = res.getResponse().getContentAsString();
        assertThat(json).contains(h.text());
    }

    @Test
    void should_returnCreated_when_ingestValid() throws Exception {
        // GIVEN
        when(knowledgePersistenceService.ingestDocument(org.mockito.ArgumentMatchers.any()))
            .thenReturn(3);

        // WHEN
        MvcResult res = mockMvc.perform(post("/knowledge/ingest")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectCode\":\"p1\",\"content\":\"something\",\"tags\":[\"a\",\"b\"]}"))
            .andExpect(status().isOk())
            .andReturn();

        // THEN
        String json = res.getResponse().getContentAsString();
        assertThat(json).contains("p1");
        assertThat(json).contains("ingestedChunks");
    }
}
