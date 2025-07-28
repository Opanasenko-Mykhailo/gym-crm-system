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


## Postman Collection

To quickly test the available API endpoints, you can import the provided Postman collection:

1. Open **Postman**.
2. Click on the **"Import"** button (top left).
3. Select the **"File"** tab.
4. Navigate to the file:  
   `src/main/resources/postman/GCA-API-Collection.json`
5. Click **"Open"** to import the collection.

The collection includes predefined requests for authentication, trainers, trainees, and training operations.  
You can modify the environment variables or headers as needed for your local setup.
---