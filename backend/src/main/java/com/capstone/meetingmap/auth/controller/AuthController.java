package com.capstone.meetingmap.auth.controller;

import com.capstone.meetingmap.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.startsWith("kakao_")) {
            // 카카오 로그아웃
            authService.kakaoLogout(userId);
        }

        // "accessToken" 쿠키 삭제
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setMaxAge(0); // 쿠키 만료
        cookie.setPath("/"); // 쿠키 경로 설정 (서버에서 설정한 경로와 동일해야 함)
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie); // 응답에 쿠키 추가

        // 로그아웃 성공 메시지 반환
        return ResponseEntity.ok("Logout successful");
    }

    // 카카오 로그인
    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) {
        String jwt = authService.kakaoLogin(code);

        Cookie cookie = new Cookie("accessToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
