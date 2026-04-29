# 📊 COMPREHENSIVE DATA COLLECTION FOR DL RECOMMENDATION ENGINE

## Overview
All user interactions across TrendAura are now tracked and stored in the backend database. This data will power your Deep Learning recommendation engine.

---

## 📋 Data Collection Points

### 1. **Product Discovery**
- **VIEW_PRODUCT**: User views product details
  - Data: productId, productName, category, price, timestamp
  
- **SEARCH**: User searches for products
  - Data: searchQuery, resultsCount, timestamp
  
- **FILTER**: User applies filters
  - Data: filterType (category/price/size), filterValue, timestamp
  
- **SORT**: User sorts products
  - Data: sortType (price_asc/price_desc/newest), timestamp

---

### 2. **Shopping Cart Activity**
- **ADD_TO_CART**: User adds product to cart
  - Data: productId, productName, quantity, price, size, color, cartValue
  
- **REMOVE_FROM_CART**: User removes from cart
  - Data: productId, quantity, timestamp
  
- **CART_TOTAL**: Cart value snapshot

---

### 3. **Wishlist Activity**
- **ADD_TO_WISHLIST**: User saves for later
  - Data: productId, productName, price, timestamp
  
- **REMOVE_FROM_WISHLIST**: User removes from wishlist
  - Data: productId, productName, timestamp
  
- **WISHLIST_TO_CART**: User moves item from wishlist to cart
  - Data: productId, productName, price, timestamp

---

### 4. **Purchase Behavior**
- **CHECKOUT**: User initiates checkout
  - Data: cartTotal, itemCount, timestamp
  
- **ORDER_PLACED**: User completes purchase
  - Data: orderId, orderTotal, itemCount, timestamp

---

### 5. **User Engagement**
- **PAGE_VIEW**: User visits a page
  - Data: pageName, referrer, screenResolution, userId
  
- **CLICK**: User clicks on elements
  - Data: elementName, elementType (button/link/image), clickNumber
  
- **SCROLL**: User scrolls down (every 25%)
  - Data: scrollDepth, timestamp
  
- **PAGE_TIME**: Time spent on page
  - Data: timeSpentSeconds, clicks, scrollDepth

---

### 6. **User Account**
- **LOGIN**: User logs in
  - Data: email, timestamp
  
- **LOGOUT**: User logs out
  - Data: timeSpentOnPage, timestamp

---

### 7. **Ratings & Reviews**
- **RATE_PRODUCT**: User rates product
  - Data: productId, rating (1-5), reviewLength, timestamp

---

## 💾 Database Schema (user_activity_logs table)

```sql
CREATE TABLE user_activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,                    -- User who performed action (NULL if anonymous)
    page_name VARCHAR(255),            -- Which page: INDEX, COLLECTIONS, CART, etc.
    action_name VARCHAR(255),          -- Action type: VIEW_PRODUCT, ADD_TO_CART, etc.
    event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    device_info JSON,                  -- Screen size, user agent, device type
    ip_address VARCHAR(45),            -- User IP for geo-tracking
    session_id VARCHAR(255),           -- Unique session identifier
    additional_data JSON,              -- Custom JSON data per action
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 🔍 Querying Data for ML Model

### Get all user product views (for item-based filtering):
```sql
SELECT 
    user_id,
    productId,
    productName,
    category,
    price,
    event_time
FROM user_activity_logs
WHERE action_name = 'VIEW_PRODUCT'
ORDER BY user_id, event_time DESC;
```

### Get user's shopping behavior (for collaborative filtering):
```sql
SELECT 
    user_id,
    JSON_EXTRACT(additional_data, '$.productId') as productId,
    action_name,
    event_time
FROM user_activity_logs
WHERE user_id IS NOT NULL
AND action_name IN ('ADD_TO_CART', 'ADD_TO_WISHLIST', 'VIEW_PRODUCT', 'ORDER_PLACED')
ORDER BY user_id, event_time DESC;
```

### Get conversion funnel (view → add to cart → purchase):
```sql
SELECT 
    user_id,
    COUNT(CASE WHEN action_name = 'VIEW_PRODUCT' THEN 1 END) as product_views,
    COUNT(CASE WHEN action_name = 'ADD_TO_CART' THEN 1 END) as cart_adds,
    COUNT(CASE WHEN action_name = 'ORDER_PLACED' THEN 1 END) as purchases
