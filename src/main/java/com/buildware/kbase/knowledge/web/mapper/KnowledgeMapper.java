package com.buildware.kbase.knowledge.web.mapper;

import com.buildware.kbase.knowledge.service.model.KnowledgeHit;
import com.buildware.kbase.knowledge.web.dto.KnowledgeChunkResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KnowledgeMapper {
    KnowledgeChunkResponse toDto(KnowledgeHit hit);

    List<KnowledgeChunkResponse> toDtoList(List<KnowledgeHit> hits);
}
