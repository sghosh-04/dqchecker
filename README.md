# dqchecker

A Spring Boot REST API for data quality checking. Upload CSV files and validate them against configurable rules to identify data quality issues.

## Tech Stack

- Java 17
- Spring Boot 4.0.6
- Apache Commons CSV 1.10.0
- Spring Security JWT
- Spring Data JPA
- PostgreSQL
- Springdoc OpenAPI
- Maven

## Prerequisites

- Java 17+
- Docker Desktop, optional
- PostgreSQL, if not using Docker
- Maven (or use the included `mvnw` wrapper)

## Getting Started

### Clone the repository

```bash
git clone https://github.com/sghosh-04/dqchecker.git
cd dqchecker
```

### Run the application

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application starts on `http://localhost:8080` by default.

Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Build a JAR

```bash
./mvnw clean package
java -jar target/dqchecker-0.0.1-SNAPSHOT.jar
```

## Running Tests

```bash
./mvnw test
```

The Maven build runs JaCoCo and enforces 70%+ instruction coverage.

## Docker Setup

```bash
docker compose up --build
```

This starts:

- `app` on `http://localhost:8080`
- `postgres` on `localhost:5432`

## Configuration

Runtime secrets are read from `application.properties` with environment variable overrides:

```properties
jwt.secret=${JWT_SECRET:mysecretkeymysecretkeymysecretkey1234567890}
jwt.expiration=${JWT_EXPIRATION:86400000}
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/dqchecker}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:postgres}
```

## API Testing Examples

Register:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"sayuri\",\"email\":\"sayuri@example.com\",\"password\":\"password123\",\"role\":\"USER\"}"
```

Login:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"sayuri@example.com\",\"password\":\"password123\"}"
```

Validate CSV:

```bash
curl -X POST http://localhost:8080/validate \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -F "file=@customers.csv"
```

Reports:

```bash
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/reports
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/reports/1
```

Admin users:

```bash
curl -H "Authorization: Bearer <ADMIN_JWT_TOKEN>" http://localhost:8080/admin/users
```

Import `dqchecker-postman-collection.json` into Postman for the same request set.

## Project Structure

```text
dqchecker
├── Dockerfile
├── docker-compose.yml
├── dqchecker-postman-collection.json
├── pom.xml
└── src
    ├── main
    │   ├── java/com/sayuri/dqchecker
    │   │   ├── config
    │   │   ├── controller
    │   │   ├── dto
    │   │   ├── engine
    │   │   ├── entity
    │   │   ├── exception
    │   │   ├── model
    │   │   ├── repository
    │   │   ├── rules
    │   │   ├── security
    │   │   ├── service
    │   │   └── util
    │   └── resources/application.properties
    └── test
        ├── java/com/sayuri/dqchecker
        │   ├── controller
        │   ├── exception
        │   ├── security
        │   └── service
        └── resources/application.properties
```
