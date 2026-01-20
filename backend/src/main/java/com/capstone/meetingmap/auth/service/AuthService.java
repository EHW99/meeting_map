package com.capstone.meetingmap.auth.service;

import com.capstone.meetingmap.KakaoOAuthProperties;
import com.capstone.meetingmap.auth.dto.KakaoTokenResponse;
import com.capstone.meetingmap.auth.dto.KakaoUserInfo;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.user.repository.UserRepository;
import com.capstone.meetingmap.user.service.UserService;
import com.capstone.meetingmap.util.JWTUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class AuthService {
    private final WebClient authWebClient;
    private final WebClient apiWebClient;
    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final UserService userService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final HttpSession session;

    @Value("${kakao.api.admin-key}")
    private String serviceAppAdminKey;

    public AuthService(KakaoOAuthProperties kakaoOAuthProperties, UserService userService, UserRepository userRepository, JWTUtil jwtUtil, HttpSession session) {
        this.kakaoOAuthProperties = kakaoOAuthProperties;
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.session = session;

        DefaultUriBuilderFactory authFactory = new DefaultUriBuilderFactory(kakaoOAuthProperties.getAuthBaseUrl());
        authFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        this.authWebClient = WebClient.builder()
                .baseUrl(kakaoOAuthProperties.getAuthBaseUrl())
                .uriBuilderFactory(authFactory)
                .build();

        DefaultUriBuilderFactory apiFactory = new DefaultUriBuilderFactory(kakaoOAuthProperties.getApiBaseUrl());
        apiFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        this.apiWebClient = WebClient.builder()
                .baseUrl(kakaoOAuthProperties.getApiBaseUrl())
                .uriBuilderFactory(apiFactory)
                .build();
    }

    private KakaoTokenResponse requestToken(String code) {
        return authWebClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "authorization_code")
                        .with("client_id", kakaoOAuthProperties.getClientId())
                        .with("redirect_uri", kakaoOAuthProperties.getRedirectUri())
                        .with("code", code)
                        .with("client_secret", kakaoOAuthProperties.getClientSecret()))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();
    }

    private KakaoUserInfo requestUserInfo(String accessToken) {
        return apiWebClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();
    }

    public String kakaoLogin(String code) {
        // 1. 카카오 토큰 요청
        KakaoTokenResponse tokenResponse = requestToken(code);

//        // 2. 세션에 accessToken 저장
//        session.setAttribute("accessToken", tokenResponse.getAccessToken());
//        System.out.println(tokenResponse.getAccessToken());

        // 3. 사용자 정보 요청
        KakaoUserInfo kakaoUser = requestUserInfo(tokenResponse.getAccessToken());


        // 4. 사용자 존재 확인 (없으면 가입 처리)
        User user;
        boolean isExists = userRepository.existsById("kakao_" + kakaoUser.getId());
        if (!isExists) {
            user = userService.kakaoJoin(kakaoUser);
        } else {
            user = userRepository.findById("kakao_" + kakaoUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
        }

        // 5. JWT 발급
        return jwtUtil.createJwt(user.getUserId(), user.getUserRole().getUserTypeName(), 60 * 60 * 1000L);
    }

    public void kakaoLogout(String userId) {

        // 'kakao_' 접두사 제거
        if (userId.startsWith("kakao_")) {
            userId = userId.substring(6); // 'kakao_' 길이만큼 자르기
        }

        apiWebClient.post()
                .uri("/v1/user/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "KakaoAK " + serviceAppAdminKey)
                .body(BodyInserters
                        .fromFormData("target_id_type", "user_id")
                        .with("target_id", userId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
