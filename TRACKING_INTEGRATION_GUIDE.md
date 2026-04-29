# 🚀 QUICK INTEGRATION GUIDE - Add Tracking to All Pages

## Step 1: Add Tracker Script to Every Page

Add this line to the `<head>` section of EVERY HTML page (after other scripts):

```html
<script src="js/page-tracker.js"></script>
```

---

## Step 2: Add Tracking Events to Page-Specific JavaScript

---

### 📄 **index.html** - Home Page

In the `<script>` section, add these tracking calls:

```javascript
// When user clicks on products/cards
document.querySelectorAll('.dress-card').forEach(card => {
    card.addEventListener('click', async function() {
        const productName = this.querySelector('.dress-name').textContent;
        const category = this.closest('.state-section').querySelector('.state-header').textContent;
        await tracker.trackProductView(
            Math.random() * 1000,  // Generate a fake product ID (use real ID from API)
            productName,
            category,
            Math.random() * 10000
        );
    });
});

// When user clicks search
document.getElementById('searchTrigger').addEventListener('click', async function() {
    const query = document.getElementById('fashionSelect').value;
    if (query) {
        await tracker.trackSearch(query, 1);
    }
});

// When user clicks on wishlist link
document.querySelector('a[href="wishlist1.html"]').addEventListener('click', async function() {
    await tracker.trackClick('Wishlist Link', 'navigation');
});

// When user clicks on cart link
document.querySelector('a[href="cart.html"]').addEventListener('click', async function() {
    await tracker.trackClick('Cart Link', 'navigation');
});
```

---

### 🛍️ **collections.html** or **collections1.html** - Product Listing Page

In the `<script>` section:

```javascript
// Track when user filters products
function filterProducts(category) {
    tracker.trackFilter('category', category);
    // ... rest of filtering logic
}

// Track when user clicks on a product
function viewProductDetails(productId, productName, price) {
    tracker.trackProductView(productId, productName, 'COLLECTIONS', price);
}

// Track when user sorts products
function sortProducts(sortType) {
    tracker.trackSort(sortType);
    // ... rest of sorting logic
}

// Track when user adds to cart from collection
async function addProductToCart(productId, productName, price, quantity = 1) {
    await tracker.trackAddToCart(productId, productName, quantity, price, 'M', 'Default');
    // ... rest of add to cart logic
}

// Track when user adds to wishlist from collection
async function addProductToWishlist(productId, productName, price) {
    await tracker.trackAddToWishlist(productId, productName, price);
    // ... rest of add to wishlist logic
}
```

---

### 📦 **product-details.html** - Single Product Page

In the `<script>` section:

```javascript
// Automatically track product view on page load
document.addEventListener('DOMContentLoaded', async function() {
    const productId = getProductIdFromURL(); // Get from URL or page data
    const productName = document.querySelector('.product-name').textContent;
    const price = parseFloat(document.querySelector('.product-price').textContent);
    const category = document.querySelector('.product-category').textContent;
    
    await tracker.trackProductView(productId, productName, category, price);
});

// Track add to cart
document.getElementById('addToCartBtn').addEventListener('click', async function() {
    const productId = getProductIdFromURL();
    const productName = document.querySelector('.product-name').textContent;
    const price = parseFloat(document.querySelector('.product-price').textContent);
    const quantity = parseInt(document.getElementById('quantityInput').value);
    const size = document.getElementById('sizeSelect').value;
    const color = document.getElementById('colorSelect').value;
    
    await tracker.trackAddToCart(productId, productName, quantity, price, size, color);
    // ... rest of add to cart logic
});

// Track add to wishlist
document.getElementById('addToWishlistBtn').addEventListener('click', async function() {
    const productId = getProductIdFromURL();
    const productName = document.querySelector('.product-name').textContent;
    const price = parseFloat(document.querySelector('.product-price').textContent);
    
    await tracker.trackAddToWishlist(productId, productName, price);
    // ... rest of add to wishlist logic
});

// Track rating
document.querySelectorAll('.rating-btn').forEach(btn => {
    btn.addEventListener('click', async function() {
        const rating = this.dataset.rating;
        const review = document.getElementById('reviewText').value;
        const productId = getProductIdFromURL();
        const productName = document.querySelector('.product-name').textContent;
        
        await tracker.trackRating(productId, productName, rating, review);
    });
});
```

---

### 🛒 **cart.html** - Shopping Cart Page

In the `<script>` section:

```javascript
// Track when quantity changes
function updateQuantity(itemId, newQuantity) {
    // Track cart update
    const itemName = document.querySelector(`[data-product-id="${itemId}"] .item-name`).textContent;
    const itemPrice = parseFloat(document.querySelector(`[data-product-id="${itemId}"] .item-price`).textContent);
    
    // Only track if adding
    if (newQuantity > getCurrentQuantity(itemId)) {
        tracker.trackAddToCart(itemId, itemName, newQuantity - getCurrentQuantity(itemId), itemPrice, 'N/A', 'N/A');
    }
    // ... update logic
}

// Track when removing from cart
function removeFromCart(itemId) {
    const itemName = document.querySelector(`[data-product-id="${itemId}"] .item-name`).textContent;
    const quantity = parseInt(document.querySelector(`[data-product-id="${itemId}"] .quantity`).value);
    
    tracker.trackRemoveFromCart(itemId, itemName, quantity);
    // ... remove logic
}

// Track checkout initiation
document.getElementById('checkoutBtn').addEventListener('click', async function() {
    const cartTotal = calculateCartTotal();
    const itemCount = document.querySelectorAll('.cart-item').length;
    
    await tracker.trackCheckout(cartTotal, itemCount);
    // ... checkout logic
});
```

---

