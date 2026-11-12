# SwiftURL - High-Performance URL Shortener & Real-Time Analytics (Spring Boot + JPA / MySQL)

A production-ready, interview-optimized **URL Shortener and Real-Time Analytics Platform** built with **Java 17/24**, **Spring Boot 3**, and **Spring Data JPA** (with zero-setup H2 default and MySQL support).

---

## 💡 Architecture & Key Features

1. **Zero-Setup Local Execution (H2 In-Memory + MySQL-Ready)**:
   - Out-of-the-box, the application boots with zero external dependencies using an embedded H2 in-memory database with MySQL compatibility mode enabled.
   - Includes full support for MySQL 8.0 via `application-mysql.properties` (`-Dspring.profiles.active=mysql`).

2. **Asynchronous Non-Blocking Click Tracking**:
   - Redirection (`/{shortCode}`) issues an instant **HTTP 302 Found** redirect to the client.
   - User-Agent, referer, timestamp, and client metadata are tracked asynchronously using `@Async` without delaying user redirection.

3. **Demographic & Time-Series Analytics**:
   - `AnalyticsService` parses user agent strings into Browser, OS, and Device Type.
   - Comprehensive aggregation endpoints serve real-time metrics to interactive Chart.js dashboards.

4. **Base62 Short Code Generation**:
   - Collision-resistant Base62 algorithm generating clean 6-character alphanumeric keys.
   - Support for custom aliases and expiration dates.

---

## 📂 Project Structure

```
url-shortener/
├── pom.xml                                     # Spring Boot 3 + Spring Data JPA + H2 + MySQL
├── mvnw.cmd / mvnw                             # Self-contained Maven Wrapper
├── README.md                                   # Architecture, API docs, & setup guide
└── src/
    ├── main/
    │   ├── java/com/urlshortener/
    │   │   ├── UrlShortenerApplication.java    # Application entry point (@EnableAsync)
    │   │   ├── controller/
    │   │   │   ├── UrlRestController.java      # REST endpoints under /api/v1/urls
    │   │   │   └── RedirectController.java     # HTTP 302 public redirect (/{shortCode})
    │   │   ├── dto/                            # Request/Response payloads
    │   │   │   ├── ShortenRequest.java
    │   │   │   ├── ShortenResponse.java
    │   │   │   ├── UrlSummaryResponse.java
    │   │   │   ├── AnalyticsResponse.java
    │   │   │   └── ClickLogDto.java
    │   │   ├── exception/                      # Global exception handling & API errors
    │   │   ├── model/                          # JPA Entities
    │   │   │   ├── UrlMapping.java             # @Entity url_mappings
    │   │   │   └── ClickAnalytics.java         # @Entity click_analytics
    │   │   ├── repository/                     # Spring Data JPA Repositories
    │   │   ├── service/                        # Business logic
    │   │   │   ├── UrlService.java             # Shortening, alias check, redirection
    │   │   │   ├── AnalyticsService.java       # Async click logging & aggregations
    │   │   │   └── ShortCodeGenerator.java     # Base62 generation & collision loop
    │   │   └── util/
    │   │       ├── Base62Encoder.java          # Math-based Base62 encoder/decoder
    │   │       └── UserAgentParser.java        # Lightweight OS/Browser/Device parser
    │   └── resources/
    │       ├── application.properties          # Default H2 in-memory configuration
    │       ├── application-mysql.properties    # Optional MySQL configuration
    │       └── static/                         # Responsive Glassmorphic Dashboard
    │           ├── index.html                  # Shortener & URL Management UI
    │           ├── analytics.html              # Chart.js Visual Analytics Dashboard
    │           ├── css/style.css               # Modern glassmorphism UI styling
    │           └── js/
    │               ├── app.js                  # Shortener UI script & clipboard
    │               └── analytics.js            # Chart.js rendering & activity stream
    └── test/java/com/urlshortener/
        ├── service/UrlServiceTest.java         # Unit tests (Mocking & validation)
        └── controller/
            ├── UrlRestControllerTest.java      # MockMvc REST API tests
            └── RedirectControllerTest.java     # Redirection & header tests
```

---

## 🚀 How to Run

### Option 1: Using the Maven Wrapper (Terminal)
Run from the `url-shortener` directory:
```powershell
.\mvnw.cmd spring-boot:run
```

### Option 2: From Your IDE
1. Open [`UrlShortenerApplication.java`](file:///c:/Users/prave/.gemini/antigravity/scratch/url-shortener/src/main/java/com/urlshortener/UrlShortenerApplication.java).
2. Click **Run ▶** on `main(...)`.

### Option 3: Run Tests
```powershell
.\mvnw.cmd test
```

---

## 🌐 Application URLs

| Interface | URL | Description |
| :--- | :--- | :--- |
| **URL Shortener Dashboard** | **`http://localhost:8080/`** | Create short links, set aliases/expiry, copy links, view table |
| **Analytics Dashboard** | **`http://localhost:8080/analytics.html?code={shortCode}`** | Real-time charts for browsers, OS, devices, timeline, and logs |
| **H2 Web Console** | **`http://localhost:8080/h2-console`** | Inspect tables and SQL records (JDBC URL: `jdbc:h2:mem:urlshortener`) |

---

## 📡 REST API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/urls/shorten` | Shorten a long URL with optional custom alias and expiration |
| `GET` | `/{shortCode}` | Public HTTP 302 redirect & non-blocking analytics logging |
| `GET` | `/api/v1/urls` | List all shortened URLs with status and total clicks |
| `GET` | `/api/v1/urls/{shortCode}/analytics` | Retrieve comprehensive analytics JSON for a short code |
| `PATCH`| `/api/v1/urls/{shortCode}/deactivate` | Deactivate a short link |
| `DELETE`| `/api/v1/urls/{shortCode}` | Delete a short link and its click history |
