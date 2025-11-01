# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/buildware/k_base/`: Spring Boot application code (`Application.java`).
- `src/main/resources/`: configuration (e.g., `application.yaml`).
- `src/test/java/`: JUnit tests mirroring main package paths.
- `local_stack/docker-compose.yaml`: local Postgres (pgvector) for development.
- Build files: `build.gradle`, `settings.gradle`, Gradle wrapper scripts.
 - Spring Modulith: module annotations via `package-info.java`; see `docs/modulith.md`.

## Build, Test, and Development Commands
- `./gradlew clean build`: compile, run checks, and package the app.
- `./gradlew bootRun`: start the Spring Boot server on `:8080`.
- `./gradlew test`: run unit/integration tests (JUnit 5).
- `./gradlew checkstyleMain checkstyleTest` or `./gradlew check`: run Checkstyle with custom rules in `checkstyle/checkstyle.xml`.
- `./gradlew jacocoTestReport`: generate coverage report at `build/reports/jacoco/test/html/index.html`.
- `docker compose -f local_stack/docker-compose.yaml up -d`: start local Postgres.
- Environment overrides (examples):
  - `OPENAI_API_KEY=...` (required for embeddings)
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/k_base`
  - `SPRING_DATASOURCE_USERNAME=postgres` `SPRING_DATASOURCE_PASSWORD=user123`
  - Versions are centralized in `build.gradle` under the `ext` block.

## Coding Style & Naming Conventions
- Language: Java 21; framework: Spring Boot 3.x.
- Indentation: 4 spaces (GJF AOSP); aim for ≤120 chars/line.
- Packages: `com.buildware.kbase...` (lowercase); Classes: `PascalCase`; methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Prefer constructor injection; keep controllers thin and delegate to services.
- Configuration lives in `application.yaml`; avoid hard‑coded secrets.
- Full guide: see `docs/coding-guidelines.md`.

## Libraries & Patterns
- Mapping: MapStruct for DTO ↔ domain mappers; keep mappers in `..mapper` packages.
- Boilerplate: Lombok (`@Getter`, `@Setter`, `@Builder`, etc.) — runtime optional; compile-time only.
- Validation: `spring-boot-starter-validation` with `@Valid` and constraint annotations.
- Utilities: Apache Commons Lang (`StringUtils`, `Validate`, etc.).
- Migrations: Flyway SQL scripts in `src/main/resources/db/migration` (e.g., `V1__init.sql`).

## Testing Guidelines
- Framework: JUnit 5 via `spring-boot-starter-test`.
- Location: `src/test/java/...` mirroring main packages; name files `*Tests.java` (e.g., `ApplicationTests.java`).
- Use `@SpringBootTest` for integration, `@WebMvcTest` for MVC slices, and mocks for unit tests.
- Run locally with `./gradlew test`; add tests for controllers, services, and repository logic.

## Commit & Pull Request Guidelines
- Commits: follow Conventional Commits (e.g., `feat: add query endpoint`, `fix: handle null projectCode`).
- PRs must include: clear description, linked issue (if any), how to test (commands/curl), and notes on config changes.
- Ensure Gradle build and tests pass before requesting review.

## Security & Configuration Tips
- Never commit secrets; supply via env vars (e.g., `OPENAI_API_KEY`).
- Local DB defaults differ: Docker stack uses DB `k_base`/password `user123`; adjust Spring `datasource` via env.
- If using pgvector, ensure `CREATE EXTENSION IF NOT EXISTS vector;` is enabled on the database.

## Related Docs
- `README.md`: project overview and endpoints.
- `docs/architecture.md`: high-level design and module plan.
- `docs/modulith.md`: Modulith modules and tests.
- `docs/test-guidelines.md`: detailed testing practices for this repo.
- `docs/coding-guidelines.md`: full coding rules and conventions.
- `docs/openapi.yaml`: OpenAPI spec; Swagger UI at `/swagger-ui/index.html`.
- `src/main/resources/application.yaml`: runtime configuration defaults.
- `local_stack/docker-compose.yaml`: local Postgres with pgvector.
