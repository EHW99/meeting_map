package com.capstone.meetingmap.api.kakao.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.api")
@Getter
@Setter
public class KakaoApiProperties {
    private String baseUrl;
    private String adminKey;
    private String restApiKey;
}
