# URL-Shortening-Service
A URL Shortener API that helps shorten long URLs.
Project URL: https://roadmap.sh/projects/url-shortening-service


A backend REST API for creating and managing shortened URLs, built with **Java and Spring Boot**.

The service uses **PostgreSQL** for persistent storage, **Caffeine** for in-memory caching, and **Docker Compose** to run the application and database together.

## Features

* Create shortened URLs
* Redirect short URLs to their original URLs
* Track URL access count
* Retrieve URL statistics
* Update shortened URLs
* Deactivate shortened URLs
* Short-code uniqueness validation
* Request validation
* Global exception handling
* Caffeine in-memory caching
* Cache invalidation on update and delete
* PostgreSQL persistence
* Dockerized application
* Docker Compose setup
* Environment-based database configuration

## Tech Stack

* **Java 21**
* **Spring Boot**
* **Spring Data JPA**
* **Hibernate**
* **PostgreSQL**
* **Caffeine Cache**
* **Maven**
* **Docker**
* **Docker Compose**
* **Postman**

## Architecture

The application follows a layered architecture:

```text
Client
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

Caching is handled in the service layer:

```text
Client
   ↓
URL Controller
   ↓
URL Service
   ↓
Caffeine Cache
   ↓
URL Repository
   ↓
PostgreSQL
```

Frequently accessed URLs can be served from the in-memory cache, reducing repeated database lookups.

## Database Schema

The URL entity contains:

| Field         | Description                               |
| ------------- | ----------------------------------------- |
| `id`          | Primary key                               |
| `url`         | Original URL                              |
| `shortCode`   | Unique shortened URL identifier           |
| `isActive`    | Indicates whether the URL is active       |
| `accessCount` | Number of times the URL has been accessed |
| `createdAt`   | URL creation timestamp                    |
| `updatedAt`   | Last update timestamp                     |

Timestamps are stored using Java's `Instant` type.

## API Endpoints

### Create Short URL

**POST**

```text
/shorten
```

Request:

```json
{
  "url": "https://www.example.com/some/very/long/url"
}
```

The service generates a unique short code for the URL.

---

### Redirect to Original URL

**GET**

```text
/shorten/{shortCode}
```

Example:

```text
GET /shorten/1316e0
```

The service resolves the short code and redirects the client to the original URL.

Each successful access increments the URL's `accessCount`.

---

### Get URL Statistics

**GET**

```text
/shorten/{shortCode}/stats
```

Example:

```text
GET /shorten/1316e0/stats
```

Returns statistics and details associated with the shortened URL.

---

### Update Short URL

**PUT**

```text
/shorten/{shortCode}
```

Updates the original URL associated with the specified short code.

The corresponding cache entry is invalidated after the update.

---

### Deactivate Short URL

**DELETE**

```text
/shorten/{shortCode}
```

Deactivates the shortened URL.

The corresponding cache entry is also invalidated.

## Caching

The application uses **Caffeine** for in-memory caching.

The cache stores frequently accessed URL mappings using:

```text
shortCode → URL
```

When a shortened URL is requested:

1. The application checks the Caffeine cache.
2. If the URL is cached, it can be retrieved without querying PostgreSQL.
3. If it is not cached, the application retrieves it from PostgreSQL.
4. The retrieved URL is then cached for subsequent requests.

Cache entries are invalidated when a URL is updated or deactivated to prevent stale data.

## Validation & Exception Handling

Incoming requests are validated before processing.

The application uses a global exception handler with `@RestControllerAdvice` to provide consistent error responses.

Example error response:

```json
{
  "message": "URL not found",
  "httpStatusCode": 404,
  "timestamp": "2026-08-16T12:00:00Z"
}
```

## Running with Docker Compose

### Prerequisites

* Docker Desktop

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd URLShortener
```

### 2. Configure environment variables

Create a `.env` file in the project root:

```env
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

Database credentials are supplied through environment variables and are not hardcoded into the application configuration.

### 3. Start the application

```bash
docker compose up --build
```

The `--build` option builds the Spring Boot application image using the project's `Dockerfile` before starting the containers.

Docker Compose then starts both the application and PostgreSQL containers.

The application is available at:

```text
http://localhost:8081
```

### 4. Stop the application

```bash
docker compose down
```

## How Docker Compose Works in This Project

The project does not require a pre-built application image to be pushed to Docker Hub.

Docker Compose builds the Spring Boot application image locally using the project's `Dockerfile`.

The overall flow is:

```text
Docker Compose
      ↓
Reads docker-compose.yml
      ↓
Builds Spring Boot image using Dockerfile
      ↓
Creates application container
      ↓
Starts PostgreSQL container
      ↓
Application connects to PostgreSQL
```

The PostgreSQL container uses an existing PostgreSQL Docker image, while the application image is built from the project's source code.

This allows the complete application stack to be started with a single command:

```bash
docker compose up --build
```

## Running Without Docker

The application can also be run locally using Maven.

### Prerequisites

* Java 21+
* Maven
* PostgreSQL

Make sure PostgreSQL is running and the required database/environment variables are configured.

On Windows:

```bash
mvnw.cmd spring-boot:run
```

On Linux/macOS:

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8081
```

## Example API Flow

A typical workflow is:

```text
Create URL
    ↓
Receive short code
    ↓
Access short URL
    ↓
Redirect to original URL
    ↓
Access count increases
    ↓
Retrieve URL statistics
```

## Project Structure

```text
src/
└── main/
    └── java/
        └── ...
            ├── controller/
            ├── service/
            ├── repository/
            ├── entity/
            ├── dto/
            ├── config/
            └── exception/
```

The project separates responsibilities across controllers, services, repositories, entities, DTOs, configuration, and exception handling.

## Key Design Decisions

### Unique Short Codes

A short code is generated for every new URL and checked for uniqueness before being persisted.

### Access Count

The service maintains an `accessCount` for each shortened URL and increments it whenever the URL is successfully accessed.

### Cache Invalidation

Cache entries are evicted when URLs are updated or deactivated, preventing stale URL mappings from remaining in the cache.

### Environment-Based Configuration

Database credentials are provided through environment variables rather than being committed directly to the repository.

## Future Improvements

* Redis-based distributed caching
* Rate limiting
* Authentication and authorization
* Custom short-code support
* Automated integration testing
* OpenAPI/Swagger documentation
* Cloud deployment
* Monitoring and application metrics

## Author

**Avni Sharma**

Backend-focused project demonstrating practical experience with **Java, Spring Boot, REST APIs, PostgreSQL, Caffeine caching, and Docker**.
