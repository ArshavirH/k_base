# Coding Guidelines

## Code Style
- Java 21, Spring Boot 3.x; 4-space indentation; target 120 columns.
- Formatting: follow project style (4-space indent). Use your IDE formatter; style is enforced by Checkstyle.
- Static analysis: Checkstyle with custom rules in `checkstyle/checkstyle.xml`. Run `./gradlew checkstyleMain checkstyleTest` or `./gradlew check`.
- Import order: enforced by Checkstyle (static vs non-static groups).

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

## Testing
- JUnit 5 for all tests; name test classes with `*Test`.
- Use AssertJ for assertions (static `assertThat` import recommended).
- Mockito for mocking dependencies.
  - In Spring MVC/Data slices, use `@WebMvcTest`/`@DataJpaTest` and `@org.springframework.test.context.bean.override.mockito.MockitoBean` instead of deprecated `@MockBean`.
  - For plain unit tests, prefer `@ExtendWith(MockitoExtension.class)` with `@Mock` and `@InjectMocks`.
- Instancio for data generation.
  - Always use `InstancioUtils.random(...)` with a static import to generate test data (e.g., `random(Project.class)`, `random(String.class)`).
  - Add `@ExtendWith(InstancioExtension.class)` to the test class when using Instancio.
- BDD method naming: `should_<behavior>_when_<condition>` with `// GIVEN`, `// WHEN`, `// THEN` blocks in tests.

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

## Exceptions & Logging
- Use domain-specific exceptions; do not swallow exceptions.
- Propagate with context; convert to meaningful HTTP errors in controllers.
- Logging via SLF4J (`LoggerFactory.getLogger(...)`); no `printStackTrace`.

## Git Hygiene
- Conventional Commits (e.g., `feat: add query endpoint`).
- Small, focused PRs with description, testing steps, and screenshots/logs where relevant.

## Formatting & Checks: Recommended Flow
- Before commit: run `./gradlew checkstyleMain checkstyleTest` to verify style.
- Pre-push/CI: run `./gradlew check` to execute Checkstyle and tests.
- Checkstyle treats warnings as errors (build fails on violations).
