# 🔌 FRONTEND API INTEGRATION GUIDE

## 📋 Quick Integration Checklist

- [x] **api-client.js** - Complete API wrapper created ✅
- [x] **Q&A.html** - Activity tracking integrated ✅
- [ ] **index.html** - TODO: Add activity tracking
- [ ] **collections.html** - TODO: Add product APIs + tracking
- [ ] **product-details.html** - TODO: Add product API + cart/wishlist
- [ ] **cart.html** - TODO: Add cart APIs + checkout
- [ ] **wishlist1.html** - TODO: Add wishlist APIs
- [ ] **login.html** - TODO: Add auth APIs
- [ ] **contact.html** - TODO: Add contact form API
- [ ] **payment.html** - TODO: Add order creation API

---

## 🚀 How to Integrate api-client.js

### Step 1: Include Script in HTML

Add to the `<head>` section of each HTML file:

```html
<!-- API Client (Must be included before any page-specific scripts) -->
<script src="js/api-client.js"></script>
<script>
  // Now you can use API object globally
  console.log('API client loaded:', typeof API);
</script>
```

### Step 2: Use API in Your JavaScript

```javascript
// API is globally available
await API.products.getAll();
await API.cart.add(productId, quantity);
await API.activity.log({ pageName: 'Home', actionName: 'PAGE_VIEW' });
```

---

## 📄 Page Integration Examples

### ✅ Q&A.html (DONE)

```javascript
// ✅ PAGE VIEW - Automatic
document.addEventListener('DOMContentLoaded', () => {
  API.activity.log({
    pageName: 'Q&A Page',
    actionName: 'PAGE_VIEW',
    additionalData: JSON.stringify({ referrer: document.referrer })
  }).catch(err => console.log('Activity log skipped'));
});

// ✅ CHAT MESSAGE - Automatic
async function sendMessage() {
  const userMessage = chatInput.value.trim();
  
  // ... existing chat logic ...
  
  // Log to backend
  API.activity.log({
    pageName: 'Q&A Page',
    actionName: 'SEND_CHAT_MESSAGE',
    additionalData: JSON.stringify({ 
      messageLength: userMessage.length, 
      category: currentCategory 
    })
  }).catch(err => console.log('Activity log skipped'));
}

// ✅ FILTER CATEGORY - Automatic
function filterByCategory(category) {
  currentCategory = category;
  
  // Log filter action
  API.activity.log({
    pageName: 'Q&A Page',
    actionName: 'FILTER_BY_CATEGORY',
    additionalData: JSON.stringify({ category })
  }).catch(err => console.log('Activity log skipped'));
}

// ✅ RATE ANSWER - Automatic
helpfulBtn.onclick = () => {
  helpfulBtn.classList.add('active');
  
  // Log rating
  API.activity.log({
    pageName: 'Q&A Page',
    actionName: 'RATE_ANSWER',
    additionalData: JSON.stringify({ 
      helpful: true, 
      question: qa.q 
    })
  }).catch(err => console.log('Activity log skipped'));
};
```

---

### 📝 collections.html (TODO)

