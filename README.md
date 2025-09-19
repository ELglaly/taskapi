# Task Management API

A robust, secure, and scalable RESTful API for task management built with Spring Boot 3.x featuring JWT authentication, validation

---

## Quick Start

### Prerequisites

* Java 17+
* Maven 3.6+ or Gradle 7+
* H2 for development/testing
* Git

### Installation

```bash
git clone https://github.com/yourusername/taskapi.git
cd taskapi
```

### Access

* API Base URL: `http://localhost:8080`
* H2 Console: `http://localhost:8080/h2-console`

---

## Features

### Authentication & Security

* JWT authentication
* Role-based access control
* BCrypt password encryption
* Custom user details service
* Security filter chain configuration

### Task Management

* CRUD for tasks
* User-specific isolation
* Task status management (OPEN, DONE)
* Pagination and sorting

### Validation & Error Handling

* Bean validation with custom annotations
* Global exception handler
* Structured error responses
* Input sanitization

### Architecture & Quality

* Clean architecture
* DTO pattern
* Repository pattern with Spring Data JPA
* Service layer abstraction
* Unit tests

---

## Configuration

### Environment Variables

```bash
DB_URL=jdbc:your_url
DB_CLOSE_DELAY=your_delay
DB_CLOSE_ON_EXIT=your_data
DB_USERNAME=your_username
DB_PASSWORD=your_password
H2_PATH=yourpath
JWT_EXPIRE=86400000
```

---

## API Endpoints

### Authentication

| Method | Endpoint       | Description       | Request Body        | Response             |
| ------ | -------------- | ----------------- | ------------------- | -------------------- |
| POST   | /auth/register | Register new user | RegistrationRequest | ApiResponse<UserDto> |
| POST   | /auth/login    | Login and get JWT | LoginRequest        | ApiResponse<String>  |

### Task Management

| Method | Endpoint    | Description         | Parameters                  | Response                    |
| ------ | ----------- | ------------------- | --------------------------- | --------------------------- |
| POST   | /tasks      | Create new task     | -                           | ApiResponse<TaskDto>        |
| GET    | /tasks      | Get tasks paginated | page, size, sortBy, sortDir | ApiResponse\<Page<TaskDto>> |
| GET    | /tasks/{id} | Get task by ID      | id                          | ApiResponse<TaskDto>        |
| PUT    | /tasks/{id} | Update task         | id                          | ApiResponse<TaskDto>        |
| DELETE | /tasks/{id} | Delete task         | id                          | ApiResponse<Void>           |

---

## Request Examples

### Register User

```http
POST /auth/register
Content-Type: application/json

{
  "name": "Sherif",
  "email": "Sherif@example.com",
  "password": "Password!"
}
```

### Create Task

```http
POST /tasks
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "title": "Complete API Documentation",
  "description": "Write comprehensive README for the task API",
  "status": "OPEN"
}
```

---

## Project Structure

```
taskapi/
├── README.md
├── pom.xml
├── .env.example
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/example/taskapi/
    │   │   ├── TaskapiApplication.java
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java
    │   │   │   ├── JwtConfig.jav
    │   │   ├── controller/
    │   │   │   ├── AuthController.java
    │   │   │   └── TaskController.java
    │   │   ├── dto/
    │   │   │   ├── TaskDto.java
    │   │   │   └── UserDto.java
    │   │   ├── entity/
    │   │   │   ├── Task.java
    │   │   │   ├── AppUser.java
    │   │   │   ├── appenum/
    │   │   │   │   └── TaskStatus.java
    │   │   │   └── user/
    │   │   │       └── AppUserSecurity.java
    │   │   ├── exception/
    │   │   ├── factory/
    │   │   ├── mapper/
    │   │   ├── repository/
    │   │   ├── request/
    │   │   ├── response/
    │   │   ├── security/
    │   │   ├── service/
    │   │   │   ├── task/
    │   │   │   └── user/
    │   │   └── validation/
    │   └── resources/
    └── test/
        └── java/com/example/taskapi/
            └── TaskControllerTest.java
```

---

## Testing

### Categories

* Controller tests with `@WebMvcTest`
