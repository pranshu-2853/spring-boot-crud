# Secure Engineer Management API

A secure, production-ready RESTful backend service built using Spring Boot.

This project demonstrates JWT authentication, role-based authorization, pagination, filtering, structured logging, and **cloud deployment using Render + Neon PostgreSQL**.

---

## 🌐 Live Deployment

🔗 **Live API**
https://secure-engineer-management-api.onrender.com

📘 **Swagger Documentation**
https://secure-engineer-management-api.onrender.com/swagger-ui/index.html

💡 Note:
This is a secured backend API. Use Swagger UI to test endpoints.Free-tier deployment may take ~30 seconds to wake up on first request.

---

## 🚀 Tech Stack

* Java 21
* Spring Boot 3
* Spring Security (JWT + RBAC)
* Spring Data JPA (Hibernate)
* PostgreSQL (Neon Cloud DB)
* Docker (for deployment)
* Spring Validation
* OpenAPI / Swagger
* Maven

---

## 🔐 Security Implementation

* JWT-based authentication
* Role-Based Access Control (ROLE_USER / ROLE_ADMIN)
* Method-level security using `@PreAuthorize`
* Custom AuthenticationEntryPoint (401 handling)
* Custom AccessDeniedHandler (403 handling)
* Password encryption using BCrypt

---

## 📊 Core Features

* Full CRUD operations
* Pagination & Sorting
* Filtering using Spring Data Specifications
* DTO-based request & response structure
* Centralized exception handling
* Structured logging (INFO / WARN / DEBUG levels)
* Proper HTTP status codes (400 / 401 / 403 / 404 / 409 / 500)

---

## 🧱 Architecture

Controller Layer → Handles HTTP requests
Service Layer → Business logic + logging
Repository Layer → Data access via JPA
Security Layer → JWT filter + role validation
Exception Layer → Global exception handling

Clean, layered, and maintainable backend architecture.

---

## 🧪 How to Test (Live)

1. Open Swagger UI
2. Register a user → `/auth/register`
3. Login → `/auth/login`
4. Copy JWT token
5. Click **Authorize** and paste:

   ```
   Bearer <your_token>
   ```
6. Test secured endpoints

---

## 🧪 Security Testing Matrix

| Scenario           | Expected Status |
| ------------------ | --------------- |
| No Token           | 401             |
| Invalid Token      | 401             |
| USER → GET         | 200             |
| USER → POST        | 403             |
| ADMIN → POST       | 201             |
| Validation Error   | 400             |
| Duplicate Resource | 409             |
| Not Found          | 404             |

---

## 🔮 Future Improvements

* Refresh token implementation
* Integration testing
* CI/CD pipeline
* Rate limiting
* Frontend integration

---

## 📌 Notes

* Local PostgreSQL setup via Docker is included for development
* Production uses Neon (serverless PostgreSQL)
* Backend is deployed on Render using Docker

---

This project focuses on building a secure, production-style backend API rather than a basic CRUD application.
