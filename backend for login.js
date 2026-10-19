// server.js - Complete Backend Implementation

require('dotenv').config();
const express = require('express');
const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const bodyParser = require('body-parser');
const cors = require('cors');
const path = require('path');
const multer = require('multer');
const fs = require('fs');

// Create Express app
const app = express();

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Database Configuration
const pool = mysql.createPool({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'trendaura',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// Configure file upload
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        // Create uploads directory if it doesn't exist
        if (!fs.existsSync('uploads')) {
            fs.mkdirSync('uploads');
        }
        cb(null, 'uploads/');
    },
    filename: (req, file, cb) => {
        cb(null, `${Date.now()}-${file.originalname}`);
    }
});

const upload = multer({ storage });

// ==================== MODELS ====================

// User Model
class User {
    static async create({ firstName, lastName, email, password }) {
        const hashedPassword = await bcrypt.hash(password, 10);
        const [result] = await pool.query(
            'INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)',
            [firstName, lastName, email, hashedPassword]
        );
        return result.insertId;
    }

    static async findByEmail(email) {
        const [rows] = await pool.query('SELECT * FROM users WHERE email = ?', [email]);
        return rows[0];
    }

    static async findById(id) {
        const [rows] = await pool.query('SELECT * FROM users WHERE id = ?', [id]);
        return rows[0];
    }

    static async comparePasswords(password, hashedPassword) {
        return await bcrypt.compare(password, hashedPassword);
    }

    static generateToken(userId) {
        return jwt.sign({ id: userId }, process.env.JWT_SECRET || 'your_jwt_secret_key', {
            expiresIn: '30d'
        });
    }
}

// Profile Model
class Profile {
    static async create(userId) {
        const [result] = await pool.query(
            'INSERT INTO user_profiles (user_id) VALUES (?)',
            [userId]
        );
        return result.insertId;
    }

    static async findByUserId(userId) {
        const [rows] = await pool.query(
            `SELECT u.id, u.first_name, u.last_name, u.email, u.created_at,
                    up.profile_picture, up.bio, up.location, up.website
             FROM users u
             LEFT JOIN user_profiles up ON u.id = up.user_id
             WHERE u.id = ?`,
            [userId]
        );
        return rows[0];
    }

    static async update(userId, { profile_picture, bio, location, website }) {
        await pool.query(
            `INSERT INTO user_profiles (user_id, profile_picture, bio, location, website)
             VALUES (?, ?, ?, ?, ?)
             ON DUPLICATE KEY UPDATE
             profile_picture = VALUES(profile_picture),
             bio = VALUES(bio),
             location = VALUES(location),
             website = VALUES(website)`,
            [userId, profile_picture, bio, location, website]
        );
    }
}

// ==================== MIDDLEWARE ====================

// Authentication middleware
const protect = async (req, res, next) => {
    let token;
    
    if (req.headers.authorization && req.headers.authorization.startsWith('Bearer')) {
        token = req.headers.authorization.split(' ')[1];
    }
    
    if (!token) {
        return res.status(401).json({ message: 'Not authorized, no token' });
    }
    
    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET || 'your_jwt_secret_key');
        req.user = await User.findById(decoded.id);
        next();
    } catch (err) {
        res.status(401).json({ message: 'Not authorized, token failed' });
    }
};

// Error handling middleware
const errorHandler = (err, req, res, next) => {
    console.error(err.stack);
    res.status(500).json({ 
        success: false,
        message: 'Something went wrong on the server'
    });
};

// ==================== CONTROLLERS ====================

// Auth Controller
const authController = {
    register: async (req, res, next) => {
        try {
            const { firstName, lastName, email, password } = req.body;
            
            // Check if user already exists
            const existingUser = await User.findByEmail(email);
            if (existingUser) {
                return res.status(400).json({ message: 'Email already in use' });
            }
            
            // Create user
            const userId = await User.create({ firstName, lastName, email, password });
            
            // Create empty profile
            await Profile.create(userId);
            
            // Generate token
            const token = User.generateToken(userId);
            
            res.status(201).json({
                success: true,
                token,
                userId
            });
        } catch (err) {
            next(err);
        }
    },

    login: async (req, res, next) => {
        try {
            const { email, password } = req.body;
            
            // Check if user exists
            const user = await User.findByEmail(email);
            if (!user) {
                return res.status(401).json({ message: 'Invalid credentials' });
            }
            
            // Check password
            const isMatch = await User.comparePasswords(password, user.password);
            if (!isMatch) {
                return res.status(401).json({ message: 'Invalid credentials' });
            }
            
            // Generate token
            const token = User.generateToken(user.id);
            
            res.json({
                success: true,
                token,
                userId: user.id
            });
        } catch (err) {
            next(err);
        }
    },

    getMe: async (req, res, next) => {
        try {
            const user = await Profile.findByUserId(req.user.id);
            if (!user) {
                return res.status(404).json({ message: 'User not found' });
            }
            
            res.json({
                success: true,
                data: user
            });
        } catch (err) {
            next(err);
        }
    }
};

// Profile Controller
const profileController = {
    getProfile: async (req, res, next) => {
        try {
            const profile = await Profile.findByUserId(req.params.userId);
            if (!profile) {
                return res.status(404).json({ message: 'Profile not found' });
            }
            
            res.json({
                success: true,
                data: profile
            });
        } catch (err) {
            next(err);
        }
    },

    updateProfile: async (req, res, next) => {
        try {
            const userId = req.user.id;
            let profilePicture;
            
            if (req.file) {
                profilePicture = `/uploads/${req.file.filename}`;
                
                // Delete old profile picture if exists
                const oldProfile = await Profile.findByUserId(userId);
                if (oldProfile.profile_picture) {
                    const oldPath = path.join(__dirname, oldProfile.profile_picture);
                    if (fs.existsSync(oldPath)) {
                        fs.unlinkSync(oldPath);
                    }
                }
            }
            
            await Profile.update(userId, {
                profile_picture: profilePicture,
                bio: req.body.bio,
                location: req.body.location,
                website: req.body.website
            });
            
            const updatedProfile = await Profile.findByUserId(userId);
            
            res.json({
                success: true,
                data: updatedProfile
            });
        } catch (err) {
            next(err);
        }
    }
};

// ==================== ROUTES ====================

// Auth Routes
app.post('/api/auth/register', authController.register);
app.post('/api/auth/login', authController.login);
app.get('/api/auth/me', protect, authController.getMe);

// Profile Routes
app.get('/api/profile/:userId', profileController.getProfile);
app.put('/api/profile', protect, upload.single('profile_picture'), profileController.updateProfile);

// Error handling
app.use(errorHandler);

// Start server
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    console.log(`Database: ${process.env.DB_NAME || 'trendaura'}`);
});

// Create database tables if they don't exist
async function initializeDatabase() {
    try {
        await pool.query(`
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
        `);
        
        await pool.query(`
            CREATE TABLE IF NOT EXISTS user_profiles (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                profile_picture VARCHAR(255),
                bio TEXT,
                location VARCHAR(100),
                website VARCHAR(100),
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        `);
        
        console.log('Database tables initialized');
    } catch (err) {
        console.error('Error initializing database:', err);
    }
}

initializeDatabase();