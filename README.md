# Customer Call Monitoring System (THT-MON-US-001)

A full-stack enterprise web application built for Customer Service Supervisors to monitor, search, filter, and analyze customer call records and sentiment scores.

---

## Table of Contents
- [Overview](#overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Architecture & Design Decisions](#architecture--design-decisions)
- [Project Directory Structure](#project-directory-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Database Setup](#database-setup)
  - [Backend Setup & Run](#backend-setup--run)
  - [Frontend Setup & Run](#frontend-setup--run)
- [Running Automated Tests](#running-automated-tests)
- [API Documentation](#api-documentation)
- [AI Usage](#ai-usage)

---

## Overview

In enterprise customer service operations, supervisors need real-time visibility into customer interactions to proactively evaluate service quality, resolve complaints, and identify satisfaction anomalies. 

This application implements User Story **THT-MON-US-001** (*Customer Call Monitoring*), enabling supervisors to:
1. View a paginated list of customer calls with essential transaction details (Call ID, Timestamp, CS Name, Customer Name, Sentiment Score).
2. Filter calls dynamically by multi-attribute keyword search, date range (constrained within the latest 3 months), and sentiment thresholds.
3. Sort results bi-directionally by any column.
4. Navigate through datasets with responsive pagination and total record counting.

---

## Key Features

- **Intuitive Supervisor Dashboard**: Clean, modern UI styled with Tailwind CSS, clear visual badges for sentiment score performance (`>= 70%` in emerald, `< 70%` in rose).
- **Multi-criteria Filtering**:
  - Full-text keyword search across `call_id`, `cs_name`, and `customer_name`.
  - Date period filter constrained strictly to the latest 3 months (`min` and `max` date validations).
  - Sentiment category filter: All Sentiments, Under 70% (`< 70%`), and 70% or more (`>= 70%`).
- **Dynamic Multi-column Sorting**: Interactive sort headers with ascending/descending indicators for all columns.
- **Robust Native Spring JDBC Persistence**: Implemented using `NamedParameterJdbcTemplate` with unified pagination (`AbstractJdbcRepository`), parameterized queries to prevent SQL injection, and custom `COUNT` query support for complex projections.
- **Enterprise Observability**: Custom HTTP servlet filter (`ApiLoggingFilter`) automatically injecting distributed `traceId` / correlation ID into request attributes, response headers (`X-Trace-Id`), and MDC logs.

---

## Tech Stack

### Backend
- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 3.3.x (`spring-boot-starter-web`, `spring-boot-starter-jdbc`)
- **Data Access**: Pure Native Spring JDBC via `NamedParameterJdbcTemplate` (No JPA/Hibernate overhead)
- **Database**: PostgreSQL (Production) / H2 In-Memory (Development & Testing)
- **Build Tool**: Apache Maven (`mvnw`)
- **Testing**: JUnit 5, Mockito, AssertJ, Spring Boot Test (`@WebMvcTest`, `@SpringBootTest`)

### Frontend
- **Framework**: Vue 3 (Composition API `<script setup>`)
- **Build Tool**: Vite 5
- **Styling**: Tailwind CSS 3
- **Icons**: Lucide Vue Next
- **HTTP Client**: Axios
- **Unit Testing**: Vitest

---

## Architecture & Design Decisions

1. **Pure Native JDBC with Unified Pagination (`AbstractJdbcRepository`)**:
   - Engineered an extensible `AbstractJdbcRepository<T, R>` base repository providing standardized query execution, parameter binding, dynamic `ORDER BY` clause mapping, pagination offset calculation, and automated total record count retrieval.
   - Supports custom count query overrides to handle high-performance `COUNT(DISTINCT ...)` or optimized indexed counts.
2. **Direct Service Class Pattern**:
   - Uses a focused `@Service` class (`CallMonitoringService`) directly injected into the controller without superfluous single-implementation interfaces, adhering to modern Spring best practices and KISS/YAGNI principles.
3. **Structured API Routing & Standardization**:
   - Base routing configured via class-level `@RequestMapping("/api/v1")` and endpoint-level `@GetMapping("/call-monitoring")`.
   - Unified API response wrapper returning payload data along with pagination metadata (`page`, `limit`, `total_records`, `total_pages`, `has_previous`, `has_next`).
4. **Resilient Frontend State Management**:
   - Encapsulated reactive state into a composable (`useCallMonitoring`) separating API communication, debounced filter updates, sorting logic, and pagination state from view components.

---

## Project Directory Structure

```text
call-monitoring/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/iqbal/callmonitoring/
│   │   │   │   ├── config/            # CorsConfig, ApiLoggingFilter
│   │   │   │   ├── controller/        # CallMonitoringController
│   │   │   │   ├── dto/               # Request DTO, Response wrappers
│   │   │   │   ├── entity/            # CallMonitoring model
│   │   │   │   ├── exception/         # BadRequestException, GlobalExceptionHandler
│   │   │   │   ├── repository/        # AbstractJdbcRepository, CallMonitoringRepository
│   │   │   │   ├── service/           # CallMonitoringService
│   │   │   │   └── validator/         # DateRangeValidator
│   │   │   └── resources/
│   │   │       ├── application.yml    # App configuration & H2/Postgres profiles
│   │   │       └── db/
│   │   │           ├── schema.sql     # DDL table creation
│   │   │           └── data.sql       # 100 seed records
│   │   └── test/                      # 15 automated backend test suites
│   │       ├── java/com/iqbal/callmonitoring/
│   │       │   ├── controller/        # CallMonitoringControllerTest (4 tests)
│   │       │   ├── repository/        # CallMonitoringRepositoryTest (7 tests)
│   │       │   └── service/           # CallMonitoringServiceTest (4 tests)
│   │       └── resources/
│   │           └── application.yml
│   ├── mvnw / mvnw.cmd
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api/                       # callMonitoringApi (Axios client)
│   │   ├── components/
│   │   │   ├── CallTable.vue          # Interactive data table with sorting
│   │   │   ├── EmptyState.vue         # Filter reset empty state UI
│   │   │   ├── FilterBar.vue          # Search, date range, sentiment filter
│   │   │   ├── Navbar.vue             # Header & supervisor profile badge
│   │   │   └── Pagination.vue         # Record counter & Prev/Next controls
│   │   ├── composables/               # useCallMonitoring reactive state
│   │   ├── utils/                     # dateFormatter, numberFormatter
│   │   │   └── __tests__/             # formatters.spec.js (7 unit tests)
│   │   ├── App.vue                    # Main layout container
│   │   └── main.js
│   ├── package.json
│   ├── tailwind.config.js
│   └── vite.config.js
├── database/
│   ├── schema.sql                     # PostgreSQL schema DDL with indexes
│   └── data.sql                       # 100 realistic seed records
└── README.md
```

---

## Getting Started

### Prerequisites
- **Java**: OpenJDK 21 or higher
- **Node.js**: v18.x or v20.x+
- **npm**: v9.x+
- **Git**

---

### Database Setup

#### Option A: Automatic In-Memory H2 (Recommended for Rapid Local Evaluation)
By default, running the backend with the default profile (`application.yml`) uses an embedded H2 database pre-populated with schema and seed data. No manual database installation is required.

#### Option B: PostgreSQL
To connect to an external PostgreSQL database:
1. Create a database in PostgreSQL:
   ```sql
   CREATE DATABASE call_monitoring_db;
   ```
2. Execute the schema DDL and seed records:
   ```bash
   psql -U postgres -d call_monitoring_db -f database/schema.sql
   psql -U postgres -d call_monitoring_db -f database/data.sql
   ```
3. Update `backend/src/main/resources/application.yml` with your PostgreSQL credentials:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/call_monitoring_db
       username: your_username
       password: your_password
       driver-class-name: org.postgresql.Driver
   ```

---

### Backend Setup & Run

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Run the application using the Maven wrapper:
   - **Linux / macOS**:
     ```bash
     ./mvnw spring-boot:run
     ```
   - **Windows (PowerShell / CMD)**:
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
3. The backend server will start on port `8080` (`http://localhost:8080`).

---

### Frontend Setup & Run

1. Navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
4. Open your browser and access `http://localhost:5173`.

---

## Running Automated Tests

### Backend Unit & Integration Tests (15 Tests)
```bash
cd backend
.\mvnw.cmd test    # Windows
./mvnw test        # Linux / macOS
```
- **Test Coverage**:
  - `CallMonitoringControllerTest` (4 tests): Controller layer endpoint tests, query parameter binding, response DTO verification, and `X-Trace-Id` header assertion (`@WebMvcTest`).
  - `CallMonitoringServiceTest` (4 tests): Service layer business logic, boundary limit/page clamping, and repository delegation verification (Mockito).
  - `CallMonitoringRepositoryTest` (7 tests): Pure JDBC execution on H2/Postgres mode, dynamic query assembly, multi-column search, date period filtering, sentiment thresholds, and dynamic sorting.

### Frontend Unit Tests (7 Tests)
```bash
cd frontend
npm run test
```
- **Test Coverage**:
  - Date formatting utilities (`formatCallTimestamp`, `getThreeMonthsAgoDateString`, `getTodayDateString`).
  - Sentiment score number formatting (`formatSentimentScore`).

---

## API Documentation

### Get Call Monitoring Records
Retrieves a paginated list of call records filtered by keyword, date range, and sentiment score.

- **Endpoint**: `GET /api/v1/call-monitoring`
- **Headers**:
  - `Accept: application/json`
  - `X-Trace-Id` (Optional client correlation ID; automatically generated if omitted)

#### Query Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `search` | `string` | `null` | Keyword search matching `call_id`, `cs_name`, or `customer_name` |
| `startDate` | `string` (YYYY-MM-DD) | `null` | Start period for call timestamp |
| `endDate` | `string` (YYYY-MM-DD) | `null` | End period for call timestamp |
| `sentiment` | `string` | `ALL` | Filter by sentiment (`ALL`, `UNDER_70`, `70_AND_ABOVE`) |
| `sortBy` | `string` | `call_timestamp` | Column to sort (`call_id`, `call_timestamp`, `cs_name`, `customer_name`, `sentiment_score`) |
| `sortOrder` | `string` | `desc` | Sort direction (`asc` or `desc`) |
| `page` | `integer` | `1` | Page number (1-indexed) |
| `limit` | `integer` | `10` | Number of items per page |

#### Example Request
```http
GET /api/v1/call-monitoring?search=Budi&sentiment=70_AND_ABOVE&sortBy=call_timestamp&sortOrder=desc&page=1&limit=5 HTTP/1.1
Host: localhost:8080
```

#### Example Response (`200 OK`)
```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "call_id": "CALL-2026-0042",
      "call_timestamp": "2026-09-12T14:20:00+07:00",
      "cs_name": "Budi Santoso",
      "customer_name": "Aditya Wijaya",
      "sentiment_score": 88.50
    }
  ],
  "meta": {
    "page": 1,
    "limit": 5,
    "total_records": 12,
    "total_pages": 3,
    "has_previous": false,
    "has_next": true
  }
}
```

---

## AI Usage

In accordance with the Take-Home Test evaluation guidelines, this section provides full transparency regarding the utilization of AI assistance during the planning, implementation, and testing phases of this project.

### 1. AI Tools & Models Used
- **AI Coding Assistant**: Google Antigravity (Powered by Gemini 2.5 Pro)

### 2. Work Assisted by AI
- **Scaffolding & Boilerplate Generation**: Initial setup of the Spring Boot project structure (Maven dependencies, `pom.xml`, and initial Vite + Vue 3 project scaffolding).
- **Data Mocking & Seed Generation**: Generation of 100 realistic Indonesian customer call records distributed across the last 3 months (June–September 2026), with sentiment scores realistically categorized (`< 70%` and `>= 70%`).
- **Unit Test Templates**: Initial drafting of boilerplate test fixtures for JUnit 5 (`@WebMvcTest`, Mockito) and Vitest specs.

### 3. Key Prompts Used

1. **Pure Spring JDBC Repository Architecture**:
   > *"Design an extensible `AbstractJdbcRepository` base repository using Pure Spring JDBC (`NamedParameterJdbcTemplate`) without JPA/Hibernate overhead. Provide unified offset pagination calculation, dynamic column sorting, parameterized query binding to prevent SQL injection, and a hook method for custom count queries when needed."*

2. **Backend API & Observability Routing**:
   > *"Build a REST Controller for User Story THT-MON-US-001 with class-level routing `@RequestMapping("/api/v1")` and endpoint `@GetMapping("/call-monitoring")`. Include an HTTP servlet filter (`ApiLoggingFilter`) that automatically injects a distributed `traceId` into SLF4J MDC and response header `X-Trace-Id`, while logging incoming request payloads and execution durations."*

3. **Frontend Composable & UI Craftsmanship**:
   > *"Implement a reactive composable `useCallMonitoring` in Vue 3 that manages debounced search filters (300ms), date range selection constrained within the last 3 months, sentiment score category filtering, multi-column sorting, and pagination that preserves active filters across page changes (AC-11). Apply the signature CIMB Niaga red palette across buttons, headers, and UI accent elements."*

### 4. Verification & Quality Control

All AI-assisted suggestions and code underwent a rigorous 4-layer manual verification and auditing process prior to committing:

1. **Architectural & Design Compliance**:
   - Strictly ensured no JPA/Hibernate dependencies were introduced, keeping data access 100% native Spring JDBC via `NamedParameterJdbcTemplate`.
   - Verified that the Service layer uses direct `@Service` classes without redundant single-implementation interfaces, adhering to KISS and YAGNI principles.
   - Ensured REST conventions and strict DTO encapsulation (`record` types, clear separation between Entity and Response DTOs).

2. **Security & SQL Injection Audit**:
   - Manually audited every dynamically assembled SQL query to ensure all user parameters (`search`, `startDate`, `endDate`, `sentiment`) are securely bound via `MapSqlParameterSource`.
   - Implemented strict whitelist validation on sorting identifiers (`ALLOWED_SORT_COLUMNS`) in the repository layer to prevent SQL injection through column identifiers.

3. **Business Logic & Edge Case Validation**:
   - Verified pagination offset calculation `(page - 1) * limit` to eliminate off-by-one errors.
   - Tested 3-month date range boundaries both client-side (`min`/`max` inputs) and server-side (inclusive `atStartOfDay` through `LocalTime.MAX`).
   - Confirmed sentiment score filtering (`< 70.00` and `>= 70.00`) matched user story criteria across PostgreSQL database queries and Vue badge components.

4. **Automated Test Execution & Build Verification**:
   - **Backend Testing**: Executed `.\mvnw.cmd test` to verify all 15 automated test suites across Controller, Service, and Repository layers passed with 0 failures.
   - **Frontend Testing**: Executed `npm run test` (Vitest) to ensure 100% pass rate across date and number formatting test specs.
   - **Production Build**: Executed `npm run build` to guarantee clean Vite bundling without warnings, unused imports, or broken asset paths.
