/**
 * TrendAura Storage Manager
 * Centralized storage management for collections, wishlist, and cart
 * Ensures data persistence across all pages
 */

class StorageManager {
    constructor() {
        this.keys = {
            products: 'trendaura_products',
            wishlist: 'trendaura_wishlist',
            cart: 'trendaura_cart',
            user: 'trendaura_user',
            authToken: 'trendaura_auth_token',
            isAuthenticated: 'trendaura_is_authenticated'
        };
        
        // Initialize storage on creation
        this.initializeStorage();
    }

    /**
     * Initialize storage with default values and migrate old data
     */
    initializeStorage() {
        this.migrateOldData();
        this.ensureDefaultData();
    }

    /**
     * Migrate old localStorage keys to new standardized keys
     */
    migrateOldData() {
        const migrations = [
            { oldKey: 'products', newKey: this.keys.products },
            { oldKey: 'wishlist', newKey: this.keys.wishlist },
            { oldKey: 'cart', newKey: this.keys.cart },
            { oldKey: 'currentUser', newKey: this.keys.user },
            { oldKey: 'authToken', newKey: this.keys.authToken },
            { oldKey: 'isAuthenticated', newKey: this.keys.isAuthenticated }
        ];

        migrations.forEach(({ oldKey, newKey }) => {
            const oldData = localStorage.getItem(oldKey);
            if (oldData && !localStorage.getItem(newKey)) {
                localStorage.setItem(newKey, oldData);
                localStorage.removeItem(oldKey);
                console.log(`Migrated ${oldKey} to ${newKey}`);
            }
        });
    }

    /**
     * Ensure all storage keys have default values
     */
    ensureDefaultData() {
        const defaults = {
            [this.keys.products]: [],
            [this.keys.wishlist]: [],
            [this.keys.cart]: [],
            [this.keys.user]: null,
            [this.keys.authToken]: null,
            [this.keys.isAuthenticated]: 'false'
        };

        Object.entries(defaults).forEach(([key, defaultValue]) => {
            if (!localStorage.getItem(key)) {
                localStorage.setItem(key, JSON.stringify(defaultValue));
            }
        });
    }

    /**
     * Get data from localStorage
     */
    get(key, defaultValue = null) {
        try {
            const data = localStorage.getItem(key);
            return data ? JSON.parse(data) : defaultValue;
        } catch (error) {
            console.error(`Error getting data for key ${key}:`, error);
            return defaultValue;
        }
    }

    /**
     * Set data in localStorage
     */
    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
            return true;
        } catch (error) {
            console.error(`Error setting data for key ${key}:`, error);
            return false;
        }
    }

    /**
     * Remove data from localStorage
     */
    remove(key) {
        try {
            localStorage.removeItem(key);
            return true;
        } catch (error) {
            console.error(`Error removing data for key ${key}:`, error);
            return false;
        }
    }

    /**
     * Clear all TrendAura data
     */
    clearAll() {
        Object.values(this.keys).forEach(key => {
            this.remove(key);
        });
    }

    // Products
    getProducts() {
        return this.get(this.keys.products, []);
    }

    setProducts(products) {
        return this.set(this.keys.products, products);
    }

    addProduct(product) {
        const products = this.getProducts();
        products.unshift(product);
        return this.setProducts(products);
    }

    updateProduct(productId, updates) {
        const products = this.getProducts();
        const index = products.findIndex(p => p.id === productId);
        if (index !== -1) {
            products[index] = { ...products[index], ...updates };
            return this.setProducts(products);
        }
        return false;
    }

    removeProduct(productId) {
        const products = this.getProducts();
        const filtered = products.filter(p => p.id !== productId);
        return this.setProducts(filtered);
    }

    // Wishlist
    getWishlist() {
        return this.get(this.keys.wishlist, []);
    }

    setWishlist(wishlist) {
        return this.set(this.keys.wishlist, wishlist);
    }

    addToWishlist(productId) {
        const wishlist = this.getWishlist();
        if (!wishlist.includes(productId)) {
            wishlist.push(productId);
            return this.setWishlist(wishlist);
        }
        return true;
    }

    removeFromWishlist(productId) {
        const wishlist = this.getWishlist();
        const filtered = wishlist.filter(id => id !== productId);
        return this.setWishlist(filtered);
    }

    isInWishlist(productId) {
        const wishlist = this.getWishlist();
        return wishlist.includes(productId);
    }

    // Cart
    getCart() {
        return this.get(this.keys.cart, []);
    }

    setCart(cart) {
        return this.set(this.keys.cart, cart);
    }

    addToCart(product, quantity = 1) {
        const cart = this.getCart();
        const existingItem = cart.find(item => item.id === product.id);
        
        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            cart.push({ ...product, quantity });
        }
        
        return this.setCart(cart);
    }

    updateCartItemQuantity(productId, quantity) {
        const cart = this.getCart();
        const item = cart.find(item => item.id === productId);
        
        if (item) {
            if (quantity <= 0) {
                return this.removeFromCart(productId);
            } else {
                item.quantity = quantity;
                return this.setCart(cart);
            }
        }
        return false;
    }

    removeFromCart(productId) {
        const cart = this.getCart();
        const filtered = cart.filter(item => item.id !== productId);
        return this.setCart(filtered);
    }

    clearCart() {
        return this.setCart([]);
    }

    getCartItemCount() {
        const cart = this.getCart();
        return cart.reduce((total, item) => total + item.quantity, 0);
    }

    getCartTotal() {
        const cart = this.getCart();
        return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    }

    // User Authentication
    getUser() {
        return this.get(this.keys.user);
    }

    setUser(user) {
        return this.set(this.keys.user, user);
    }

    getAuthToken() {
        return this.get(this.keys.authToken);
    }

    setAuthToken(token) {
        return this.set(this.keys.authToken, token);
    }

    isAuthenticated() {
        return this.get(this.keys.isAuthenticated, 'false') === 'true';
    }

    setAuthenticated(authenticated) {
        return this.set(this.keys.isAuthenticated, String(authenticated));
    }

    // Clear user session
    logout() {
        this.remove(this.keys.user);
        this.remove(this.keys.authToken);
        this.setAuthenticated(false);
    }

    // Utility methods
    getStorageSize() {
        let totalSize = 0;
        for (let key in localStorage) {
            if (key.startsWith('trendaura_')) {
                totalSize += localStorage.getItem(key).length;
            }
        }
        return totalSize;
    }

    exportData() {
        const data = {};
        Object.entries(this.keys).forEach(([name, key]) => {
            data[name] = this.get(key);
        });
        return data;
    }

    importData(data) {
        Object.entries(data).forEach(([name, value]) => {
            const key = this.keys[name];
            if (key) {
                this.set(key, value);
            }
        });
    }
}

// Create singleton instance
const storageManager = new StorageManager();

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = StorageManager;
} else if (typeof window !== 'undefined') {
    window.StorageManager = StorageManager;
    window.storage = storageManager;
}
