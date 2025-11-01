# Coding Guidelines

## Code Style
- Java 21, Spring Boot 3.x; 4-space indentation; target 120 columns.
- Formatting: Spotless + Google Java Format (AOSP, 4-space indent). Run `./gradlew spotlessApply` to fix; `./gradlew spotlessCheck` to verify.
- Static analysis: Checkstyle with custom rules in `checkstyle/checkstyle.xml`. Run `./gradlew checkstyleMain checkstyleTest` or `./gradlew check`.
- Import order: enforced by Checkstyle (static vs non-static groups); Spotless does not reorder imports.

## Structure & Layering
- Controllers → Services → Repositories. No controller-to-repository calls.
- Keep controllers thin; DTOs for I/O; map to domain in services.
- Configuration in `application.yaml`; no hard-coded secrets.
- Spring Modulith: define modules via `package-info.java` with `@ApplicationModule`; respect module boundaries and avoid cross-module shortcuts.

## Mapping & Boilerplate
- Prefer MapStruct for deterministic DTO ↔ domain mapping. Place mappers under `..mapper` packages.
- Allow Lombok for boilerplate (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`) — keep it compile-only.

## Validation
- Use `spring-boot-starter-validation` (Jakarta) and `@Valid` on controller method parameters.
- Put constraints on request DTOs; surface violations via Spring’s `MethodArgumentNotValidException` handling.

## Testing Data
- Use Instancio to generate rich test data; keep factories/builders near tests.

## Naming
- Packages: lowercase (e.g., `com.buildware.kbase.ingestion`).
- Classes: PascalCase; methods/fields: camelCase; constants: UPPER_SNAKE_CASE.
- Tests end with `Tests` (e.g., `KnowledgeServiceTests`).

## Spring & Java Conventions
- Prefer constructor injection; avoid field injection.
- Use `@Transactional` at service layer for write operations.
- Avoid static utility classes for business logic; prefer components.
- Return `Optional<T>` for absent values instead of nulls in APIs.

## Exceptions & Logging
- Use domain-specific exceptions; do not swallow exceptions.
- Propagate with context; convert to meaningful HTTP errors in controllers.
- Logging via SLF4J (`LoggerFactory.getLogger(...)`); no `printStackTrace`.

## Git Hygiene
- Conventional Commits (e.g., `feat: add query endpoint`).
- Small, focused PRs with description, testing steps, and screenshots/logs where relevant.

## Formatting & Checks: Recommended Flow
- Before commit: run `./gradlew spotlessApply` to format.
- Pre-push/CI: run `./gradlew check` to execute Checkstyle and tests.
