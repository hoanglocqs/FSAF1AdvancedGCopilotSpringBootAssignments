package com.example.copilot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for rate limiting
 * Allows customization of rate limits via application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "app.rate-limiting")
public class RateLimitConfig {
    
    private int loginRequestsPerMinute = 10;
    private int registerRequestsPerMinute = 5;
    private boolean enabled = true;
    
    // Getters and Setters
    public int getLoginRequestsPerMinute() {
        return loginRequestsPerMinute;
    }
    
    public void setLoginRequestsPerMinute(int loginRequestsPerMinute) {
        this.loginRequestsPerMinute = loginRequestsPerMinute;
    }
    
    public int getRegisterRequestsPerMinute() {
        return registerRequestsPerMinute;
    }
    
    public void setRegisterRequestsPerMinute(int registerRequestsPerMinute) {
        this.registerRequestsPerMinute = registerRequestsPerMinute;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
