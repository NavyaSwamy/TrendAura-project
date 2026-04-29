-- ========================================
-- TRENDAURA COMPREHENSIVE DATABASE SCHEMA
-- ========================================

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER'
);

-- Products Table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    category ENUM('WOMEN', 'MEN', 'KIDS') NOT NULL,
    subcategory VARCHAR(100),
    type ENUM('SAREE', 'SALWAR_KAMEEZ', 'LEHENGA', 'DHOTI', 'KURTA', 'SHERWANI', 'SHIRT', 'TSHIRT', 'JEANS', 'JACKET', 'DRESS', 'ETHNIC_WEAR', 'DAILY_WEAR') NOT NULL,
    state VARCHAR(50) NOT NULL,
    image_url TEXT NOT NULL,
    additional_images JSON,
    available_sizes JSON,
    available_colors JSON,
    boutique_name VARCHAR(255),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Cart Activity Table
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    action_type ENUM('ADD_TO_CART', 'REMOVE_FROM_CART', 'VIEW_PRODUCT') NOT NULL,
    quantity INT DEFAULT 1,
    size VARCHAR(10),
    color VARCHAR(50),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Wishlist Table
CREATE TABLE wishlist_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_wishlist_item (user_id, product_id)
);

-- User Activity Tracking Table (Most Important)
CREATE TABLE user_activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    page_name VARCHAR(255) NOT NULL,
    action_name VARCHAR(255) NOT NULL,
    event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    device_info JSON,
    ip_address VARCHAR(45),
    session_id VARCHAR(255),
    additional_data JSON,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Contact Messages Table
CREATE TABLE contact_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_resolved BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Chat Sessions Table
CREATE TABLE chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    session_id VARCHAR(255) UNIQUE NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Chat Messages Table
CREATE TABLE chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    message_type ENUM('USER', 'BOT') NOT NULL,
    message_text TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE
);

-- Orders Table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(100) UNIQUE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    shipping_address JSON NOT NULL,
    payment_method VARCHAR(50),
    payment_status ENUM('PENDING', 'PAID', 'FAILED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Order Items Table
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    size VARCHAR(10),
    color VARCHAR(50),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Reviews Table
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_review (user_id, product_id)
);

-- Search History Table
CREATE TABLE search_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    search_query VARCHAR(255) NOT NULL,
    filters JSON,
    results_count INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Product Recommendations Table
