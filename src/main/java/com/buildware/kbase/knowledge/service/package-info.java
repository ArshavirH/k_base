@ApplicationModule(
    displayName = "Knowledge Services",
    allowedDependencies = {
        "spi",
        "knowledge.domain",
        "knowledge.mapper"
    }
)
package com.buildware.kbase.knowledge.service;

import org.springframework.modulith.ApplicationModule;
