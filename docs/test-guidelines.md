## Testing Guidelines

- Location: place tests under `src/test/java` mirroring the main package.
- Naming: end classes with `Tests` (e.g., `ApplicationTests.java`).
- How to run: `./gradlew test` (uses JUnit 5 via Spring Boot starter).

### Test Types
- Unit tests: isolate a class with mocks; fast and deterministic.
- Slice tests: `@WebMvcTest` for controller layer, `@DataJpaTest` for repositories.
- Integration tests: `@SpringBootTest` with minimal context; prefer testcontainers or a local DB when needed.

### Tooling & Conventions
- Assertions: AssertJ (`import static org.assertj.core.api.Assertions.*;`).
- Mocking: Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`).
- Given/When/Then naming encouraged, e.g., `should_return_200_when_valid_request`.
- Keep one assertion focus per test; extract builders/helpers when duplication appears.

### Data & DB
- Prefer in-memory fakes for unit tests; avoid real I/O.
- For JPA tests, use `@DataJpaTest` with a test profile and clean schema per run.
- Use Instancio for generating test fixtures (see examples in Instancio docs). Keep builders in test scope.

### Coverage
- Generate a coverage report: `./gradlew jacocoTestReport` → `build/reports/jacoco/test/html/index.html`.
- Aim for meaningful coverage of services and controllers; avoid chasing percentage metrics.
