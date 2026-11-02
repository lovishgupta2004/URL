# URL Shortener

A URL shortener with a React frontend and a Spring Boot REST API. It stores shortened links in PostgreSQL, supports optional custom short codes, redirects visitors to the original URL, and expires links after two days.

## Features

- Generate a six-character alphanumeric short code for a URL.
- Optionally choose a custom short code.
- Reuse an existing short code when shortening the same original URL again, as long as that link has not expired.
- Redirect short links to their original URLs.
- Expire links 48 hours after creation. Expired links return `404 Not Found` even if the scheduled cleanup has not run yet.
- Delete expired rows from the database with a scheduled cleanup that runs hourly while the backend is running.

## Project layout

```text
URL/       Spring Boot backend, REST API, and tests
frontend/  React frontend built with Vite
```

## Requirements

- Java 21
- Node.js and npm
- PostgreSQL database

## Database configuration

The backend uses PostgreSQL. Before starting it, configure the database connection in `URL/src/main/resources/application.properties`:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

Use your own database and keep credentials out of source control. Hibernate is configured to update the database schema automatically.

## Run locally

Start the backend from the repository root:

```powershell
.\URL\mvnw.cmd -f URL\pom.xml spring-boot:run
```

The API listens on `http://localhost:8080`.

In another terminal, install and start the frontend:

```powershell
cd frontend
npm install
npm run dev
```

Open the Vite URL shown in the terminal (normally `http://localhost:5173`). The frontend sends requests to the local backend at `http://localhost:8080`.

## API

### Create a short URL

`POST /url`

Send the original URL as plain text to generate a short code:

```http
POST http://localhost:8080/url
Content-Type: text/plain

https://example.com/a/long/path
```

The response body is the short code. The frontend builds a link in this form:

```text
http://localhost:8080/url/{shortCode}
```

Alternatively, send JSON to request a custom short code:

```http
POST http://localhost:8080/url
Content-Type: application/json

{
  "originalUrl": "https://example.com/a/long/path",
  "customUrl": "my-link"
}
```

Set `customUrl` to an empty string or `null` to generate a code instead. A custom code already in use by an active link returns `409 Conflict`.

### Follow a short URL

`GET /url/{shortCode}`

An active link responds with a redirect to its original URL. A missing or expired code returns `404 Not Found`.

## Expiration and cleanup

A link expires 48 hours after its creation time. The backend checks expiration when a link is requested and during URL creation, so expired codes are not served and can be reused. A Spring scheduled task deletes expired rows from PostgreSQL once per hour. This cleanup only runs while the backend application is running.

## Tests and checks

Run backend tests:

```powershell
.\URL\mvnw.cmd -f URL\pom.xml test
```

Run frontend lint and production build:

```powershell
cd frontend
npm run lint
npm run build
```