```html
<!DOCTYPE html>
<html>
<head>
  <script src="js/api-client.js"></script>
  <script>
    let currentPage = 1;
    let currentFilters = {};
    let allProducts = [];

    // ✅ PAGE VIEW
    document.addEventListener('DOMContentLoaded', async () => {
      // Log page visit
      API.activity.log({
        pageName: 'Collections Page',
        actionName: 'PAGE_VIEW',
        additionalData: JSON.stringify({ referrer: document.referrer })
      }).catch(err => console.log('Activity log skipped (non-critical)'));

      // Load initial products
      await loadProducts();
      
      // Update cart/wishlist badges
      updateBadges();
    });

    // ✅ LOAD PRODUCTS WITH FILTERS
    async function loadProducts(filters = {}) {
      try {
        // Log filter action (if any filters applied)
        if (Object.keys(filters).length > 0) {
          API.activity.log({
            pageName: 'Collections Page',
            actionName: 'FILTER_BY_CATEGORY',
            additionalData: JSON.stringify(filters)
          }).catch(err => console.log('Activity log skipped'));
        }

        // Get products from API
        const response = await API.products.getAll({
          category: filters.category || undefined,
          type: filters.type || undefined,
          state: filters.state || undefined,
          minPrice: filters.minPrice || undefined,
          maxPrice: filters.maxPrice || undefined,
          page: currentPage,
          size: 20
        });

        allProducts = response.data;
        displayProducts(allProducts);
        
        // Update pagination UI
        updatePagination(response.page, response.totalPages);
      } catch (error) {
        console.error('Failed to load products:', error);
        alert('Failed to load products. Please try again.');
      }
    }

    // Display products in DOM
    function displayProducts(products) {
      const container = document.getElementById('products-container');
      container.innerHTML = '';

      products.forEach(product => {
        const productEl = createProductElement(product);
        container.appendChild(productEl);
      });
    }

    // Create product element with API integration
    function createProductElement(product) {
      const el = document.createElement('div');
      el.className = 'product-card';
      el.innerHTML = `
        <div class="product-image">
          <img src="${product.imageUrl}" alt="${product.name}" 
               onclick="API.activity.log({
                 pageName: 'Collections Page',
                 actionName: 'PRODUCT_CLICK',
                 additionalData: JSON.stringify({productId: ${product.id}, 
                   productName: '${product.name}'})
               }); window.location.href='product-details.html?id=${product.id}'">
        </div>
        <h3>${product.name}</h3>
        <p class="price">₹${product.price}</p>
        <div class="actions">
          <button onclick="addToCart(${product.id})">🛒 Add to Cart</button>
          <button onclick="addToWishlist(${product.id})">❤️ Wishlist</button>
        </div>
      `;
      return el;
    }

    // ✅ ADD TO CART
    async function addToCart(productId) {
      try {
        const response = await API.cart.add(productId, 1);
        
        // Show success message
        alert('Added to cart!');
        
        // Update cart badge
        updateBadges();
        
        // Automatic activity logging (done by API.cart.add)
      } catch (error) {
        console.error('Failed to add to cart:', error);
        alert('Failed to add to cart. Please try again.');
      }
    }

    // ✅ ADD TO WISHLIST
    async function addToWishlist(productId) {
      try {
        const response = await API.wishlist.add(productId);
        
        // Show success message
        alert('Added to wishlist!');
        
        // Update wishlist badge
        updateBadges();
        
        // Automatic activity logging (done by API.wishlist.add)
      } catch (error) {
        console.error('Failed to add to wishlist:', error);
        alert('Failed to add to wishlist. Please try again.');
      }
    }

    // Update cart and wishlist badges
    function updateBadges() {
      API.cart.getCount()
        .then(count => document.getElementById('cart-badge').innerText = count)
        .catch(err => {
          // Use localStorage as fallback
          const cartCount = JSON.parse(localStorage.getItem('cart') || '[]').length;
          document.getElementById('cart-badge').innerText = cartCount;
        });

      API.wishlist.getCount()
        .then(count => document.getElementById('wishlist-badge').innerText = count)
        .catch(err => {
          // Use localStorage as fallback
          const wishlistCount = JSON.parse(localStorage.getItem('wishlist') || '[]').length;
          document.getElementById('wishlist-badge').innerText = wishlistCount;
        });
    }

    // ✅ FILTER BY CATEGORY
    function filterByCategory(category) {
      currentFilters.category = category;
      currentPage = 1;
      loadProducts(currentFilters);
    }

    // ✅ FILTER BY PRICE RANGE
    function filterByPrice(minPrice, maxPrice) {
      currentFilters.minPrice = minPrice;
      currentFilters.maxPrice = maxPrice;
      currentPage = 1;
      loadProducts(currentFilters);
    }

    // ✅ SEARCH PRODUCTS
    function searchProducts(query) {
      currentFilters.searchQuery = query;
      currentPage = 1;
      loadProducts(currentFilters);
      
      // Log search
      API.activity.log({
        pageName: 'Collections Page',
        actionName: 'SEARCH_PRODUCTS',
        additionalData: JSON.stringify({ query })
      }).catch(err => console.log('Activity log skipped'));
    }
  </script>
</head>
<body>
  <!-- Filters -->
  <div class="filters">
    <button onclick="filterByCategory('WOMEN')">Women</button>
    <button onclick="filterByCategory('MEN')">Men</button>
    <button onclick="filterByCategory('KIDS')">Kids</button>
    <input type="text" placeholder="Search..." 
           onchange="searchProducts(this.value)">
  </div>

  <!-- Products Grid -->
  <div id="products-container" class="products-grid"></div>

  <!-- Pagination -->
  <div id="pagination" class="pagination"></div>

  <div id="cart-badge" style="display: inline-block;">0</div>
  <div id="wishlist-badge" style="display: inline-block;">0</div>
</body>
</html>
```

