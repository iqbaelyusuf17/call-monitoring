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
take-home-test-cn/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bank/callmonitoring/
│   │   │   │   ├── config/            # CorsConfig, WebMvcConfig
│   │   │   │   ├── controller/        # CallMonitoringController
│   │   │   │   ├── dto/               # Request DTO, Response wrappers
│   │   │   │   ├── entity/            # CallMonitoring model
│   │   │   │   ├── filter/            # ApiLoggingFilter (traceId / correlation)
│   │   │   │   ├── repository/        # AbstractJdbcRepository, CallMonitoringRepository
│   │   │   │   └── service/           # CallMonitoringService
│   │   │   └── resources/
│   │   │       ├── application.yml    # App configuration & H2/Postgres profiles
│   │   │       └── db/
│   │   │           ├── schema.sql     # DDL table creation
│   │   │           └── data.sql       # Initial seed records
│   │   └── test/                      # 15 automated backend test suites
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
  - `CallMonitoringControllerTest`: Controller layer endpoint tests, parameter binding, and status assertions (`@WebMvcTest`).
  - `CallMonitoringServiceTest`: Service layer business logic and repository delegation verification.
  - `CallMonitoringRepositoryTest`: Pure JDBC execution, dynamic query assembly, pagination calculation, and parameter binding.
  - `CallMonitoringApplicationTests`: Spring Boot application context load verification.

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

### 1. Tools & Models Used
- **AI Coding Assistant**: Google Antigravity (Powered by Gemini 2.5 Pro)

### 2. Prompts & Interaction Summary
- **Architecture & Scaffolding**: Formulated prompts to architect an enterprise-grade pure Spring JDBC repository pattern without JPA, featuring reusable pagination calculus and custom distinct count query mechanisms.
- **SQL & Data Generation**: Prompted to generate 100 realistic seed records distributed across the last 3 months (June – September 2026) with balanced sentiment scores (< 70% and >= 70%) and representative Indonesian names.
- **Testing Scenarios**: Prompts used to generate comprehensive edge-case test suites for date parsing, number rounding, and controller parameter bindings.

### 3. What Was Generated vs. Manually Reviewed & Modified
- **AI-Generated**:
  - Initial project boilerplate structures (Maven dependencies, Vite + Vue 3 config).
  - Seed data generation script producing 100 realistic records.
  - Initial scaffolding for Vitest and JUnit test suites.
- **Manually Engineered / Modified & Verified**:
  - **Custom Pagination Calculus**: Verified and fine-tuned `AbstractJdbcRepository` to guarantee accurate `COUNT` queries and safe parameter binding preventing SQL injection.
  - **Routing Architecture**: Adjusted API routing to strictly use `@RequestMapping("/api/v1")` at the class level and `@GetMapping("/call-monitoring")` at the method level.
  - **Distributed Tracing**: Implemented `ApiLoggingFilter` using SLF4J MDC and `AsyncLocalStorage`-compatible correlation patterns with `X-Trace-Id` headers.
  - **UX & Localization Refinement**: Tailored the UI components to adhere strictly to supervisor user story specifications—standardizing all user-facing labels to English, removing redundant row index columns, and simplifying pagination to total count and navigation buttons.

### 4. Validation Steps Taken
- **Automated Testing**: 15 backend tests (JUnit 5 / Mockito) and 7 frontend unit tests (Vitest) executed and passed with 100% success rate.
- **Production Build Verification**: Executed full production builds (`npm run build` and Maven package) verifying zero build warnings or type discrepancies.
- **Security & Code Quality**: Audited SQL queries for parameter injection vulnerabilities and validated reactive state management against race conditions during debounced filter inputs.
