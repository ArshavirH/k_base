@org.springframework.modulith.ApplicationModule(
    displayName = "Knowledge Web",
    allowedDependencies = {
        "knowledge.service",
        "knowledge.domain",
        "knowledge.web.mapper",
        "knowledge.service.model",
        "knowledge.web.dto"
    }
)
package com.buildware.kbase.knowledge.web;

