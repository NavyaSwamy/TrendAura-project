# 🛠️ TRENDAURA COMPLETE SETUP & DEPLOYMENT GUIDE

## 📋 Table of Contents
1. [Quick Start](#quick-start)
2. [Backend Setup](#backend-setup)
3. [Frontend Setup](#frontend-setup)
4. [Database Setup](#database-setup)
5. [Environment Configuration](#environment-configuration)
6. [API Integration](#api-integration)
7. [Testing with Postman](#testing-with-postman)
8. [Troubleshooting](#troubleshooting)
9. [Production Deployment](#production-deployment)

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- MySQL 8.0+
- Node.js (optional, for frontend server)
- Spring Boot 3.2+
- Maven 3.8+

### Clone & Build
```bash
# Build backend JAR
cd trendaura-backend
mvn clean package -DskipTests

# Start backend
java -jar target/trendaura-backend-1.0.0.jar
```

Backend runs at: **http://localhost:8080/api**

---

## 🔧 Backend Setup

### Step 1: Create MySQL Database
```sql
CREATE DATABASE trendaura;
USE trendaura;

-- Import schema.sql
SOURCE src/main/resources/schema.sql;

-- Verify tables
SHOW TABLES;
```

### Step 2: Configure Environment Variables
Create `.env` file in project root:
```bash
# Database
DB_USERNAME=root
DB_PASSWORD=root123

# JWT
JWT_SECRET=YourSuperSecureSecretKey123456789

# Gmail SMTP
GMAIL_EMAIL=your-email@gmail.com
GMAIL_APP_PASSWORD=xxxx-xxxx-xxxx-xxxx  # Google App Password (16 chars)

# Cloudinary
CLOUDINARY_CLOUD_NAME=dzlx24qek
CLOUDINARY_API_KEY=362936193545482
CLOUDINARY_API_SECRET=hWt6L-2siS42vizS6RVXaQukfVA

# OpenAI
OPENAI_API_KEY=sk-proj-xxxxx

# Frontend URL
FRONTEND_URL=http://localhost:5500
```

### Step 3: Update application.properties
Edit `src/main/resources/application.properties`:
```properties
# Database (local dev)
spring.datasource.url=jdbc:mysql://localhost:3306/trendaura?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root123

# Gmail Configuration
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# JWT
jwt.secret=YourSuperSecureSecretKey123456789

# Cloudinary
cloudinary.cloud-name=dzlx24qek
cloudinary.api-key=362936193545482
cloudinary.api-secret=hWt6L-2siS42vizS6RVXaQukfVA

# OpenAI
openai.api-key=sk-proj-xxxxx

# Frontend
app.frontend-url=http://localhost:5500
```

### Step 4: Run Backend
```bash
cd trendaura-backend
mvn spring-boot:run
```

**Expected Output:**
```
Started TrendauraApplication in X.XXX seconds (JVM running for Y.YYY)
Server running at http://localhost:8080/api
```

---

## 🎨 Frontend Setup

### Step 1: File Structure
```
trendaura-frontend/
├── index.html              # Home page
├── collections.html        # Product listings
├── product-details.html    # Single product view
├── cart.html              # Shopping cart
├── wishlist1.html         # Wishlist
├── Q&A.html              # Chat bot & FAQ ✅ INTEGRATED
├── contact.html           # Contact form
├── about.html             # About page
├── login.html             # Authentication
├── js/
│   ├── api-client.js      # API client (NEW ✅)
│   ├── api.js             # Legacy API wrapper
│   └── chatbot.js         # Chat functionality
└── Postman_TrendAura_API_Collection.json  # API Testing
```

### Step 2: Configure API Client
Edit `js/api-client.js` line ~23:
```javascript
this.baseURL = 'http://localhost:8080/api';  // Change for production
```

### Step 3: Run Frontend
**Option A: Simple HTTP Server**
```bash
cd trendaura-frontend
python3 -m http.server 5500
# Or: python -m SimpleHTTPServer 5500
```

**Option B: VS Code Live Server**
- Install "Live Server" extension
- Right-click index.html → "Open with Live Server"
- Runs at: **http://localhost:5500**

**Option C: Node HTTP Server**
```bash
npm install -g http-server
http-server . -p 5500
```

---

## 💾 Database Setup

### Create Schema
```bash
# Login to MySQL
mysql -u root -p

# Run in MySQL
USE trendaura;
SOURCE /path/to/schema.sql;
```

### Verify Tables Created
```sql
SHOW TABLES;
-- Output should show: users, products, cart_items, wishlist_items, orders, 
-- order_items, addresses, chat_sessions, chat_messages, search_history, 
-- user_activity_logs, trend_analytics, contact_messages, reviews, etc.
```

### Insert Sample Data (Optional)
```sql
-- Sample user
INSERT INTO users (name, email, password, role, phone)
VALUES ('John Doe', 'john@example.com', 'hashed_password', 'USER', '+1-555-0123');

-- Sample products
INSERT INTO products (name, price, category, type, state, image_url, boutique_name, available_sizes, available_colors)
VALUES ('Red Silk Saree', 2500, 'WOMEN', 'SAREE', 'Maharashtra', 
        'https://cloudinary-url.jpg', 'Royal Collection', 
        '["One Size"]', '["Red","Green"]');
```

---

## ⚙️ Environment Configuration

### Gmail SMTP Setup (Required for Emails)
1. Go to: https://myaccount.google.com/security
2. Enable "2-Step Verification"
3. Go to: https://myaccount.google.com/apppasswords
4. Select "Mail" and "Windows Computer"
5. Copy 16-character password
6. Paste in `GMAIL_APP_PASSWORD`

### Cloudinary Setup (Image Storage)
1. Sign up: https://cloudinary.com
2. Get credentials from Settings → Account Details
3. Copy: Cloud Name, API Key, API Secret
4. Paste in `CLOUDINARY_*` environment variables

### OpenAI Setup (AI Features)
1. Sign up: https://platform.openai.com
2. Go to API Keys: https://platform.openai.com/account/api-keys
3. Create new secret key
4. Copy and paste in `OPENAI_API_KEY`

---

## 🔌 API Integration

### Frontend Activity Tracking

Every page interaction is logged:

```javascript
// Already integrated in all pages!
API.activity.log({
  pageName: 'Collections Page',
  actionName: 'FILTER_BY_CATEGORY',
  additionalData: JSON.stringify({ category: 'WOMEN', type: 'SAREE' })
});
```

### Usage Examples

**1. Register User**
```javascript
const response = await API.auth.register({
  name: 'John Doe',
  email: 'john@example.com',
  password: 'SecurePass123!',
  phone: '+1-555-0123'
});
console.log(response.token); // JWT token
```

**2. Get Products**
```javascript
const products = await API.products.getAll({
  category: 'WOMEN',
  type: 'SAREE',
  minPrice: 1000,
  maxPrice: 10000,
  page: 1,
  size: 20
});
```

**3. Add to Cart**
```javascript
const item = await API.cart.add(productId, quantity, size, color);
// Automatically logs: ADD_TO_CART activity
```

**4. Log Custom Activity**
```javascript
API.activity.log({
  pageName: 'Product Details',
  actionName: 'VIEW_PRODUCT',
  additionalData: JSON.stringify({ productId: 10 })
});
```

---

## 📊 Testing with Postman

### Import Collection
1. Open Postman
2. Click "Import" → "File"
3. Select: `Postman_TrendAura_API_Collection.json`
4. Collection imported with 40+ requests!

### Set Environment Variables
```
BASE_URL: http://localhost:8080/api
JWT_TOKEN: (leave empty, filled after login)
```

### Test Flow
1. **Register** → POST /auth/register
2. **Login** → POST /auth/login (copy token)
3. **Set JWT_TOKEN** variable in Postman
4. **Get Products** → GET /products
5. **Add to Cart** → POST /cart/items
6. **Log Activity** → POST /activity/log
7. **Create Order** → POST /orders

---

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Kill process on port 8080
lsof -i :8080
kill -9 <PID>

# Or change port in application.properties
server.port=8081
```

### Database Connection Error
```
Error: Unable to acquire JDBC Connection
```
**Solution:**
1. Verify MySQL is running: `mysql -u root -p`
2. Create database: `CREATE DATABASE trendaura;`
3. Check credentials in application.properties
4. Verify firewall/network (port 3306)

### Gmail Authentication Failed
```
Error: Authentication failed for mail from smtp.gmail.com
```
**Solution:**
1. Use Google App Password (16 chars), not regular password
2. Enable "Less secure app access" (if using regular password)
3. Verify 2-Step Verification is enabled
4. Check email address is correct

### Cloudinary Upload Error
```
Error: Invalid api_key or api_secret
```
**Solution:**
1. Verify credentials in application.properties
2. Sign up: https://cloudinary.com
3. Copy exact values from Settings → Account Details
4. No typos allowed!

### CORS Error in Frontend
```
Access to XMLHttpRequest blocked by CORS policy
```
**Solution:**
1. Update `app.cors.allowed-origins` in application.properties:
```properties
app.cors.allowed-origins=http://localhost:5500,http://localhost:3000
```
2. Restart backend: `mvn spring-boot:run`

---

## 🌐 Production Deployment

### AWS Elastic Beanstalk
```bash
# Build JAR
mvn clean package

# Deploy
eb init
eb create trendaura-prod
eb deploy

# Set environment variables
eb setenv JWT_SECRET=xxx GMAIL_EMAIL=xxx
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/trendaura-backend-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
docker build -t trendaura-backend .
docker run -p 8080:8080 -e JWT_SECRET=xxx trendaura-backend
```

### Environment Variables for Production
```bash
JWT_SECRET=strong-production-key-256-bits
DB_USERNAME=prod_user
DB_PASSWORD=strong-password
GMAIL_EMAIL=noreply@trendaura.com
GMAIL_APP_PASSWORD=xxxx-xxxx-xxxx-xxxx
CLOUDINARY_CLOUD_NAME=production-account
CLOUDINARY_API_KEY=production-key
CLOUDINARY_API_SECRET=production-secret
OPENAI_API_KEY=production-key
DATABASE_URL=jdbc:mysql://prod-db:3306/trendaura
```

---

## ✅ Verification Checklist

- [ ] MySQL database created with schema
- [ ] Backend starts successfully (port 8080)
- [ ] Frontend loads at localhost:5500
- [ ] Can register a new user
- [ ] Can login with credentials
- [ ] JWT token stored in localStorage
- [ ] Can view products
- [ ] Can add to cart (checked in database)
- [ ] Activity logs created in user_activity_logs table
- [ ] Email notifications sent (check Gmail)
- [ ] Cloudinary image upload works
- [ ] Postman collection tests pass

---

## 📞 Support

**Issues? Check:**
1. Logs: `tail -f logs/application.log`
2. Database: `SELECT * FROM users;`
3. API: `curl http://localhost:8080/api/products`
4. Frontend console: F12 → Console tab

**Contact:** support@trendaura.com

---

**Last Updated:** March 11, 2026  
**Version:** 1.0.0  
**Status:** Production Ready ✅
