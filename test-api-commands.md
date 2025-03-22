# Test API Commands

## Health Check

```bash
curl -X GET http://localhost:8080/api/public/health
```

## Create Test User

```bash
curl -X POST \
  http://localhost:8080/api/public/test/users \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "deniz.altun@example.com",
    "name": "Deniz Altun",
    "imageUrl": "https://example.com/avatar.jpg"
}'
```

Example successful response:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "deniz.altun@example.com",
  "name": "Deniz Altun",
  "imageUrl": "https://example.com/avatar.jpg",
  "createdAt": "2023-05-25T10:30:45.123456",
  "lastLoginAt": "2023-05-25T10:30:45.123456"
}
```

## Get All Test Users

```bash
curl -X GET http://localhost:8080/api/public/test/users
```

Example response:
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "email": "deniz.altun@example.com",
    "name": "Deniz Altun",
    "imageUrl": "https://example.com/avatar.jpg",
    "createdAt": "2023-05-25T10:30:45.123456",
    "lastLoginAt": "2023-05-25T10:30:45.123456"
  }
]
```

## Get OAuth2 Login Options

```bash
curl -X GET http://localhost:8080/api/public/oauth2/login-options
```

Example response:
```json
{
  "options": [
    {
      "provider": "google",
      "url": "/oauth2/authorization/google",
      "name": "Google"
    }
  ]
}
```

## Get User by ID (Authentication Required)

```bash
# Replace the UUID with one from your database
curl -X GET http://localhost:8080/api/users/123e4567-e89b-12d3-a456-426614174000 \
  -H 'Cookie: JSESSIONID=your_session_id'
```

## Using the Real OAuth2 Login (Browser Flow)

1. Open your browser and navigate to:
   ```
   http://localhost:8080/oauth2/authorization/google
   ```

2. After successful login, you will be redirected to:
   ```
   http://localhost:8080/api/auth/login/success
   ```

3. To use the JSESSIONID cookie in subsequent curl commands, copy it from your browser's developer tools (Network tab) after successful login.

## Check Current User (Authentication Required)

```bash
curl -X GET \
  http://localhost:8080/api/users/me \
  -H 'Cookie: JSESSIONID=your_session_id'
```

## Logout (Authentication Required)

```bash
curl -X POST \
  http://localhost:8080/api/auth/logout \
  -H 'Cookie: JSESSIONID=your_session_id'
```

## Complete Test Flow (For Local Testing)

1. Start the application: `mvn spring-boot:run`
2. Create a test user:
   ```bash
   curl -X POST \
     http://localhost:8080/api/public/test/users \
     -H 'Content-Type: application/json' \
     -d '{
       "email": "deniz.altun@example.com",
       "name": "Deniz Altun",
       "imageUrl": "https://example.com/avatar.jpg"
     }'
   ```
3. Verify you can get the list of users:
   ```bash
   curl -X GET http://localhost:8080/api/public/test/users
   ```
4. Note the UUID from the response to use in subsequent API calls if needed. 