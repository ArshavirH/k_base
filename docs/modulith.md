# Spring Modulith

Spring Modulith enforces clear module boundaries inside the Spring Boot app and provides tests and docs for them.

## Why
- Keep features decoupled and testable
- Prevent cyclic dependencies across packages
- Generate module documentation (optional)

## Basics
- Define modules via `package-info.java` with `@ApplicationModule`.
- Keep API types in a named interface or dedicated package (e.g., `..api`).
- Use Spring events or explicit interfaces for cross-module communication.

Example root module annotation:

```java
// src/main/java/com/buildware/k_base/package-info.java
@org.springframework.modulith.ApplicationModule(displayName = "k_base Root")
package com.buildware.k_base;
```

Example test:

```java
// src/test/java/com/buildware/k_base/ModularityTests.java
@ApplicationModuleTest
class ModularityTests {
  @Test void noCycles(ModularityFixture fixture) { fixture.assertNoCycles(); }
}
```

## Commands
- Run tests (includes Modulith checks): `./gradlew test`
- Full verification: `./gradlew check`

To add a new module, create `package-info.java` in the module package:

```java
@ApplicationModule(displayName = "Ingestion")
package com.buildware.k_base.ingestion;

import org.springframework.modulith.ApplicationModule;
```
