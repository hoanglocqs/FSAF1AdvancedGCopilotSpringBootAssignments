#!/bin/bash
# Rate Limiting Validation Script
# Tests the /api/auth/login endpoint to verify rate limiting works

echo "Testing Rate Limiting on /api/auth/login endpoint"
echo "Making 15 requests to exceed the 10 requests/minute limit..."
echo "==============================================="

success_count=0
rate_limited_count=0

for i in {1..15}; do
    echo "Request $i:"
    
    # Make POST request to login endpoint
    response=$(curl -s -w "%{http_code}" -X POST \
        -H "Content-Type: application/json" \
        -d '{"email":"test@example.com","password":"wrongpassword"}' \
        http://localhost:8080/api/auth/login)
    
    # Extract HTTP status code (last 3 characters)
    http_code="${response: -3}"
    
    # Extract response body (all but last 3 characters)
    response_body="${response%???}"
    
    echo "  HTTP Status: $http_code"
    
    if [ "$http_code" == "429" ]; then
        echo "  ✅ Rate limiting working! Request $i blocked with 429 Too Many Requests"
        echo "  Response: $response_body"
        rate_limited_count=$((rate_limited_count + 1))
    elif [ "$http_code" == "400" ]; then
        echo "  ✓ Request allowed (wrong credentials, but not rate limited)"
        success_count=$((success_count + 1))
    else
        echo "  ⚠️ Unexpected status: $http_code"
        echo "  Response: $response_body"
    fi
    
    echo ""
    sleep 0.5
done

echo "==============================================="
echo "Rate Limiting Test Results:"
echo "  Successful requests (400): $success_count"
echo "  Rate limited requests (429): $rate_limited_count"

if [ $rate_limited_count -gt 0 ]; then
    echo "  ✅ Rate limiting is working correctly!"
else
    echo "  ❌ Rate limiting may not be working properly"
fi

echo ""
echo "Waiting 60 seconds for rate limit to reset..."
echo "Making one more request to verify reset..."

sleep 60

echo "Post-reset request:"
response=$(curl -s -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"wrongpassword"}' \
    http://localhost:8080/api/auth/login)

http_code="${response: -3}"
echo "  HTTP Status: $http_code"

if [ "$http_code" == "400" ]; then
    echo "  ✅ Rate limit successfully reset! Request allowed again"
elif [ "$http_code" == "429" ]; then
    echo "  ⚠️ Still rate limited - may need more time"
else
    echo "  ⚠️ Unexpected status: $http_code"
fi

echo ""
echo "Rate limiting validation completed."
