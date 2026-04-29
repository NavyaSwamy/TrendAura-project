// Chatbot Widget Implementation
class ChatbotWidget {
    constructor() {
        this.sessionId = null;
        this.isOpen = false;
        this.messages = [];
        this.suggestionInterval = null;
        this.init();
    }

    init() {
        this.createChatWidget();
        this.setupEventListeners();
        this.startSuggestionRotation();
        this.loadOrCreateSession();
    }

    createChatWidget() {
        // Create chat widget HTML
        const chatWidget = document.createElement('div');
        chatWidget.id = 'chatbot-widget';
        chatWidget.innerHTML = `
            <div class="chat-widget-container">
                <!-- Chat Toggle Button -->
                <div class="chat-toggle-btn" id="chat-toggle">
                    <i class="fas fa-comments"></i>
                    <span class="chat-notification">3</span>
                </div>
                
                <!-- Chat Box -->
                <div class="chat-box" id="chat-box">
                    <!-- Chat Header -->
                    <div class="chat-header">
                        <div class="chat-header-info">
                            <img src="https://img.icons8.com/color/48/000000/customer-support.png" alt="Support" class="chat-avatar">
                            <div>
                                <h4>TrendAura Support</h4>
                                <span class="chat-status online">We're online</span>
                            </div>
                        </div>
                        <button class="chat-close-btn" id="chat-close">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    
                    <!-- Chat Messages -->
                    <div class="chat-messages" id="chat-messages">
                        <div class="chat-welcome-message">
                            <p>👋 Welcome to TrendAura! How can I help you today?</p>
                        </div>
                    </div>
                    
                    <!-- Chat Suggestions -->
                    <div class="chat-suggestions" id="chat-suggestions">
                        <div class="suggestion-text">Need help? Chat with us!</div>
                    </div>
                    
                    <!-- Chat Input -->
                    <div class="chat-input-container">
                        <div class="chat-input-wrapper">
                            <input type="text" id="chat-input" placeholder="Type your message..." maxlength="500">
                            <button id="chat-send-btn" class="chat-send-btn">
                                <i class="fas fa-paper-plane"></i>
                            </button>
                        </div>
                        <div class="chat-input-actions">
                            <button class="chat-action-btn" id="clear-chat-btn">
                                <i class="fas fa-trash"></i> Clear
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Add chat widget styles
        this.addStyles();
        
        // Append to body
        document.body.appendChild(chatWidget);
    }

    addStyles() {
        const styles = `
            #chatbot-widget {
                position: fixed;
                bottom: 20px;
                right: 20px;
                z-index: 9999;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .chat-widget-container {
                position: relative;
            }

            .chat-toggle-btn {
                width: 60px;
                height: 60px;
                background: linear-gradient(135deg, #e83e8c, #d2337a);
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                box-shadow: 0 4px 20px rgba(232, 62, 140, 0.3);
                transition: all 0.3s ease;
                position: relative;
            }

            .chat-toggle-btn:hover {
                transform: scale(1.1);
                box-shadow: 0 6px 25px rgba(232, 62, 140, 0.4);
            }

            .chat-toggle-btn i {
                color: white;
                font-size: 24px;
            }

            .chat-notification {
                position: absolute;
                top: -5px;
                right: -5px;
                background: #ff4757;
                color: white;
                border-radius: 50%;
                width: 20px;
                height: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 11px;
                font-weight: bold;
                animation: pulse 2s infinite;
            }

            @keyframes pulse {
                0% { transform: scale(1); }
                50% { transform: scale(1.1); }
                100% { transform: scale(1); }
            }

            .chat-box {
                position: absolute;
                bottom: 80px;
                right: 0;
                width: 350px;
                height: 500px;
                background: white;
                border-radius: 12px;
                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                display: none;
                flex-direction: column;
                overflow: hidden;
                animation: slideUp 0.3s ease-out;
            }

            .chat-box.open {
                display: flex;
            }

            @keyframes slideUp {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .chat-header {
                background: linear-gradient(135deg, #e83e8c, #d2337a);
                color: white;
                padding: 15px;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .chat-header-info {
                display: flex;
                align-items: center;
                gap: 12px;
            }

            .chat-avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
            }

            .chat-header h4 {
                margin: 0;
                font-size: 16px;
                font-weight: 600;
            }

            .chat-status {
                font-size: 12px;
                opacity: 0.9;
            }

            .chat-status.online::before {
                content: "● ";
                color: #4cd137;
            }

            .chat-close-btn {
                background: none;
                border: none;
                color: white;
                font-size: 18px;
                cursor: pointer;
                padding: 5px;
                border-radius: 50%;
                transition: background 0.3s;
            }

            .chat-close-btn:hover {
                background: rgba(255, 255, 255, 0.1);
            }

            .chat-messages {
                flex: 1;
                overflow-y: auto;
                padding: 15px;
                background: #f8f9fa;
            }

            .chat-welcome-message {
                text-align: center;
                padding: 20px;
                color: #666;
                font-style: italic;
            }

            .message {
                margin-bottom: 15px;
                animation: fadeIn 0.3s ease-out;
            }

            @keyframes fadeIn {
                from { opacity: 0; transform: translateY(10px); }
                to { opacity: 1; transform: translateY(0); }
            }

            .message.user {
                text-align: right;
            }

            .message.bot {
                text-align: left;
            }

            .message-content {
                display: inline-block;
                max-width: 80%;
                padding: 10px 15px;
                border-radius: 18px;
                word-wrap: break-word;
            }

            .message.user .message-content {
                background: linear-gradient(135deg, #e83e8c, #d2337a);
                color: white;
            }

            .message.bot .message-content {
                background: white;
                color: #333;
                border: 1px solid #e1e8ed;
            }

            .message-time {
                font-size: 11px;
                color: #999;
                margin-top: 5px;
            }

            .chat-suggestions {
                padding: 10px 15px;
                background: white;
                border-top: 1px solid #e1e8ed;
            }

            .suggestion-text {
                text-align: center;
                color: #666;
                font-size: 14px;
                font-style: italic;
                animation: fadeInOut 5s ease-in-out infinite;
            }

            @keyframes fadeInOut {
                0%, 100% { opacity: 0.5; }
                50% { opacity: 1; }
            }

            .chat-input-container {
                padding: 15px;
                background: white;
                border-top: 1px solid #e1e8ed;
            }

            .chat-input-wrapper {
                display: flex;
                gap: 10px;
                margin-bottom: 10px;
            }

            #chat-input {
                flex: 1;
                padding: 10px 15px;
                border: 1px solid #e1e8ed;
                border-radius: 25px;
                outline: none;
                font-size: 14px;
                transition: border-color 0.3s;
            }

            #chat-input:focus {
                border-color: #e83e8c;
            }

            .chat-send-btn {
                width: 40px;
                height: 40px;
                background: linear-gradient(135deg, #e83e8c, #d2337a);
                border: none;
                border-radius: 50%;
                color: white;
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                transition: all 0.3s;
            }

            .chat-send-btn:hover {
                transform: scale(1.1);
            }

            .chat-send-btn:disabled {
                opacity: 0.5;
                cursor: not-allowed;
            }

            .chat-input-actions {
                display: flex;
                justify-content: center;
            }

            .chat-action-btn {
                background: none;
                border: 1px solid #ddd;
                color: #666;
                padding: 5px 10px;
                border-radius: 15px;
                font-size: 12px;
                cursor: pointer;
                transition: all 0.3s;
            }

            .chat-action-btn:hover {
                border-color: #e83e8c;
                color: #e83e8c;
            }

            /* Responsive Design */
            @media (max-width: 480px) {
                .chat-box {
                    width: 300px;
                    height: 450px;
                    right: -10px;
                }
            }
        `;

        const styleSheet = document.createElement('style');
        styleSheet.textContent = styles;
        document.head.appendChild(styleSheet);
    }

    setupEventListeners() {
        // Toggle chat box
        document.getElementById('chat-toggle').addEventListener('click', () => {
            this.toggleChat();
        });

        // Close chat box
        document.getElementById('chat-close').addEventListener('click', () => {
            this.closeChat();
        });

        // Send message
        const sendBtn = document.getElementById('chat-send-btn');
        const input = document.getElementById('chat-input');

        sendBtn.addEventListener('click', () => {
            this.sendMessage();
        });

        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.sendMessage();
            }
        });

        // Clear chat
        document.getElementById('clear-chat-btn').addEventListener('click', () => {
            this.clearChat();
        });

        // Close chat when clicking outside
        document.addEventListener('click', (e) => {
            const chatWidget = document.getElementById('chatbot-widget');
            if (!chatWidget.contains(e.target) && this.isOpen) {
                this.closeChat();
            }
        });
    }

    async loadOrCreateSession() {
        try {
            const storedSessionId = localStorage.getItem('chatSessionId');
            if (storedSessionId) {
                this.sessionId = storedSessionId;
                // Load previous messages
                const history = await getChatHistory(this.sessionId);
                if (history && history.length > 0) {
                    this.messages = history;
                    this.displayMessages();
                }
            } else {
                const session = await createChatSession();
                this.sessionId = session.sessionId;
                localStorage.setItem('chatSessionId', this.sessionId);
            }
        } catch (error) {
            console.error('Failed to load/create chat session:', error);
        }
    }

    toggleChat() {
        if (this.isOpen) {
            this.closeChat();
        } else {
            this.openChat();
        }
    }

    openChat() {
        const chatBox = document.getElementById('chat-box');
        chatBox.classList.add('open');
        this.isOpen = true;
        
        // Focus input
        setTimeout(() => {
            document.getElementById('chat-input').focus();
        }, 300);

        // Log chat activity
        logActivity('Chat Widget', 'OPEN_CHAT');
    }

    closeChat() {
        const chatBox = document.getElementById('chat-box');
        chatBox.classList.remove('open');
        this.isOpen = false;

        // Log chat activity
        logActivity('Chat Widget', 'CLOSE_CHAT');
    }

    async sendMessage() {
        const input = document.getElementById('chat-input');
        const message = input.value.trim();

        if (!message || !this.sessionId) return;

        // Add user message to UI
        this.addMessage(message, 'user');
        input.value = '';

        // Disable send button
        const sendBtn = document.getElementById('chat-send-btn');
        sendBtn.disabled = true;

        try {
            // Log the outgoing chat message for dataset tracking
            logActivity('Chat Widget', 'CHAT_MESSAGE', {
                message: message,
                sessionId: this.sessionId
            });

            // Send to backend
            const response = await sendChatMessage(this.sessionId, message);
            
            // Add bot response
            if (response.message) {
                this.addMessage(response.message, 'bot');
                logActivity('Chat Widget', 'CHAT_RESPONSE', {
                    response: response.message,
                    sessionId: this.sessionId
                });
            }
        } catch (error) {
            console.error('Failed to send message:', error);
            this.addMessage('Sorry, I encountered an error. Please try again.', 'bot');
        } finally {
            // Re-enable send button
            sendBtn.disabled = false;
            input.focus();
        }
    }

    addMessage(text, type) {
        const message = {
            text,
            type,
            timestamp: new Date()
        };

        this.messages.push(message);
        this.displayMessage(message);
    }

    displayMessage(message) {
        const messagesContainer = document.getElementById('chat-messages');
        
        // Remove welcome message if it exists
        const welcomeMsg = messagesContainer.querySelector('.chat-welcome-message');
        if (welcomeMsg) {
            welcomeMsg.remove();
        }

        const messageEl = document.createElement('div');
        messageEl.className = `message ${message.type}`;
        
        const time = new Date().toLocaleTimeString('en-US', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });

        messageEl.innerHTML = `
            <div class="message-content">${message.text}</div>
            <div class="message-time">${time}</div>
        `;

        messagesContainer.appendChild(messageEl);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    displayMessages() {
        const messagesContainer = document.getElementById('chat-messages');
        messagesContainer.innerHTML = '';

        this.messages.forEach(message => {
            this.displayMessage(message);
        });
    }

    clearChat() {
        this.messages = [];
        const messagesContainer = document.getElementById('chat-messages');
        messagesContainer.innerHTML = `
            <div class="chat-welcome-message">
                <p>👋 Welcome to TrendAura! How can I help you today?</p>
            </div>
        `;

        // Clear session
        localStorage.removeItem('chatSessionId');
        this.sessionId = null;
        this.loadOrCreateSession();

        // Log activity
        logActivity('Chat Widget', 'CLEAR_CHAT');
    }

    startSuggestionRotation() {
        const suggestions = [
            "Need help? Chat with us!",
            "Questions about products? Ask me!",
            "Looking for something specific? Let me help!",
            "Need style advice? I'm here!",
            "Questions about orders? Just ask!"
        ];

        let currentIndex = 0;
        const suggestionEl = document.getElementById('chat-suggestions');

        this.suggestionInterval = setInterval(() => {
            if (!this.isOpen) {
                currentIndex = (currentIndex + 1) % suggestions.length;
                suggestionEl.innerHTML = `<div class="suggestion-text">${suggestions[currentIndex]}</div>`;
            }
        }, 5000);
    }

    destroy() {
        if (this.suggestionInterval) {
            clearInterval(this.suggestionInterval);
        }
        const widget = document.getElementById('chatbot-widget');
        if (widget) {
            widget.remove();
        }
    }
}

// Initialize chatbot when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Only initialize on pages other than login
    if (!window.location.pathname.includes('login.html')) {
        window.chatbotWidget = new ChatbotWidget();
    }
});

// Clean up on page unload
window.addEventListener('beforeunload', function() {
    if (window.chatbotWidget) {
        window.chatbotWidget.destroy();
    }
});
