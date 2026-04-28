# dqchecker

A Spring Boot REST API for data quality checking. Upload CSV files and validate them against configurable rules to identify data quality issues.

## Tech Stack

- Java 17
- Spring Boot 4.0.6
- Apache Commons CSV 1.10.0
- Maven

## Prerequisites

- Java 17+
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

### Build a JAR

```bash
./mvnw clean package
java -jar target/dqchecker-0.0.1-SNAPSHOT.jar
```

## Running Tests

```bash
./mvnw test
```

## Project Structure
