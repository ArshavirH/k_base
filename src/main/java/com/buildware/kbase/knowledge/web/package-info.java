@ApplicationModule(
    displayName = "Knowledge Web Adapter",
    allowedDependencies = {
        "knowledge.service",
        "knowledge.domain",
        "knowledge.mapper"
    }
)
package com.buildware.kbase.knowledge.web;

import org.springframework.modulith.ApplicationModule;
