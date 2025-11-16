package com.buildware.kbase.knowledge.service;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import com.buildware.kbase.knowledge.mapper.KnowledgeHitMapper;
import com.buildware.kbase.spi.KnowledgeSearchSPI.KnowledgeHitView;
import com.buildware.kbase.spi.KnowledgeSearchSPI.KnowledgeQuery;
import java.util.List;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class KnowledgeSearchSPIImplTest {

    @Mock
    private KnowledgeQueryService service;

    @Mock
    private KnowledgeHitMapper mapper;

    @InjectMocks
    private KnowledgeSearchSPIImpl adapter;

    @Test
    void should_mapHitsToViews_when_semanticSearchInvoked() {
        // GIVEN
        KnowledgeQuery input = random(KnowledgeQuery.class);
        KnowledgeHit hit = random(KnowledgeHit.class);
        List<KnowledgeHit> hits = List.of(hit);
        KnowledgeHitView view = random(KnowledgeHitView.class);

        when(service.query(input.projectCode(), input.text(), input.topK(), input.tags())).thenReturn(hits);
        when(mapper.toViews(hits)).thenReturn(List.of(view));

        // WHEN
        List<KnowledgeHitView> out = adapter.semanticSearch(input);

        // THEN
        assertThat(out).hasSize(1);
        assertThat(out.getFirst().text()).isEqualTo(view.text());
    }
}
