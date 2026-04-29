/**
 * ═══════════════════════════════════════════════════════════════════════════
 * 🌟 TRENDAURA API CLIENT
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * Centralized API client for all TrendAura backend communication
 * Handles: Authentication, Product APIs, Cart, Wishlist, Activity Tracking, Chat
 * 
 * USAGE:
 * ------
 * import API from './api-client.js';
 * API.auth.login({email, password})
 * API.products.getAll()
 * API.cart.add(productId, quantity)
 * API.activity.log({pageName, actionName})
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 */

class APIClient {
    constructor() {
        // Base configuration
        this.baseURL = 'http://localhost:8081/api';
        this.timeout = 10000;
        this.headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        };
        
        // JWT Token management
        this.tokenKey = 'trendaura_jwt_token';
        this.userKey = 'trendaura_user';
        this.sessionId = this.generateSessionId();
    }

    /**
     * ══════════════════════════════════════════════════════════════
     * 🔐 AUTHENTICATION ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    auth = {
        // Register new user
        register: async (userData) => {
            return this.post('/auth/register', {
                firstName: userData.firstName || userData.name?.split(' ')[0] || '',
                lastName: userData.lastName || userData.name?.split(' ').slice(1).join(' ') || '',
                email: userData.email,
                password: userData.password,
                phone: userData.phone || null,
                countryCode: userData.countryCode || null
            });
        },

        // Login user
        login: async (credentials) => {
            const response = await this.post('/auth/login', {
                email: credentials.email,
                password: credentials.password
            });
            
            if (response.token) {
                this.setToken(response.token);
                localStorage.setItem(this.userKey, JSON.stringify(response.user));
            }
            return response;
        },

        // Logout user
        logout: () => {
            localStorage.removeItem(this.tokenKey);
            localStorage.removeItem(this.userKey);
        },

        // Forgot password
        forgotPassword: async (email) => {
            return this.post('/auth/forgot-password', { email });
        },

        // Check if user is authenticated
        isAuthenticated: () => {
            return !!localStorage.getItem(this.tokenKey);
        },

        // Get current user
        getCurrentUser: () => {
            const user = localStorage.getItem(this.userKey);
            return user ? JSON.parse(user) : null;
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 🛍️ PRODUCT ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    products = {
        // Get all products with filters
        getAll: async (filters = {}) => {
            let url = '/products';
            const params = new URLSearchParams();
            
            if (filters.category) params.append('category', filters.category);
            if (filters.type) params.append('type', filters.type);
            if (filters.state) params.append('state', filters.state);
            if (filters.minPrice) params.append('minPrice', filters.minPrice);
            if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);
            if (filters.searchQuery) params.append('searchQuery', filters.searchQuery);
            if (filters.page) params.append('page', filters.page);
            if (filters.size) params.append('size', filters.size);
            
            if (params.toString()) {
                url += '?' + params.toString();
            }
            
            // Log product view activity
            this.activity.log({
                pageName: 'Collections Page',
                actionName: 'VIEW_PRODUCTS',
                additionalData: JSON.stringify(filters)
            }).catch(err => console.log('Activity logging skipped'));
            
            return this.get(url);
        },

        // Get product by ID
        getById: async (productId) => {
            // Log product view activity
            this.activity.log({
                pageName: 'Product Details',
                actionName: 'VIEW_PRODUCT',
                additionalData: JSON.stringify({ productId })
            }).catch(err => console.log('Activity logging skipped'));
            
            return this.get(`/products/${productId}`);
        },

        // Search products
        search: async (query, filters = {}) => {
            const searchFilters = { ...filters, searchQuery: query };
            return this.products.getAll(searchFilters);
        },

        // Get trending products
        getTrending: async () => {
            return this.get('/products/trending');
        },

        // Get recommendations
        getRecommendations: async (userId) => {
            return this.get(`/recommendations/user/${userId}`);
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 🛒 CART ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    cart = {
        // Get user's cart
        get: async () => {
            return this.get('/cart');
        },

        // Add item to cart
        add: async (productId, quantity = 1, size = null, color = null) => {
            const response = await this.post('/cart/items', {
                productId,
                quantity,
                size,
                color
            });
            
            // Log cart activity
            this.activity.log({
                pageName: 'Product Details / Collections',
                actionName: 'ADD_TO_CART',
                additionalData: JSON.stringify({ productId, quantity, size, color })
            }).catch(err => console.log('Activity logging skipped'));
            
            return response;
        },

        // Update quantity
        updateQuantity: async (productId, quantity) => {
            return this.put(`/cart/items/${productId}`, { quantity });
        },

        // Remove item from cart
        remove: async (productId) => {
            const response = await this.delete(`/cart/items/${productId}`);
            
            // Log removal activity
            this.activity.log({
                pageName: 'Cart Page',
                actionName: 'REMOVE_FROM_CART',
                additionalData: JSON.stringify({ productId })
            }).catch(err => console.log('Activity logging skipped'));
            
            return response;
        },

        // Get cart count
        getCount: async () => {
            return this.get('/cart/count');
        },

        // Clear entire cart
        clear: async () => {
            return this.delete('/cart/clear');
        },

        // Get cart total
        getTotal: async () => {
            return this.get('/cart/total');
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * ❤️ WISHLIST ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    wishlist = {
        // Get user's wishlist
        get: async () => {
            return this.get('/wishlist');
        },

        // Add item to wishlist
        add: async (productId) => {
            const response = await this.post('/wishlist/items', {
                productId
            });
            
            // Log wishlist activity
            this.activity.log({
                pageName: 'Product Details',
                actionName: 'ADD_TO_WISHLIST',
                additionalData: JSON.stringify({ productId })
            }).catch(err => console.log('Activity logging skipped'));
            
            return response;
        },

        // Remove item from wishlist
        remove: async (productId) => {
            const response = await this.delete(`/wishlist/items/${productId}`);
            
            // Log removal activity
            this.activity.log({
                pageName: 'Wishlist Page',
                actionName: 'REMOVE_FROM_WISHLIST',
                additionalData: JSON.stringify({ productId })
            }).catch(err => console.log('Activity logging skipped'));
            
            return response;
        },

        // Get wishlist count
        getCount: async () => {
            return this.get('/wishlist/count');
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 📦 ORDER ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    orders = {
        // Create new order
        create: async (orderData) => {
            const response = await this.post('/orders', orderData);
            
            // Log order creation
            this.activity.log({
                pageName: 'Payment Page',
                actionName: 'PLACE_ORDER',
                additionalData: JSON.stringify({ orderId: response.id })
            }).catch(err => console.log('Activity logging skipped'));
            
            return response;
        },

        // Get user's orders
        getMyOrders: async () => {
            return this.get('/orders/my-orders');
        },

        // Get order by ID
        getById: async (orderId) => {
            return this.get(`/orders/${orderId}`);
        },

        // Update order status
        updateStatus: async (orderId, status) => {
            return this.put(`/orders/${orderId}/status`, { status });
        },

        // Cancel order
        cancel: async (orderId) => {
            return this.put(`/orders/${orderId}/cancel`, {});
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 📊 ACTIVITY TRACKING ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    activity = {
        // Log user activity
        log: async (activityData) => {
            const data = {
                pageName: activityData.pageName || 'Unknown',
                actionName: activityData.actionName || 'PAGE_VIEW',
                timestamp: new Date().toISOString(),
                sessionId: this.sessionId,
                additionalData: activityData.additionalData || null
            };
            
            // Log silently - don't block user actions
            return this.post('/activity/log', data).catch(err => {
                console.log('Activity logging failed (non-critical):', err);
            });
        },

        // Get user's activity history
        getHistory: async () => {
            return this.get('/activity/user-history');
        },

        // Get analytics - most visited pages
        getMostVisitedPages: async (days = 7) => {
            return this.get(`/activity/analytics/most-visited-pages?days=${days}`);
        },

        // Get analytics - most common actions
        getMostCommonActions: async (days = 7) => {
            return this.get(`/activity/analytics/most-common-actions?days=${days}`);
        },

        // Get page visit count
        getPageVisitCount: async (pageName, days = 7) => {
            return this.get(`/activity/analytics/page-visits?pageName=${pageName}&days=${days}`);
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 💬 CHAT/Q&A ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    chat = {
        // Create new chat session
        createSession: async () => {
            const response = await this.post('/chat/sessions', {});
            
            this.activity.log({
                pageName: 'Q&A Page',
                actionName: 'START_CHAT',
                additionalData: JSON.stringify({ sessionId: response.id })
            }).catch(err => console.log('Activity logging skipped'));
            
            return response;
        },

        // Get chat session by ID
        getSession: async (sessionId) => {
            return this.get(`/chat/sessions/${sessionId}`);
        },

        // Send message
        sendMessage: async (sessionId, message) => {
            const response = await this.post(`/chat/${sessionId}/messages`, {
                message: message,
                messageType: 'USER'
            });
            
            // Log chat interaction
            this.activity.log({
                pageName: 'Q&A Page',
                actionName: 'SEND_MESSAGE',
                additionalData: JSON.stringify({ messageLength: message.length })
            }).catch(err => console.log('Activity logging skipped'));
            
            return response;
        },

        // Get chat history
        getMessages: async (sessionId) => {
            return this.get(`/chat/${sessionId}/messages`);
        },

        // End chat session
        endSession: async (sessionId) => {
            return this.put(`/chat/sessions/${sessionId}/close`, {});
        },

        // Log helpful feedback
        logFeedback: async (messageId, isHelpful) => {
            this.activity.log({
                pageName: 'Q&A Page',
                actionName: 'RATE_MESSAGE',
                additionalData: JSON.stringify({ messageId, helpful: isHelpful })
            }).catch(err => console.log('Activity logging skipped'));
            
            return this.post(`/chat/messages/${messageId}/feedback`, {
                isHelpful
            });
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 📞 CONTACT ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    contact = {
        // Submit contact form
        submitForm: async (contactData) => {
            const response = await this.post('/contact/send-anonymous', {
                name: contactData.name,
                email: contactData.email,
                subject: contactData.subject,
                message: contactData.message
            });
            
            // Log contact submission
            this.activity.log({
                pageName: 'Contact Page',
                actionName: 'SUBMIT_CONTACT_FORM',
                additionalData: JSON.stringify({ email: contactData.email })
            }).catch(err => console.log('Activity logging skipped'));
            
            return response;
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 👤 USER ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    users = {
        // Get current user profile
        getProfile: async () => {
            return this.get('/users/profile');
        },

        // Update user profile
        updateProfile: async (userData) => {
            return this.put('/users/profile', userData);
        },

        // Get user addresses
        getAddresses: async () => {
            return this.get('/addresses');
        },

        // Add new address
        addAddress: async (addressData) => {
            return this.post('/addresses', addressData);
        },

        // Update address
        updateAddress: async (addressId, addressData) => {
            return this.put(`/addresses/${addressId}`, addressData);
        },

        // Delete address
        deleteAddress: async (addressId) => {
            return this.delete(`/addresses/${addressId}`);
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 🤖 AI/CHATBOT ENDPOINTS
     * ══════════════════════════════════════════════════════════════
     */
    ai = {
        // Get AI recommendations
        getRecommendations: async (userId) => {
            return this.get(`/ai/recommendations/${userId}`);
        },

        // Get personalized suggestions
        getSuggestions: async () => {
            return this.get('/ai/suggestions');
        },

        // Get trend analysis
        getTrendAnalysis: async () => {
            return this.get('/ai/trends');
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 🔧 HTTP METHODS (Private)
     * ══════════════════════════════════════════════════════════════
     */

    // GET request
    get = async (endpoint) => {
        return this.request('GET', endpoint);
    };

    // POST request
    post = async (endpoint, data) => {
        return this.request('POST', endpoint, data);
    };

    // PUT request
    put = async (endpoint, data) => {
        return this.request('PUT', endpoint, data);
    };

    // DELETE request
    delete = async (endpoint) => {
        return this.request('DELETE', endpoint);
    };

    // Generic request handler
    request = async (method, endpoint, data = null) => {
        const url = this.baseURL + endpoint;
        const options = {
            method,
            headers: { ...this.headers }
        };

        // Add JWT token if available
        const token = localStorage.getItem(this.tokenKey);
        if (token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }

        // Add body for POST, PUT, DELETE with data
        if (data) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(url, options);
            
            // Handle 401 Unauthorized
            if (response.status === 401) {
                this.auth.logout();
                window.location.href = '/login.html';
                throw new Error('Unauthorized. Please login again.');
            }

            // Parse response
            const responseData = await response.json();

            // Check for success
            if (!response.ok) {
                throw new Error(
                    responseData.message || 
                    `HTTP Error: ${response.status}`
                );
            }

            return responseData;
        } catch (error) {
            console.error(`API Error [${method} ${endpoint}]:`, error);
            throw error;
        }
    };

    /**
     * ══════════════════════════════════════════════════════════════
     * 🛠️ UTILITY METHODS
     * ══════════════════════════════════════════════════════════════
     */

    // Set JWT token
    setToken = (token) => {
        localStorage.setItem(this.tokenKey, token);
    };

    // Get JWT token
    getToken = () => {
        return localStorage.getItem(this.tokenKey);
    };

    // Generate session ID
    generateSessionId = () => {
        return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    };

    // Update base URL (useful for different environments)
    setBaseURL = (url) => {
        this.baseURL = url;
    };

    // Get API health check
    health = async () => {
        try {
            return await this.get('/health');
        } catch (error) {
            return { status: 'offline' };
        }
    };
}

// Export singleton instance
const API = new APIClient();

// Make available globally if not using modules
if (typeof window !== 'undefined') {
    window.API = API;
}

// Also export for modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = API;
}
