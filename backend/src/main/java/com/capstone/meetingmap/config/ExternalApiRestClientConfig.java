package com.capstone.meetingmap.config;

import com.capstone.meetingmap.api.google.properties.GoogleApiProperties;
import com.capstone.meetingmap.api.kakao.properties.KakaoApiProperties;
import com.capstone.meetingmap.api.openai.properties.OpenAiApiProperties;
import com.capstone.meetingmap.api.tmap.properties.TMapApiProperties;
import com.capstone.meetingmap.api.tourapi.properties.TourApiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Configuration
public class ExternalApiRestClientConfig {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiRestClientConfig.class);

    @Bean
    public RestClient openAiRestClient(final OpenAiApiProperties openAiApiProperties) {
        return RestClient.builder()
                .baseUrl(openAiApiProperties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + openAiApiProperties.getKey())
                .requestInterceptor(loggingInterceptor())
                .build();
    }

    @Bean
    public RestClient googleRestClient(final GoogleApiProperties googleApiProperties) {
        return RestClient.builder()
                .baseUrl(googleApiProperties.getBaseUrl())
                .requestInterceptor(
                        combineInterceptors(
                                queryParamAppender("key", googleApiProperties.getKey()),
                                loggingInterceptor()
                        )
                )
                .build();
    }

    @Bean
    public RestClient kakaoRestClient(final KakaoApiProperties kakaoApiProperties) {
        return RestClient.builder()
                .baseUrl(kakaoApiProperties.getBaseUrl())
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiProperties.getRestApiKey())
                .requestInterceptor(loggingInterceptor())
                .build();
    }

    @Bean
    public RestClient tMapRestClient(final TMapApiProperties tMapApiProperties) {
        return RestClient.builder()
                .baseUrl(tMapApiProperties.getBaseUrl())
                .defaultHeader("appKey", tMapApiProperties.getKey())
                .requestInterceptor(loggingInterceptor())
                .build();
    }

    @Bean
    public RestClient tourApiRestClient(final TourApiProperties tourApiProperties) {
        return RestClient.builder()
                .baseUrl(tourApiProperties.getBaseUrl())
                .requestInterceptor(
                        combineInterceptors(
                                queryParamAppender("serviceKey", tourApiProperties.getKey()),
                                queryParamAppender("MobileOS", "ETC"),
                                queryParamAppender("MobileApp", "MeetingMap"),
                                queryParamAppender("_type", "json"),
                                loggingInterceptor()
                        )
                )
                .build();
    }

    // http 요청을 로깅
    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("HTTP {} {}", request.getMethod(), request.getURI());
            log.debug("Request Headers {}", request.getHeaders());
            var response = execution.execute(request, body);
            log.debug("Response {}", response.getStatusCode());
            return response;
        };
    }

    // http 요청에 쿼리 파라미터 추가
    private ClientHttpRequestInterceptor queryParamAppender(String key, String value) {
        return (request, body, execution) -> {
            URI originalUri = request.getURI();
            URI newUri = UriComponentsBuilder.fromUri(originalUri)
                    .queryParam(key, value)
                    .build(true)
                    .toUri();

            HttpRequest modifiedRequest = new HttpRequestWrapper(request) {
                @Override
                public URI getURI() {
                    return newUri;
                }
            };

            return execution.execute(modifiedRequest, body);
        };
    }

    // 위 두 요청을 하나로 묶음
    private ClientHttpRequestInterceptor combineInterceptors(ClientHttpRequestInterceptor... interceptors) {
        return (request, body, execution) -> {
            // 체이닝 구조 구성
            ClientHttpRequestExecution chain = execution;
            for (int i = interceptors.length - 1; i >= 0; i--) {
                final ClientHttpRequestInterceptor interceptor = interceptors[i];
                final ClientHttpRequestExecution next = chain;
                chain = (req, bdy) -> interceptor.intercept(req, bdy, next);
            }
            return chain.execute(request, body);
        };
    }
}
