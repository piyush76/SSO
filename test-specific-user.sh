#!/bin/bash

# Configuration
SSO_BASE_URL="${SSO_SERVICE_URL:-http://localhost:8080}/sso"
TEST_USER="${TEST_USER:-user@example.com}"

# Validate required environment variables
if [ -z "${TEST_USER}" ]; then
    echo "Error: TEST_USER environment variable is not set"
    echo "Example: export TEST_USER=user@yourdomain.com"
    exit 1
fi

echo "Testing Hazcom SSO Service with specific user: ${TEST_USER}"
echo "======================================================"

# 1. Test Welcome Endpoint (Public)
echo -e "\n1. Testing Welcome Endpoint"
curl -s "${SSO_BASE_URL}/"

# 2. Initiate SAML Login Flow
echo -e "\n\n2. Initiating SAML Login Flow"
echo "Opening browser for SAML authentication..."
echo "Please complete these steps:"
echo "a) Visit: ${SSO_BASE_URL}/maxcomsc/login"
echo "b) Enter credentials when prompted:"
echo "   Username: ${TEST_USER}"
echo "   Password: (your password)"
echo "c) After successful login, you will be redirected"

# 3. Verify Authentication
echo -e "\n3. To verify authentication after login:"
echo "Run: curl -v ${SSO_BASE_URL}/auth/user"
echo "Expected successful response:"
echo '{
  "name": "'${TEST_USER}'",
  "authorities": ["ROLE_USER"]
}'

# 4. Session Management
echo -e "\n4. Session Management:"
echo "- Session will timeout after 10 minutes of inactivity"
echo "- To test session expiry, wait 10 minutes and try:"
echo "  curl -v ${SSO_BASE_URL}/auth/user"
echo "- Expected response after timeout: 302 redirect to login"

echo -e "\nNote: SAML authentication requires browser interaction."
echo "The script provides instructions for manual testing steps."
