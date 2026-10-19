require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const nodemailer = require('nodemailer');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(bodyParser.json());

// Configure your email service
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: process.env.EMAIL_USER, // Your Gmail address from .env
    pass: process.env.EMAIL_PASSWORD // Your App Password from .env
  }
});

// Email verification endpoint
app.post('/api/send-verification-email', async (req, res) => {
  const { email, firstName, code } = req.body;

  try {
    const mailOptions = {
      from: 'TrendAura <your-email@gmail.com>',
      to: email,
      subject: 'Verify Your TrendAura Account',
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2 style="color: #e83e8c;">Welcome to TrendAura, ${firstName}!</h2>
          <p>Thank you for registering with TrendAura! Please verify your email address to complete your registration.</p>
          
          <div style="background: #f8f9fa; padding: 20px; text-align: center; margin: 20px 0; border-radius: 5px;">
            <h3 style="margin: 0; color: #e83e8c;">Your Verification Code</h3>
            <div style="font-size: 24px; font-weight: bold; letter-spacing: 2px; margin: 10px 0;">${code}</div>
            <p style="font-size: 12px; color: #666;">This code will expire in 30 minutes</p>
          </div>
          
          <p>If you didn't request this, please ignore this email.</p>
          
          <p>Happy shopping!<br>
          The TrendAura Team</p>
          
          <hr>
          <p style="font-size: 12px; color: #666;">
            © ${new Date().getFullYear()} TrendAura. All rights reserved.
          </p>
        </div>
      `
    };

    await transporter.sendMail(mailOptions);
    res.json({ success: true, message: 'Verification email sent successfully' });
  } catch (error) {
    console.error('Error sending verification email:', error);
    res.status(500).json({ success: false, message: 'Failed to send verification email' });
  }
});

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});