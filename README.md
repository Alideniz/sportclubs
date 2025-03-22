# Sport Clubs Backend

A Spring Boot backend application for the Sport Clubs UI application with Google OAuth2 authentication.

## Features

- User authentication via Google OAuth2
- User management with JPA and PostgreSQL
- RESTful API endpoints for user data
- CORS support for frontend integration
- Session management with JDBC

## Prerequisites

- JDK 21
- Maven
- PostgreSQL database
- Google Developer Console account

## Setup

### Database Setup

1. Make sure PostgreSQL is running
2. Create a database named `sportclubs`

### Google OAuth2 Setup

1. Go to the [Google Developer Console](https://console.developers.google.com/)
2. Create a new project
3. Enable the Google+ API
4. Create OAuth2 credentials (Web application type)
5. Add authorized redirect URIs:
   - `http://localhost:8080/login/oauth2/code/google`
   - Your production URL if applicable
6. Note your Client ID and Client Secret

### Application Configuration

1. Copy the `.env.example` file to `.env`
2. Update the `.env` file with your Google OAuth2 credentials:
   ```
   GOOGLE_CLIENT_ID=your_google_client_id
   GOOGLE_CLIENT_SECRET=your_google_client_secret
   ```
3. Configure other settings as needed (database credentials, etc.)

## Running the Application

### With Maven

```bash
mvn spring-boot:run
```

### With Docker Compose

```bash
docker-compose -f docker-compose-dev.yml up
```

## API Endpoints

### Public Endpoints

- `GET /api/public/health` - Check application health
- `GET /api/public/oauth2/login-options` - Get OAuth2 login options

### Authentication Endpoints

- `GET /oauth2/authorization/google` - Initiate Google OAuth2 login
- `GET /api/auth/login/success` - OAuth2 login success handler
- `GET /api/auth/login/failure` - OAuth2 login failure handler
- `GET /api/auth/logout` - Logout
- `GET /api/auth/user` - Get current authenticated user

### User Endpoints

- `GET /api/users/me` - Get current user
- `GET /api/users/{id}` - Get user by ID

## Frontend Integration

Your frontend application should:

1. Redirect the user to `/oauth2/authorization/google` for login
2. Set up CORS in your frontend to include credentials
3. Use session cookies for authentication

## License

[MIT License](LICENSE) 