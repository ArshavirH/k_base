@ApplicationModule(
    displayName = "Knowledge Web Adapter",
    allowedDependencies = {
        "knowledge.service",
        "knowledge.domain"
    }
)
package com.buildware.kbase.knowledge.web;

import org.springframework.modulith.ApplicationModule;