### ❤️ **wishlist1.html** - Wishlist Page

In the `<script>` section:

```javascript
// Track when clicking to add wishlist item to cart
async function moveToCart(productId, productName, price) {
    await tracker.trackWishlistToCart(productId, productName, price);
    // ... add to cart logic
}

// Track when removing from wishlist
async function removeFromWishlist(productId, productName) {
    await tracker.trackRemoveFromWishlist(productId, productName);
    // ... remove logic
}

// Track wishlist item click
document.querySelectorAll('.wishlist-item').forEach(item => {
    item.addEventListener('click', async function() {
        const productId = this.dataset.productId;
        const productName = this.querySelector('.item-name').textContent;
        const category = this.dataset.category;
        const price = this.dataset.price;
        
        await tracker.trackProductView(productId, productName, category, price);
    });
});
```

---

### 💳 **payment.html** / **checkout.html** - Payment Page

In the `<script>` section:

```javascript
// Track place order
document.getElementById('placeOrderBtn').addEventListener('click', async function() {
    const orderId = generateOrderId(); // Your order ID generation
    const orderTotal = calculateTotal();
    const itemCount = getCartItemCount();
    
    await tracker.trackOrderPlaced(orderId, orderTotal, itemCount);
    // ... order logic
});
```

---

### 🔐 **login.html** - Login Page

In the `<script>` section:

```javascript
// Track login
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const email = document.getElementById('emailInput').value;
    
    await tracker.trackLogin(email);
    // ... login logic
});

// Track logout (if logout button exists)
document.getElementById('logoutBtn')?.addEventListener('click', async function() {
    await tracker.trackLogout();
    // ... logout logic
});
```

---

### 🔍 **search-enhanced.html** - Search Results Page

In the `<script>` section:

```javascript
// Track search on page load
document.addEventListener('DOMContentLoaded', async function() {
    const searchQuery = getURLParameter('q');
    const resultsCount = document.querySelectorAll('.search-result').length;
    
    if (searchQuery) {
        await tracker.trackSearch(searchQuery, resultsCount);
    }
});

// Track filter on search results
document.querySelectorAll('.filter-option').forEach(filter => {
    filter.addEventListener('change', async function() {
        await tracker.trackFilter(this.name, this.value);
    });
});
```

---

### 📞 **contact.html** - Contact Page

In the `<script>` section:

```javascript
// Track contact form submission
document.getElementById('contactForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    await tracker.trackClick('Contact Form', 'form_submission');
    // ... form submission logic
});
```

---

### 📱 **Q&A.html** - Already Integrated ✅

This page already has tracking integrated! Keep it as is.

---

## Complete Example: Add to collections.html

Here's a FULL working example for **collections.html**:

```html
<!DOCTYPE html>
<html>
<head>
    <!-- ... other head content ... -->
    <script src="js/page-tracker.js"></script> <!-- ADD THIS LINE -->
</head>
<body>
    <!-- ... page content ... -->
    
    <script>
        // Example: When user clicks a filter
        document.querySelectorAll('.filter-btn').forEach(btn => {
            btn.addEventListener('click', async function() {
                const filterValue = this.dataset.filter;
                await tracker.trackFilter('category', filterValue);
                console.log('Tracked filter:', filterValue);
            });
        });

        // Example: When user clicks a product
        document.querySelectorAll('.product-card').forEach(card => {
            card.addEventListener('click', async function() {
                const productId = this.dataset.productId;
                const productName = this.querySelector('.product-name').textContent;
                const price = this.dataset.price;
                await tracker.trackProductView(productId, productName, 'COLLECTIONS', price);
                console.log('Tracked product view:', productName);
            });
        });

        // Example: When user adds to cart
        document.querySelectorAll('.add-to-cart-btn').forEach(btn => {
            btn.addEventListener('click', async function(e) {
                e.preventDefault();
                const productId = this.dataset.productId;
                const productName = this.parentElement.querySelector('.product-name').textContent;
                const price = this.parentElement.dataset.price;
                await tracker.trackAddToCart(productId, productName, 1, price, 'M', 'Default');
                console.log('Tracked add to cart:', productName);
            });
        });
    </script>
</body>
</html>
```

---

## Verification Checklist

After adding tracking to each page, verify:

```bash
# 1. Check console for no JavaScript errors
# 2. Open browser DevTools (F12)
# 3. Watch Network tab for POST requests to http://localhost:8080/api/activity/log
# 4. Verify database has activity logs:

mysql -u root -p trendaura << EOF
SELECT COUNT(*) as total_logs FROM user_activity_logs;
SELECT action_name, COUNT(*) as count FROM user_activity_logs GROUP BY action_name;
EOF

# 5. Expected page views:
# - HOME (from index.html)
# - COLLECTIONS or COLLECTIONS1 (from collections pages)
# - PRODUCT-DETAILS (from product detail pages)
# - CART, WISHLIST1, LOGIN, PAYMENT, etc.
```

---

## Testing Your Implementation

After adding tracking to a page, visit it and check:

1. **Browser Console** (F12): Should see activity logs being sent
2. **Network Tab** (F12): POST requests to `/api/activity/log` 
3. **Database**: New records in `user_activity_logs` table

```bash
# Real-time monitoring
watch -n 2 'mysql -u root -p trendaura -e "SELECT * FROM user_activity_logs ORDER BY id DESC LIMIT 5;"'
```

---

## Summary

- ✅ Created `js/page-tracker.js` with 20+ tracking methods
- ✅ Add `<script src="js/page-tracker.js"></script>` to ALL pages
- ✅ Add tracker calls to each page's JavaScript
- ✅ All data automatically sent to backend
- ✅ Ready for ML model training on user behavior

**Next Step:** Copy these code snippets to each page and test!
