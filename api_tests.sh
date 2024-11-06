#!/bin/bash

# Wait for the backend to initialize
sleep 30

# Registration Request
register_response=$(curl -s -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{ "email": "foo@example.com", "password": "password@123", "firstName": "Foo", "lastName": "Bar" }')

echo "Registration Response: $register_response"

# Login Request
login_response=$(curl -s -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{ "email": "foo@example.com", "password": "password@123" }')

echo "Login Response: $login_response"

# Extract token from login response
auth_token=$(echo "$login_response" | jq -r .token)

# Check if token is present and save it in the correct directory
if [ "$auth_token" != "null" ]; then
  echo "$auth_token" > testing/auth_token.txt
  echo "Obtained Auth Token: $auth_token"
else
  echo "Login failed: $login_response"
  exit 1
fi