---

### 🛒 cart.html (TODO)

```html
<!DOCTYPE html>
<html>
<head>
  <script src="js/api-client.js"></script>
  <script>
    // ✅ PAGE VIEW
    document.addEventListener('DOMContentLoaded', async () => {
      API.activity.log({
        pageName: 'Cart Page',
        actionName: 'PAGE_VIEW'
      }).catch(err => console.log('Activity log skipped'));

      await loadCart();
    });

    // ✅ LOAD CART ITEMS
    async function loadCart() {
      try {
        const response = await API.cart.get();
        displayCartItems(response);
      } catch (error) {
        console.error('Failed to load cart:', error);
      }
    }

    // ✅ UPDATE QUANTITY
    async function updateQuantity(productId, newQuantity) {
      try {
        await API.cart.updateQuantity(productId, newQuantity);
        
        // Log update
        API.activity.log({
          pageName: 'Cart Page',
          actionName: 'UPDATE_CART_QUANTITY',
          additionalData: JSON.stringify({ productId, newQuantity })
        }).catch(err => console.log('Activity log skipped'));

        await loadCart();
      } catch (error) {
        console.error('Failed to update quantity:', error);
      }
    }

    // ✅ REMOVE FROM CART
    async function removeFromCart(productId) {
      try {
        await API.cart.remove(productId);
        
        // Automatic activity logging (done by API.cart.remove)
        
        await loadCart();
      } catch (error) {
        console.error('Failed to remove from cart:', error);
      }
    }

    // ✅ CHECKOUT
    async function checkout() {
      try {
        // Get user addresses first
        const addresses = await API.users.getAddresses();
        
        if (addresses.length === 0) {
          alert('Please add a delivery address first!');
          window.location.href = 'profile.html';
          return;
        }

        // Proceed to payment
        window.location.href = 'payment.html?addressId=' + addresses[0].id;
        
        // Log checkout
        API.activity.log({
          pageName: 'Cart Page',
          actionName: 'PROCEED_TO_CHECKOUT'
        }).catch(err => console.log('Activity log skipped'));
      } catch (error) {
        console.error('Checkout failed:', error);
      }
    }
  </script>
</head>
<body>
  <div id="cart-items"></div>
  <button onclick="checkout()">Proceed to Checkout</button>
</body>
</html>
```

---

### 💳 payment.html (TODO)

```html
<!DOCTYPE html>
<html>
<head>
  <script src="js/api-client.js"></script>
  <script>
    let orderId = null;

    document.addEventListener('DOMContentLoaded', () => {
      API.activity.log({
        pageName: 'Payment Page',
        actionName: 'PAGE_VIEW'
      }).catch(err => console.log('Activity log skipped'));
    });

    // ✅ PLACE ORDER
    async function placeOrder() {
      try {
        // Collect form data
        const formData = new FormData(document.getElementById('payment-form'));
        
        // Create order via API
        const response = await API.orders.create({
          addressId: new URLSearchParams(window.location.search).get('addressId'),
          paymentMethod: formData.get('paymentMethod'),
          // Cart items auto-sent from session
        });

        orderId = response.id;
        
        // Log order creation
        API.activity.log({
          pageName: 'Payment Page',
          actionName: 'PLACE_ORDER',
          additionalData: JSON.stringify({ orderId, totalAmount: response.totalAmount })
        }).catch(err => console.log('Activity log skipped'));

        // Show success
        alert('Order placed successfully! Order ID: ' + response.orderNumber);
        
        // Redirect to order details
        window.location.href = 'order-details.html?orderId=' + orderId;
      } catch (error) {
        console.error('Failed to place order:', error);
        alert('Failed to place order: ' + error.message);
      }
    }
  </script>
</head>
<body>
  <form id="payment-form">
    <input type="radio" name="paymentMethod" value="CARD" checked> Credit/Debit Card
    <button type="button" onclick="placeOrder()">Place Order</button>
  </form>
</body>
</html>
```

---

### 🔐 login.html (TODO)

