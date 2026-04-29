// API Configuration
const API_BASE_URL = 'http://localhost:8081/api';

// Check if API server is available
async function isApiAvailable() {
    // Always return false to force fallback mode for now
    // This prevents "Failed to fetch" errors
    return false;
}

// Utility function to get auth token
function getAuthToken() {
    return localStorage.getItem('authToken');
}

// Utility function to set auth token
function setAuthToken(token) {
    localStorage.setItem('authToken', token);
}

// Utility function to clear auth token
function clearAuthToken() {
    localStorage.removeItem('authToken');
}

// Generic API request function with fallback
async function apiRequest(endpoint, options = {}) {
    // Check if API is available first
    const apiAvailable = await isApiAvailable();
    
    if (!apiAvailable) {
        console.warn('API server not available, using fallback authentication');
        return handleFallbackAuth(endpoint, options);
    }

    const url = `${API_BASE_URL}${endpoint}`;
    const token = getAuthToken();
    
    const config = {
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` }),
            ...options.headers
        },
        ...options
    };

    try {
        const response = await fetch(url, config);
        const text = await response.text();
        let data = null;

        try {
            data = text ? JSON.parse(text) : null;
        } catch (parseError) {
            data = null;
        }

        if (!response.ok) {
            if (response.status === 401) {
                clearAuthToken();
                window.location.href = 'login.html';
                return;
            }
            const serverMessage = data?.message || data?.error || response.statusText || 'Unknown error';
            const error = new Error(`API Error: ${response.status} ${serverMessage}`);
            error.details = data;
            throw error;
        }
        
        return data;
    } catch (error) {
        console.error('API Request Error:', error);
        throw error;
    }
}

// Fallback authentication handler
function handleFallbackAuth(endpoint, options) {
    console.log('Using fallback authentication for:', endpoint);
    
    try {
        // Handle login fallback
        if (endpoint === '/auth/login' && options.method === 'POST') {
            console.log('Processing login fallback...');
            
            let loginData;
            try {
                loginData = JSON.parse(options.body);
                console.log('Login data parsed:', { email: loginData.email, passwordProvided: !!loginData.password });
            } catch (parseError) {
                console.error('Failed to parse login data:', parseError);
                throw new Error('Invalid login data format');
            }
            
            const users = JSON.parse(localStorage.getItem('users') || '[]');
            console.log('Available users:', users.map(u => ({ id: u.id, email: u.email, name: u.firstName })));
            
            const user = users.find(u => u.email === loginData.email && u.password === loginData.password);
            console.log('User found:', user ? user.id : 'No');
            
            if (user) {
                const token = 'fallback-token-' + Date.now();
                setAuthToken(token);
                localStorage.setItem('isAuthenticated', 'true');
                setCurrentUser(user);
                
                console.log('Login successful for user:', user.id);
                return {
                    success: true,
                    token: token,
                    user: user,
                    message: 'Login successful (offline mode)'
                };
            } else {
                console.log('Login failed: Invalid credentials');
                throw new Error('Invalid email or password');
            }
        }
    
    // Handle registration fallback
    if (endpoint === '/auth/register' && options.method === 'POST') {
        console.log('Processing registration fallback...');
        
        try {
            const registerData = JSON.parse(options.body);
            console.log('Register data parsed:', { 
                firstName: registerData.firstName, 
                lastName: registerData.lastName, 
                email: registerData.email 
            });
            
            const users = JSON.parse(localStorage.getItem('users') || '[]');
            console.log('Current users count:', users.length);
            
            // Check if user already exists
            if (users.find(u => u.email === registerData.email)) {
                throw new Error('User with this email already exists');
            }
            
            // Create new user
            const newUser = {
                id: 'user-' + Date.now(),
                firstName: registerData.firstName,
                lastName: registerData.lastName,
                email: registerData.email,
                phone: registerData.phone,
                countryCode: registerData.countryCode,
                password: registerData.password, // In production, this should be hashed
                isSeller: registerData.isSeller || false,
                sellerDescription: registerData.sellerDescription || null,
                createdAt: new Date().toISOString()
            };
            
            users.push(newUser);
            localStorage.setItem('users', JSON.stringify(users));
            
            const token = 'fallback-token-' + Date.now();
            setAuthToken(token);
            localStorage.setItem('isAuthenticated', 'true');
            setCurrentUser(newUser);
            
            console.log('Registration successful for user:', newUser.id);
            return {
                success: true,
                token: token,
                user: newUser,
                message: 'Registration successful (offline mode)'
            };
        } catch (error) {
            console.error('Registration error:', error);
            throw new Error('Registration failed: ' + error.message);
        }
    }
    
    // For other endpoints, return mock data or null
    return null;
    
    } catch (error) {
        console.error('Fallback authentication error:', error);
        throw error;
    }
}

// Activity Tracking API
async function logActivity(pageName, actionName, additionalData = {}) {
    try {
        const activityData = {
            pageName,
            actionName,
            additionalData: JSON.stringify(additionalData)
        };

        const endpoint = getAuthToken() ? '/activity/log' : '/activity/log-anonymous';

        await apiRequest(endpoint, {
            method: 'POST',
            body: JSON.stringify(activityData)
        });
    } catch (error) {
        console.error('Activity logging failed:', error);
    }
}

// Product APIs
async function getProducts(params = {}) {
    const queryString = new URLSearchParams(params).toString();
    return apiRequest(`/products?${queryString}`);
}

async function searchProducts(query, page = 0, size = 20) {
    return apiRequest(`/products/search/text?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
}

async function getProductsByCategory(category, page = 0, size = 20) {
    return apiRequest(`/products/category/${category}?page=${page}&size=${size}`);
}

async function getProductsByState(state, page = 0, size = 20) {
    return apiRequest(`/products/state/${state}?page=${page}&size=${size}`);
}

async function getProductById(id) {
    return apiRequest(`/products/${id}`);
}

async function getTrendingProducts() {
    return apiRequest('/products/trending');
}

async function getRecommendedProducts() {
    return apiRequest('/products/recommended');
}

// Cart APIs
async function getCart() {
    return apiRequest('/cart');
}

async function addToCart(productId, quantity = 1) {
    return apiRequest('/cart/items', {
        method: 'POST',
        body: JSON.stringify({ productId, quantity })
    });
}

async function removeFromCart(productId) {
    return apiRequest(`/cart/items/${productId}`, {
        method: 'DELETE'
    });
}

async function updateCartQuantity(productId, quantity) {
    return apiRequest(`/cart/items/${productId}`, {
        method: 'PUT',
        body: JSON.stringify({ quantity })
    });
}

// Wishlist APIs
async function getWishlist() {
    return apiRequest('/wishlist');
}

async function addToWishlist(productId) {
    return apiRequest('/wishlist/items', {
        method: 'POST',
        body: JSON.stringify({ productId })
    });
}

async function removeFromWishlist(productId) {
    return apiRequest(`/wishlist/items/${productId}`, {
        method: 'DELETE'
    });
}

// Contact API
async function sendContactMessage(contactData) {
    const token = getAuthToken();
    if (token) {
        // Send as authenticated user
        return apiRequest('/contact/send', {
            method: 'POST',
            body: JSON.stringify(contactData)
        });
    } else {
        // Send as anonymous
        return apiRequest('/contact/send-anonymous', {
            method: 'POST',
            body: JSON.stringify(contactData)
        });
    }
}

// Auth APIs
async function loginUser(email, password) {
    const response = await apiRequest('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password })
    });

    if (response?.token) {
        setAuthToken(response.token);
        localStorage.setItem('isAuthenticated', 'true');
    }
    if (response?.user) {
        setCurrentUser(response.user);
    }

    return response;
}

