package com.buildware.kbase.knowledge.web;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface KnowledgeApiMapper {

    KnowledgeChunkDTO toDto(KnowledgeHit hit);

    List<KnowledgeChunkDTO> toDtoList(List<KnowledgeHit> hits);

    IngestDocument toDomain(KnowledgeIngestDTO dto);

    /**
     * Map ingest response from domain document and ingested chunk count.
     */
    KnowledgeIngestResponseDTO toIngestResponse(IngestDocument doc, int ingestedChunks);
}
