# tpm-machine-service

Machine registry for the TPM system. Owns machine identity and machine status.

Part of a set of four repositories — **start with
[tpm-platform](../tpm-platform/README.md)**, which explains the architecture and runs
everything together.

## What it does

Registers machines and keeps their status: `RUNNING`, `STOPPED`, `UNDER_MAINTENANCE`.

The status is not set from outside. It changes in reaction to events published by the work
order service: `WorkOrderStarted` sends a machine into maintenance, `WorkOrderResolved` returns
it to service. Registering a machine publishes `MachineRegistered`, which other services use to
build their own local copy of the registry.

## Endpoints

Both require a `Bearer` token signed by the auth service.

| Method | Path | |
|---|---|---|
| `POST` | `/machines` | register a machine, returns `201` |
| `GET` | `/machines/{id}` | read one |
| `GET` | `/actuator/health` | open, no token required |

## Trying it by hand

With the platform running (`cd ../tpm-platform && make up`). Copy-paste in order.

```bash
# A token. Any role works here - this service does not check roles, only signatures.
TOKEN=$(curl -s -X POST localhost:8080/token -H 'Content-Type: application/json' \
  -d '{"username":"kierownik","password":"kierownik"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

# Health - open, no token
curl -s localhost:8081/actuator/health

# Without a token -> 401
curl -s -i -X POST localhost:8081/machines \
  -H 'Content-Type: application/json' -d '{"name":"Press A"}' | head -1

# Register a machine -> 201
MACHINE=$(curl -s -X POST localhost:8081/machines \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"name":"Hydraulic press"}' | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "$MACHINE"

# Read it back -> 200, status RUNNING
curl -s localhost:8081/machines/"$MACHINE" -H "Authorization: Bearer $TOKEN"

# Unknown id -> 404
curl -s -i localhost:8081/machines/no-such-machine -H "Authorization: Bearer $TOKEN" | head -1

# Blank name -> 400 (rejected by request validation, before the domain)
curl -s -i -X POST localhost:8081/machines \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"name":""}' | head -1

# The machine reacts to events from the other service. Start a repair there
# and read this machine again - its status will have changed to UNDER_MAINTENANCE
# without anyone calling an endpoint here. See ../tpm-workorder-service/README.md
```

## Running it on its own

```bash
docker compose up --build -d   # service, its own PostgreSQL, its own RabbitMQ
```

Standalone it will not receive work order events — nothing publishes them. Use the platform
for the full picture.

For development, `./mvnw spring-boot:test-run` starts the application with a PostgreSQL
container brought up automatically by Testcontainers.

## Tests

```bash
./mvnw test
```

Aggregate tests run without Spring and without a database. Integration tests use Testcontainers.
