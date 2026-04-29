# 🏗️ TRENDAURA COMPLETE SYSTEM ARCHITECTURE & API SPECIFICATION

## 📐 System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              TRENDAURA ARCHITECTURE                             │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌──────────────────────────────┐         ┌──────────────────────────────────┐ │
│  │   FRONTEND LAYER             │         │   BACKEND SERVICES              │ │
│  │  (HTML/CSS/JavaScript)       │         │   (Spring Boot APIs)            │ │
│  │                              │         │                                  │ │
│  │ • index.html                 │         │ • AuthController                │ │
│  │ • collections.html           ├──REST──▶│ • ProductController             │ │
│  │ • product-details.html       │  APIs   │ • CartController                │ │
│  │ • cart.html                  │         │ • WishlistController            │ │
│  │ • wishlist1.html             │         │ • OrderController               │ │
│  │ • Q&A.html ✅ (integrated)   │         │ • ActivityTrackingController    │ │
│  │ • contact.html               │   JSON  │ • ChatbotController             │ │
│  │ • login.html                 │         │ • UserController                │ │
│  │                              │         │ • ContactController             │ │
│  │ js/api-client.js ✅ (NEW)    │         │ • AiController                  │ │
│  │ js/chatbot.js                │         │                                  │ │
│  └──────────────────────────────┘         └──────────────────────────────────┘ │
│           │                                          │                         │
│           │                                          │                         │
│           ▼                                          ▼                         │
│  ┌────────────────────────┐        ┌──────────────────────────────────────┐  │
│  │  Local Storage         │        │  Service Layer                       │  │
│  │                        │        │                                      │  │
│  │ • cart (JSON)          │        │ • AuthService                        │  │
│  │ • wishlist (JSON)      │        │ • ProductService                     │  │
│  │ • chatHistory (JSON)   │        │ • CartService                        │  │
│  │ • JWT token            │        │ • WishlistService                    │  │
│  │ • user info            │        │ • OrderService                       │  │
│  │ • sessionId            │        │ • ActivityTrackingService ✅ (CORE) │  │
│  │                        │        │ • ChatbotService                     │  │
│  │                        │        │ • EmailService ✅ (INTEGRATED)      │  │
│  │                        │        │ • CloudinaryService                  │  │
│  │                        │        │ • AiService                          │  │
│  └────────────────────────┘        │ • UserService                        │  │
│                                    │ • ContactService                     │  │
│                                    │ • ReviewService                      │  │
│                                    └──────────────────────────────────────┘  │
│                                            │                                  │
│                                            ▼                                  │
│                                    ┌──────────────────────┐                   │
│                                    │  Repository Layer    │                   │
│                                    │  (Data Access JPA)   │                   │
│                                    │                      │                   │
│                                    │ • UserRepository     │                   │
│                                    │ • ProductRepository  │                   │
│                                    │ • CartItemRepository │                   │
│                                    │ • OrderRepository    │                   │
│                                    │ • ActivityLogRepo ✅ │                   │
│                                    │ • ChatRepository     │                   │
│                                    │ • WebhookRepository  │                   │
│                                    └──────────────────────┘                   │
│                                            │                                  │
│                                            ▼                                  │
│                          ┌────────────────────────────────┐                   │
│                          │  PERSISTENCE LAYER             │                   │
│                          │                                │                   │
│                          │  MySQL Database ✅             │                   │
│                          │  (users, products,             │                   │
│                          │   orders, activity_logs, etc)  │                   │
│                          └────────────────────────────────┘                   │
│                                                                                │
└─────────────────────────────────────────────────────────────────────────────────┘


                    ┌─────────────────────────────────────────────────┐
                    │  EXTERNAL INTEGRATIONS                          │
                    │                                                 │
                    │ ☁️  Cloudinary (Image Storage & Optimization)   │
                    │ 📧 Gmail SMTP (Email Notifications)             │
                    │ 🤖 OpenAI (AI Recommendations)                  │
                    │ 📦 Payment Gateway (Stripe/Razorpay future)     │
                    └─────────────────────────────────────────────────┘
