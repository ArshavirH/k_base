package com.buildware.kbase.knowledge.service;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.buildware.kbase.spi.ProjectInfoSPI;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class KnowledgeSyncServiceTest {

    @Mock
    private ProjectInfoSPI projectInfoSPI;

    @Mock
    private VectorStore vectorStore;

    @InjectMocks
    private KnowledgeSyncService service;

    @Nested
    class SyncProject {

        @Test
        void should_returnEmpty_when_projectNotFound() {
            // GIVEN
            String missing = random(String.class);
            when(projectInfoSPI.getByCode(missing)).thenReturn(Optional.empty());

            // WHEN
            Optional<KnowledgeSyncService.SyncResult> res = service.syncProject(missing);

            // THEN
            assertThat(res).isEmpty();
        }

        @Test
        void should_returnZeroCounts_when_basePathMissing() {
            // GIVEN
            var info = random(ProjectInfo.class);
            when(projectInfoSPI.getByCode(info.code())).thenReturn(Optional.of(info));

            // WHEN
            Optional<KnowledgeSyncService.SyncResult> res = service.syncProject(info.code());

            // THEN
            assertThat(res).isPresent();
            assertThat(res.get().documents()).isEqualTo(0);
            assertThat(res.get().chunks()).isEqualTo(0);
        }

        @Test
        void should_upsertMarkerAndChunks_when_documentsPresent() throws Exception {
            // GIVEN
            Path tmp = Files.createTempDirectory("kb-sync-test");
            Path projDir = tmp.resolve("acme");
            Files.createDirectories(projDir);
            Files.writeString(projDir.resolve("readme.md"), "# Title\nHello world\nThis is content.");

            var info = new ProjectInfo(randomUUID(), random(String.class), projDir.toString());
            when(projectInfoSPI.getByCode(info.code())).thenReturn(Optional.of(info));
            when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

            // WHEN
            Optional<KnowledgeSyncService.SyncResult> res = service.syncProject(info.code());

            // THEN
            assertThat(res).isPresent();
            assertThat(res.get().documents()).isEqualTo(1);
            assertThat(res.get().chunks()).isGreaterThan(0);
            verify(vectorStore, times(1)).add(any());
        }
    }
}
