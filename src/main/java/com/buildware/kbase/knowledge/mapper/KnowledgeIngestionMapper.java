package com.buildware.kbase.knowledge.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.spi.KnowledgeIngestionSPI.KnowledgeIngestCommand;
import com.buildware.kbase.spi.KnowledgeIngestionSPI.KnowledgeIngestSummaryView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface KnowledgeIngestionMapper {

    IngestDocument toDomain(KnowledgeIngestCommand command);

    KnowledgeIngestSummaryView toSummaryView(IngestDocument doc, int ingestedChunks);
}