CREATE TABLE product_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    recommendation_type ENUM('BASED_ON_VIEW', 'BASED_ON_CART', 'BASED_ON_WISHLIST', 'SIMILAR_PRODUCTS', 'TRENDING') NOT NULL,
    score DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- User Preferences Table
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    preferred_categories JSON,
    preferred_states JSON,
    price_range_min DECIMAL(10,2),
    price_range_max DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for Performance Optimization
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_type ON products(type);
CREATE INDEX idx_products_state ON products(state);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_cart_items_user ON cart_items(user_id);
CREATE INDEX idx_cart_items_product ON cart_items(product_id);
CREATE INDEX idx_wishlist_user ON wishlist_items(user_id);
CREATE INDEX idx_wishlist_product ON wishlist_items(product_id);
CREATE INDEX idx_activity_logs_user ON user_activity_logs(user_id);
CREATE INDEX idx_activity_logs_time ON user_activity_logs(event_time);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_reviews_product ON reviews(product_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_search_history_user ON search_history(user_id);
CREATE INDEX idx_chat_messages_session ON chat_messages(session_id);

-- Insert Sample Data
INSERT INTO users (name, email, password, phone) VALUES
('Admin User', 'admin@trendaura.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jskPqH.kTj2', '+1234567890'),
('Test User', 'test@trendaura.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jskPqH.kTj2', '+0987654321');

-- Sample Products Data
INSERT INTO products (name, price, category, subcategory, type, state, image_url, available_sizes, available_colors, boutique_name, description) VALUES
('Uppada Silk Saree', 4599.00, 'WOMEN', 'Sarees', 'SAREE', 'andhra_pradesh', 'https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcTWHlpXBq07fH8TtK_GNFVFeSp1OnM5P_4zCWl_lqWBG5vuoA8Lh3QBtvi7imZIL2ZkTcibgWQx7ClkX1CdovDmE_2ywZOKwXIs8LYalH4P', '["S", "M", "L"]', '["Red", "Blue", "Green"]', 'Uppada Silks', 'Traditional Uppada silk saree with intricate zari work'),
('Mysore Silk Saree', 4999.00, 'WOMEN', 'Sarees', 'SAREE', 'karnataka', 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcQNpiymeVBHPbsSxecoI1BRLZGr9BGTXhBMt4JV6wW96pawMAex3tc5uI_QkIl7VTQNb5oVcklVhLSnbrF2D7CmkOnHLNyjAJcJDHabK33q', '["S", "M", "L", "XL"]', '["Gold", "Maroon", "Purple"]', 'Mysore Silks', 'Pure Mysore silk saree with gold zari border'),
('Paithani Silk Saree', 8999.00, 'WOMEN', 'Sarees', 'SAREE', 'maharashtra', 'https://sunasa.in/cdn/shop/files/handloom-paithani-silk-pink-bridal-saree-online-shopping_1445x.jpg?v=1712328088', '["M", "L", "XL"]', '["Pink", "Green", "Yellow"]', 'Paithani Weavers', 'Authentic Paithani silk saree with traditional motifs'),
('Kasavu Saree', 3299.00, 'WOMEN', 'Sarees', 'SAREE', 'kerala', 'https://sargoshi.in/cdn/shop/articles/cb9543978aad259a8ff34fa56973a056_600x.jpg?v=1716279298', '["S", "M", "L"]', '["White", "Cream", "Off-White"]', 'Kerala Handlooms', 'Traditional Kerala Kasavu saree with golden border'),
('Bandhani Saree', 2799.00, 'WOMEN', 'Sarees', 'SAREE', 'gujarat', 'https://i.pinimg.com/736x/fd/31/4c/fd314ca31a0ec68181ba9e18e2a9ea1d.jpg', '["S", "M", "L"]', '["Red", "Blue", "Yellow", "Green"]', 'Gujarat Bandhani', 'Tie-dye Bandhani saree from Gujarat'),
('Muga Silk Saree', 5899.00, 'WOMEN', 'Sarees', 'SAREE', 'assam', 'https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcSfvY-IVoUJ5j14Vy0_vVpp5Kp0xcxr_z3xIIc9V4cg1ByJ80uygxvPQm9-iOpvuZAGaqjEn6Dl7GuQyhOGKapkDm28gqRyfjoncFgXAhY', '["M", "L", "XL"]', '["Golden", "Cream", "Maroon"]', 'Assam Silks', 'Golden Muga silk saree from Assam'),
('Men\'s Shirt', 1299.00, 'MEN', 'Shirts', 'SHIRT', 'maharashtra', 'https://images.unsplash.com/photo-1598033129183-c4f50c736f10?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80', '["S", "M", "L", "XL", "XXL"]', '["White", "Blue", "Black", "Grey"]', 'Formal Wear', 'Premium cotton formal shirt'),
('Men\'s T-Shirt', 699.00, 'MEN', 'T-Shirts', 'TSHIRT', 'karnataka', 'https://images.unsplash.com/photo-1529374255404-311a2a4f1fd9?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80', '["S", "M", "L", "XL"]', '["Black", "White", "Navy", "Grey"]', 'Casual Wear', 'Comfortable cotton t-shirt'),
('Kids Dress', 899.00, 'KIDS', 'Dresses', 'DRESS', 'rajasthan', 'https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcTXuIqARHn0PBZ5mtl8aL-dw5JuB3KMv4krLMGvJtRQdccV9-12SapqQgJO_ijXn_0xrRznGMBzTcPxaKlBMPBoMc7j85PqZ-MXIosMOTHj', '["2Y", "3Y", "4Y", "5Y"]', '["Pink", "Blue", "Yellow"]', 'Kids Fashion', 'Cute party dress for kids'),
('Kids Ethnic Wear', 1199.00, 'KIDS', 'Ethnic Wear', 'ETHNIC_WEAR', 'gujarat', 'https://i.pinimg.com/originals/c9/f5/6b/c9f56b1f42a094174d80ba83fb779061.jpg', '["2Y", "3Y", "4Y", "5Y", "6Y"]', '["Red", "Green", "Blue"]', 'Ethnic Kids', 'Traditional ethnic wear for children');
