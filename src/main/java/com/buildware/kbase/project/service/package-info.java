@ApplicationModule(
    displayName = "Project Service",
    allowedDependencies = {"config", "spi", "project.repository", "project.domain"}
)
package com.buildware.kbase.project.service;

import org.springframework.modulith.ApplicationModule;
