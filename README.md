# SwiftURL — URL Shortener & Click Analytics

A Java 17 and Spring Boot URL shortener backed by MongoDB. It supports custom aliases, expiring links, redirects, and click analytics.

## Features

- HTTP 302 redirects and asynchronous click logging.
- Browser, operating system, device, referrer, and recent-click analytics.
- Base62 short-code generation, custom aliases, and expiration dates.
- MongoDB Atlas connection through the `MONGODB_URI` environment variable.

## Configure MongoDB Atlas

1. Create a database user and database in MongoDB Atlas, and allow your app's IP address in Atlas Network Access.
2. Copy `.env.example` to `.env` in the project root.
3. Replace the placeholders in `MONGODB_URI` with your Atlas cluster host, database username, URL-encoded password, and database name. Keep the real URI in `.env`; Git ignores this file.
4. Start the app with `./mvnw spring-boot:run` (Windows: `./mvnw.cmd spring-boot:run`). Spring Boot loads `.env` from the project root.

The project does not contain your Atlas credentials. Do not commit `.env` or paste the URI into source files. For local MongoDB without Atlas, the default connection is `mongodb://localhost:27017/url_shortener_db`; `docker compose up -d mongodb` starts a local database.

## Run

```powershell
Copy-Item .env.example .env
# Edit .env with your Atlas connection string
.\mvnw.cmd spring-boot:run
```

The service runs at `http://localhost:8080`. MongoDB collections and indexes are created as needed.

## Application URLs

| Interface | URL | Description |
| --- | --- | --- |
| URL Shortener Dashboard | `http://localhost:8080/` | Create and manage links |
| Analytics Dashboard | `http://localhost:8080/analytics.html?code={shortCode}` | View click analytics |
| Mongo Express (local Docker only) | `http://localhost:8081/` | Inspect local MongoDB collections |

## REST API

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/v1/urls/shorten` | Shorten a URL with optional alias and expiration |
| `GET` | `/{shortCode}` | Redirect and log a click asynchronously |
| `GET` | `/api/v1/urls` | List shortened URLs |
| `GET` | `/api/v1/urls/{shortCode}/analytics` | Retrieve analytics for a short code |
| `PATCH` | `/api/v1/urls/{shortCode}/deactivate` | Deactivate a short link |
| `DELETE` | `/api/v1/urls/{shortCode}` | Delete a short link and its click history |
