package com.capstone.meetingmap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
@Getter
@Setter
public class KakaoOAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authBaseUrl = "https://kauth.kakao.com"; // 토큰 발급용
    private String apiBaseUrl = "https://kapi.kakao.com";   // 사용자 정보 조회용
}
