# Trendaura Backend (Java + MySQL)

Scalable, secure backend for the **Trendaura** fashion platform. Built with **Spring Boot 3**, **MySQL**, and **JWT** authentication. Aligns with your backend goals: state-wise fashion data, user/cart/wishlist/orders, AI integration stubs, and real-time recommendations.

---

## Backend Goals Covered

| Goal | Implementation |
|------|-----------------|
| **Scalable & secure architecture** | Spring Boot, JWT, BCrypt, connection pooling (HikariCP) |
| **Efficient data management** | MySQL + JPA; state-wise products, users, cart, wishlist, orders, addresses |
| **AI model integration** | Stub endpoints for CNN (classify) and LSTM (trends); ready to wire to your ML service |
| **Real-time recommendation processing** | `/api/recommendations`; TrendAnalytics entity for behaviour tracking |
| **Performance & scalability** | Stateless JWT, HikariCP pool, indexed queries |
| **Security & data protection** | Env-based config, hashed passwords, authenticated APIs |

---

## Prerequisites

- **Java 17**
- **Maven 3.8+**
- **MySQL 8** (or 5.7) running locally or remote

---

## MySQL Setup

1. Install and start MySQL.
2. Create a database (optional; app can create it if `createDatabaseIfNotExist=true`):

```sql
CREATE DATABASE IF NOT EXISTS trendaura
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

3. Create a user and grant privileges:

```sql
CREATE USER IF NOT EXISTS 'trendaura'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON trendaura.* TO 'trendaura'@'localhost';
FLUSH PRIVILEGES;
```

---

## Configuration

Edit `src/main/resources/application.properties` (or use env vars / `application-local.properties`):

```properties
# MySQL - change to your credentials
spring.datasource.url=jdbc:mysql://localhost:3306/trendaura?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=trendaura
spring.datasource.password=your_password

# JWT - use a long random secret in production (e.g. env TRENDAURA_JWT_SECRET)
jwt.secret=YourSecretKeyAtLeast32CharactersLongForHS256
jwt.expiration-ms=86400000

# CORS - add your frontend origin (e.g. where your .html pages are served)
app.cors.allowed-origins=http://localhost:5500,http://127.0.0.1:5500,http://localhost:3000
```

---

## Run the application

```bash
cd trendaura-backend
mvn spring-boot:run
```

Optional: run with dev profile to seed sample products:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

API base URL: **http://localhost:8080/api**

---

## API Overview (connect your .html frontend)

All authenticated endpoints require header: `Authorization: Bearer <token>`.

| Area | Method | Endpoint | Description |
|------|--------|----------|-------------|
| **Auth** | POST | `/api/auth/register` | Register (email, password, firstName, lastName, phone, countryCode) |
| | POST | `/api/auth/login` | Login → returns `{ token, user }` |
| | POST | `/api/auth/forgot-password?email=...` | Forgot password (stub) |
| **User** | GET | `/api/users/me` | Current user profile |
| | PUT | `/api/users/me` | Update profile |
| | PUT | `/api/users/me/password` | Change password (body: currentPassword, newPassword) |
| **Products** | GET | `/api/products` | List products (paginated) |
| | GET | `/api/products/search?state=&type=` | Search by state + fashion type (matches your search form) |
| | GET | `/api/products/{id}` | Product by ID |
| **Cart** | GET | `/api/cart` | Get cart |
| | POST | `/api/cart/items` | Add item (body: `{ "productId": 1, "quantity": 1 }`) |
| | PUT | `/api/cart/items/{productId}` | Update quantity (body: `{ "quantity": 2 }`) |
| | DELETE | `/api/cart/items/{productId}` | Remove item |
| **Wishlist** | GET | `/api/wishlist` | Get wishlist |
| | POST | `/api/wishlist/items` | Add (body: `{ "productId": 1 }`) |
| | DELETE | `/api/wishlist/items/{productId}` | Remove |
| **Addresses** | GET | `/api/addresses` | List addresses |
| | POST | `/api/addresses` | Add address |
| | PUT | `/api/addresses/{id}` | Update address |
| | DELETE | `/api/addresses/{id}` | Delete address |
| **Orders** | POST | `/api/orders` | Create order (body: `{ "addressId": 1, "shippingAmount": 50 }`) |
| | GET | `/api/orders` | My orders |
| | GET | `/api/orders/{id}` | Order by ID |
| | GET | `/api/orders/number/{orderNumber}` | Order by number |
| **Recommendations** | GET | `/api/recommendations?limit=10` | Personalized recommendations (stub) |
| **AI** | POST | `/api/ai/classify` | CNN classify image (stub; multipart `image`) |
| | GET | `/api/ai/trends?state=&fashionType=` | LSTM trend forecast (stub) |

---

## Connecting your HTML frontend

1. **Base URL**: set in your JS to `http://localhost:8080/api`.
2. **Login/Register** (`login.html`):  
   - `POST /api/auth/register` with `{ firstName, lastName, email, password, phone?, countryCode? }`  
   - `POST /api/auth/login` with `{ email, password }`  
   - Store `response.token` and use it as `Authorization: Bearer <token>` for all subsequent requests.
