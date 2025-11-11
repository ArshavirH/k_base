# Repository Guidelines

## Project Structure & Module Organization

- `src/main/java/com/buildware/kbase/`: Spring Boot application code (`Application.java`).
- `src/main/resources/`: configuration (e.g., `application.yaml`).
- `src/test/java/`: JUnit tests mirroring main package paths.
- `local_stack/docker-compose.yaml`: local Postgres (pgvector) for development.
- Build files: `build.gradle`, `settings.gradle`, Gradle wrapper scripts.
- Spring Modulith: module annotations via `package-info.java`; see `docs/modulith.md`.

## Build, Test, and Development Commands

- `./gradlew clean build`: compile, run checks, and package the app.
- `./gradlew bootRun`: start the Spring Boot server on `:8080`.
- `./gradlew test`: run unit/integration tests (JUnit 5).
- `./gradlew checkstyleMain checkstyleTest` or `./gradlew check`: run Checkstyle with custom rules
  in `checkstyle/checkstyle.xml` (warnings are treated as errors).
- `./gradlew jacocoTestReport`: generate coverage report at
  `build/reports/jacoco/test/html/index.html`.
- `docker compose -f local_stack/docker-compose.yaml up -d`: start local Postgres.
- Environment overrides (examples):
  - `OPENAI_API_KEY=...` (required for embeddings)
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/kbase`
  - `SPRING_DATASOURCE_USERNAME=postgres` `SPRING_DATASOURCE_PASSWORD=user123`
  - Versions are centralized in `build.gradle` under the `ext` block.

## Coding Style & Naming Conventions

- Language: Java 21; framework: Spring Boot 3.x.
- Indentation: 4 spaces (GJF AOSP); aim for ≤120 chars/line.
- Packages: `com.buildware.kbase...` (lowercase); Classes: `PascalCase`; methods/fields:
  `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Prefer constructor injection; keep controllers thin and delegate to services.
- Imports: avoid fully qualified class names in code; use imports unless resolving ambiguity.
- Configuration lives in `application.yaml`; avoid hard‑coded secrets.
- Full guide: see `docs/coding-guidelines.md`.

## Libraries & Patterns

- Mapping: Use MapStruct for DTO ↔ domain mapping; do not hand-write mapping in controllers or services. Prefer dedicated mapper interfaces (package `..mapper` when present) and use `@BeanMapping(nullValuePropertyMappingStrategy = IGNORE)` with `@MappingTarget` for updates.
- Boilerplate: Lombok (`@Getter`, `@Setter`, `@Builder`, etc.) — runtime optional; compile-time
  only.
- Validation: `spring-boot-starter-validation` with `@Valid` and constraint annotations.
- Utilities: Apache Commons Lang (`StringUtils`, `Validate`, etc.).
- Migrations: Flyway SQL scripts in `src/main/resources/db/migration` (e.g.,
  `V1__create_projects.sql`). Primary keys use UUIDs; ensure `pgcrypto` is enabled.

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
  - Use InstancioUtils `random(<Class>.class)` methods with static import to generate random data.
  - Add `@ExtendWith(InstancioExtension.class)` to the test class.
  - Prefer Instancio built-ins; no external helper utilities required.

### MockitoBean Migration

- Use `@MockitoBean` from `org.springframework.boot.test.mock.mockito.MockitoBean` in all Spring
  test slices instead of `@MockBean`.
- `@MockBean` is deprecated and should not be used in new tests.

### Coverage

- Generate a coverage report: `./gradlew jacocoTestReport` →
  `build/reports/jacoco/test/html/index.html`.
- Aim for meaningful coverage of services and controllers; avoid chasing percentage metrics.

## Commit & Pull Request Guidelines

- Commits: follow Conventional Commits (e.g., `feat: add query endpoint`,
  `fix: handle null projectCode`).
- PRs must include: clear description, linked issue (if any), how to test (commands/curl), and notes
  on config changes.
- Ensure Gradle build and tests pass before requesting review.

## Security & Configuration Tips

- Never commit secrets; supply via env vars (e.g., `OPENAI_API_KEY`).
- Local DB defaults differ: Docker stack uses DB `kbase`/password `user123`; adjust Spring
  `datasource` via env.
- If using pgvector, ensure `CREATE EXTENSION IF NOT EXISTS vector;` is enabled on the database.

## Related Docs

- `README.md`: project overview and endpoints.
- `docs/architecture.md`: high-level design and module plan.
- `docs/modulith.md`: Modulith modules and tests.
- `docs/coding-guidelines.md`: full coding rules and conventions.
- OpenAPI available at runtime via Swagger UI at `/swagger-ui/index.html`.
- `src/main/resources/application.yaml`: runtime configuration defaults.
- `local_stack/docker-compose.yaml`: local Postgres with pgvector.

## Modulith Rules & SPI Usage

- Do not use cross-module dependencies. Always depend on `spi` from other feature modules.
- Provide adapters in the owning module to implement SPI interfaces (e.g., knowledge implements
  `KnowledgeQueryPort`).
- Do not export internal domain models across modules. Use view types under `spi.view` for
  cross-module data (e.g., `KnowledgeHitView`).
- MCP or other integration modules must only call into `spi`, never directly into feature services.
