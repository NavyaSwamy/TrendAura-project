# 👗 TrendAura — State-Aware Fashion Recommendation System

### Discover fashion. Celebrate culture. Shop local.

TrendAura is a full-stack, AI-powered fashion recommendation platform designed to make fashion discovery more personalized, culturally relevant, and accessible. It combines deep learning-based fashion image classification with a state-aware recommendation approach and a marketplace that connects customers with local boutiques and traditional fashion sellers across India.

By considering regional preferences, cultural styles, and fashion categories, TrendAura aims to bridge the gap between India's rich traditional clothing heritage and modern digital shopping experiences.

---

## ✨ Key Features

- 🇮🇳 **State-Aware Fashion Discovery** — Explore fashion inspired by India's states and Union Territories.
- 🧠 **Deep Learning Integration** — Uses a CNN-based image classification approach to recognize fashion categories.
- 🛍️ **Local Boutique Marketplace** — Supports discovering fashion products and regional clothing collections.
- 📸 **Image-Based Fashion Recognition** — Upload fashion images for model-based category prediction.
- 👚 **Traditional & Contemporary Fashion** — Discover sarees, blouses, kurtas, and other fashion categories supported by the dataset.
- 🔎 **Product Discovery** — Browse collections and explore available fashion products.
- ❤️ **Wishlist & Cart** — Save preferred products and manage shopping selections.
- 👤 **User Accounts** — Provides account-related pages and profile functionality.
- 📦 **Order Workflow** — Includes checkout-related pages, payment flow, confirmation, and order details.
- 🏪 **Regional Seller Focus** — Designed to help local fashion businesses showcase culturally relevant products.
- 📱 **Web-Based Experience** — Built with standard web technologies for accessible browser-based interaction.

## 🎯 Problem Statement

India has a diverse fashion heritage, with clothing styles varying across regions, traditions, occasions, and cultural identities. Generic fashion recommendation platforms may not adequately reflect these regional differences or help customers discover local sellers.

TrendAura addresses this challenge by combining fashion image classification, regional context, and an integrated shopping experience.

## 💡 Our Solution

TrendAura brings together three major components:

1. **Fashion Intelligence:** A deep learning model analyzes fashion images and predicts supported clothing categories.
2. **Regional Context:** The platform organizes fashion discovery around state-specific styles and cultural preferences.
3. **E-Commerce Integration:** Customers can browse products, maintain wishlists and carts, and access order-related workflows.

The objective is to make fashion recommendations more relevant while creating opportunities for regional fashion sellers.

## 🏗️ System Architecture

```text
                  ┌─────────────────────────┐
                  │         USER            │
                  └────────────┬────────────┘
                               │
                               ▼
                  ┌─────────────────────────┐
                  │   FRONTEND WEB APP      │
                  │     HTML, CSS, JS       │
                  └────────────┬────────────┘
                               │
                               ▼
                  ┌─────────────────────────┐
                  │   BACKEND / REST API    │
                  │      Java / Node.js     │
                  └────────────┬────────────┘
                               │
                    ┌──────────┴──────────┐
                    ▼                     ▼
          ┌─────────────────┐  ┌──────────────────┐
          │   MySQL Database│  │ ML Prediction API│
          │  Products,      │  │ Python / CNN     │
          │  Users, Orders  │  │ Image Categories │
          └─────────────────┘  └──────────────────┘
```

*Architecture is a conceptual overview. Actual service connections depend on the current project configuration.*

## 🛠️ Technology Stack

| Component | Technologies |
|---|---|
| Frontend | HTML5, CSS3, JavaScript |
| Backend | Java, Spring Boot, Node.js |
| Database | MySQL |
| Machine Learning | Python, TensorFlow, CNN |
| API Integration | REST APIs |
| Version Control | Git, GitHub |

*The repository may contain additional tools and services. Verify which components are configured and actively connected before describing them as deployed integrations.*

## 🧠 Machine Learning Approach

TrendAura incorporates a deep learning-based image classification approach for fashion recognition.

**Workflow:**

1. Collect and prepare fashion image data.
2. Preprocess images into the model's required input format.
3. Train a CNN-based image classification model.
4. Evaluate the model on validation data.
5. Use the trained model to predict supported fashion categories.
6. Integrate predictions into the fashion discovery workflow.

**Fashion categories include examples such as:**
- Sarees
- Blouses
- Women's kurtas

The actual categories depend on the model's configured dataset and trained classes.

## 🗂️ Project Structure

