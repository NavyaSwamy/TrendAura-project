const express = require('express');
const nodemailer = require('nodemailer');
const app = express();

app.use(express.json());

// Email configuration
const transporter = nodemailer.createTransport({
    service: 'Gmail', // or your email service
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    }
});

app.post('/send-login-notification', async (req, res) => {
    const { email } = req.body;

    try {
        await transporter.sendMail({
            from: '"TrendAura" <noreply@trendaura.com>',
            to: email,
            subject: 'Successful Login Notification',
            html: `
                <h1>Login Successful</h1>
                <p>You have successfully logged in to your TrendAura account.</p>
                <p>If this wasn't you, please contact our support team immediately.</p>
                <p>Thank you,</p>
                <p>The TrendAura Team</p>
            `
        });

        res.json({ success: true });
    } catch (error) {
        console.error('Error sending email:', error);
        res.status(500).json({ error: 'Failed to send notification email' });
    }
});

app.listen(3000, () => console.log('Server running on port 3000'));
