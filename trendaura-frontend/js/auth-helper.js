// Authentication Helper - Ensures login persistence across all pages

// Check if user is logged in and redirect if needed
function checkAuthStatus() {
    const isLoggedIn = isUserLoggedIn();
    const currentUser = getCurrentUser();
    
    if (!isLoggedIn || !currentUser) {
        // Store the current page for redirect after login
        sessionStorage.setItem('redirectAfterLogin', window.location.href);
        window.location.href = 'login.html';
        return false;
    }
    
    return true;
}

// Update UI elements based on login status
function updateAuthUI() {
    const isLoggedIn = isUserLoggedIn();
    const currentUser = getCurrentUser();
    
    if (isLoggedIn && currentUser) {
        // Update profile links
        const profileLinks = document.querySelectorAll('a[href="profile.html"]');
        profileLinks.forEach(link => {
            link.style.display = 'inline-flex';
        });
        
        // Update account text if elements exist
        const accountElements = document.querySelectorAll('.account-text, .user-name');
        accountElements.forEach(element => {
            const displayName = currentUser.firstName ? 
                `${currentUser.firstName} ${currentUser.lastName || ''}`.trim() : 
                currentUser.email;
            element.textContent = displayName;
        });
        
        // Show seller indicators if user is a seller
        if (currentUser.isSeller) {
            const sellerIndicators = document.querySelectorAll('#sellerIndicator, .seller-only');
            sellerIndicators.forEach(element => {
                element.style.display = 'block';
            });
        }
        
        // Hide login/register links
        const loginLinks = document.querySelectorAll('a[href="login.html"]');
        const registerLinks = document.querySelectorAll('a[href*="register"]');
        loginLinks.forEach(link => link.style.display = 'none');
        registerLinks.forEach(link => link.style.display = 'none');
        
        // Update cart and wishlist badges
        updateCartAndWishlistBadges();
    } else {
        // Hide profile links
        const profileLinks = document.querySelectorAll('a[href="profile.html"]');
        profileLinks.forEach(link => {
            link.style.display = 'none';
        });
        
        // Show login/register links
        const loginLinks = document.querySelectorAll('a[href="login.html"]');
        loginLinks.forEach(link => link.style.display = 'inline-flex');
    }
}

// Update cart and wishlist badges
function updateCartAndWishlistBadges() {
    try {
        const cart = JSON.parse(localStorage.getItem('cart') || '[]');
        const wishlist = JSON.parse(localStorage.getItem('wishlist') || '[]');
        
        const cartBadge = document.getElementById('cart-badge');
        const wishlistBadge = document.getElementById('wishlist-badge');
        
        if (cartBadge) {
            const cartCount = cart.reduce((total, item) => total + (item.quantity || 1), 0);
            cartBadge.textContent = cartCount;
        }
        
        if (wishlistBadge) {
            wishlistBadge.textContent = wishlist.length;
        }
    } catch (error) {
        console.error('Error updating badges:', error);
    }
}

// Handle logout
function handleLogout() {
    logoutUser();
    window.location.href = 'login.html';
}

// Initialize authentication on page load
function initAuth() {
    // Check authentication status
    if (checkAuthStatus()) {
        // Update UI elements
        updateAuthUI();
        
        // Add logout handlers
        const logoutButtons = document.querySelectorAll('#logout-btn, .logout-btn');
        logoutButtons.forEach(button => {
            button.addEventListener('click', handleLogout);
        });
    }
}

// Auto-redirect to intended page after login
function handleLoginRedirect() {
    const redirectUrl = sessionStorage.getItem('redirectAfterLogin');
    if (redirectUrl && redirectUrl !== window.location.href) {
        sessionStorage.removeItem('redirectAfterLogin');
        window.location.href = redirectUrl;
    }
}

// Smart profile navigation - goes to profile if logged in, login if not
function handleProfileNavigation() {
    const isLoggedIn = isUserLoggedIn();
    const currentUser = getCurrentUser();
    
    if (isLoggedIn && currentUser) {
        window.location.href = 'profile.html';
    } else {
        // Store that user wanted to go to profile
        sessionStorage.setItem('redirectAfterLogin', 'profile.html');
        window.location.href = 'login.html';
    }
}

// Export functions for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        checkAuthStatus,
        updateAuthUI,
        updateCartAndWishlistBadges,
        handleLogout,
        initAuth,
        handleLoginRedirect,
        handleProfileNavigation
    };
}
