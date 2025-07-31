// server.js
require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const multer = require('multer');
const cors = require('cors');
const path = require('path');

// Initialize Express
const app = express();
app.use(cors());
app.use(express.json());
app.use('/uploads', express.static('uploads'));

// MongoDB Connection
mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/collection-db')
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.log(err));

// Collection Model
const Collection = mongoose.model('Collection', new mongoose.Schema({
  name: { type: String, required: true },
  image: { type: String, required: true }
}));

// Multer for Image Uploads
const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, 'uploads/'),
  filename: (req, file, cb) => cb(null, Date.now() + path.extname(file.originalname))
});
const upload = multer({ storage });

// Admin Check Middleware
const isAdmin = (req, res, next) => {
  if (req.headers.admin !== 'true') return res.status(403).json({ error: 'Access denied' });
  next();
};

// Routes
// Admin: Add Collection
app.post('/collections', isAdmin, upload.single('image'), async (req, res) => {
  const { name } = req.body;
  const image = req.file.path;
  const collection = new Collection({ name, image });
  await collection.save();
  res.status(201).json(collection);
});

// User: Get All Collections
app.get('/collections', async (req, res) => {
  res.json(await Collection.find());
});

// Start Server
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));
