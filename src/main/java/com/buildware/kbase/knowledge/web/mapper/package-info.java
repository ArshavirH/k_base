@org.springframework.modulith.ApplicationModule(
    displayName = "Knowledge Repository",
    allowedDependencies = {
        "knowledge.domain",
        "knowledge.service",
        "knowledge.service.model",
        "knowledge.web",
        "knowledge.web.dto"
    }
)
package com.buildware.kbase.knowledge.web.mapper;
