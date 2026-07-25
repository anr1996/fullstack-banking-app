# Full-Stack Banking App

A full stack banking application demonstrating JWT authentication, role based access control, and atomic money transfers.

**Stack:** Java 21 LTS + Spring Boot 4.0.7, PostgreSQL 16, JWT authentication, Docker,
Maven.

## Local Setup

### Prerequisites
- Java 21 (LTS)
- Maven 3.9+
- Docker Desktop
- Node.js 18+

## Start PostgreSQL
``` bash
docker compose up -d
```

### Run the backend
The backend reads three values from the environment. JWT_SECRET and DB_PASSWORD have no defaults, 
so the application will not start unless they are set. DB_PASSWORD must match POSTGRES_PASSWORD in
docker-compose.yml.

```bash
cd backend
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export JWT_SECRET=$(openssl rand -base64 32)
export DB_PASSWORD=banking_pass
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`. For convenience you can save these exports into a local
'run_backend.sh' script (it is gitignored so the credentials stay out of version control).

### Run the frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173`.


## Test the auth flow
```bash
# Register
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \ 
-d '{"email":"test@example.com","password":"password123",“name”:"Test User"}'

# Login
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"test@example.com","password":"password123"}'

# Access protected endpoint (requires Bearer token)
curl -X GET http://localhost:8080/accounts \
-H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Milestones
| # | Goal | Status |
|---|------|--------|
| 1 | Foundation + Auth | Complete |
| 2 | Accounts + RBAC | Complete |
| 3 | Transactions | Complete |
| 4 | Atomic Transfers | Complete |
| 5 | React Frontend | In Progress |
| 6 | Tests + Hardening | In Progress |
| 7 | Docker + CI/CD + Deploy | Pending |


## Architecture
- Stateless JWT authentication: no server-side sessions
- BCrypt password hashing with automatic salting
- Role-based access control (CUSTOMER / ADMIN)
- 401 for unauthenticated requests, 403 for unauthorized ones
- PostgreSQL with manual schema management
- Docker Compose for consistent local database

## Security Features
- Hashed passwords (bcrypt, cost factor 12)
- JWT tokens with expiration
- Stateless sessions
- Input validation on authentication endpoints
- Secrets via environment variables, with no committed fallback values
- Uniform login errors to prevent account enumeration
- CORS restricted to explicit allowed origins

## Known Limitations and Roadmap
This project is under active development. Current known limitations, being addressed in milestones 6 and 7:
- JWT is stored in browser localStorage. A production deployment would move it to an httpOnly, Secure cookie to reduce XSS exposure, which requires re-enabling CSRF protection.
- Account endpoints need per-user ownership checks to prevent access to other users' data.
- Login has no rate limiting yet.
- Schema is managed manually. Flyway migrations are planned for reproducible schema history.
- Automated test suite and CI pipeline are in progress.
- Production deployment assumes TLS terminated at a reverse proxy.

