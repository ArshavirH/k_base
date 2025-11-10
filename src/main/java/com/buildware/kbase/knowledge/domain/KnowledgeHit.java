package com.buildware.kbase.knowledge.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnowledgeHit {

    private String text;
    private double score;
    private String docPath;
    private String title;
    private int chunkIndex;
}

