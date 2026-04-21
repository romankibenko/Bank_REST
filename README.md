# 💳 Bank Card Management — REST API

A secure backend service for managing bank cards, built with Java and Spring Boot.

Implements JWT authentication, role-based access control, card number encryption, fund transfers, and full REST API with Swagger documentation.

---

## Features

### 👤 Authentication & Authorization
- JWT-based authentication (Spring Security)
- Two roles: **ADMIN** and **USER**

### 🔐 Admin capabilities
- Create, activate, block, and delete cards
- Manage users
- View all cards across all users

### 👤 User capabilities
- View own cards with search and pagination
- Request card blocking
- Transfer funds between own cards
- Check balance

### 🛡️ Security
- Card numbers are **encrypted** in the database
- Displayed with masking only: `**** **** **** 1234`
- Role-based endpoint protection

---

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| Java 17+ | Core language |
| Spring Boot | Application framework |
| Spring Security + JWT | Authentication & authorization |
| Spring Data JPA | Data access layer |
| PostgreSQL / MySQL | Database |
| Liquibase | Database migrations |
| Docker Compose | Dev environment |
| Swagger / OpenAPI | API documentation |

---

## Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose

### Run with Docker

```bash
docker-compose up
```

### Run locally

```bash
./gradlew bootRun
```

### API Docs

After startup, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI spec: `docs/openapi.yaml`

---

## API Overview

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| `POST` | `/auth/login` | — | Get JWT token |
| `GET` | `/cards` | ADMIN | Get all cards |
| `POST` | `/cards` | ADMIN | Create a card |
| `PATCH` | `/cards/{id}/block` | ADMIN | Block a card |
| `PATCH` | `/cards/{id}/activate` | ADMIN | Activate a card |
| `DELETE` | `/cards/{id}` | ADMIN | Delete a card |
| `GET` | `/cards/my` | USER | Get own cards (with filter & pagination) |
| `POST` | `/cards/my/{id}/block-request` | USER | Request card blocking |
| `POST` | `/transfers` | USER | Transfer between own cards |

---

## Card Model

| Field | Details |
|-------|---------|
| Number | Encrypted in DB, masked on display (`**** **** **** 1234`) |
| Owner | Linked to user account |
| Expiry date | MM/YY |
| Status | `ACTIVE` / `BLOCKED` / `EXPIRED` |
| Balance | Decimal, updated on transfers |

---

## Author

**Roman Kibenko** — Java Developer & Construction Project Manager

