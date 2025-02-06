#!/bin/bash

# Configuration
if [ -z "${SSO_SERVICE_URL}" ]; then
    echo "Error: SSO_SERVICE_URL environment variable is not set"
    echo "Example: export SSO_SERVICE_URL=http://localhost:8080"
    exit 1
fi

SSO_BASE_URL="${SSO_SERVICE_URL}/sso"
SAML_ENDPOINT="${SSO_BASE_URL}/login/saml2/sso/azure-ad"

echo "Testing Hazcom SSO Service API Endpoints"
echo "======================================="

# 1. Test Welcome Endpoint
echo -e "\n1. Testing Welcome Endpoint"
echo "Expected: Welcome message"
curl -v "${SSO_BASE_URL}/"

# 2. Test Protected User Info Endpoint (should redirect to SAML login)
echo -e "\n\n2. Testing Protected User Info Endpoint"
echo "Expected: 302 Redirect to SAML login"
curl -v "${SSO_BASE_URL}/auth/user"

# 3. Initiate SAML SSO Flow
echo -e "\n\n3. Testing SAML SSO Initiation"
echo "Expected: 302 Redirect to Azure AD login"
curl -v "${SSO_BASE_URL}/maxcomsc/login"

# 4. Test Session Timeout
echo -e "\n\n4. Testing Session Timeout (wait 10 minutes)"
echo "Expected: 401 Unauthorized after timeout"
echo "Run: curl -v ${SSO_BASE_URL}/auth/user"

echo -e "\nNote: SAML authentication requires browser interaction."
echo "For automated testing, configure test certificates and mock SAML responses."
