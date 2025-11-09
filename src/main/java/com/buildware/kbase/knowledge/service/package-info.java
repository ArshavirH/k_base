@ApplicationModule(
    displayName = "Knowledge Service",
    allowedDependencies = {"ai", "spi", "knowledge.repository", "knowledge.domain", "knowledge.service.model"}
)
package com.buildware.kbase.knowledge.service;

import org.springframework.modulith.ApplicationModule;