```

---

## 🔄 Complete API Request/Response Flow

### Example 1: Product Browse & Activity Tracking Flow

```
USER ACTION: Visit Collections Page
     │
     ▼
1. Page Load
   ├─ Load index.html
   └─ Execute DOMContentLoaded event

2. Activity Log (Automatic ✅)
   POST /api/activity/log
   {
     "pageName": "Collections Page",
     "actionName": "PAGE_VIEW",
     "additionalData": {"referrer": "index.html"}
   }

3. Load Products
   GET /api/products?category=WOMEN&type=SAREE&page=1&size=20
   
   Response: 200 OK
   {
     "success": true,
     "data": [
       {
         "id": 1,
         "name": "Red Silk Saree",
         "price": 2500,
         "category": "WOMEN",
         "type": "SAREE",
         "imageUrl": "https://res.cloudinary.com/.../saree-1.jpg",
         "availableSizes": ["One Size"],
         "availableColors": ["Red", "Green"],
         "boutiqueName": "Royal Collection"
       },
       ... more products
     ],
     "page": 1,
     "totalPages": 8
   }

4. Update Cart Badge
   GET /api/cart/count
   
   Response: 
   {
     "success": true,
     "data": 3  // 3 items in cart
   }

5. Update Wishlist Badge
   GET /api/wishlist/count
   
   Response:
   {
     "success": true,
     "data": 5  // 5 items in wishlist
   }

6. User Clicks Product
   Activity Log (Automatic ✅)
   POST /api/activity/log
   {
     "pageName": "Collections Page",
     "actionName": "PRODUCT_CLICK",
     "additionalData": {"productId": 1, "productName": "Red Silk Saree"}
   }

7. View Product Details
   GET /api/products/1
   
   Response:
   {
     "success": true,
     "data": {
       "id": 1,
       "name": "Red Silk Saree",
       "price": 2500,
       "description": "Traditional hand-woven silk saree...",
       "material": "100% Silk",
       "stock": 15,
       "rating": 4.5,
       "reviews": 23,
       "boutiqueName": "Royal Collection",
       "images": [
         "https://res.cloudinary.com/.../saree-1.jpg",
         "https://res.cloudinary.com/.../saree-2.jpg"
       ],
       "availableSizes": ["One Size"],
       "availableColors": ["Red", "Green", "Blue"]
     }
   }
```

### Example 2: Shopping Cart & Wishlist Actions

```
USER ACTION: Add to Cart
     │
     ▼
1. Click "Add to Cart" Button
   POST /api/cart/items
   {
     "productId": 1,
     "quantity": 2,
     "size": "One Size",
     "color": "Red"
   }
   
   Response: 200 OK
   {
     "success": true,
     "data": {
       "id": 15,
       "productId": 1,
       "quantity": 2,
       "size": "One Size",
       "color": "Red"
     }
   }

2. Activity Logging (Automatic ✅)
   POST /api/activity/log
   {
     "pageName": "Product Details",
     "actionName": "ADD_TO_CART",
     "additionalData": {
       "productId": 1,
       "quantity": 2,
       "size": "One Size",
       "color": "Red"
     }
   }

3. Update Cart Badge (Automatic)
   Cart count updates in UI from 3 → 4


USER ACTION: Add to Wishlist
     │
     ▼
1. Click Heart Icon
   POST /api/wishlist/items
   {
     "productId": 5
   }
   
   Response: 200 OK
   {
     "success": true,
     "data": {
       "id": 22,
       "productId": 5,
       "userId": 1
     }
   }

2. Activity Logging (Automatic ✅)
   POST /api/activity/log
   {
     "pageName": "Product Details",
     "actionName": "ADD_TO_WISHLIST",
     "additionalData": {"productId": 5}
   }

3. Update Wishlist Badge (Automatic)
   Wishlist count updates from 5 → 6
```

### Example 3: Complete Order & Payment Flow

```
USER ACTION: Proceed to Checkout
     │
     ▼
