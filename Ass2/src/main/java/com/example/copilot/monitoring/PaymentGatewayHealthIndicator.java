package com.example.copilot.monitoring;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Custom Health Indicator for Payment Gateway
 * Simulates random UP/DOWN status for demonstration
 * In production, this would ping actual payment provider's status page
 * Simplified implementation due to Spring Boot 3.x module issues
 */
@Component
public class PaymentGatewayHealthIndicator {

    private final Random random = new Random();

    /**
     * Check payment gateway health status
     * @return Map with health status and details
     */
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Simulate payment gateway status check
            // In real implementation: HTTP call to payment provider's status page
            boolean isUp = random.nextBoolean();
            
            if (isUp) {
                result.put("status", "UP");
                result.put("gateway", "Stripe");
                result.put("gatewayStatus", "Connected");
                result.put("responseTime", random.nextInt(100) + 50 + "ms");
                result.put("lastChecked", LocalDateTime.now().toString());
                result.put("availabilityZone", "us-east-1");
            } else {
                result.put("status", "DOWN");
                result.put("gateway", "Stripe");
                result.put("gatewayStatus", "Connection failed");
                result.put("error", "Timeout or service unavailable");
                result.put("lastChecked", LocalDateTime.now().toString());
                result.put("retryAfter", "30 seconds");
            }
            
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("gateway", "Stripe");
            result.put("error", "Health check failed: " + e.getMessage());
            result.put("lastChecked", LocalDateTime.now().toString());
        }
        
        return result;
    }
}
