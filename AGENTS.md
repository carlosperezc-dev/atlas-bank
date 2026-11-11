# AGENTS.md

Spring Boot 4.1.1 + Java 25 monolith using DDD / Hexagonal architecture (ports & adapters). Single Maven module at the repo root; build with the committed wrapper (`mvnw` / `mvnw.cmd`, Maven 3.9.16).

## Build & tests (Windows)

- **`JAVA_HOME` must point to a JDK 25.** On this machine `java` on PATH is JDK 25 but `JAVA_HOME` is JDK 21, so the wrapper fails with `release version 25 not supported`. Set it first:
  ```powershell
  
  $env:JAVA_HOME = "<ruta-a-tu-jdk-25>"
.\mvnw.cmd test
  ```
- All tests: `.\mvnw.cmd test`
- One test class: `.\mvnw.cmd -Dtest=MoneyDomainTest test`
- No lint/format/typecheck plugin is configured; compilation + tests are the only verification step.
- Tests do **not** need Keycloak or Docker running (the Spring context loads without them).

## Architecture rules (enforced by ArchUnit in `src/test/.../archtest`)

These run as part of `test`, so a layering change can fail the build even when code compiles:

- `..domain..` must not depend on `..application..`, `..infrastructure..`, `org.springframework..`, or `org.springframework.security..`. The domain is plain Java.
- `..application..` must not depend on `..infrastructure..` or `org.springframework.security..`; it talks through ports.
- `application.port.in.*` and `application.port.out.*` must be interfaces; `*UseCase` classes live in `application.port.in`; `*Controller` classes live in `infrastructure.adapter.in.rest`.
- Domain modules (`domain.model.*`) and the top-level layers must stay free of dependency cycles.

## Layout / wiring

- Base package is `com.atlas.bank.atlas_bank` (underscore — `atlas-bank` is an invalid Java identifier; see `HELP.md`). Put all new code under it.
- `application`: `port/in` (use cases), `port/out` (repository/integration ports), `service` (implementations), `command`/`query` (inputs + read models), `facade` (read-side aggregation).
- `infrastructure`: `adapter/in/rest` (controllers, DTOs, MapStruct mappers), `adapter/out/persistence` (JPA entities, Spring Data repos, port adapters), `adapter/out/fraud` (external fraud adapter), `adapter/listener` (domain event listeners), `config` (`DomainBeanConfig`, `SecurityConfig`, `GlobalExceptionHandler`).
- Pure domain collaborators (fee strategies, validators, `TransferDomainService`) are plain classes registered as `@Bean` in `DomainBeanConfig`. The `@Order` values there set fee-calculator and validator precedence — preserve ordering when adding one.
- `AccountService` is wrapped by a `@Primary AuditableAccountService` that implements the same use-case interfaces; the real implementation is still `AccountService`.
- Transfers use a template method: `TransactionProcessor.process()` = validate -> calculateFee -> execute -> save (`TransferService` provides the steps).

## Gotchas

- **Duplicate value-object names.** `domain.model.shared.{Money,Email}` are the domain types; `infrastructure.adapter.out.persistence.{Money,Email}` are separate JPA `@Embeddable` copies. Persistence mappers convert between them — import the correct one.
- **JWT role mapping.** The custom `JwtAuthenticationConverter` maps Keycloak
  `realm_access.roles` to Spring Security authorities using the `ROLE_` prefix.
  For example, the Keycloak realm role `ADMIN` becomes `ROLE_ADMIN`,
  matching the `hasRole("ADMIN")` authorization rules.
- Runtime is H2 in-memory (`jdbc:h2:mem:atlasbank`, `ddl-auto: create-drop`, `show-sql: true`) — schema and data reset on every restart; H2 console at `/h2-console`. App port `8080`.
- Auth depends on Keycloak at `http://localhost:8181/realms/atlas-bank`; `docker/docker-compose.yml` starts Keycloak + Postgres (`docker compose -f docker/docker-compose.yml up -d`). The Postgres service is only for Keycloak, not the app.
- MapStruct + Lombok need the `lombok-mapstruct-binding` annotation-processor entry in `pom.xml`; keep it if you touch the processor config.