```text
Final-TrendAura/
│
├── trendaura-frontend/
│   ├── index.html
│   ├── about.html
│   ├── collections.html
│   ├── login.html
│   ├── cart.html
│   ├── wishlist1.html
│   ├── payment.html
│   ├── profile.html
│   ├── order-details.html
│   ├── js/
│   │   ├── api.js
│   │   ├── api-client.js
│   │   ├── auth-helper.js
│   │   └── storage-manager.js
│   └── package-lock.json
│
├── trendaura-backend/
│   └── src/
│       └── main/
│           └── resources/
│               └── schema.sql
│
├── .gitignore
└── README.md
```

*This is a simplified illustration of the repository. The exact directory structure may differ.*

## 🚀 Getting Started

### Prerequisites

Install the tools required by the components you intend to run:

- Git
- Java Development Kit (JDK 17, if required by the backend)
- Maven, or the project's Maven Wrapper if included
- Node.js and npm, if required by the frontend or Node.js services
- MySQL Server
- Python and the required ML dependencies, if running the prediction service

### 1. Clone the Repository

```bash
git clone https://github.com/NavyaSwamy/TrendAura-project.git
cd TrendAura-project
```

### 2. Configure the Database

1. Start your MySQL server.
2. Create the database specified in the backend configuration.
3. Review `trendaura-backend/src/main/resources/schema.sql`.
4. Execute the schema against the intended database.
5. Configure database credentials using environment variables or a local configuration file.

Never commit real passwords, access tokens, or production credentials.

### 3. Configure and Run the Backend

Navigate to the backend directory:

```bash
cd trendaura-backend
```

If a Maven `pom.xml` is present, build the project:

```bash
mvn clean package
```

Run the application using the project's configured Spring Boot entry point or Maven configuration.

```bash
mvn spring-boot:run
```

Confirm the configured database connection, application port, and required environment variables before starting.

### 4. Run the Frontend

Open a new terminal and navigate to the frontend:

```bash
cd trendaura-frontend
```

If a `package.json` is present, install dependencies:

```bash
npm install
```

Start the frontend using the script defined in `package.json`. If the project consists of standalone HTML pages, serve the directory using a local web server instead.

### 5. Configure the Machine Learning Service

If the Python prediction service is included, install its documented dependencies, configure the trained model and dataset paths, and start the service using its actual entry point.

Ensure that the frontend or backend calls the correct prediction API endpoint.

## 🔐 Security Best Practices

- Keep `.env` files and credentials out of Git.
- Use a `.gitignore` file for generated files, dependencies, and local configuration.
- Validate user input on the backend.
- Protect account, payment, and order-related endpoints.
- Never store plaintext passwords.
- Use secure transport and appropriate access controls when deploying.

## 🌱 Sustainable Development Goals

TrendAura is aligned with the following United Nations Sustainable Development Goals:

- **SDG 8 — Decent Work and Economic Growth:** Supports the visibility of local boutiques and regional fashion businesses.
- **SDG 9 — Industry, Innovation and Infrastructure:** Applies AI and full-stack technologies to fashion discovery.
- **SDG 12 — Responsible Consumption and Production:** Encourages the discovery of regional fashion and traditional clothing.

These are intended project contributions, not claims of independently measured social or environmental impact.

## 🔮 Future Enhancements

- Improve recommendation personalization using user interactions and product preferences.
- Expand and validate the regional fashion dataset.
- Improve model accuracy, robustness, and inference efficiency.
- Add seller dashboards and inventory management.
- Integrate secure payment gateways and order tracking.
- Deploy the application using scalable cloud infrastructure.
- Add accessibility, multilingual support, and improved mobile responsiveness.

## 👩‍💻 Research & Development

TrendAura was developed as a research-oriented project exploring the integration of deep learning, culturally informed fashion discovery, and full-stack web technologies.

**Research Paper:** *Trendaura: A State-Aware Fashion Recommendation System Using Deep Learning and Full-Stack Web Integration*

**Conference:** IEEE ICSIT 2026

Add the official paper link, DOI, or publication details here once available.

## 👥 Contributors

Developed collaboratively by the TrendAura project team at B. V. Raju Institute of Technology (BVRIT), Telangana, India.

- **Project Lead / Contributor:** Navya Swamy

## 📄 License

A license has not yet been specified. Add an appropriate `LICENSE` file before distributing or reusing this project publicly.

---

### Made with ❤️ to connect fashion, culture, and technology.

**TrendAura — Where tradition meets trend.**
