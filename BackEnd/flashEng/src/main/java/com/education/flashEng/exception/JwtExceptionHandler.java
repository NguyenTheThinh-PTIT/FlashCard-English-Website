package com.education.flashEng.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtExceptionHandler {

    public void handleJwtException(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = String.format("{\"success\":false, \"message\":\"%s\"}", message);
        response.getWriter().write(jsonResponse);
    }
}