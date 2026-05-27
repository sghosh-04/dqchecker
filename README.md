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

## Tech Stack

### Backend
- Java 17
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
  "password":"abc123"
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
