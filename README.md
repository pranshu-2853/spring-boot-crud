# Secure Engineer Management API

A secure, production-structured RESTful backend service built using Spring Boot.

This project demonstrates JWT authentication, role-based authorization, pagination, filtering, structured logging, and Dockerized PostgreSQL integration.

---

## 🚀 Tech Stack

- Java 21  
- Spring Boot 3  
- Spring Security (JWT + RBAC)  
- Spring Data JPA (Hibernate)  
- PostgreSQL (Dockerized)  
- Spring Validation  
- OpenAPI / Swagger  
- Maven  

---

## 🔐 Security Implementation

- JWT-based authentication  
- Role-Based Access Control (ROLE_USER / ROLE_ADMIN)  
- Method-level security using `@PreAuthorize`  
- Custom AuthenticationEntryPoint (401 handling)  
- Custom AccessDeniedHandler (403 handling)  
- Password encryption using BCrypt  

---

## 📊 Core Features

- Full CRUD operations  
- Pagination & Sorting  
- Filtering using Spring Data Specifications  
- DTO-based request & response structure  
- Centralized exception handling  
- Structured logging (INFO / WARN / DEBUG levels)  
- Proper HTTP status codes (400 / 401 / 403 / 404 / 409 / 500)

---

## 🧱 Architecture

Controller Layer → Handles HTTP requests  
Service Layer → Business logic + logging  
Repository Layer → Data access via JPA  
Security Layer → JWT filter + role validation  
Exception Layer → Global exception handling  

Clean, layered, and maintainable backend architecture.

---

## 🐳 Database Setup (Docker)

PostgreSQL runs inside Docker using `docker-compose.yml`.

Start database:

```bash
docker-compose up -d
```

Database configuration:

- Host: localhost  
- Port: 5332  
- Database: my_first_db  
- Username: pranshu  
- Password: password  

---

## ▶️ Running the Application

1. Start PostgreSQL container:

```bash
docker-compose up -d
```

2. Run Spring Boot application:

```bash
mvn spring-boot:run
```

Application runs at:

http://localhost:8080

---

## 📘 API Documentation

Swagger UI available at:

http://localhost:8080/swagger-ui/index.html

---

## 🧪 Security Testing Matrix

| Scenario | Expected Status |
|----------|-----------------|
| No Token | 401 |
| Invalid Token | 401 |
| USER → GET | 200 |
| USER → POST | 403 |
| ADMIN → POST | 201 |
| Validation Error | 400 |
| Duplicate Resource | 409 |
| Not Found | 404 |

---

## 🔮 Future Improvements

- Cloud deployment (Render / Railway / AWS)
- Refresh token implementation
- Integration testing
- CI/CD pipeline
- Rate limiting

This project focuses on building a secure, production-style backend API rather than a basic CRUD application.