1. Get Cart Items
   GET /api/cart
   
   Response:
   {
     "success": true,
     "data": [
       {
         "id": 15,
         "productName": "Red Silk Saree",
         "quantity": 2,
         "price": 2500,
         "total": 5000
       }
     ]
   }

2. Get Addresses
   GET /api/addresses
   
   Response:
   {
     "success": true,
     "data": [
       {
         "id": 1,
         "fullName": "John Doe",
         "address": "123 Main Street",
         "city": "New York",
         "state": "NY",
         "pinCode": "10001",
         "isDefault": true
       }
     ]
   }

3. Create Order
   POST /api/orders
   {
     "addressId": 1,
     "paymentMethod": "CARD",
     "cartItems": [
       {
         "productId": 1,
         "quantity": 2,
         "size": "One Size",
         "color": "Red"
       }
     ]
   }
   
   Response: 201 CREATED
   {
     "success": true,
     "data": {
       "id": 50,
       "orderNumber": "ORD-20260311-001",
       "totalAmount": 5000,
       "status": "PENDING",
       "paymentStatus": "PENDING",
       "createdAt": "2026-03-11T14:30:00Z"
     }
   }

4. Activity Logging (Automatic ✅)
   POST /api/activity/log
   {
     "pageName": "Payment Page",
     "actionName": "PLACE_ORDER",
     "additionalData": {
       "orderId": 50,
       "totalAmount": 5000,
       "itemCount": 1
     }
   }

5. Send Order Confirmation Email ✅
   EmailService.sendOrderConfirmationEmail(
     "john@example.com",
     "ORD-20260311-001",
     "John Doe",
     5000
   )
   
   Email Sent:
   From: noreply@trendaura.com
   To: john@example.com
   Subject: Order Confirmed! 📦 Order #ORD-20260311-001
   
   HTML Template with:
   - Order number
   - Order total
   - Tracking link
   - Return policy

6. Order Shipped (Admin Action)
   PUT /api/orders/50/status
   {
     "status": "SHIPPED",
     "trackingNumber": "TRK-10928374652"
   }
   
   Backend automatically:
   - Updates order status
   - Sends shipping notification email ✅

7. Email Notification
   From: noreply@trendaura.com
   To: john@example.com
   Subject: Your Order Has Shipped! 🚚
```

### Example 4: Q&A Chat & Activity Tracking Flow

```
USER ACTION: Open Q&A Page
     │
     ▼
1. Page Load
   POST /api/activity/log
   {
     "pageName": "Q&A Page",
     "actionName": "PAGE_VIEW",
     "additionalData": {"referrer": "index.html"}
   }

2. Filter by Category
   Activity Log (Automatic ✅)
   POST /api/activity/log
   {
     "pageName": "Q&A Page",
     "actionName": "FILTER_BY_CATEGORY",
     "additionalData": {"category": "shipping"}
   }

3. Send Chat Message
   User types: "How long does shipping take?"
   
   Activity Log (Automatic ✅)
   POST /api/activity/log
   {
     "pageName": "Q&A Page",
     "actionName": "SEND_CHAT_MESSAGE",
     "additionalData": {
       "messageLength": 32,
       "category": "shipping"
     }
   }

4. Rate Answer
   User clicks "👍 Helpful"
   
   Activity Log (Automatic ✅)
   POST /api/activity/log
   {
     "pageName": "Q&A Page",
     "actionName": "RATE_ANSWER",
     "additionalData": {
       "helpful": true,
       "question": "How long does shipping take?"
     }
   }
```

---

## 📊 Database Schema & Activity Tracking

### User Activity Logs Table (Core Tracking ✅)

```sql
CREATE TABLE user_activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,                          -- NULL for anonymous users
    page_name VARCHAR(255) NOT NULL,         -- "Collections Page", "Q&A Page", etc.
    action_name VARCHAR(255) NOT NULL,       -- "PAGE_VIEW", "ADD_TO_CART", "SEND_MESSAGE"
    event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    device_info JSON,                        -- {"userAgent": "Mozilla/5.0...", "resolution": "1920x1080"}
    ip_address VARCHAR(45),                  -- IPv4 or IPv6
    session_id VARCHAR(255),                 -- Unique per browser session
    additional_data JSON,                    -- Custom data per action
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_time (event_time),
    INDEX idx_page (page_name),
    INDEX idx_action (action_name)
);
```

### Example Activity Records

```
Activity 1:
{
  id: 1001,
  user_id: NULL,
  page_name: "Home Page",
  action_name: "PAGE_VIEW",
  event_time: "2026-03-11 14:05:23",
  device_info: {"userAgent": "Mozilla/5.0...", "resolution": "1920x1080"},
  ip_address: "192.168.1.100",
  session_id: "session_1705316323000_abc123",
  additional_data: {"referrer": "direct"}
}

