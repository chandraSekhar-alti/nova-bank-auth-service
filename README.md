Here is the professional Markdown code for your **README.md** file. I have cleaned up the formatting, improved the table structures, and organized the flow to meet standard GitHub documentation practices.

```markdown
# 🏦 Nova Bank Auth Service

A production-style Authentication & Authorization microservice built using **Spring Boot**, **Spring Security**, **JWT Authentication**, **Refresh Tokens**, and **MySQL**.

This project demonstrates modern backend development practices including secure authentication flows, role-based access control foundations, stateless JWT security, Swagger API documentation, and enterprise-grade layered architecture.

---

## 🚀 Features

### ✅ Authentication & Authorization
*   **User Registration & Login**: Full onboarding and authentication flow.
*   **JWT Management**: Access token and Refresh token generation/rotation.
*   **Secure Logout**: Invalidation of session tokens.
*   **Encryption**: Robust password hashing using **BCrypt**.
*   **Statelessness**: Fully stateless authentication using JWT.

### ✅ Security & Database
*   **Spring Security**: Deep integration for filter chains and security contexts.
*   **RBAC**: Foundation for Role-Based Access Control.
*   **Refresh Token Revocation**: Mechanism to handle token security.
*   **Persistence**: MySQL integration with **Spring Data JPA** and **Hibernate**.
*   **Data Integrity**: Automatic entity mapping and role seeding on startup.

### ✅ Developer Experience
*   **Swagger/OpenAPI**: Interactive documentation for API testing.
*   **Clean Architecture**: Separation of concerns across Controller, Service, and Repository layers.
*   **Validation**: DTO-based request/response handling with global exception handling.

---

## 🛠️ Tech Stack

| Technology | Version |
| :--- | :--- |
| **Java** | 17 |
| **Spring Boot** | 3.5.0 |
| **Spring Security** | Latest |
| **Spring Data JPA** | Latest |
| **Hibernate** | 6.x |
| **JWT** | JJWT 0.11.5 |
| **MySQL** | 8+ |
| **Maven** | Latest |
| **Swagger/OpenAPI** | 2.8.8 |
| **Lombok** | Latest |

---

## 📁 Project Structure

```text
src/main/java/com/novabank/auth
│
├── config          # Configuration classes (Swagger, App Config)
├── controller      # REST API Endpoints
├── dto             # Data Transfer Objects (Request/Response)
├── entity          # JPA Entities (User, Role, RefreshToken)
├── exception       # Custom Exception Handling
├── mapper          # Entity-DTO Mappers
├── repository      # Data Access Layer
├── security        # Security Core
│   ├── config      # Security Filter Chain
│   ├── filter      # JWT Authentication Filter
│   ├── jwt         # Token Provider & Utilities
│   └── service     # UserDetails Implementation
├── service         # Business Logic Layer
└── NovaBankAuthServiceApplication.java

```

---

## 🔐 Authentication Flows

### User Login Flow

1. **User Login Request** → Payload sent to endpoint.
2. **AuthenticationManager** → Validates credentials.
3. **CustomUserDetailsService** → Loads user data from MySQL.
4. **JWT Token Generation** → Generates Access + Refresh tokens.
5. **Response** → Returns tokens to the client.

### Refresh Token Flow

1. **Refresh Request** → Client sends Refresh Token.
2. **Validation** → System checks token validity and expiry in DB.
3. **Rotation** → Generates a new Access Token.
4. **Response** → Returns new JWT to the client.

---

## ⚙️ Configuration & Setup

### 1. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE nova_bank_auth_db;

```

### 2. Application Properties

Update `src/main/resources/application.yml` (or `.properties`):

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nova_bank_auth_db
    username: YOUR_USERNAME
    password: YOUR_PASSWORD

jwt:
  secret-key: YOUR_BASE64_SECRET_KEY
  expiration: 3600000 # 1 hour

```

### 3. Running the App

```bash
# Clone the repository
git clone [https://github.com/your-username/nova-bank-auth-service.git](https://github.com/your-username/nova-bank-auth-service.git)

# Navigate to directory
cd nova-bank-auth-service

# Build and run
mvn clean install
mvn spring-boot:run

```

---

## 📚 API Documentation

Once the application is running, you can access the documentation at:

* **Swagger UI**: `http://localhost:8081/swagger-ui/index.html`
* **OpenAPI Docs**: `http://localhost:8081/v3/api-docs`

### Core Endpoints

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Register a new user | No |
| `POST` | `/api/v1/auth/login` | Authenticate & get tokens | No |
| `POST` | `/api/v1/auth/refresh-token` | Get new access token | No (Uses Refresh) |
| `POST` | `/api/v1/auth/logout` | Invalidate session | Yes |

---

## 📈 Future Enhancements

* [ ] Email Verification & OTP Authentication.
* [ ] Redis integration for Token Blacklisting.
* [ ] Dockerization & Kubernetes manifests.
* [ ] OAuth2 / Social Login integration.
* [ ] API Gateway & Microservices orchestration.

---

## 👨‍💻 Author

**Chandrasekhar**
*Backend Development Learning Project focused on building production-style authentication systems.*

## 📄 License

This project is for learning and educational purposes.

```

```