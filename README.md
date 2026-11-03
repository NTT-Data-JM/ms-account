# ms-account (Taller 5 – Patrones + Resiliencia + Redis)

Reactive Spring Boot service for account balances: Cache Aside (Redis), Strategy fees, and Resilience4j (Circuit Breaker, Retry, RateLimiter, TimeLimiter). Core Banking is mocked with Mockoon on port `3001`.

## Prerequisites

- Java 17+, Maven 3.8+
- Redis on `localhost:6379` (`redis-server`)
- Mockoon (portable EXE in `session05/` **or** CLI)

## Mockoon evaluation

| Option | Role |
|--------|------|
| [`mockoon.portable.9.10.0.exe`](../mockoon.portable.9.10.0.exe) | Useful GUI: open/import `mockoon/core-banking.json` and press Play |
| `@mockoon/cli` + JSON | Better for automation / AI-maintained mocks in git |

The workshop anexos “usuarios/:id” demo is a Mockoon tutorial only. **This service** needs Core Banking:

`GET http://localhost:3001/accounts/core/{accountId}/balance`

That mock is versioned at [`mockoon/core-banking.json`](mockoon/core-banking.json).

### Start Mockoon (recommended)

**GUI (portable):**

1. Run `session05/mockoon.portable.9.10.0.exe`
2. Open / import `ms-account/mockoon/core-banking.json`
3. Start the environment (port **3001**)

**CLI:**

```bash
npx --yes @mockoon/cli start --data ./mockoon/core-banking.json --port 3001
```

### Mock account IDs

| accountId | Behavior |
|-----------|----------|
| `premium-001` | 200, `PREMIUM`, balance 1500 (5% fee via Strategy) |
| `basic-001` | 200, `STANDARD`, balance 500 (fixed fee 1.00) |
| `fail` | HTTP 500 → Circuit Breaker + Retry + fallback `ERROR` |
| `slow` | 200 after ~3s → TimeLimiter + fallback `ERROR TIMELIMITER` |
| any other | 200 default STANDARD balance 250 |

## Run the service

```bash
# terminal 1: redis-server
# terminal 2: Mockoon on :3001
mvn spring-boot:run
```

Service: `http://localhost:9092`

## Endpoints

| Endpoint | Demo |
|----------|------|
| `GET /accounts/{accountId}/balance` | Redis Cache Aside + Strategy fee + CB |
| `GET /accounts/cbretry/{accountId}/balance` | CB + Retry (use `fail`) |
| `GET /accounts/rl/{accountId}/balance` | RateLimiter (burst > 2 calls / 50s) |
| `GET /accounts/tl/{accountId}/balance` | TimeLimiter (use `slow`) |

Actuator: `http://localhost:9092/actuator/circuitbreakers`, `/actuator/retryevents`

## What was completed (lab gaps)

- `PercentageFeeStrategy` + `FeeStrategySelector` (PREMIUM → 5%, else fixed)
- `RedisConfig` + `AccountBalanceCacheRepository` + TTL `cache.account-balance.ttl`
- Resilience4j on `WebClientCoreBankingClient` (CB/Retry/TL + fallbacks) and YAML
