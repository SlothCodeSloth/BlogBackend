# Personal Blog Backend (Spring Boot)

A secure REST API backend for a personal blog and project portfolio website, featuring JWT authentication, post management, and image uploading.

## 🎯 Overview

This Spring Boot application serves as the backend for a full stack blog platform, and provides authenticated CRUD operations for posts, image upload functionality, and secure user authentication via JWT tokens. The API is consumed by a React frontend and can be used for containerized deployment.

---

## ✨ Features

- **🔐 JWT Authentication**: Secure token based authentication for administrative functions
- **✍️ Post Management**: Full CRUD operations for blog posts and project entries
- **🖼️ Image Uploads**: File upload and serving functionality for post images/gifs
- **📄 Pagination & Filtering**: Support for paginated results with category filters (blog/projects)
- **🔄 CORS Configuration**: Configured for both local development and production deployment
- **💾 Flexible Database**: Support for H2, MySQL, and PostgreSQL via configuration
- **🛡️ Security**: Spring Security integration with JWT filter chain

---

## 🏗️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Java |
| **Framework** | Spring Boot |
| **Security** | Spring Security + JWT |
| **Data Layer** | Spring Data JPA (Hibernate) |
| **Database** | PostgreSQL / MySQL / H2 |
| **Build Tool** | Maven |
| **Authentication** | JSON Web Tokens (JWT) |

---

## 📂 Project Structure

### Development Structure
```
blog-app/
├── src/
│   ├── java/com/example/blog/           
|   │   ├── BlogAppAppliation.java       # Main entry point; configures startup tasks & CORS
|   │   ├── JwtFilter.java               # Security filter; validates JWT per request
|   │   ├── JwtUtil.java                 # Utility class; generate/validate/parse JWT tokens
|   │   ├── LoginController.java         # Handles /api/auth endpoints; admin login + token issue
|   │   ├── Post.java                    # Entity model; represents a blog post (title, content, etc.)
|   │   ├── PostController.java          # REST API for blog posts; CRUD + pagination + image upload
|   │   ├── PostRepository.java          # JPA repository; built-in CRUD + custom queries
|   │   ├── puppy.java                   # Unused File, can be ignored. Made for fun
|   │   ├── SecurityConfig.java          # Configures Spring Security, JWT filter, access rules
|   │   └── WebConfig.java               # Exposes "uploads" folder for serving images
│   └── resources/                       
|   │   └── application.properties       # App configuration (DB, JWT secret, admin credentials, etc.)
└── pom.xml                              # Maven project definition
```

### Docker Deployment Structure
```
deployment/
├── appdata/
│   ├── blog_backend/
│   │   ├── application.properties         # Production configuration
│   │   ├── blog-app.jar                   # Compiled Spring Boot JAR
│   │   └── uploads/                       # Persistent image storage
│   └── postgres_blog/                     # PostgreSQL data volume
└── docker-compose.yml                     # Container orchestration
```

---

## 🚀 Getting Started

### Prerequisites
- **Java JDK**: 17 or higher
- **Maven**: 3.8+
- **Database**: PostgreSQL, MySQL, or H2 (in-memory)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/blog-backend.git
   cd blog-backend
   ```

2. **Configure application properties**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   # Server Configuration
   server.port=8090
   
   # Database Configuration (PostgreSQL example)
   spring.datasource.url=Database URL or PORT
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   
   # JWT Configuration
   app.jwt.secret=your-256-bit-secret-key-here
   app.jwt.expiration=86400000
   
   # Admin Credentials
   app.admin.user=admin
   app.admin.pass=secure_password_here
   
   # CORS Origins (comma-separated)
   CORS_ORIGINS=http://localhost:5173,https://your-domain.com
   
   # File Upload
   spring.servlet.multipart.max-file-size=10MB
   spring.servlet.multipart.max-request-size=10MB
   ```

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   The API will be available at `http://localhost:8090/api`

### Alternative: Run with JAR

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/blog-app-0.0.1-SNAPSHOT.jar
```

---

## 🔌 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | Admin login, returns JWT token | No |

**Request Body (Login):**
```json
{
  "username": "admin",
  "password": "your_password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "username": "admin"
  }
}
```

### Posts

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/posts` | Fetch paginated posts | No |
| GET | `/api/posts/{id}` | Fetch single post by ID | No |
| POST | `/api/posts` | Create new post | Yes (JWT) |
| PUT | `/api/posts/{id}` | Update existing post | Yes (JWT) |
| DELETE | `/api/posts/{id}` | Delete post | Yes (JWT) |

**Query Parameters (GET /api/posts):**
- `page` (int): Page number (default: 0)
- `size` (int): Items per page (default: 10)
- `type` (string): Filter by type - "blog" or "project"

**Request Body (Create/Update Post):**
```json
{
  "title": "My Blog Post",
  "content": "<p>Rich text content here</p>",
  "type": "blog",
  "imageUrls": ["image1.jpg", "image2.jpg"]
}
```

### Image Upload

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/posts/upload-image` | Upload image for posts | Yes (JWT) |
| GET | `/api/posts/uploads/{filename}` | Serve uploaded image | No |

**Upload Request:**
- Content-Type: `multipart/form-data`
- Field name: `file`
- Accepted formats: JPG, PNG, GIF
- Max size: 10MB

---

## 🔐 Authentication Flow

1. **Client requests login** via `/api/auth/login` with credentials
2. **Server validates** credentials against configured admin user
3. **Server generates JWT** with expiration time
4. **Client stores token** (localStorage/sessionStorage)
5. **Client includes token** in `Authorization: Bearer <token>` header for protected routes
6. **JwtFilter validates token** on each protected request
7. **Token expires** after configured duration

---

## 🛡️ Security Configuration

### Implemented Security Measures
- **JWT Token Authentication**: Stateless authentication with configurable expiration
- **CORS Configuration**: Whitelised origin control
- **Request Filtering**: JWT validation on protected endpoints
- **SQL Injection Prevention**: JPA parameterized queries
- **XSS Protection**: Content sanitization on frontend (DOMPurify)

### Protected Routes
All endpoints under `/api/posts` (except GET requests) require a valid JWT token in the Authorization header.

---

## 🐳 Docker Deployment

### Building for Production

1. **Package the application**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Copy JAR to deployment directory**
   ```bash
   cp target/blog-app-0.0.1-SNAPSHOT.jar deployment/appdata/blog_backend/blog-app.jar
   ```

3. **Configure production properties**
   
   Copy the previous `application.properties` into the same directory as the .jar file

4. **Run with Docker Compose**

---

## 🔗 Related Repositories

- **Frontend**: [Personal Website Frontend](https://github.com/SlothCodeSloth/BlogFrontend)
- **Docker Setup**: [Home Server Configuration](https://github.com/SlothCodeSloth/DockerServer)

---

## 🚀 Future Enhancements

- [ ] Comment system for blog posts
- [ ] Tag/category management
- [ ] Search functionality with Elasticsearch

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
