# Full-Stack Banking App

A production-ready banking API demonstrating real authentication, role-based access control, and atomic money transfers.

**Stack:** Java 21 LTS + Spring Boot 4.0.7, PostgreSQL 16, JWT authentication, Docker,
Maven.

## Live Demo

*Coming soon at bank.yourdomain.dev*

## Local Setup

### Prerequisites
- Java 21 (LTS)
- Maven 3.9+
- Docker Desktop

## Start PostgreSQL
``` bash
docker compose up -d
```

## Run the application
``` bash
cd backend
export JWT_SECRET=your-secret-here
mvn spring-boot:run
```

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
curl -X GET http://localhost:8080/api/me \
-H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Milestones
| # | Goal | Status |
|---|------|--------|
| 1 | Foundation + Auth | Complete |
| 2 | Accounts + RBAC | Complete |
| 3 | Transactions | Complete |
| 4 | Atomic Transfers | Pending |
| 5 | React Frontend | Pending |
| 6 | Tests + Hardening | Pending |
| 7 | Docker + CI/CD + Deploy | Pending |


## Architecture
- Stateless JWT authentication: no server-side sessions
- BCrypt password hashing with automatic salting
- Role-based access control (CUSTOMER / ADMIN)
- PostgreSQL with manual schema management
- Docker Compose for consistent local database

## Security Features
- Hashed passwords (bcrypt)
- JWT tokens with expiration
- Stateless sessions
- Input validation on all endpoints
- Secrets via environment variables

## Metrics
- Transfer latency: TBD
- Test coverage: TBD
- Atomicity: DB transactions with row locking

## Next Steps
- Background job for interest accrual
- Two-factor authentication
- PDF statement generation