Activity 2:
{
  id: 1002,
  user_id: 1,
  page_name: "Collections Page",
  action_name: "FILTER_BY_CATEGORY",
  event_time: "2026-03-11 14:06:45",
  device_info: {"userAgent": "Mozilla/5.0...", "resolution": "1920x1080"},
  ip_address: "192.168.1.100",
  session_id: "session_1705316323000_abc123",
  additional_data: {"category": "WOMEN", "type": "SAREE"}
}

Activity 3:
{
  id: 1003,
  user_id: 1,
  page_name: "Product Details",
  action_name: "ADD_TO_CART",
  event_time: "2026-03-11 14:07:12",
  device_info: {"userAgent": "Mozilla/5.0...", "resolution": "1920x1080"},
  ip_address: "192.168.1.100",
  session_id: "session_1705316323000_abc123",
  additional_data: {"productId": 5, "quantity": 2, "color": "Red"}
}

Activity 4:
{
  id: 1004,
  user_id: 1,
  page_name: "Q&A Page",
  action_name: "SEND_CHAT_MESSAGE",
  event_time: "2026-03-11 14:08:30",
  device_info: {"userAgent": "Mozilla/5.0...", "resolution": "1920x1080"},
  ip_address: "192.168.1.100",
  session_id: "session_1705316323000_abc123",
  additional_data: {"messageLength": 32, "category": "shipping"}
}
```

---

## 🎯 All Tracked Actions

### Page Views
- `PAGE_VIEW`: User visits any page
  - Data: referrer, entry time

### Product Actions
- `VIEW_PRODUCT`: User opens product details
  - Data: productId, category, type
- `FILTER_BY_CATEGORY`: User filters products
  - Data: category, type, priceRange
- `SEARCH_PRODUCTS`: User searches
  - Data: searchQuery, resultsCount
- `PRODUCT_CLICK`: User clicks product link
  - Data: productId, productName, source

### Cart Actions
- `ADD_TO_CART`: User adds item to cart
  - Data: productId, quantity, size, color
- `REMOVE_FROM_CART`: User removes from cart
  - Data: productId, quantity
- `UPDATE_CART_QUANTITY`: User changes quantity
  - Data: productId, oldQuantity, newQuantity
- `VIEW_CART`: User opens cart page
  - Data: itemCount, totalValue

### Wishlist Actions
- `ADD_TO_WISHLIST`: User add to wishlist
  - Data: productId, productName
- `REMOVE_FROM_WISHLIST`: User removes from wishlist
  - Data: productId
- `MOVE_TO_CART`: User moves wishlist item to cart
  - Data: productId, quantity

### Order Actions
- `PLACE_ORDER`: User creates order
  - Data: orderId, totalAmount, itemCount, addressId
- `VIEW_ORDER_DETAILS`: User views order
  - Data: orderId, orderValue
- `CANCEL_ORDER`: User cancels order
  - Data: orderId, reason

### Q&A Chat Actions
- `SEND_CHAT_MESSAGE`: User sends chat message
  - Data: messageLength, category, foundAnswer
- `RATE_ANSWER`: User rates answer
  - Data: helpful (true/false), question
- `FILTER_BY_CATEGORY`: User filters Q&A by category
  - Data: category
- `START_CHAT`: User starts chat session
  - Data: sessionId

### Contact Actions
- `SUBMIT_CONTACT_FORM`: User submits contact
  - Data: email, subject, messageLength

### Authentication Actions
- `USER_REGISTER`: User creates account
  - Data: email, registrationMethod
- `USER_LOGIN`: User logs in
  - Data: email, loginMethod, ipAddress
- `USER_LOGOUT`: User logs out
  - Data: sessionDuration

---

## 🔐 Authentication Flow

```
Frontend (Browser)              Backend (Spring Boot)              Database
    │                                   │                            │
    │  1. Register                      │                            │
    │  POST /auth/register              │                            │
    │  { name, email, password }        │                            │
    ├──────────────────────────────────▶│                            │
    │                                   │  2. Hash password          │
    │                                   │  BCrypt.encode()           │
    │                                   │                            │
    │                                   │  3. Create user entity     │
    │                                   ├───────────────────────────▶│
    │                                   │  INSERT INTO users         │
    │                                   │◀───────────────────────────┤
    │  4. JWT Token                     │                            │
    │  { token, user }                  │  5. Generate JWT           │
    │◀──────────────────────────────────┤  JJWT.signWith()           │
    │                                   │                            │
    │  6. Store JWT in localStorage     │                            │
    │  localStorage.setItem('token')    │                            │
    │                                   │                            │
    ├─ Subsequent Requests ──────────────────────────────────────────┤
    │                                   │                            │
    │  GET /api/products                │                            │
    │  Header: Authorization: Bearer <JWT> │                         │
    ├──────────────────────────────────▶│                            │
    │                                   │  7. Verify JWT             │
    │                                   │  Extract user from token   │
    │                                   │                            │
    │                                   │  8. Check Authorization    │
    │                                   │  Compare userId in token   │
    │                                   │                            │
    │  Response: 200 + Products         │  9. Execute Request       │
    │◀──────────────────────────────────┤  WITH USER CONTEXT         │
    │                                   │                            │
