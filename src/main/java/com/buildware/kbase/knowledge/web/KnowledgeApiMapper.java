package com.buildware.kbase.knowledge.web;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = SPRING,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface KnowledgeApiMapper {

    KnowledgeChunkResponse toDto(KnowledgeHit hit);

    List<KnowledgeChunkResponse> toDtoList(List<KnowledgeHit> hits);
}
