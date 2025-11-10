package com.buildware.kbase.knowledge.web;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import com.buildware.kbase.knowledge.service.KnowledgeQueryService;
import com.buildware.kbase.knowledge.service.KnowledgeSyncService;
import com.buildware.kbase.knowledge.service.KnowledgeSyncService.SyncResult;
import java.util.List;
import java.util.Optional;
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
@Import(KnowledgeApiMapperImpl.class)
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
        KnowledgeHit h = random(KnowledgeHit.class);
        when(knowledgeQueryService.query(Mockito.eq("proj"), Mockito.eq("q"), Mockito.eq(5)))
            .thenReturn(List.of(h));

        // WHEN
        MvcResult res = mockMvc.perform(post("/knowledge/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectCode\":\"proj\",\"query\":\"q\"}"))
            .andExpect(status().isOk())
            .andReturn();

        // THEN
        String json = res.getResponse().getContentAsString();
        assertThat(json).contains(h.getText());
    }

    @Test
    void should_returnSyncResults_when_syncAllProjectsCalled() throws Exception {
        // GIVEN
        var syncResult = random(SyncResult.class);
        when(knowledgeSyncService.syncAllProjects())
            .thenReturn(List.of(syncResult));

        // WHEN
        MvcResult res = mockMvc.perform(post("/knowledge/sync"))
            .andExpect(status().isOk())
            .andReturn();

        // THEN
        String body = res.getResponse().getContentAsString();
        assertThat(body)
            .contains("projectCode")
            .contains("documentsProcessed")
            .contains("chunksProcessed");
    }

    @Test
    void should_return404_when_syncSingleProjectNotFound() throws Exception {
        // GIVEN
        when(knowledgeSyncService.syncProject("missing")).thenReturn(java.util.Optional.empty());

        // WHEN
        MvcResult res = mockMvc.perform(post("/knowledge/sync/missing"))
            .andReturn();

        // THEN
        assertThat(res.getResponse().getStatus()).isEqualTo(404);
    }

    @Test
    void should_returnSyncResult_when_syncSingleProjectFound() throws Exception {
        // GIVEN
        var syncResult = random(SyncResult.class);
        when(knowledgeSyncService.syncProject(syncResult.projectCode())).thenReturn(Optional.of(syncResult));

        // WHEN
        MvcResult res = mockMvc.perform(post("/knowledge/sync/" + syncResult.projectCode()))
            .andExpect(status().isOk())
            .andReturn();

        // THEN
        String body = res.getResponse().getContentAsString();
        assertThat(body)
            .contains("\"projectCode\":\"" + syncResult.projectCode() + "\"")
            .contains("\"documentsProcessed\":" + syncResult.documents())
            .contains("\"chunksProcessed\":" + syncResult.chunks());
    }
}
