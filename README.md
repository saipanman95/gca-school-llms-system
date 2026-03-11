# GCA School Management System

Spring Boot 3 / Java 21 web application for replacing fragmented school operations across:

- student records and compliance files
- tuition, fees, and receivables
- courses, sections, and grade management

## Stack

- Java 21
- Spring Boot 3.4
- Spring MVC + Thymeleaf
- HTMX
- Bootstrap 5
- Spring Security
- Spring Data JPA
- MySQL

## Roles

- `SYSTEM_ADMIN`
- `SCHOOL_ADMIN`
- `SCHOOL_STAFF`
- `SCHOOL_FINANCE`
- `PARENT_GUARDIAN`
- `STUDENT`

## Local run

1. Create a MySQL database and user that match `src/main/resources/application.yml`, or update the credentials.
2. Start the app:

```bash
mvn spring-boot:run
```

3. Sign in with one of the seeded in-memory users. All starter passwords are `change-me`.

## Current scope

This first scaffold includes:

- a secured dashboard
- records, finance, and academics modules
- starter data seeders
- a base domain model to extend into transcripts, medical files, billing, payments, course rosters, and gradebooks

## Suggested next increments

1. Replace in-memory users with persistent identity and role management.
2. Add parent/guardian and student portals with restricted record and statement access.
3. Design transcript ingestion, document storage, and audit logging.
4. Add charges, payments, statements, and aging reports.
5. Add grading periods, assignments, attendance, and report cards.
