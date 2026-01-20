package com.capstone.meetingmap.config;

import com.capstone.meetingmap.jwt.JWTFilter;
import com.capstone.meetingmap.jwt.LoginFilter;
import com.capstone.meetingmap.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class  SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();

                                config.setAllowedOrigins(Arrays.asList(
                                        "http://localhost:3000",             // 로컬 개발용
                                        "https://meeting-map.kro.kr"        // 실제 배포 도메인
                                ));
                                config.setAllowedMethods(Collections.singletonList("*"));
                                config.setAllowCredentials(true);
                                config.setAllowedHeaders(Collections.singletonList("*"));
                                config.setMaxAge(3600L);

                                config.setExposedHeaders(Collections.singletonList("Authorization"));

                                return config;
                            }
                        }));

        // csrf 비활성화
        http
                .csrf((auth) -> auth.disable());

        //Form 로그인 방식 비활성화
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //세션 logout 방식 disable
        http
                 .logout((auth) -> auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/api/auth/**", "/api/user/check-id", "/api/user/register", "/api/map/**", "/api/path/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/boards").authenticated()
                        .requestMatchers("/api/user/list").hasAuthority("Admin")
                        .anyRequest().authenticated());

        // 필터 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 로그인 필요시 응답 설정
        http
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );

        return http.build();
    }
}
