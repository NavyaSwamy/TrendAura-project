# ⚡ QUICK REFERENCE - Add Tracking in 5 Minutes

## 🚀 Super Quick Start

### 1. Add to Every Page's `<head>` (Copy-Paste):
```html
<script src="js/page-tracker.js"></script>
```

### 2. Add to Page's `<script>` Section:

**For Collections Page (Add to End):**
```javascript
// Track product interactions
document.querySelectorAll('.product-card').forEach(card => {
    card.addEventListener('click', () => {
        tracker.trackProductView(
            card.dataset.productId,
            card.querySelector('.product-name').textContent,
            'COLLECTIONS',
            card.dataset.price
        );
    });
});

// Track add to cart
document.getElementById('addToCartBtn')?.addEventListener('click', async function() {
    await tracker.trackAddToCart(productId, productName, quantity, price, size, color);
});
```

**For Cart Page (Add to End):**
```javascript
// Track checkout
document.getElementById('checkoutBtn').addEventListener('click', async function() {
    await tracker.trackCheckout(cartTotal, itemCount);
});
```

**For Wishlist Page (Add to End):**
```javascript
// Track wishlist to cart
document.querySelectorAll('.move-to-cart-btn').forEach(btn => {
    btn.addEventListener('click', async function() {
        await tracker.trackWishlistToCart(productId, productName, price);
    });
});
```

**For Login Page (Add to End):**
```javascript
// Track login
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    await tracker.trackLogin(email);
    // ... your login logic
});
```

**For Search (Add to End):**
```javascript
// Track search
document.getElementById('searchBtn').addEventListener('click', async function() {
    const query = document.getElementById('searchInput').value;
    await tracker.trackSearch(query, resultsCount);
});
```

---

## 📋 Tracker Methods Reference

```javascript
// Navigation
tracker.trackPageView(pageName)
tracker.trackClick(elementName, elementType)
tracker.trackScroll(scrollPercentage)

// Products
tracker.trackProductView(productId, productName, category, price)
tracker.trackSearch(searchQuery, resultsCount)
tracker.trackFilter(filterType, filterValue)
tracker.trackSort(sortType)

// Shopping
tracker.trackAddToCart(productId, productName, quantity, price, size, color)
tracker.trackRemoveFromCart(productId, productName, quantity)
tracker.trackCheckout(cartTotal, itemCount)
tracker.trackOrderPlaced(orderId, orderTotal, itemCount)

// Wishlist
tracker.trackAddToWishlist(productId, productName, price)
tracker.trackRemoveFromWishlist(productId, productName)
tracker.trackWishlistToCart(productId, productName, price)

// User
tracker.trackLogin(email)
tracker.trackLogout()

// Reviews
tracker.trackRating(productId, productName, rating, reviewText)
```

---

## ✅ Pages to Update (Priority Order)

```
Priority 1 (Must Do):
☐ collections.html
☐ product-details.html
☐ cart.html
☐ login.html
☐ payment.html

Priority 2 (Should Do):
☐ wishlist1.html
☐ search-enhanced.html
☐ index.html
☐ profile.html
☐ contact.html

Priority 3 (Nice to Have):
☐ about.html
☐ address.html
☐ order-details.html
☐ confirmation.html
```

---

## 🔍 Verify It Works

**In Browser Console (F12):**
```javascript
console.log('Session:', tracker.sessionId);  // Should show session ID

// Manually test a track
await tracker.trackProductView(1, 'Test Product', 'WOMEN', 500);
```

**Check Network Tab (F12):**
- Should see POST request to `http://localhost:8080/api/activity/log`
- Status should be "200 OK"

**Check Database:**
```bash
mysql -u root -p trendaura -e "SELECT COUNT(*) FROM user_activity_logs;"
```

---

## 📊 What Gets Stored

Every interaction creates a record with:
- **What**: page name + action name (e.g., "COLLECTIONS" + "ADD_TO_CART")
- **Who**: user ID (if logged in) or anonymous
- **When**: timestamp
- **Where**: page name, IP address
- **Extra**: product ID, price, quantity, category, etc. (in JSON)

---

## 🎯 Real Example - Complete collections.html Integration