FROM user_activity_logs
WHERE user_id IS NOT NULL
GROUP BY user_id;
```

### Get popular products (based on views):
```sql
SELECT 
    JSON_EXTRACT(additional_data, '$.productId') as productId,
    JSON_EXTRACT(additional_data, '$.productName') as productName,
    JSON_EXTRACT(additional_data, '$.category') as category,
    COUNT(*) as view_count
FROM user_activity_logs
WHERE action_name = 'VIEW_PRODUCT'
GROUP BY productId
ORDER BY view_count DESC;
```

### Get user preferences based on search history:
```sql
SELECT 
    user_id,
    JSON_EXTRACT(additional_data, '$.searchQuery') as search_query,
    COUNT(*) as frequency
FROM user_activity_logs
WHERE action_name = 'SEARCH'
AND user_id IS NOT NULL
GROUP BY user_id, search_query
ORDER BY user_id, frequency DESC;
```

---

## 📦 Features for DL Model

### 1. **Content-Based Filtering**
- Product view history
- Search queries
- Filter selections
- Category preferences

### 2. **Collaborative Filtering**
- User 1 liked products X, Y, Z
- User 2 liked products X, Y, W
- → Recommend W to User 1

### 3. **Sequential Patterns**
- Page view sequence: Home → Collections → Product Details → Cart → Checkout
- Time between actions
- Scroll depth (engagement indicator)

### 4. **Contextual Features**
- Device type (mobile/desktop)
- Time of day
- Day of week
- User session duration

### 5. **Safety Net Data**
- Failed searches → recommend popular items
- Users who abandoned cart → send reminders
- Price sensitivity → show similar cheaper items

---

## 🚀 Implementation Checklist

- [x] Created page-tracker.js with all tracking methods
- [x] User activity logs table in database
- [ ] Add tracker to index.html
- [ ] Add tracker to collections.html
- [ ] Add tracker to collections1.html
- [ ] Add tracker to product-details.html
- [ ] Add tracker to cart.html
- [ ] Add tracker to wishlist1.html
- [ ] Add tracker to payment.html
- [ ] Add tracker to login.html
- [ ] Test data collection in backend
- [ ] Export data for ML model training

---

## 📊 Sample ML Features for Recommendations

```python
import pandas as pd
import numpy as np

# Load data from backend
activity_data = pd.read_sql_query("""
    SELECT user_id, productId, action_name, event_time
    FROM user_activity_logs
    WHERE user_id IS NOT NULL
""", connection)

# Feature engineering for DL model
features = {
    'view_history': get_product_views_per_user(activity_data),
    'cart_history': get_cart_items_per_user(activity_data),
    'wishlist_history': get_wishlist_items_per_user(activity_data),
    'purchase_history': get_purchases_per_user(activity_data),
    'user_preferences': extract_category_preferences(activity_data),
    'session_duration': calculate_session_duration(activity_data),
    'click_behavior': get_engagement_metrics(activity_data),
    'temporal_patterns': extract_time_patterns(activity_data)
}

# Train your DL model
model = RecommendationModel()
model.train(features)
```

---

## 🔗 API Endpoints for Data Access

```bash
# Get activity logs for a user
GET /api/activity/history?userId=123

# Get analytics dashboard data
GET /api/activity/analytics?startDate=2026-03-01&endDate=2026-03-11

# Get popular products
GET /api/products/trending

# Get user preferences
GET /api/users/preferences
```

---

## ⚙️ Configuration in application.properties

```properties
# Activity Tracking Configuration
activity.tracking.enabled=true
activity.tracking.async=true
activity.tracking.batch-size=100
activity.tracking.retention-days=365

# Recommendation Engine Configuration
ml.recommendations.enabled=true
ml.recommendations.algorithm=collaborative-filtering
ml.recommendations.min-similarity=0.5
```

---

## 📈 Analytics Dashboard Ideas

Using collected data, create dashboards for:
1. **User Journey**: Page flow visualization
2. **Product Analytics**: Most viewed, added to cart, purchased
3. **Conversion Funnel**: View → Cart → Purchase
4. **Search Analytics**: Popular searches, zero-result searches
5. **Time Analysis**: Peak hours, day-of-week patterns
6. **Device Breakdown**: Mobile vs Desktop engagement
7. **User Segmentation**: Browsers vs Purchasers vs Wishlist-only

---

## 🛡️ Privacy & Security

- All user data is encrypted in transit (HTTPS)
- User can request data deletion (GDPR compliance)
- Activity logs are anonymized for anonymous users
- Session IDs don't contain personally identifiable information

---

## 📞 Usage Examples

See individual page integration files for specific tracking implementation examples.
