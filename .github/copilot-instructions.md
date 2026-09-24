# Project Overview
This service (`data-loader-1`) is part of the Stock Trading microservices platform. It fetches live financial
instrument prices from public market-data APIs (currently FinnHub via WebSocket), unifies the data into an
internal domain model, and publishes price updates as events to Apache Kafka.

It periodically communicates with an external **Subscription Manager** service over HTTP:
- registers itself on startup (`registerToSubscriptionManager`)
- sends regular check-ins (health/heartbeat)
- fetches its current subscription (list of financial instruments it's responsible for)

Status: **work in progress** — some parts (e.g. multi-client WebSocket management, typed exceptions for the
Subscription Manager client) are incomplete or contain TODOs. Don't assume unfinished areas are intentional
final design; check for TODO comments (some in Polish) before "fixing" behavior there.

# Architecture
Domain-Driven Design + hexagonal (ports & adapters), with domain logic under
`com.stocktrading.dataloader1.domain`:

- `domain.model` — framework-independent core models (`FinancialInstrumentModel`, `FinancialInstrumentPriceModel`,
  `SubscriptionModel`, `DataLoaderModel`). Built with Lombok `@Builder`; prefer records where possible.
- `domain.ports` — interfaces the domain depends on, implemented by adapters:
  `SubscriptionManagerClient`, `FinancialInstrumentRepository`, `RemoteSecretsManagerClient` (Kafka publishing
  port lives similarly — check `event`/`ports` before assuming there isn't one).
- `domain.service` — application services orchestrating use cases (`DataLoaderService`, `FinancialInstrumentService`).
- `domain.usecase` — scheduled/entry-point use cases (`@Scheduled` jobs), e.g. `RegularCheckInOfDataLoaderUseCase`,
  `ObtainCurrentSubscriptionUseCase`.
- `domain.event` / `domain.exception` — domain events (e.g. `FinancialInstrumentSubscriptionStateChangedEvent`)
  and domain-specific exceptions (`AlreadySubscribedException`, `ModelNotFoundException`).

Adapters:
- `data` — JPA persistence adapter (`FinancialInstrumentEntity`, `FinancialInstrumentJpaRepository`,
  `FinancialInstrumentRepositoryImpl`), implements `FinancialInstrumentRepository`.
- `remote.subscriptionmanager` — REST client implementing `SubscriptionManagerClient`.
- `remote.finnhub` — OkHttp WebSocket client/handler subscribing to live trade updates from FinnHub.
- `remote.kafkaeventpublisher` — publishes unified price events to Kafka.
- `remote.secretmanager` — AWS Secrets Manager integration for API keys (`RemoteSecretsManagerClient`).
- `remote.restapi` — local REST API (see below).

# Conventions
- Each package that crosses a layer boundary (`remote.*`) typically has its own package-private DTOs
  (records with `@Builder`) plus a dedicated `*Mapper` component (e.g. `ApiMapper`, `DataMapper`,
  `DataLoaderMapper`) to translate to/from domain models. Follow this pattern for new adapters instead of
  reusing domain models directly in DTOs/entities.
- Exceptions: adapters/domain define their own `RuntimeException` subclasses (e.g.
  `FinnHubApiClientRuntimeException`, `SecretManagerClientRuntimeException`, `AlreadySubscribedException`,
  `ModelNotFoundException`) rather than throwing generic `RuntimeException`. Prefer this pattern for new code
  even where existing code (e.g. `SubscriptionManagerClientService`) hasn't been updated yet.
- Domain-level exceptions are translated to HTTP responses via `@RestControllerAdvice`
  (`GlobalExceptionHandler` in `remote.restapi`).
- Most classes/methods in adapter packages are package-private by default unless they need to be exposed
  (e.g. implement a `domain.ports` interface or are Spring `@Bean`/`@Configuration` classes).
- Logging: `@Slf4j` or `@Log4j2` (both appear in the codebase — either is acceptable, don't standardize
  without discussion).

# About the `restapi` package

This module exposes a local REST API (`/api/v1/financialInstrument`) with full CRUD-style endpoints
(get/subscribe/unsubscribe by id, name or symbol), backed by a PostgreSQL-persisted `FinancialInstrumentEntity`.

This predates (and overlaps with) the Subscription Manager integration — an early, self-contained way to manage
subscriptions before that responsibility moved to the external Subscription Manager service. It's slated to be
repurposed (candidates: local read-model/cache of current subscription, admin/ops API, or historical price
storage) — don't assume it should be removed or is dead code.

# Core use cases
- Register with Subscription Manager on startup (`DataLoaderService.registerDataLoaderToSubscriptionManagerOnStartup`)
- Scheduled check-ins with Subscription Manager (health/heartbeat) — `RegularCheckInOfDataLoaderUseCase`
- Scheduled fetch of current subscription — `ObtainCurrentSubscriptionUseCase`
- Subscribe to live trade updates via FinnHub WebSocket for instruments in the current subscription
- Publish unified price events to Kafka

# Code guidelines
- Follow standard Java conventions and best practices; IntelliJ default formatting.
- Use Lombok (`@Builder`, `@AllArgsConstructor`, `@Getter`, `@Slf4j`/`@Log4j2`) to reduce boilerplate, matching
  existing style in each package.
- Keep domain code framework-agnostic — no Spring/JPA/Jackson/OkHttp types in `domain.model` or `domain.ports`.
- When adding a new external integration, add it under `remote.<providerName>` with its own DTOs + mapper,
  and expose only a `domain.ports` interface to the rest of the app.
- Write tests alongside new use cases/services; current test coverage is minimal
  (`DataLoader1ApplicationTests` is just a context-load smoke test) — new features should include unit tests.

# Tech stack
- Java 25
- Spring Boot 4.1
- Spring Kafka
- Spring Data JPA + PostgreSQL
- OkHttp (WebSocket client for FinnHub)
- AWS Secrets Manager SDK
- Lombok, Jackson (Jackson 3 `tools.jackson` namespace is used, not the legacy `com.fasterxml.jackson.databind`)
