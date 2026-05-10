# 📚 BookNest Backend

BookNest Backend is a microservices-based backend system for the BookNest e-commerce bookstore platform.

It provides REST APIs for authentication, book management, cart, orders, wallet, reviews, notifications, payment integration, and wishlist operations.

---

# 🏗️ Microservices Architecture

The backend follows a scalable microservices architecture using Spring Boot and Spring Cloud.

## 🔹 Available Services

| Service | Responsibility |
|---|---|
| auth-service | Authentication & Authorization |
| book-service | Book Catalog Management |
| cart-service | Shopping Cart |
| order-service | Order Management |
| wallet-service | Wallet & Transactions |
| review-service | Reviews & Ratings |
| notification-service | Notifications |
| eureka-service | Service Discovery |
| api-gateway | API Gateway & Routing |

---

# ✨ Features

## 👤 Customer Features

- Register/Login
- JWT Authentication
- GitHub OAuth Login
- Browse & Search Books
- Add to Cart
- Wishlist Management
- Place Orders
- Wallet Payment
- Razorpay Payment Integration
- Track Orders
- Write Reviews & Ratings
- Notification System

---

## 🛠️ Admin Features

- Manage Books
- Manage Inventory
- Manage Orders
- Manage Users
- View Analytics
- Moderate Reviews

---

# 🏗️ Tech Stack

## 🔹 Backend

- Java
- Spring Boot
- Spring Security
- Spring Cloud
- Spring Data JPA
- REST APIs

## 🔹 Security

- JWT Authentication
- BCrypt Password Encoding
- OAuth2 GitHub Login

## 🔹 Database & Cache

- MySQL
- Redis

## 🔹 Messaging & Communication

- RabbitMQ
- REST Template / OpenFeign

## 🔹 Microservices Tools

- Eureka Server
- API Gateway

## 🔹 Payment

- Razorpay Integration

## 🔹 DevOps & Documentation

- Maven
- Docker
- Swagger / OpenAPI

---

# 📂 Project Structure

```bash
BookNest-Backend/
 ┣ auth-service/
 ┣ book-service/
 ┣ cart-service/
 ┣ order-service/
 ┣ wallet-service/
 ┣ review-service/
 ┣ notification-service/
 ┣ eureka-service/
 ┣ api-gateway/
 ┗ common-config/
```

---

# ⚙️ Installation & Setup

## 1️⃣ Clone Repository

```bash
git clone <backend-repository-url>
```

---

## 2️⃣ Navigate to Project Folder

```bash
cd BookNest-Backend
```

---

## 3️⃣ Configure Database

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booknest
spring.datasource.username=root
spring.datasource.password=yourpassword
```

---

## 4️⃣ Build Project

```bash
mvn clean install
```

---

## 5️⃣ Start Eureka Server

```bash
cd eureka-service
mvn spring-boot:run
```

---

## 6️⃣ Start API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

---

## 7️⃣ Run Other Microservices

```bash
mvn spring-boot:run
```

---

# 🔐 Security Features

- JWT Authentication
- Role-Based Authorization
- BCrypt Password Encryption
- OAuth2 GitHub Login
- Secure REST APIs

---

# 💳 Payment Integration

BookNest supports:

- Wallet-Based Payment
- Razorpay Payment Gateway
- Cash On Delivery (COD)

---

# 📡 REST APIs

## 🔹 Authentication APIs

```http
POST /auth/register
POST /auth/login
POST /auth/logout
```

---

## 🔹 Book APIs

```http
GET /books
GET /books/{id}
POST /books
PUT /books/{id}
DELETE /books/{id}
```

---

## 🔹 Cart APIs

```http
POST /cart/add
GET /cart/{userId}
DELETE /cart/remove/{itemId}
```

---

## 🔹 Order APIs

```http
POST /orders/place
GET /orders/user/{userId}
PUT /orders/status/{id}
```

---

# 📨 Notification System

Supports:

- Email Notifications
- In-App Notifications
- Order Updates
- Payment Alerts
- Low Stock Alerts

---

# 🌐 API Gateway

API Gateway handles:

- Centralized Routing
- Authentication Filtering
- Load Balancing
- Request Forwarding

---

# 🔍 Eureka Service Discovery

Eureka Server is used for:

- Service Registration
- Service Discovery
- Dynamic Communication Between Services

---

# 🧪 Testing

Run tests using:

```bash
mvn test
```

---

# 🚀 Future Enhancements

- Kubernetes Deployment
- CI/CD Pipeline
- Elasticsearch Integration
- Centralized Logging
- Docker Compose Deployment
- Real-Time Chat Support

---

# 👩‍💻 Developed By

Khushi Kumari

---

# 📄 License

This project is developed for educational and learning purposes.
