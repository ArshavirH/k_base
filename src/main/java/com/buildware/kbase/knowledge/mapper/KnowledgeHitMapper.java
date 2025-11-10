package com.buildware.kbase.knowledge.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import com.buildware.kbase.spi.KnowledgeSearchSPI.KnowledgeHitView;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface KnowledgeHitMapper {

    KnowledgeHitView toView(KnowledgeHit hit);

    List<KnowledgeHitView> toViews(List<KnowledgeHit> hits);
}

