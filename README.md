# Gym CRM System

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK) 17**
- **Maven**
- **Apache Tomcat 9+**
- **Git**

## Setup Instructions

Run the following SQL script to create the database and user:

```sql
CREATE DATABASE "gym_db";
CREATE USER gcs WITH PASSWORD 'gcs';
GRANT ALL PRIVILEGES ON DATABASE "gym_db" TO gcs;
```

## API Documentation

Swagger UI is available at:

👉 [http://localhost:8080/gym-crm/swagger-ui/index.html](http://localhost:8080/gym-crm/swagger-ui/index.html)

It provides interactive documentation for all available API endpoints.

---