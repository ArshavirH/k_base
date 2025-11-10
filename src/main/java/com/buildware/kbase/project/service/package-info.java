@ApplicationModule(
    displayName = "Project Service",
    allowedDependencies = {
        "spi",
        "config",
        "project.repository",
        "project.domain",
        "project.mapper"
    }
)
package com.buildware.kbase.project.service;

import org.springframework.modulith.ApplicationModule;
