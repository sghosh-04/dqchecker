# DQChecker – Intelligent Data Quality Validation Platform

A production-ready **Data Quality Validation Platform** built using **Spring Boot**, **React**, **Neon PostgreSQL**, **JWT Authentication**, and **Docker**.

DQChecker enables users to securely upload **CSV and Excel datasets**, dynamically select validation rules, identify data quality issues, store validation reports in a cloud database, and monitor validation history through an interactive dashboard.

---

# Features

## Authentication

- User Registration
- Secure Login
- JWT Authentication
- BCrypt Password Encryption
- Protected REST APIs
- User Profile Endpoint

---

## File Processing

Supported file formats:

- CSV (.csv)
- Excel (.xlsx)

Features:

- File Upload
- Automatic File Parsing
- Multi-format Support
- Validation Report Generation

---

## Configurable Validation Engine

Users can choose which validation rules should be executed before processing a dataset.

Supported validation rules:

- Null Value Check
- Duplicate Record Check
- Numeric Range Validation
- Email Format Validation

The validation engine executes only the selected rules, making it modular and extensible.

---

## Validation Reports

- Store every validation run
- Retrieve previous reports
- Validation history
- Detailed validation summary
- Delete reports

---

## Dashboard

Displays:

- Total Files Processed
- Total Validation Reports
- Validation Success Rate
- Failed Records
- Recent Validation Activity

---

## Cloud Database

Powered by **Neon PostgreSQL**

Stores:

- Users
- Uploaded Files
- Validation Reports
- Validation Results

---

## Security

- Spring Security
- JWT Authentication
- BCrypt Password Encryption
- Protected Endpoints
- CORS Configuration
- Stateless Authentication

---

## API Documentation

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

## Logging

Application logs include:

- Authentication Events
- Upload Events
- Validation Events
- Error Logs

---

## Docker Support

Run the application using

```bash
docker-compose up
```

---

# Tech Stack

## Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT
- Maven

## Frontend

- React
- TypeScript
- Vite
- Tailwind CSS
- Axios
- React Router

## Database

- Neon PostgreSQL

## File Processing

- Apache Commons CSV
- Apache POI

## DevOps

- Docker
- Docker Compose

---

# System Architecture

```
                    React Frontend
                           │
                           ▼
                   REST API (Spring Boot)
                           │
                   JWT Authentication
                           │
                    Service Layer
                           │
                 Validation Engine
                           │
            User Selected Validation Rules
                           │
      ┌──────────┬──────────┬──────────┬──────────┐
      │          │          │          │
  Null Rule  Duplicate  Range Rule  Email Rule
                           │
                           ▼
                CSV / Excel Processing
                           │
                           ▼
                 Neon PostgreSQL Database
                           │
                           ▼
           Validation Reports & History
```

---

# Validation Workflow

```
Register
      │
      ▼
Login
      │
      ▼
Upload CSV / Excel File
      │
      ▼
Select Validation Rules
      │
      ▼
Validation Engine
      │
      ▼
Execute Selected Rules
      │
      ▼
Generate Validation Report
      │
      ▼
Store Report
      │
      ▼
Dashboard & Report History
```

---

# Project Structure

```
dqchecker
│
├── frontend
│
├── src
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   ├── security
│   ├── config
│   ├── exception
│   ├── engine
│   ├── rules
│   ├── util
│   └── model
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

# REST APIs

## Authentication

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/register` | Register User |
| POST | `/auth/login` | Login |
| GET | `/auth/profile` | Current User Profile |

---

## Validation

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | `/validate` | Upload & Validate CSV/Excel |
| GET | `/validate/{id}` | Get Validation Result |

---

## Reports

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | `/reports` | List All Reports |
| GET | `/reports/{id}` | Report Details |
| DELETE | `/reports/{id}` | Delete Report |

---

## Dashboard

| Method | Endpoint |
|---------|----------|
| GET | `/dashboard` |

---

# Validation Rules

Current implementation includes:

- Null Value Validation
- Duplicate Detection
- Numeric Range Validation
- Email Format Validation

The validation engine is modular and supports adding additional validation rules with minimal changes.

---

# Database Schema

Main tables:

- users
- uploaded_files
- validation_reports
- validation_results

---

# Local Setup

## Clone Repository

```bash
git clone https://github.com/sghosh-04/dqchecker.git
```

## Backend

Windows

```bash
mvnw.cmd spring-boot:run
```

Linux / macOS

```bash
./mvnw spring-boot:run
```

## Frontend

```bash
cd frontend
npm install
npm run dev
```

---

# Environment Variables

```properties
DATABASE_URL=<Neon PostgreSQL URL>
DB_USERNAME=<username>
DB_PASSWORD=<password>

JWT_SECRET=<your-secret-key>
JWT_EXPIRATION=86400000
```

---

# Application URLs

Frontend

```
http://localhost:5173
```

Backend

```
http://localhost:8080
```

Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

# Future Enhancements

- PDF Report Export
- Excel Report Export
- Email Notifications
- Async Validation
- Redis Caching
- Flyway Database Migrations
- GitHub Actions CI/CD
- Cloud Deployment
- Additional Validation Rules
- AI-powered Data Quality Suggestions

---

# Resume Highlights

- Built a full-stack Data Quality Validation Platform using Spring Boot, React, JWT Authentication, and Neon PostgreSQL.
- Designed a configurable rule-based validation engine supporting user-selectable validation rules for CSV and Excel datasets.
- Developed secure REST APIs with report persistence, dashboard analytics, Swagger documentation, and Docker support.
- Built a modular validation architecture that allows new validation rules to be added with minimal code changes.

---

# Author

**Sayuri Ghosh**

B.Tech CSE (AI & ML)

Java • Spring Boot • React • PostgreSQL • Backend Development • AI • Data Engineering