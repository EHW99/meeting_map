package com.capstone.meetingmap.jwt;

import com.capstone.meetingmap.user.dto.CustomUserDetails;
import com.capstone.meetingmap.user.dto.LoginRequestDto;
import com.capstone.meetingmap.user.dto.UserResponseDto;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //클라이언트의 json 요청 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

            //Dto에서 userId, passwd 추출
            String userId = loginRequestDto.getUserId();
            String userPasswd = loginRequestDto.getUserPasswd();

            //userId와 userPasswd를 token에 담음
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, userPasswd, null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, role, 60 * 60 * 1000L);

        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true); // JavaScript 접근 불가
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1시간

        response.addCookie(cookie);

        // User 엔티티 정보를 JSON으로 응답
        User user = customUserDetails.getUser(); // CustomUserDetails에서 User 엔티티 가져오는 메서드가 있다고 가정
        UserResponseDto userResponseDto = UserResponseDto.fromEntity(user);

        // 응답 헤더 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 생성 및 응답에 쓰기
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(userResponseDto);
        response.getWriter().write(userJson);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String message = switch (failed.getClass().getSimpleName()) {
            case "BadCredentialsException" -> "아이디 또는 비밀번호가 잘못되었습니다.";
            case "UsernameNotFoundException" -> "존재하지 않는 사용자입니다.";
            default -> "인증에 실패했습니다.";
        };

        String json = String.format("""
            {
              "message": "%s"
            }
            """, message);

        response.getWriter().write(json);
    }

}