```

---

## 📈 Analytics & Reporting

### Popular Pages (Last 7 Days)
```
GET /api/activity/analytics/most-visited-pages?days=7

Response:
{
  "data": [
    {
      "pageName": "Collections Page",
      "visitCount": 1250,
      "uniqueUsers": 845,
      "avgTimeSpent": 180,  // seconds
      "bounceRate": 0.12
    },
    {
      "pageName": "Product Details",
      "visitCount": 2100,
      "uniqueUsers": 1200,
      "avgTimeSpent": 240,
      "bounceRate": 0.08
    },
    {
      "pageName": "Q&A Page",
      "visitCount": 450,
      "uniqueUsers": 380,
      "avgTimeSpent": 120,
      "bounceRate": 0.05
    }
  ]
}
```

### Common User Actions
```
GET /api/activity/analytics/most-common-actions?days=7

Response:
{
  "data": [
    {
      "actionName": "PAGE_VIEW",
      "count": 8500
    },
    {
      "actionName": "PRODUCT_CLICK",
      "count": 3200
    },
    {
      "actionName": "ADD_TO_CART",
      "count": 1800
    },
    {
      "actionName": "ADD_TO_WISHLIST",
      "count": 950
    },
    {
      "actionName": "FILTER_BY_CATEGORY",
      "count": 2100
    }
  ]
}
```

---

## 🚀 Key Features Implemented

✅ **Complete REST API** (12+ endpoints)
✅ **Activity Tracking** (Automatic on every user action)
✅ **Email Notifications** (Register, Orders, Password Reset)
✅ **Image Management** (Cloudinary integration)
✅ **JWT Authentication** (Secure token-based auth)
✅ **Database Schema** (14 tables, optimized with indexes)
✅ **Frontend Integration** (api-client.js with full API wrapper)
✅ **Q&A Chat Integration** (Activity tracking on messages, ratings)
✅ **Postman Collection** (40+ test requests ready)
✅ **Error Handling** (Graceful errors, non-blocking activity logs)
✅ **CORS Support** (Multi-origin frontend access)
✅ **Performance Optimization** (Database indexes, connection pooling)

---

**Last Updated:** March 11, 2026  
**Version:** 1.0.0 Complete  
**Status:** Production Ready ✅
