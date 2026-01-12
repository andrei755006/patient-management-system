package com.pm.authservice.dto;

/**
 * Data Transfer Object for returning the JWT token
 */
public record AuthResponse(
        String token
) {}