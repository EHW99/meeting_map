package com.capstone.meetingmap.config;

import com.capstone.meetingmap.CustomErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");

        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .message("로그인이 필요합니다.")
                .build();

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
