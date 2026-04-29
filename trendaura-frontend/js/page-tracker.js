/**
 * ═══════════════════════════════════════════════════════════════════════════
 * 🎯 PAGE TRACKER - Comprehensive Activity Tracking for All Pages
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * This module tracks ALL user interactions across the entire TrendAura frontend
 * Data collected is stored in backend for:
 * - User behavior analysis
 * - Deep Learning recommendation engine
 * - A/B testing and analytics
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 */

class PageTracker {
    constructor() {
        this.sessionId = this.getOrCreateSessionId();
        this.pageLoadTime = Date.now();
        this.pageViewStartTime = Date.now();
        this.clickCount = 0;
        this.scrollDepth = 0;
        this.timeSpentOnPage = 0;
    }

    /**
     * Generate or retrieve session ID for tracking user journey
     */
    getOrCreateSessionId() {
        let sessionId = sessionStorage.getItem('trendaura_session_id');
        if (!sessionId) {
            sessionId = `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
            sessionStorage.setItem('trendaura_session_id', sessionId);
        }
        return sessionId;
    }

    /**
     * Track PAGE VIEW - Called when page loads
     */
    async trackPageView(pageName) {
        const userData = JSON.parse(localStorage.getItem('trendaura_user') || '{}');
        
        const pageViewData = {
            pageName: pageName,
            actionName: 'PAGE_VIEW',
            additionalData: JSON.stringify({
                referrer: document.referrer,
                userAgent: navigator.userAgent,
                screenResolution: `${window.innerWidth}x${window.innerHeight}`,
                url: window.location.href,
                timestamp: new Date().toISOString(),
                userId: userData.id || null
            })
        };

        await this.logActivity(pageViewData);
        this.pageViewStartTime = Date.now();
    }

    /**
     * Track PRODUCT VIEW - When user views a product
     */
    async trackProductView(productId, productName, category, price) {
        const viewData = {
            pageName: this.getCurrentPageName(),
            actionName: 'VIEW_PRODUCT',
            additionalData: JSON.stringify({
                productId: productId,
                productName: productName,
                category: category,
                price: price,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(viewData);
    }

    /**
     * Track ADD TO CART - When user adds product to cart
     */
    async trackAddToCart(productId, productName, quantity, price, size, color) {
        const cartData = {
            pageName: this.getCurrentPageName(),
            actionName: 'ADD_TO_CART',
            additionalData: JSON.stringify({
                productId: productId,
                productName: productName,
                quantity: quantity,
                price: price,
                size: size,
                color: color,
                cartValue: price * quantity,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(cartData);
    }

    /**
     * Track REMOVE FROM CART
     */
    async trackRemoveFromCart(productId, productName, quantity) {
        const data = {
            pageName: this.getCurrentPageName(),
            actionName: 'REMOVE_FROM_CART',
            additionalData: JSON.stringify({
                productId: productId,
                productName: productName,
                quantity: quantity,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(data);
    }

    /**
     * Track ADD TO WISHLIST - When user adds product to wishlist
     */
    async trackAddToWishlist(productId, productName, price) {
        const wishlistData = {
            pageName: this.getCurrentPageName(),
            actionName: 'ADD_TO_WISHLIST',
            additionalData: JSON.stringify({
                productId: productId,
                productName: productName,
                price: price,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(wishlistData);
    }

    /**
     * Track REMOVE FROM WISHLIST
     */
    async trackRemoveFromWishlist(productId, productName) {
        const data = {
            pageName: this.getCurrentPageName(),
            actionName: 'REMOVE_FROM_WISHLIST',
            additionalData: JSON.stringify({
                productId: productId,
                productName: productName,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(data);
    }

    /**
     * Track SEARCH - When user searches for products
     */
    async trackSearch(searchQuery, resultsCount) {
        const searchData = {
            pageName: this.getCurrentPageName(),
            actionName: 'SEARCH',
            additionalData: JSON.stringify({
                searchQuery: searchQuery,
                resultsCount: resultsCount,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(searchData);
    }

    /**
     * Track FILTER - When user filters products
     */
    async trackFilter(filterType, filterValue) {
        const filterData = {
            pageName: this.getCurrentPageName(),
            actionName: 'FILTER',
            additionalData: JSON.stringify({
                filterType: filterType,  // e.g., 'category', 'price', 'size'
                filterValue: filterValue,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(filterData);
    }

    /**
     * Track CLICK - When user clicks on elements
     */
    async trackClick(elementName, elementType) {
        this.clickCount++;
        const clickData = {
            pageName: this.getCurrentPageName(),
            actionName: 'CLICK',
            additionalData: JSON.stringify({
                elementName: elementName,
                elementType: elementType,  // e.g., 'button', 'link', 'image'
                clickNumber: this.clickCount,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(clickData);
    }

    /**
     * Track SORT - When user sorts products
     */
    async trackSort(sortType) {
        const sortData = {
            pageName: this.getCurrentPageName(),
            actionName: 'SORT',
            additionalData: JSON.stringify({
                sortType: sortType,  // e.g., 'price_asc', 'price_desc', 'newest'
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(sortData);
    }

    /**
     * Track SCROLL - When user scrolls (track scroll depth for engagement)
     */
    async trackScroll(scrollPercentage) {
        if (scrollPercentage > this.scrollDepth) {
            this.scrollDepth = scrollPercentage;
            
            // Only log every 25% to avoid too many logs
            if (scrollPercentage % 25 === 0) {
                const scrollData = {
                    pageName: this.getCurrentPageName(),
                    actionName: 'SCROLL',
                    additionalData: JSON.stringify({
                        scrollDepth: scrollPercentage,
                        timestamp: new Date().toISOString()
                    })
                };
                await this.logActivity(scrollData);
            }
        }
    }

    /**
     * Track CHECKOUT - When user proceeds to checkout
     */
    async trackCheckout(cartTotal, itemCount) {
        const checkoutData = {
            pageName: this.getCurrentPageName(),
            actionName: 'CHECKOUT',
            additionalData: JSON.stringify({
                cartTotal: cartTotal,
                itemCount: itemCount,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(checkoutData);
    }

    /**
     * Track ORDER PLACED - When user completes order
     */
    async trackOrderPlaced(orderId, orderTotal, itemCount) {
        const orderData = {
            pageName: this.getCurrentPageName(),
            actionName: 'ORDER_PLACED',
            additionalData: JSON.stringify({
                orderId: orderId,
                orderTotal: orderTotal,
                itemCount: itemCount,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(orderData);
    }

    /**
     * Track WISHLIST TO CART - When user moves item from wishlist to cart
     */
    async trackWishlistToCart(productId, productName, price) {
        const data = {
            pageName: this.getCurrentPageName(),
            actionName: 'WISHLIST_TO_CART',
            additionalData: JSON.stringify({
                productId: productId,
                productName: productName,
                price: price,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(data);
    }

    /**
     * Track LOGIN
     */
    async trackLogin(email) {
        const loginData = {
            pageName: this.getCurrentPageName(),
            actionName: 'LOGIN',
            additionalData: JSON.stringify({
                email: email,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(loginData);
    }

    /**
     * Track LOGOUT
     */
    async trackLogout() {
        const logoutData = {
            pageName: this.getCurrentPageName(),
            actionName: 'LOGOUT',
            additionalData: JSON.stringify({
                timeSpentOnPage: Date.now() - this.pageViewStartTime,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(logoutData);
    }

    /**
     * Track RATING - When user rates a product
     */
    async trackRating(productId, productName, rating, review) {
        const ratingData = {
            pageName: this.getCurrentPageName(),
            actionName: 'RATE_PRODUCT',
            additionalData: JSON.stringify({
                productId: productId,
                productName: productName,
                rating: rating,
                reviewLength: review ? review.length : 0,
                timestamp: new Date().toISOString()
            })
        };
        await this.logActivity(ratingData);
    }

    /**
     * Core Activity Log Function - Sends to backend
     */
    async logActivity(activityData) {
        try {
            // Don't block user interaction - log asynchronously
            const userData = JSON.parse(localStorage.getItem('trendaura_user') || '{}');
            
            const payload = {
                pageName: activityData.pageName,
                actionName: activityData.actionName,
                additionalData: activityData.additionalData,
                sessionId: this.sessionId,
                userId: userData.id || null
            };

            const authToken = localStorage.getItem('authToken') || localStorage.getItem('trendaura_jwt_token');
            const headers = {
                'Content-Type': 'application/json'
            };
            if (authToken) {
                headers['Authorization'] = `Bearer ${authToken}`;
            }

            // Send to backend - non-blocking
            fetch('http://localhost:8081/api/activity/log', {
                method: 'POST',
                headers,
                body: JSON.stringify(payload)
            }).catch(err => {
                // Silently fail if logging service is down
                console.log('Activity log stored locally');
            });

        } catch (error) {
            console.log('Activity tracking error:', error);
        }
    }

    /**
     * Get current page name
     */
    getCurrentPageName() {
        const pathname = window.location.pathname;
        const filename = pathname.split('/').pop() || 'index.html';
        return filename.replace('.html', '').toUpperCase();
    }

    /**
     * Track time spent on page (call on page unload)
     */
    async trackTimeOnPage() {
        const timeSpent = Date.now() - this.pageViewStartTime;
        const timeData = {
            pageName: this.getCurrentPageName(),
            actionName: 'PAGE_TIME',
            additionalData: JSON.stringify({
                timeSpentSeconds: Math.round(timeSpent / 1000),
                clicks: this.clickCount,
                scrollDepth: this.scrollDepth
            })
        };
        await this.logActivity(timeData);
    }
}

// Create global instance
const tracker = new PageTracker();

// Auto-track page view on load
document.addEventListener('DOMContentLoaded', () => {
    tracker.trackPageView(tracker.getCurrentPageName());
});

// Track time on page before unload
window.addEventListener('beforeunload', () => {
    tracker.trackTimeOnPage();
});

// Track scroll depth
window.addEventListener('scroll', () => {
    const scrollPercentage = Math.round((window.scrollY / (document.documentElement.scrollHeight - window.innerHeight)) * 100);
    tracker.trackScroll(scrollPercentage);
});