3. **Search** (`index.html` / `collections1.html`):  
   - `GET /api/products/search?state=andhra_pradesh&type=saree` (state and type from your dropdowns).
4. **Cart** (`cart.html`):  
   - `GET /api/cart`, `POST /api/cart/items`, `PUT /api/cart/items/{productId}`, `DELETE /api/cart/items/{productId}`.
5. **Wishlist** (`wishlist1.html`):  
   - `GET /api/wishlist`, `POST /api/wishlist/items`, `DELETE /api/wishlist/items/{productId}`.
6. **Profile** (`profile.html`):  
   - `GET /api/users/me`, `PUT /api/users/me`, `PUT /api/users/me/password`.
7. **Address** (`address.html`):  
   - `GET /api/addresses`, `POST /api/addresses`.
8. **Checkout / Payment** (`payment.html`):  
   - Create order with `POST /api/orders` with `{ addressId, shippingAmount: 50 }`.
9. **Order history** (`profile.html`, `order-details.html`):  
   - `GET /api/orders`, `GET /api/orders/{id}` or `GET /api/orders/number/{orderNumber}`.

---

## Project structure

```
trendaura-backend/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/trendaura/
    │   ├── TrendauraApplication.java
    │   ├── config/          # Security, CORS, DataSeeder
    │   ├── controller/      # REST controllers
    │   ├── dto/             # Request/response DTOs
    │   ├── entity/          # JPA entities (User, Product, Order, etc.)
    │   ├── exception/       # GlobalExceptionHandler
    │   ├── repository/      # JPA repositories
    │   ├── security/        # JWT, UserDetails, Auth filter
    │   ├── service/         # Business logic
    │   └── util/            # CurrentUser
    └── resources/
        └── application.properties
```

---

## Security notes

- Do **not** commit real passwords or JWT secrets. Use environment variables (e.g. `TRENDAURA_JWT_SECRET`, `SPRING_DATASOURCE_PASSWORD`) or a local `application-local.properties` (excluded from git).
- Passwords are hashed with **BCrypt**.
- JWT secret must be at least **256 bits (32 characters)** for HS256.

---

## AI integration (CNN / LSTM)

- **`/api/ai/classify`**: Replace the stub with an HTTP call to your CNN service (e.g. Flask/FastAPI) that accepts an image and returns category/state/fashion type.
- **`/api/ai/trends`**: Replace the stub with a call to your LSTM (or other) trend-forecasting service.
- Set `app.ai.base-url` in config and implement a small `AiClient` service that calls your ML APIs.

You now have a full Java backend with MySQL that matches your Trendaura .html pages and backend goals. Use the API base URL and the table above to replace `localStorage` in your frontend with real HTTP calls.
