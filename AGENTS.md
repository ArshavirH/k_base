# Coding Guidelines

## Code Style

- Java 21, Spring Boot 3.x; 4-space indentation; target 120 columns.
- Formatting: follow project style (4-space indent). Use your IDE formatter; style is enforced by
  Checkstyle.
- Static analysis: Checkstyle with custom rules in `checkstyle/checkstyle.xml`. Run
  `./gradlew checkstyleMain checkstyleTest` or `./gradlew check`.
- Import order: enforced by Checkstyle (static vs non-static groups).

## Structure & Layering

- Controllers → Services → Repositories. No controller-to-repository calls.
- Keep controllers thin; DTOs for I/O; map to domain in services.
- Configuration in `application.yaml`; no hard-coded secrets.
- Spring Modulith: define modules via `package-info.java` with `@ApplicationModule`; respect module
  boundaries and avoid cross-module shortcuts.
- Domain models should favor immutability. In the knowledge module specifically, use Java records
  for domain types (e.g., `KnowledgeHit`, `IngestDocument`).

## Modulith Rules & SPI Usage

- Do not use cross-module dependencies. Always depend on `spi` from other feature modules.
- Provide adapters in the owning module to implement SPI interfaces (e.g., knowledge implements
  `KnowledgeQueryPort`).
- Do not export internal domain models across modules. Use view types under `spi.view` for
  cross-module data (e.g., `KnowledgeHitView`).
- MCP or other integration modules must only call into `spi`, never directly into feature services.

## Mapping & Boilerplate

- Prefer MapStruct for deterministic DTO ↔ domain mapping. Place mappers under `..mapper` packages.
- Allow Lombok for boilerplate (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`) — keep
  it compile-only.
- Constructor injection: use `@RequiredArgsConstructor` on Spring components; do not hand-write
  constructors for dependency injection.

## Validation

- Use `spring-boot-starter-validation` (Jakarta) and `@Valid` on controller method parameters.
- Put constraints on request DTOs; surface violations via Spring’s `MethodArgumentNotValidException`
  handling.

## Database Migrations

- Use Flyway SQL migrations under `src/main/resources/db/migration`.
- Naming: `V{version or timestamp}__{short_description}.sql` (e.g., `V1__create_projects.sql`).
- Primary keys use UUIDs with `gen_random_uuid()`; ensure `pgcrypto` is enabled in the DB.
- Prefer additive migrations; avoid destructive changes without a deprecation plan.
- Recommended: set `spring.jpa.hibernate.ddl-auto=validate` once Flyway schema is in place.

## Naming

- Packages: lowercase (e.g., `com.buildware.kbase.ingestion`).
- Classes: PascalCase; methods/fields: camelCase; constants: UPPER_SNAKE_CASE.
- Tests end with `Test` (e.g., `KnowledgeServiceTest`).

## Spring & Java Conventions

- Prefer constructor injection; avoid field injection.
- Use `@Transactional` at service layer for write operations.
- Avoid static utility classes for business logic; prefer components.
- Return `Optional<T>` for absent values instead of nulls in APIs.

## Formatting & Checks: Recommended Flow

- Before commit: run `./gradlew checkstyleMain checkstyleTest` to verify style.
- Pre-push/CI: run `./gradlew check` to execute Checkstyle and tests.
- Checkstyle treats warnings as errors (build fails on violations).

## Testing Guidelines

- Tests should be written in the `src/test/java` directory
  - With the same package structure as the main code
  - Name the test class with `*Test`, e.g., `ProductServiceTest`.
- Use JUnit 5 for writing unit tests.
- Use `@Nested` for nested classes to group related tests.
  - The nested class should not end with `Test`
  - Grouping by method-under-test is a common practice, but not mandatory.
  - Only perform grouping if it makes sense for the test structure. Don't add it if it is the only
    one in the test class.
- Follow BDD (Behavior Driven Development) principles.
  - Name methods with `should_` and `_when_` to describe the behavior, e.g.,
    `should_returnProduct_when_validIdIsProvided`.
  - Add comments `//GIVEN`, `//WHEN`, `//THEN` to describe the test setup, action, and expected
    outcome.
  - GIVEN-block: prepare the test data and mock dependencies
  - WHEN-block: execute the method under test and capture the result, nothing else
  - THEN-block: assert the result and verify interactions with mocks
- Assert exclusively with AssertJ assertions.
  - Use static `assertThat` import for better readability.
  - Use `catchThrowable` in WHEN-block to capture exceptions and assert them in THEN-block.
- Use Mockito for mocking dependencies
  - Prefer using `@Mock` and `@InjectMocks` annotations if possible.
  - Add `@ExtendWith(MockitoExtension.class)` to the test class.
- Use Instancio for generating test data (Prefer this over static created data).
  - Use InstancioUtils `random(Project.class)` methods with static import to generate random data.
  - Add `@ExtendWith(InstancioExtension.class)` to the test class.
  - Prefer Instancio built-ins; no external helper utilities required.

## Exceptions & Logging

- Use domain-specific exceptions; do not swallow exceptions.
- Propagate with context; convert to meaningful HTTP errors in controllers.
- Logging via SLF4J (`LoggerFactory.getLogger(...)`); no `printStackTrace`.

## Git Hygiene

- Conventional Commits (e.g., `feat: add query endpoint`).
- Small, focused PRs with description, testing steps, and screenshots/logs where relevant.
