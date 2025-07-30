package com.example.copilot.security;

import com.example.copilot.core.dto.LoginRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for Rate Limiting functionality
 * Validates that the RateLimitingInterceptor correctly limits requests to login endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RateLimitingTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenExceedingRateLimit_thenReturn429() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");
        
        String requestBody = objectMapper.writeValueAsString(loginRequest);
        
        // Make 10 requests (should be allowed but return 400 due to wrong credentials)
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest()); // Wrong credentials, but not rate limited
        }
        
        // 11th request should be rate limited
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isTooManyRequests());
    }
    
    @Test
    void whenMakingRequestsToOtherEndpoints_thenNoRateLimit() throws Exception {
        // Test that rate limiting doesn't affect other endpoints
        for (int i = 0; i < 15; i++) {
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isForbidden()); // No rate limiting, security returns 403 for unauthorized POST
        }
    }
}
