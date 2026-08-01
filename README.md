# data-loader-1

Part of the **Stock Trading** microservices system. This service is responsible for fetching live financial
instrument prices from a public market-data provider and streaming them into the platform via Apache Kafka.

## What it does

1. **Asks the Subscription Manager what to track** — on startup (and periodically) it sends HTTP requests to the
   external *Subscription Manager* service to find out which financial instruments this particular Data Loader
   instance is responsible for subscribing to.
2. **Subscribes via a public market-data API** — using that list, it opens a connection to a publicly available
   stock market API (currently [FinnHub](https://finnhub.io/), over a WebSocket) and subscribes to live trade
   updates for each instrument.
3. **Unifies the data format** — incoming provider-specific payloads are mapped into a common internal domain
   model, decoupling the rest of the system from FinnHub's (or any future provider's) message format.
4. **Publishes live prices to Kafka** — the normalized price updates are published as events on a Kafka topic, to
   be consumed by other services in the platform.

```
Subscription Manager  --HTTP-->  data-loader-1  --WebSocket-->  FinnHub API
                                       |
                                       v
                              (unify data format)
                                       |
                                       v
                                Apache Kafka topic
```

## Architecture

The codebase follows a loose hexagonal / ports-and-adapters style:

- `domain` — core business logic, independent of any framework/provider specifics
  - `model` — internal domain models (e.g. `FinancialInstrumentModel`, `FinancialInstrumentPriceModel`, `SubscriptionModel`)
  - `ports` — interfaces the domain depends on (`SubscriptionManagerClient`, `KafkaEventPublisher`, `RemoteSecretsManagerClient`, `FinancialInstrumentRepository`)
  - `service` — application services orchestrating use cases (`FinancialInstrumentService`, `DataLoaderService`)
  - `event` / `exception` — domain events and exceptions
- `remote` — adapters implementing the ports above
  - `subbscriptionmanager` — REST client that registers/checks in with the Subscription Manager and fetches the current subscription
  - `finnHub` — WebSocket client/handler talking to the FinnHub API
  - `kafkaEventPublisher` — publishes unified price events to Kafka
  - `restApi` — local REST API + JPA persistence for subscribed financial instruments (see below)
  - `secretManager` — integration with AWS Secrets Manager for API keys
- `data` — JPA entities/repositories backing the persistence layer

## Tech stack

- Java 25
- Spring Boot 4.1
- Spring Kafka
- Spring Data JPA + PostgreSQL
- OkHttp (WebSocket client for FinnHub)
- AWS Secrets Manager SDK
- Lombok, Jackson

## Running locally

1. Start supporting infrastructure (PostgreSQL) with Docker Compose:
   ```
   docker-compose up -d
   ```
2. Make sure a local Kafka broker is running and reachable at `localhost:9092`.
3. Run the application (default port `8081`):
   ```
   ./mvnw spring-boot:run
   ```

## About the `restApi` package

This module exposes a local REST API (`/api/v1/financialInstrument`) with full CRUD-style endpoints
(get/subscribe/unsubscribe by id, name or symbol), backed by a PostgreSQL-persisted `FinancialInstrumentEntity`.

This predates (and slightly overlaps with) the Subscription Manager integration — it looks like it was an early,
self-contained way to manage which instruments this loader subscribes to, before subscription responsibility was
moved to a dedicated external service. Some ideas for what to do with it now:

- **Local cache / read model**: keep it as a queryable local cache of "what am I currently subscribed to", kept in
  sync with the Subscription Manager (instead of being the source of truth itself). Handy for health checks,
  debugging, and fast local lookups without calling out to Subscription Manager.
- **Admin/ops API**: turn it into an internal admin endpoint for manually forcing a subscribe/unsubscribe or
  inspecting current WebSocket subscriptions, useful for troubleshooting a running instance.
- **Retire it**: if the Subscription Manager is now the single source of truth for subscriptions, this package
  (and the `FinancialInstrumentEntity`/JPA layer) could be removed to reduce duplicated responsibility, and
  replaced by an in-memory representation of the current subscription.
- **Historical price storage**: repurpose the persistence layer to store incoming price ticks
  (`FinancialInstrumentPriceModel`) for auditing/backfill, exposing them through the existing REST API instead of
  (or in addition to) just streaming them to Kafka.

## Status

Work in progress.
