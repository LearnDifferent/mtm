# AGENTS.md

Shared guidance for coding agents working in this repository.

## Purpose

MTM is a social bookmarking backend built with Spring Boot, MySQL, MyBatis, Redis, Elasticsearch, and Docker.
Use this file as the first-stop operational guide before making changes.

## Quick Commands

Run all commands from the repository root:

```bash
# Build
./mvnw clean package

# Run tests
./mvnw test

# Start app locally
./mvnw spring-boot:run

# Build container image
docker build -t mtm:latest .

# Start local stack
docker-compose up -d
```

## Project Layout

- `src/main/java` - Spring Boot application code (controllers, services, mappers, config)
- `src/main/resources` - `application.yml`, MyBatis XML, static resources
- `src/test/java` - unit/integration tests
- `pom.xml` - Maven dependencies and plugins
- `docker-compose.yml` - local multi-service environment
- `init.sql` - database initialization script
- `api-document.md` - generated API documentation

## Tech Notes

- Java + Spring Boot 2.3.7.RELEASE
- Persistence: MyBatis + MySQL 8
- Cache/session: Redis (Lettuce)
- Search: Elasticsearch (IK + Kuromoji plugins required)
- Auth: Sa-Token

## Agent Working Agreement

- Make minimal, scoped changes that match existing style.
- Prefer fixing root causes over quick patches.
- Do not edit generated docs unless asked.
- Do not commit secrets (tokens, passwords, keys).
- If changing behavior, add or update tests when practical.
- Before running the app, check whether MySQL and Redis are running; if either is down, start them first.
- Do not block app startup on Elasticsearch status; Elasticsearch can be ignored for basic program runs.
- Before finishing, run relevant tests (at least targeted module tests).

## Config Reminders

- Check `src/main/resources/application.yml` for environment defaults.
- Local MySQL commonly uses schema `mtm` on port `3306`.
- Elasticsearch plugins must exist under `esplugins` for Docker-based setup.

## Useful References

- [README.md](./README.md)
- [api-document.md](./api-document.md)


