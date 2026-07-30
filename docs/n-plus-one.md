# Eliminating N+1 on `GET /api/patients/with-appointments`

Measured locally against PostgreSQL with `spring.jpa.show-sql=true`, counting `Hibernate:`
statements emitted per request. Same endpoint, same seed data (10 patients, 5 appointments,
4 doctors) at every step.

| Step | SQL statements |
|---|---|
| 1. `EAGER` collection, no fetch join | 1 + N |
| 2. `LAZY` + `LEFT JOIN FETCH p.appointments` | **10** |
| 3. `LAZY` + fetch join covering `a.doctor` too | **1** |

## Step 1 — the starting point

`Patient.appointments` was mapped `fetch = FetchType.EAGER`:

```java
@OneToMany(mappedBy = "patient", cascade = {CascadeType.REMOVE},
           orphanRemoval = true, fetch = FetchType.EAGER)
private List<Appointment> appointments = new ArrayList<>();
```

Every query returning patients also loaded their appointments, whether or not the caller
wanted them — including `findAll()`, paginated list endpoints, and lookups by id.

## Step 2 — lazy plus a fetch join, and the trap

Making the collection `LAZY` and adding an explicit fetch join looks like the fix:

```java
@Query("SELECT DISTINCT p FROM Patient p LEFT JOIN FETCH p.appointments")
List<Patient> findAllPatientWithAppointment();
```

`DISTINCT` is required: a fetch join across a to-many produces one row per appointment, so
without it a patient with three appointments comes back three times.

**This still issued 10 statements.** The response DTO reads `appointment.getDoctor().getName()`,
and `Appointment.doctor` is a `@ManyToOne` — which is `EAGER` by default in JPA. The fetch join
covered patients and appointments but not doctors, so Hibernate issued one extra `SELECT` per
doctor. The N+1 hadn't been removed; it had moved one level down.

## Step 3 — fetch the whole graph the response needs

```java
@Query("""
        SELECT DISTINCT p FROM Patient p
        LEFT JOIN FETCH p.appointments a
        LEFT JOIN FETCH a.doctor
        """)
List<Patient> findAllPatientWithAppointment();
```

**1 statement.**

## What this generalizes to

Changing a collection's fetch mode is not by itself an N+1 fix. What matters is whether the
fetch plan covers every association the response actually touches — and `@ManyToOne` defaults
to `EAGER`, so the associations most likely to bite are the ones nobody declared.

The measurement is the point. Both the broken and the fixed version return byte-identical JSON;
the only way to tell them apart is to count the queries.
