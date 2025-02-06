# Hazcom SSO Service

Spring Boot service implementing SAML Single Sign-On (SSO) authentication for the Hazcom website using Azure AD as the identity provider.

## Features

- SAML 2.0 Authentication with Azure AD
- Configurable SSO endpoint URL
- Configurable session timeout
- CORS configuration for Hazcom website
- Comprehensive error handling
- Secure session management

## Configuration

### Environment Variables

- `SESSION_TIMEOUT`: Session timeout duration (default: 10m)
  - Format: {number}{unit} where unit can be s (seconds), m (minutes), h (hours)
  - Example: `15m` for 15 minutes

- `SSO_ENDPOINT_URL`: SAML SSO endpoint URL (default: /login/saml2/sso/azure-ad)
  - Example: `/custom/saml/endpoint`

- `HAZCOM_WEBSITE_URL`: Allowed origin for CORS (default: http://localhost:3000)
  - Example: `https://hazcom.example.com`

### SAML Configuration

The service requires the following SAML certificates and keys in the `src/main/resources/saml` directory:

- `azure-ad.crt`: Azure AD's certificate for signature verification
- `private.key`: Service Provider's private key
- `public.crt`: Service Provider's public certificate

## Building and Running

### Using Maven

```bash
# Build the project
mvn clean package

# Run with default configuration
java -jar target/sso-service-0.0.1-SNAPSHOT.jar

# Run with custom configuration
export SESSION_TIMEOUT=15m
export SSO_ENDPOINT_URL=/custom/saml/endpoint
export HAZCOM_WEBSITE_URL=https://hazcom.example.com
java -jar target/sso-service-0.0.1-SNAPSHOT.jar
```

### Using Docker

The application can be run using Docker with the provided Dockerfile:

```bash
# Build the Docker image
docker build -t hazcom-sso .

# Run with default configuration
docker run -p 8080:8080 hazcom-sso

# Run with custom configuration
docker run -p 8080:8080 \
  -e SESSION_TIMEOUT=15m \
  -e SSO_ENDPOINT_URL=/custom/saml/endpoint \
  -e HAZCOM_WEBSITE_URL=https://hazcom.example.com \
  -v $(pwd)/src/main/resources/saml:/app/saml \
  hazcom-sso
```

### Using Docker Compose

For easier deployment, use Docker Compose:

```bash
# Start the service with default configuration
docker-compose up -d

# Start with custom configuration (set environment variables first)
export SESSION_TIMEOUT=15m
export SSO_ENDPOINT_URL=/custom/saml/endpoint
export HAZCOM_WEBSITE_URL=https://hazcom.example.com
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the service
docker-compose down
```

Make sure to place your SAML certificates in the `src/main/resources/saml` directory:
- `azure-ad.crt`: Azure AD's certificate
- `private.key`: Service Provider's private key
- `public.crt`: Service Provider's public certificate

## API Endpoints

- `GET /`: Welcome page (publicly accessible)
- `GET /auth/user`: Get current authenticated user details (requires authentication)
- `GET /maxcomsc/login`: Initiate SAML SSO login flow
- `POST {SSO_ENDPOINT_URL}`: SAML SSO endpoint (default: /login/saml2/sso/azure-ad)

### Testing API Endpoints

A test script is provided to help you test the SSO service API endpoints:

```bash
# Set required environment variables
export SSO_SERVICE_URL=http://localhost:8080  # Replace with your SSO service URL
export TEST_USER=user@yourdomain.com         # Replace with your test user email

# Make the script executable
chmod +x test-sso-api.sh

# Run the tests
./test-sso-api.sh
```

Expected responses:
1. Welcome endpoint (`GET /`):
   - Status: 200 OK
   - Response: "Welcome to Hazcom SSO Service"

2. User info endpoint (`GET /auth/user`):
   - Without authentication: 302 Redirect to SAML login
   - With valid session: 200 OK with user details
   ```json
   {
     "name": "user@example.com",
     "authorities": ["ROLE_USER"]
   }
   ```

3. SAML login endpoint (`GET /maxcomsc/login`):
   - Status: 302 Redirect to Azure AD login page

Note: SAML authentication requires proper IdP configuration and browser interaction. The test script demonstrates the API flow but cannot complete the full authentication process.

### Testing with Specific User

A separate test script is provided for testing with specific user credentials:

```bash
# Set required environment variables
export SSO_SERVICE_URL=http://localhost:8080  # Replace with your SSO service URL
export TEST_USER=user@yourdomain.com         # Replace with your test user email

# Make the script executable
chmod +x test-specific-user.sh

# Run the tests
./test-specific-user.sh
```

Note: For testing with a specific user (e.g., testuser1@providence.org):
1. Ensure your IdP (Identity Provider) has the test user configured
2. Set the TEST_USER environment variable to the specific user's email
3. Configure your browser to trust the SSO service's SSL certificate if using HTTPS
4. Follow the browser prompts to complete the SAML authentication flow

The script will guide you through:
1. Testing the public endpoint
2. Initiating SAML login with specific user credentials
3. Verifying successful authentication
4. Testing session management

Important: When testing with specific users:
- Use the browser for SAML authentication flow
- The service expects valid credentials from your IdP
- Session timeout is set to 10 minutes by default
- After successful login, the /auth/user endpoint will return user details
