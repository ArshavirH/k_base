package com.buildware.kbase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Spring Modulith smoke tests ensuring module graph is valid and expected modules are discovered.
 */
class ModulithStructureTest {

    @Test
    void should_verifyModuleGraph_when_usingApplicationModules() {
        // GIVEN
        ApplicationModules modules = ApplicationModules.of(Application.class);

        // WHEN
        // verify() asserts no cycles and that declared allowedDependencies are respected
        modules.verify();

        // THEN
        assertThat(modules).isNotNull();
    }

    @Test
    void should_discoverExpectedModules_when_scanningApplication() {
        // GIVEN
        ApplicationModules modules = ApplicationModules.of(Application.class);

        // WHEN
        List<String> names = modules.stream().map(ApplicationModule::getName).collect(Collectors.toList());

        // THEN
        assertThat(names)
            .isNotEmpty()
            .contains("ai", "config", "knowledge", "project");
    }
}