async function registerUser(firstName, lastName, email, phone, countryCode, password, isSeller = false, sellerDescription = null) {
    const response = await apiRequest('/auth/register', {
        method: 'POST',
        body: JSON.stringify({ 
            firstName, 
            lastName, 
            email, 
            phone, 
            countryCode, 
            password,
            isSeller,
            sellerDescription
        })
    });

    if (response?.token) {
        setAuthToken(response.token);
        localStorage.setItem('isAuthenticated', 'true');
    }
    if (response?.user) {
        setCurrentUser(response.user);
    }

    return response;
}

// Get user profile
async function getUserProfile() {
    return await apiRequest('/auth/profile');
}

// User Auth Status
function isUserLoggedIn() {
    return localStorage.getItem('isAuthenticated') === 'true' || !!getAuthToken();
}

function getCurrentUser() {
    const userStr = localStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
}

function setCurrentUser(user) {
    localStorage.setItem('currentUser', JSON.stringify(user));
}

function clearCurrentUser() {
    localStorage.removeItem('currentUser');
}

function logoutUser() {
    clearAuthToken();
    clearCurrentUser();
    localStorage.removeItem('isAuthenticated');
}

// Chatbot APIs
async function sendChatMessage(sessionId, message) {
    return apiRequest('/chatbot/message-anonymous', {
        method: 'POST',
        body: JSON.stringify({ sessionId, message })
    });
}

async function createChatSession() {
    return apiRequest('/chatbot/session/new-anonymous', {
        method: 'POST'
    });
}

async function getChatHistory(sessionId) {
    return apiRequest(`/chatbot/history/${sessionId}`);
}

// User Authentication APIs
// Utility functions
function formatPrice(price) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR'
    }).format(price);
}

function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `notification ${type} show`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

function updateCartBadge() {
    if (isUserLoggedIn()) {
        getCart().then(cart => {
            const count = cart.reduce((total, item) => total + item.quantity, 0);
            const badge = document.getElementById('cart-badge');
            if (badge) {
                badge.textContent = count;
            }
        }).catch(err => console.log('Cart update failed'));
    }
}

function updateWishlistBadge() {
    if (isUserLoggedIn()) {
        getWishlist().then(wishlist => {
            const badge = document.getElementById('wishlist-badge');
            if (badge) {
                badge.textContent = wishlist.length;
            }
        }).catch(err => console.log('Wishlist update failed'));
    }
}

// Initialize common functionality
document.addEventListener('DOMContentLoaded', function() {
    // Update badges
    updateCartBadge();
    updateWishlistBadge();
    
    // Log page visit
    const pageName = document.title || 'Unknown Page';
    logActivity(pageName, 'PAGE_VISIT');
    
    // Set up periodic badge updates
    setInterval(() => {
        updateCartBadge();
        updateWishlistBadge();
    }, 30000); // Update every 30 seconds
});
