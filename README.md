# Sport Clubs Application

## Development Setup

### Prerequisites
- JDK 21
- Maven
- Docker and Docker Compose

### Setting up the Environment

1. Clone the repository
2. Copy the example environment file:
   ```
   cp .env.example .env
   ```
3. Modify the `.env` file with your specific configurations if needed

### Running PostgreSQL with Docker Compose

Start the PostgreSQL database:
```
docker-compose -f docker-compose-dev.yml up -d
```

This will start a PostgreSQL container with the configuration specified in your `.env` file.

### Running the Spring Boot Application

You can run the application using Maven:
```
./mvnw spring-boot:run
```

The application will use environment variables from the `.env` file to connect to the PostgreSQL database running in Docker.

### Database Migrations

This project uses Liquibase for database migrations. Migrations are automatically applied when the application starts. 