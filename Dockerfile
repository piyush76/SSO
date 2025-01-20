# Build stage
FROM maven:3.9.6-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Create directory for SAML certificates
RUN mkdir -p /app/saml

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy SAML certificates
COPY src/main/resources/saml/ /app/saml/

# Environment variables with defaults
ENV SERVER_PORT=8080 \
    SESSION_TIMEOUT=10m \
    SSO_ENDPOINT_URL=/login/saml2/sso/azure-ad \
    HAZCOM_WEBSITE_URL=http://localhost:3000

# Expose the application port
EXPOSE ${SERVER_PORT}

# Run the application
ENTRYPOINT ["java", \
    "-Dserver.port=${SERVER_PORT}", \
    "-Dapp.session.timeout=${SESSION_TIMEOUT}", \
    "-Dapp.sso.endpoint-url=${SSO_ENDPOINT_URL}", \
    "-Dcors.allowed-origins=${HAZCOM_WEBSITE_URL}", \
    "-jar", "/app/app.jar"]
