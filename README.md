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

- `GET /`: Welcome page
- `GET /auth/user`: Get current authenticated user details
- `POST {SSO_ENDPOINT_URL}`: SAML SSO endpoint (default: /login/saml2/sso/azure-ad)