```html
<!DOCTYPE html>
<html>
<head>
    <!-- ... other stuff ... -->
    <script src="js/page-tracker.js"></script>  <!-- ← ADD THIS -->
</head>
<body>
    <!-- ... your HTML ... -->
    
    <script>
        // Your existing code here...
        
        // ========== ADD THIS TRACKING CODE AT THE END ==========
        
        // When page loads, you get PAGE_VIEW automatically ✅
        // You don't need to do anything for that!
        
        // When user clicks a product card
        document.querySelectorAll('.product-card').forEach((card, index) => {
            card.addEventListener('click', async () => {
                const productName = card.querySelector('.product-name')?.textContent || 'Unknown';
                const price = card.dataset.price || 0;
                const productId = card.dataset.productId || index;
                
                console.log('Tracking: View product -', productName);
                await tracker.trackProductView(productId, productName, 'COLLECTIONS', price);
            });
        });
        
        // When user adds to cart
        document.querySelectorAll('.add-to-cart-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                e.preventDefault();
                
                const card = btn.closest('.product-card');
                const productId = card.dataset.productId;
                const productName = card.querySelector('.product-name').textContent;
                const price = parseFloat(card.dataset.price);
                const quantity = parseInt(document.querySelector('.quantity-input')?.value || 1);
                const size = document.querySelector('.size-select')?.value || 'M';
                const color = document.querySelector('.color-select')?.value || 'Default';
                
                console.log('Tracking: Add to cart -', productName);
                await tracker.trackAddToCart(productId, productName, quantity, price, size, color);
                
                // Your add to cart logic here
            });
        });
        
        // When user filters
        document.querySelectorAll('.filter-option').forEach(filter => {
            filter.addEventListener('change', async (e) => {
                const filterType = e.target.name;
                const filterValue = e.target.value;
                
                console.log('Tracking: Filter -', filterType, filterValue);
                await tracker.trackFilter(filterType, filterValue);
                
                // Your filter logic here
            });
        });
    </script>
</body>
</html>
```

---

## 🚨 Common Issues & Fixes

**Issue: "tracker is not defined"**
- Solution: Make sure `<script src="js/page-tracker.js"></script>` is BEFORE your code

**Issue: "POST 404 on /api/activity/log"**
- Solution: Make sure backend is running (`mvn spring-boot:run`)

**Issue: Activities showing as "null" user**
- Solution: That's normal for non-logged-in users. Tracks them by session ID.

**Issue: No POST requests in Network tab**
- Solution: Check browser console for errors. Most common: wrong path to js/page-tracker.js

---

## 📊 What Data You'll Have After 1 Week

```
User Behaviors:
├─ 1000+ page views
├─ 500+ product views
├─ 200+ add to cart
├─ 100+ add to wishlist
├─ 50+ purchases
└─ Hundreds of clicks, scrolls, searches

This data lets you:
✓ See which products are popular
✓ Understand user journey: View → Cart → Buy
✓ Find where people drop off
✓ Segment users by behavior
✓ Train ML models for recommendations
```

---

## 🤖 Export Data for ML (After 1 Week)

```bash
# Run these Python commands to export all data:
python3 << 'EOF'
import pandas as pd
import mysql.connector

# Connect to DB
conn = mysql.connector.connect(
    host="localhost", user="root", password="your_password", database="trendaura"
)

# Export all activities
df = pd.read_sql(
    "SELECT * FROM user_activity_logs ORDER BY event_time DESC",
    conn
)

df.to_csv('user_activities.csv', index=False)
print(f"Exported {len(df)} records to user_activities.csv")

conn.close()
EOF
```

---

## 🎓 Next: Building Recommendations

With 1 week of data, train a model:

```python
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

# Load activities
df = pd.read_csv('user_activities.csv')

# Extract product views per user
user_products = df[df['action_name'] == 'VIEW_PRODUCT'].groupby('user_id')['productId'].apply(list)

# Simple collaborative filtering
for user_id, products in user_products.items():
    similar_users = find_similar_users(user_id, user_products)  # Your logic
    recommend = get_products_liked_by_similar_users(similar_users)
    print(f"Recommend to user {user_id}: {recommend}")
```

---

## 📞 Quick Help

**How to add same code to 5 different pages?**
- Copy the JavaScript tracking code
- Paste to each page's `<script>` section
- Modify `elementId` and `className` names to match each page

**Can I test without real user data?**
- Yes! Manually click through your site in a browser
- DevTools will show POST requests
- Database will populate with test data

**How long to see results?**
- Instant: Backend logs immediately
- 1 hour: See activity in database queries
- 1 day: See patterns emerge
- 1 week: Enough data to train ML model

---

## 🎯 Success Criteria

✅ All pages have `<script src="js/page-tracker.js"></script>`
✅ All pages have page-specific tracking code
✅ Browser console shows no JavaScript errors
✅ Network tab shows POST requests to `/api/activity/log`
✅ Database has records: `SELECT COUNT(*) FROM user_activity_logs`
✅ You can see activity reports in database

**When you see this, you're ready for ML/DL! 🚀**

---

**Made with ❤️ for TrendAura Analytics**
