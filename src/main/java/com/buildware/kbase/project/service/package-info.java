@ApplicationModule(
    displayName = "Project Service",
    allowedDependencies = {
        "project.repository", "config", "project.domain"
    }
)
package com.buildware.kbase.project.service;

import org.springframework.modulith.ApplicationModule;
