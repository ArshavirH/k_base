package com.buildware.kbase;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    @Test
    void noCycles() {
        ApplicationModules modules = ApplicationModules.of(Application.class);
        System.out.println("MODULES COUNT: " + modules.stream().map(ApplicationModule::getName).toList());
        modules.verify();
    }
}
