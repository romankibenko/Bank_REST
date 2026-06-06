# 💳 Bank Card Management — REST API

A secure backend service for managing bank cards, built with Java and Spring Boot.

Implements JWT authentication, role-based access control, card number encryption, fund transfers, and a full REST API with Swagger documentation.

---

## Features

### 👤 Authentication & Authorization
- JWT-based authentication (Spring Security), stateless
- Two roles: **ADMIN** and **USER**

### 🔐 Admin capabilities
- Create and block cards
- Create, block, activate, and delete users; list all users

### 👤 User capabilities
- View own cards with status filter and pagination
- Request card blocking
- Transfer funds between **own** cards
- Check card balance

### 🛡️ Security
- Card numbers are **encrypted** in the database (deterministic AES, so a card
  can still be looked up by its number for transfers)
- Numbers are shown masked only: `**** **** **** 1234`
- Role-based endpoint protection (URL rules + method-level `@PreAuthorize`)

---

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| Java 21 | Core language |
| Spring Boot 3.4 | Application framework |
| Spring Security + JWT (jjwt) | Authentication & authorization |
| Spring Data JPA | Data access layer |
| PostgreSQL 16 | Database |
| Liquibase | Database migrations |
| Maven | Build tool |
| Docker Compose | Dev environment |
| Testcontainers | Integration test for migrations |
| Swagger / OpenAPI | API documentation |

---

## Getting Started

### Prerequisites
- Java 21
- Docker & Docker Compose

### Run with Docker

```bash
# build the application image (Jib), then start app + PostgreSQL
mvn clean compile jib:dockerBuild
docker-compose up
```

The app starts on `http://localhost:8080`. Liquibase applies all migrations and
seeds demo users on first run.

### Run tests

```bash
mvn clean test
```

> The migration test uses Testcontainers — with Docker running it spins up a real
> PostgreSQL and verifies the Liquibase migrations; without Docker it is skipped.

### API Docs

After startup, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI spec: `docs/openapi.yaml`

---

## Demo Credentials

Seeded on first startup (see `V3__Seed_data.sql`):

| Username | Password | Role |
|----------|----------|------|
| `admin`  | `admin123` | ADMIN |
| `user`   | `user123`  | USER |

---

## API Overview

All endpoints are prefixed with `/api`. Protected endpoints require the
`Authorization: Bearer <token>` header.

### Auth — public

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/auth/login` | `{ "username", "password" }` | Returns `{ "token": "..." }` |
| `POST` | `/api/auth/register` | `{ "username", "password" }` | Registers a new USER |

### Admin — `ROLE_ADMIN`

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/admin` | `{ "username", "password", "role" }` | Create a user |
| `GET` | `/api/admin/users` | — | List users (paginated) |
| `POST` | `/api/admin/cards` | `{ "userId", "number", "expiryDate" }` | Create a card (`number` = 16 digits) |
| `PUT` | `/api/admin/cards/{cardId}/block` | — | Block a card |
| `PUT` | `/api/admin/users/{userId}/block` | — | Block a user |
| `PUT` | `/api/admin/users/{userId}/activate` | — | Activate a user |
| `DELETE` | `/api/admin/users/{userId}` | — | Delete a user |

### User — `ROLE_USER`

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `GET` | `/api/cards?status=ACTIVE&page=0&size=10` | — | Own cards (filter + pagination) |
| `GET` | `/api/cards/{cardId}/balance` | — | Card balance |
| `PUT` | `/api/cards/{cardId}/block-request` | — | Request card blocking |
| `POST` | `/api/cards/transfers` | `{ "fromCardNumber", "toCardNumber", "amount" }` | Transfer between own cards |

---

## Testing with Postman

1. **Login as admin** → `POST http://localhost:8080/api/auth/login`
   ```json
   { "username": "admin", "password": "admin123" }
   ```
   Copy `token` from the response.

2. **Create a user** (or use the seeded `user`, id `2`) →
   `POST /api/admin` with header `Authorization: Bearer <admin-token>`
   ```json
   { "username": "alice", "password": "alice123", "role": "ROLE_USER" }
   ```

3. **Create two cards** for that user →
   `POST /api/admin/cards` (Bearer admin)
   ```json
   { "userId": 2, "number": "4111111111111111", "expiryDate": "2030-12-31" }
   ```
   ```json
   { "userId": 2, "number": "4222222222222222", "expiryDate": "2030-12-31" }
   ```

4. **Login as that user** → `POST /api/auth/login` → copy the user token.

5. **List own cards** → `GET /api/cards` (Bearer user). Numbers come back masked.

6. **Transfer** → `POST /api/cards/transfers` (Bearer user)
   ```json
   { "fromCardNumber": "4111111111111111", "toCardNumber": "4222222222222222", "amount": 100.00 }
   ```

> Note: there is no balance top-up endpoint (it was not part of the assignment) —
> new cards start at `0.00`. To demo a successful transfer, set a balance directly
> in the DB, e.g.:
> ```sql
> UPDATE cards SET balance = 1000.00 WHERE last_four_digits = '1111';
> ```
> A transfer to a card that doesn't belong to the user returns `404` (own-cards only).

---

## Card Model

| Field | Details |
|-------|---------|
| Number | Encrypted in DB, masked on display (`**** **** **** 1234`) |
| Last four digits | Stored separately for masking |
| Owner | Linked to a user account |
| Expiry date | `YYYY-MM-DD` |
| Status | `ACTIVE` / `BLOCKED` / `EXPIRED` |
| Balance | Decimal, updated on transfers |

---

## Recent Changes

Key fixes applied to the codebase:

- **Transfers fixed** — card numbers are now encrypted deterministically
  (`CardNumberEncryptor`, AES/CBC), so `findByNumber` actually matches. Previously
  the non-deterministic `Encryptors.text` made every transfer fail with "card not found".
- **Own-cards only** — the transfer target is resolved by `(number, userId)`, so
  transfers to other users' cards are rejected.
- **Idempotent encryption migration** — `DataMigrationService` only encrypts
  still-plaintext numbers, so restarts no longer double-encrypt data.
- **No secret leakage** — removed `System.out` prints of the JWT secret/token.
- **Non-destructive migration V4** — widens `card_number` with `ALTER COLUMN ... TYPE`
  instead of dropping the column (which lost data).
- **Disabled/invalid login → 401** instead of 500 (handled in `GlobalExceptionHandler`).
- **Consistent routes** — user-management moved under `/api/admin/users`.
- **Known demo credentials** seeded (`admin/admin123`, `user/user123`).
- **Test suite repaired** — full suite is green (unit + slice + Testcontainers migration test).

---

## Author

**Roman Kibenko** — Java Developer & Construction Project Manager
