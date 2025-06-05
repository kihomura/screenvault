# ScreenVault

Welcome to ScreenVault! This is a video content management system built with Spring Boot and Vue.js, featuring personal movie/TV show collection, rating, and tag management capabilities.

## Technology Stack

### Frontend
- **Vue.js 3** - Progressive JavaScript framework
- **Vite** - Modern build tool
- **Vue Router 4** - Client-side routing
- **Vuex 4** - State management
- **Pinia** - Next-generation state management
- **Bootstrap 5** - CSS framework
- **ECharts** - Data visualization
- **Axios** - HTTP client

### Backend
- **Spring Boot 3.2.3** - Java application framework
- **Spring Security 6** - Security framework
- **Spring OAuth2 Client** - Third-party authentication
- **MyBatis Plus 3.5.9** - ORM framework
- **MySQL 8.0** - Relational database
- **JWT (JJWT 0.12.6)** - Authentication tokens
- **OpenCSV** - CSV data processing

### Development Tools
- **Maven** - Project build management
- **Docker & Docker Compose** - Containerized deployment
- **Lombok** - Java code simplification

## System Requirements

- Docker 20.10+
- Docker Compose 2.0+
- At least 4GB available memory
- At least 10GB available disk space (for database and CSV data)

## 🚀 Quick Start

### 1. Download Git LFS Files (Important!)

After cloning the repository to your local machine, you need to separately download the large CSV files stored using Git LFS:

```bash
# Install Git LFS (if not already installed)
# Windows (included with Git for Windows)
# macOS: brew install git-lfs
# Ubuntu/Debian: sudo apt install git-lfs

# Initialize Git LFS
git lfs install

# Pull LFS files
git lfs pull
```

**Verify LFS files download:**
```bash
# Check file sizes in metadata directory
ls -lh metadata/
# tv_shows.csv should show ~47MB, movies.csv should show ~300MB
```

### 2. Start Project (Without CSV Data Import)

```bash
docker-compose up -d
```

Once all services are running, access:
- Frontend: http://localhost:5173
- Backend API: http://localhost:5555

### 3. Start Project with CSV Data Import

To import CSV data from the `metadata` directory into the database:

#### Modify Docker Compose Configuration

1. Edit the `docker-compose.yml` file and find the backend service environment variables:

```yaml
environment:
  # Uncomment the line below to enable CSV import
  - SPRING_PROFILES_ACTIVE=prod,csv-import
  # Comment out this line
  #- SPRING_PROFILES_ACTIVE=prod
```

2. Restart services:

```bash
docker-compose down
docker-compose up -d
```

### 📁 Supported CSV Files

The project supports importing the following CSV files (located in `metadata` directory):
- `movies.csv` - Movie data
- `tv_shows.csv` - TV show data

You can also import your own CSV files based on your needs. CSV files should contain at least 10 columns of data. Please refer to existing sample files for the specific format.

### Import Progress Monitoring

- Import process logs are output to the `logs` directory
- Monitor import progress with the following commands:

```bash
# View backend service logs
docker-compose logs -f backend

# View real-time logs
tail -f logs/spring.log
```

### Resume Interrupted Import

If the import process is interrupted, you can continue from where it left off:

1. Check logs to determine the last successfully imported row number
2. Modify startup parameters (requires custom Docker configuration)

### Port Configuration

- **Frontend**: 5173
- **Backend**: 5555  
- **MySQL Database**: 3307 (mapped to container port 3306)

## 💻 Local Development Environment Setup

### Prerequisites
- **Java 17+** (JDK 17 or higher)
- **Node.js 18+** and **npm**
- **MySQL 8.0+**
- **Maven 3.6+**
- **Git**

Ensure all the above software is installed on your computer.

### 1. Database Configuration

```sql
-- Login to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE screen_vault CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

The backend application will automatically create corresponding tables in the database when running.

### 2. Environment Configuration

#### Backend Configuration
Create `server/src/main/resources/application-local.properties`. You can refer to the example.txt file in the same directory.

OAuth2 is used for third-party login. If you don't need this functionality, you can ignore it.

#### Frontend Configuration
Create `client/.env.local`:
```env
VITE_API_URL=http://localhost:5555
```

### 3. Project Startup

#### Backend Startup
```bash
cd server

# Install dependencies and compile
mvn clean compile

# Start application
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

To import CSV files:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,csv-import
```

#### Frontend Startup
```bash
cd client

# Install dependencies
npm install

# Start development server
npm run dev
```

### 4. Access Application
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:5555