```html
<!DOCTYPE html>
<html>
<head>
  <script src="js/api-client.js"></script>
  <script>
    async function handleLogin(e) {
      e.preventDefault();

      const email = document.getElementById('email').value;
      const password = document.getElementById('password').value;

      try {
        // Log attempt
        API.activity.log({
          pageName: 'Login Page',
          actionName: 'LOGIN_ATTEMPT',
          additionalData: JSON.stringify({ email })
        }).catch(err => console.log('Activity log skipped'));

        // Authenticate
        const response = await API.auth.login({ email, password });

        // Success - redirect to home
        alert('Login successful!');
        
        // Log successful login
        API.activity.log({
          pageName: 'Login Page',
          actionName: 'USER_LOGIN',
          additionalData: JSON.stringify({ email, success: true })
        }).catch(err => console.log('Activity log skipped'));

        window.location.href = 'index.html';
      } catch (error) {
        console.error('Login failed:', error);
        alert('Login failed: ' + error.message);
        
        // Log failed login
        API.activity.log({
          pageName: 'Login Page',
          actionName: 'LOGIN_FAILED',
          additionalData: JSON.stringify({ email, error: error.message })
        }).catch(err => console.log('Activity log skipped'));
      }
    }

    async function handleRegister(e) {
      e.preventDefault();

      const name = document.getElementById('name').value;
      const email = document.getElementById('register-email').value;
      const password = document.getElementById('register-password').value;
      const phone = document.getElementById('phone').value;

      try {
        const response = await API.auth.register({
          name, email, password, phone
        });

        alert('Registration successful! Please login.');
        
        // Log registration
        API.activity.log({
          pageName: 'Login Page',
          actionName: 'USER_REGISTER',
          additionalData: JSON.stringify({ email, success: true })
        }).catch(err => console.log('Activity log skipped'));

        // Show login form again
        document.getElementById('register-form').reset();
      } catch (error) {
        console.error('Registration failed:', error);
        alert('Registration failed: ' + error.message);
      }
    }
  </script>
</head>
<body>
  <!-- LOGIN FORM -->
  <form id="login-form" onsubmit="handleLogin(event)">
    <input type="email" id="email" placeholder="Email" required>
    <input type="password" id="password" placeholder="Password" required>
    <button type="submit">Login</button>
  </form>

  <!-- REGISTER FORM -->
  <form id="register-form" onsubmit="handleRegister(event)">
    <input type="text" id="name" placeholder="Full Name" required>
    <input type="email" id="register-email" placeholder="Email" required>
    <input type="password" id="register-password" placeholder="Password" required>
    <input type="tel" id="phone" placeholder="Phone" required>
    <button type="submit">Register</button>
  </form>
</body>
</html>
```

---

## 🎯 Error Handling Pattern

```javascript
// All API calls should handle errors gracefully
async function safeApiCall() {
  try {
    const result = await API.someEndpoint();
    // Success
  } catch (error) {
    console.error('API Error:', error);
    
    // For user-facing errors
    if (error.message.includes('401')) {
      // Redirect to login
      window.location.href = 'login.html';
    } else if (error.message.includes('Network')) {
      alert('Network error. Please check your connection.');
    } else {
      alert('An error occurred. Please try again.');
    }
  }
}

// Non-critical activity logs should not block user actions
API.activity.log({...})
  .catch(err => console.log('Activity log skipped (non-critical):', err));
```

---

## 📊 Testing Checklist

- [ ] Can register new user via login.html
- [ ] Can login with credentials
- [ ] JWT token stored in localStorage
- [ ] Cart badge updates after adding to cart
- [ ] Wishlist badge updates after adding to wishlist
- [ ] Can view cart and update quantities
- [ ] Can place order via payment.html
- [ ] Activity logs created in database
- [ ] Can filter products by category
- [ ] Can search for products
- [ ] Cart items persist across page refreshes
- [ ] Wishlist items persist across page refreshes

---

**Integration Status:** Partially Complete (Q&A ✅, Others: TODO)

**Next Steps:**
1. Integrate collections.html with product APIs
2. Integrate cart.html with cart APIs
3. Integrate login.html with auth APIs
4. Integrate payment.html with order APIs
5. Test all flows end-to-end

---

**Last Updated:** March 11, 2026
**Version:** 1.0.0
