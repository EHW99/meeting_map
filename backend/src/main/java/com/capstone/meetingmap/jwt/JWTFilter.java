package com.capstone.meetingmap.jwt;

import com.capstone.meetingmap.user.dto.CustomUserDetails;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.userrole.entity.UserRole;
import com.capstone.meetingmap.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        //request에서 accessToken 쿠키를 찾음
        String accessToken = getTokenFromCookies(request);

        //accessToken 쿠키 검증
        if (accessToken == null) {
            log.debug("토큰이 없습니다");
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("토큰 인증 진행");

        try {
            //토큰 소멸 시간 검증
            if (jwtUtil.isExpired(accessToken)) {
                log.debug("토큰이 만료되었습니다");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"토큰이 만료되었습니다. 다시 로그인해주세요.\"}");
                return;
            }
        } catch (Exception e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"유효하지 않은 토큰입니다.\"}");
            return;
        }

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        //UserRole 엔티티를 생성하여 값 set
        UserRole userRole = UserRole.builder()
                .userTypeName(role)
                .build();

        //User 엔티티를 생성하여 값 set
        User user = User.builder()
                        .userId(username)
                        .userPasswd("temppassword")
                        .userRole(userRole)
                    .build();

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
