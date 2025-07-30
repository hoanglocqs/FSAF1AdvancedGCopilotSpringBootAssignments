package com.example.copilot.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Interceptor for Authentication Endpoints
 * Prevents brute-force attacks by limiting requests per IP address
 * Uses simple in-memory bucket implementation with Bucket4j concepts
 */
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, RateLimitBucket> bucketMap = new ConcurrentHashMap<>();
    
    // Rate limit: 10 requests per minute
    private static final int REQUESTS_PER_MINUTE = 10;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(1);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String clientIP = getClientIP(request);
        String requestURI = request.getRequestURI();
        
        // Apply rate limiting only to login endpoint
        if ("/api/auth/login".equals(requestURI) && "POST".equals(request.getMethod())) {
            RateLimitBucket bucket = getBucket(clientIP);
            
            if (bucket.tryConsume()) {
                // Request allowed
                return true;
            } else {
                // Rate limit exceeded
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded. Maximum " + 
                    REQUESTS_PER_MINUTE + " requests per minute allowed for login endpoint.\",\"retryAfter\":60}"
                );
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get or create bucket for client IP
     */
    private RateLimitBucket getBucket(String clientIP) {
        return bucketMap.computeIfAbsent(clientIP, k -> new RateLimitBucket(REQUESTS_PER_MINUTE, WINDOW_DURATION));
    }
    
    /**
     * Extract client IP address from request
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Simple bucket implementation for rate limiting
     */
    private static class RateLimitBucket {
        private final int capacity;
        private final Duration windowDuration;
        private int tokens;
        private LocalDateTime lastRefill;
        
        public RateLimitBucket(int capacity, Duration windowDuration) {
            this.capacity = capacity;
            this.windowDuration = windowDuration;
            this.tokens = capacity;
            this.lastRefill = LocalDateTime.now();
        }
        
        public synchronized boolean tryConsume() {
            refillIfNeeded();
            
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }
        
        private void refillIfNeeded() {
            LocalDateTime now = LocalDateTime.now();
            if (Duration.between(lastRefill, now).compareTo(windowDuration) >= 0) {
                tokens = capacity;
                lastRefill = now;
            }
        }
    }
}
