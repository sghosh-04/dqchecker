# Data Quality Validation Platform


Production-style Data Quality Validation Platform built using Spring Boot, PostgreSQL, JWT Authentication, and REST APIs.

Upload datasets, run validation rules, store reports, and analyze results through APIs and dashboard integration.

---

## Features

### Authentication & Authorization
- User Registration
- User Login
- JWT Authentication
- Role-Based Access (USER / ADMIN)
- Protected APIs

---

### Data Quality Validation
- CSV Upload
- Null Validation
- Range Validation
- Duplicate Detection
- Rule-based Validation Engine
- Validation Reports

---

### Persistence
- PostgreSQL Database
- Upload History
- Validation Report Storage
- Validation Rule Storage

---

### API Features
- REST APIs
- Global Exception Handling
- Swagger Documentation
- Logging & Monitoring

---

### DevOps
- Docker Support
- Docker Compose
- Environment Configuration

---

A Spring Boot REST API for CSV data quality validation. The project supports user registration/login with JWT, CSV upload and parsing, validation rules, persisted validation reports, report history, dashboard metrics, Swagger, Actuator, PostgreSQL, Docker, and tests with JaCoCo coverage enforcement.
 (Updated features)

## Tech Stack

### Backend
- Java 17
<<<<<<< HEAD
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- JWT
- Maven

### Validation
- Apache Commons CSV

### Testing
- JUnit 5
- Mockito

### Frontend (Optional)
- React
- Vite
- TailwindCSS

---

# Architecture

```text
Client
 ↓
REST API
 ↓
JWT Security
 ↓
Service Layer
 ↓
Validation Engine
 ↓
PostgreSQL
 ↓
Reports + History
```

---

# Project Structure

```text
dqchecker
│
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── config/
├── security/
├── exception/
├── engine/
├── rules/
├── util/
├── model/
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
=======
- Spring Boot 4.0.6
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Apache Commons CSV
- Springdoc OpenAPI
- Spring Actuator
- JUnit 5, Mockito, JaCoCo
- Docker Compose

## Startup Instructions

Run with local Java and PostgreSQL:

```bash
mvnw.cmd spring-boot:run
```

Run with Docker:

```bash
docker compose up --build
```

The API starts at `http://localhost:8080`.

Useful URLs:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`

## API Summary

Authentication:

- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/profile`

Validation:

- `POST /validate`
- `GET /validate/{id}`
- Existing legacy endpoints preserved: `POST /api/upload`, `POST /api/validate`

Reports:

- `GET /reports?page=0&size=10&passed=false&filename=customers`
- `GET /reports/{id}`
- `DELETE /reports/{id}`

Dashboard:

- `GET /dashboard`

Admin:

- `GET /admin/users`

## Example Flow

```bash
curl -X POST http://localhost:8080/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"sayuri\",\"email\":\"sayuri@example.com\",\"password\":\"password123\",\"role\":\"USER\"}"

curl -X POST http://localhost:8080/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"sayuri@example.com\",\"password\":\"password123\"}"

curl -X POST http://localhost:8080/validate ^
  -H "Authorization: Bearer <JWT_TOKEN>" ^
  -F "file=@customers.csv"

curl -H "Authorization: Bearer <JWT_TOKEN>" "http://localhost:8080/reports?page=0&size=10"
```

Import `dqchecker-postman-collection.json` into Postman for the full request set.

## Tests

```bash
mvnw.cmd test
```

The build enforces 70%+ instruction coverage with JaCoCo.

## Migration Notes

- Existing upload and in-memory validation endpoints remain available under `/api/upload` and `/api/validate`.
- Persisted validation now uses `UploadedFile`, `ValidationReport`, and `ValidationResult`.
- The previous `ValidationRule` entity is retained for compatibility with existing code/tests while new report persistence also writes canonical `ValidationResult` rows.
- `GET /reports` now returns a paginated Spring `Page<ReportSummary>` response for history, pagination, and filters.
- `GET /reports/{id}` and `GET /validate/{id}` return full report details.
- Swagger moved to `/swagger-ui/index.html`.
- Actuator exposes only `/actuator/health` and `/actuator/info`.

## Final Folder Tree

```text
dqchecker
|-- Dockerfile
|-- docker-compose.yml
|-- dqchecker-postman-collection.json
|-- pom.xml
|-- README.md
|-- src
|   |-- main
|   |   |-- java/com/sayuri/dqchecker
|   |   |   |-- config
|   |   |   |-- controller
|   |   |   |-- dto
|   |   |   |-- engine
|   |   |   |-- entity
|   |   |   |-- exception
|   |   |   |-- model
|   |   |   |-- repository
|   |   |   |-- rules
|   |   |   |-- security
|   |   |   |-- service
|   |   |   `-- util
|   |   `-- resources/application.properties
|   `-- test
|       |-- java/com/sayuri/dqchecker
|       |   |-- controller
|       |   |-- exception
|       |   |-- security
|       |   `-- service
|       `-- resources/application.properties
`-- frontend
>>>>>>> 6f5df37 (Updated features)
```

---

# Database Schema

## users

| Column |
|--------|
| id |
| username |
| email |
| password |
| role |

---

## uploaded_files

| Column |
|--------|
| id |
| file_name |
| uploaded_at |
| user_id |

---

## validation_reports

| Column |
|--------|
| id |
| status |
| created_at |
| uploaded_file_id |

---

## validation_rules

| Column |
|--------|
| id |
| rule_name |
| passed |
| report_id |

---

# API Documentation

## Auth

### Register

```http
POST /auth/register
```

Request:

```json
{
  "username":"sayuri",
  "email":"sayuri@test.com",
  "password":"abc12345"
}
```

Response:

```json
{
  "message":"User registered successfully"
}
```

---

### Login

```http
POST /auth/login
```

Request:

```json
{
  "email":"sayuri@test.com",
  "password":"abc123"
}
```

Response:

```json
{
  "token":"jwt_token_here"
}
```

---

## Validation

### Upload + Validate

```http
POST /validate
```

Body:

multipart/form-data

```text
file → dataset.csv
```

---

### Reports

```http
GET /reports
```

```http
GET /reports/{id}
```

---

# Setup

## Clone

```bash
git clone <repo-url>
cd dqchecker
```

---

## Configure Database

Create database:

```sql
CREATE DATABASE dqchecker;
```

Update:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/dqchecker
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## Run Backend

```bash
mvn clean install
mvn spring-boot:run
```

Server:

```text
http://localhost:8080
```

---

## Swagger

Open:

```text
http://localhost:8080/swagger-ui.html
```

---

## Docker

Build:

```bash
docker-compose up --build
```

---

# Testing

Run:

```bash
mvn test
```

Coverage target:

```text
70%+
```

---

# Sample Validation Rules

Implemented:

- NullCheckRule
- RangeRule
- DuplicateRule

Future:

- RegexRule
- DataTypeRule
- ReferentialIntegrityRule
- StatisticalAnomalyRule

---

# Future Improvements

- Async Validation
- Email Export
- Redis Cache
- CI/CD
- Cloud Deployment
- Multi-tenant Support
- Analytics Dashboard

---

# Author

Sayuri Ghosh

Built as a production-style backend system to demonstrate:

- OOP
- REST API Design
- Security
- Database Design
- System Architecture
- Validation Engines
- Backend Engineering
