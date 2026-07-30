# Hospital Management API

A Spring Boot 4 / Spring Data JPA service over a normalized hospital schema, with a React
front end and interactive API docs. Built to exercise every JPA relationship type and the
query-performance problems that come with them.

**Stack:** Java 21 · Spring Boot 4.0.1 · Spring Data JPA / Hibernate · PostgreSQL · React 19 · Vite

---

## Run it

The app has **no embedded-database fallback** — it connects to the PostgreSQL you give it, or it
refuses to start. That is deliberate: a fallback silently serves an empty or stale database and
looks like the seed data came back.

Create `config/application.properties` (gitignored, read automatically from `./config/`):

```properties
spring.datasource.url=jdbc:postgresql://<host>/<db>?sslmode=require
spring.datasource.username=<user>
spring.datasource.password=<password>
```

Then:

```bash
./mvnw -Pfrontend package -DskipTests && java -jar target/*.jar
```

Open **http://localhost:9090** for the UI, or **/swagger-ui.html** for the interactive API docs.

<details>
<summary>Front end with hot reload</summary>

```bash
./mvnw spring-boot:run                     # API on :9090
cd frontend && npm install && npm run dev  # UI on :5173, proxies /api to :9090
```
</details>

<details>
<summary>Seeding an empty database</summary>

`data.sql` loads only when asked, once:

```bash
SPRING_SQL_INIT_MODE=always java -jar target/*.jar
```

Run it a second time and it fails on the unique email constraint — by design.
</details>

---

## Deploy with permanent data (Neon + Render)

The default configuration is already non-destructive — `ddl-auto=update` and
`sql.init.mode=never` — so pointing it at a real database keeps the data. **No profile is
involved**: set three environment variables and that is the whole switch.

1. Create a free project at [neon.tech](https://neon.tech).
2. Set these wherever the app runs — the Render dashboard, or the IntelliJ run configuration:

   | Variable | Value |
   |---|---|
   | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>.neon.tech/<db>?sslmode=require` |
   | `SPRING_DATASOURCE_USERNAME` | your Neon role |
   | `SPRING_DATASOURCE_PASSWORD` | your Neon password |

   Neon shows a `postgresql://user:pass@host/db?sslmode=require&channel_binding=require` URI —
   that is **not** a JDBC URL. Strip the credentials, prefix `jdbc:`, drop `channel_binding`
   (a libpq parameter the JDBC driver doesn't accept), and pass user and password separately.
   Prefer the `-pooler` host: it handles the many short-lived connections a web app makes.

3. **Once, against an empty database**, add `SPRING_SQL_INIT_MODE=always` to load `data.sql`,
   then remove it. Leaving it on re-inserts the seed and fails the unique email constraint.

Neon scales to zero when idle, so the first request after a quiet period takes a few seconds
while the database wakes.

> Seed data only ever loads when `SPRING_SQL_INIT_MODE=always` is set, and the schema is never
> dropped. If you see `data.sql` content instead of your own rows, the app is pointed at a
> different database — check which URL it logged at startup.

---

## Schema

```mermaid
erDiagram
    PATIENT ||--o| INSURANCE : "has (1:1, orphanRemoval)"
    PATIENT ||--o{ APPOINTMENT : "books (1:N)"
    DOCTOR ||--o{ APPOINTMENT : "handles (1:N)"
    DOCTOR }o--o{ DEPARTMENT : "staffs (M:N via my_dpt_doctors)"
    DEPARTMENT ||--o| DOCTOR : "head_doctor (1:1)"

    PATIENT {
        bigint id PK
        string name
        date birth_date
        string email UK
        string gender
        enum blood_group
        datetime created_at
        bigint patient_insurance_id FK
    }
    INSURANCE {
        bigint id PK
        string policy_number UK
        string provider
        date valid_until
        datetime created_at
    }
    APPOINTMENT {
        bigint id PK
        datetime appointment_time
        string reason
        bigint patient_id FK
        bigint doctor_id FK
    }
    DOCTOR {
        bigint id PK
        string name
        string specialization
        string email UK
    }
    DEPARTMENT {
        bigint id PK
        string name UK
        bigint head_doctor_id FK
    }
```

Constraints live in the database, not in application code: a composite unique constraint on
`(name, birth_date)`, a unique index on `email`, and an index on `birth_date`.

---

## Endpoints

| Method | Path | Notes |
|---|---|---|
| `GET` | `/api/patients` | Paginated — `?page=&size=&sort=id,asc` |
| `POST` | `/api/patients` | Bean-validated; 409 on duplicate email |
| `GET` | `/api/patients/{id}` | 404 as a problem detail |
| `DELETE` | `/api/patients/{id}` | Cascades to appointments and insurance |
| `GET` | `/api/patients/with-appointments` | Single `JOIN FETCH` — see [docs/n-plus-one.md](docs/n-plus-one.md) |
| `GET` | `/api/patients/blood-group-distribution` | `GROUP BY` with a constructor projection |
| `PUT` `DELETE` | `/api/patients/{id}/insurance` | 1:1 assign / remove |
| `GET` `POST` | `/api/appointments` | `?patientId=` to list |
| `PATCH` | `/api/appointments/{id}/doctor/{doctorId}` | Reassign to another doctor |
| `GET` | `/api/doctors` `/api/doctors/{id}` | Read-only directory |

---

## Design decisions

**Collections are `LAZY`, and fetching is explicit.** `Patient.appointments` was originally
`EAGER`, which loads every appointment on every patient query whether or not you need them.
It is lazy now, and the endpoint that needs the graph asks for it with `LEFT JOIN FETCH`.
Measured effect: **10 SQL statements down to 1**. The full write-up, including why fixing the
collection alone wasn't enough, is in [docs/n-plus-one.md](docs/n-plus-one.md).

**Entities never cross the HTTP boundary.** Every endpoint takes and returns a record DTO with
a static `from(...)` factory. Serializing entities directly would drag lazy proxies into Jackson
and recurse across bidirectional links. No mapping library — a five-line factory has no
annotation processor and nothing to debug.

**One error format, produced by the framework.** `GlobalExceptionHandler` extends
`ResponseEntityExceptionHandler`, so every failure — domain 404s, bean-validation 400s, and
framework-level 405/415/404s alike — comes back as an RFC 7807 `ProblemDetail`. Validation
responses carry a `field -> message` map.

**The owning side is chosen deliberately.** `Patient` owns the insurance relationship via
`patient_insurance_id`; `Insurance.patient` is the inverse (`mappedBy`). With
`orphanRemoval = true`, clearing a patient's insurance deletes the row instead of leaving it
dangling.

**One artifact.** `-Pfrontend` builds the React app into the jar's `static/` folder, so a single
JAR serves the UI, the API, and the docs from one origin — no CORS configuration anywhere.

---

## Tests

```bash
./mvnw test
```

`ApiContractTest` pins the HTTP contract: status codes and the single error shape, including a
regression guard asserting that framework errors *don't* fall back to Spring's legacy error body.
