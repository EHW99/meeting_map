package com.capstone.meetingmap.api.openai.service;

import com.capstone.meetingmap.api.openai.dto.ChatCompletionResponse;
import com.capstone.meetingmap.map.dto.PlaceResponseDto;
import com.capstone.meetingmap.schedule.dto.ScheduleCreateRequestDto;
import com.capstone.meetingmap.schedule.dto.SelectedPlace;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class OpenAIService {
    private static final Logger log = LoggerFactory.getLogger(OpenAIService.class);
    private static final String MODEL = "gpt-4o-mini";
    private static final int MAX_TOKENS = 1000;

    private final RestClient openAiRestClient;
    private final ObjectMapper objectMapper;

    public OpenAIService(RestClient openAiRestClient) {
        this.openAiRestClient = openAiRestClient;
        this.objectMapper = new ObjectMapper();
    }

    public String getChatCompletion(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "messages", List.of(Map.of(
                        "role", "user",
                        "content", prompt
                )),
                "max_tokens", MAX_TOKENS
        );

        try {
            ChatCompletionResponse response = openAiRestClient.post()
                    .uri("/v1/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("OpenAI API 클라이언트 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI 추천 요청 오류");
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("OpenAI API 서버 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI 서비스를 일시적으로 사용할 수 없습니다");
                    })
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                log.warn("OpenAI API 응답이 비어있습니다");
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답을 받지 못했습니다");
            }

            return response.getChoices().get(0).getMessage().getContent();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI 추천 중 오류가 발생했습니다");
        }
    }

    public List<PlaceResponseDto> getRecommendedPlaces(ScheduleCreateRequestDto dto, List<PlaceResponseDto> placeList, String themeDescription) {
        int recommendCount = dto.getTotalPlaceCount() - dto.getSelectedPlace().size();

        try {
            String prompt = buildPrompt(dto, placeList, themeDescription);
            String aiResult = getChatCompletion(prompt);
            log.debug("AI 응답: {}", aiResult);

            // JSON 또는 다양한 형식 파싱 시도
            List<String> recommendedNames = parseAIResponse(aiResult);
            log.info("AI가 추천한 장소: {}", recommendedNames);

            // 이름 기준으로 placeList에서 찾아서 반환
            List<PlaceResponseDto> recommended = placeList.stream()
                    .filter(place -> recommendedNames.stream()
                            .anyMatch(name -> name.equalsIgnoreCase(place.getName()) ||
                                    place.getName().contains(name) ||
                                    name.contains(place.getName())))
                    .collect(Collectors.toList());

            // AI 추천 결과가 부족하면 평점 기반으로 추가
            if (recommended.size() < recommendCount) {
                log.info("AI 추천 결과 부족 ({}/{}), 평점 기반으로 보충", recommended.size(), recommendCount);
                recommended = supplementWithRatingBased(recommended, placeList, dto, recommendCount);
            }

            return recommended;

        } catch (Exception e) {
            log.warn("AI 추천 실패, 평점 기반 fallback 사용: {}", e.getMessage());
            return getFallbackRecommendations(placeList, dto, recommendCount);
        }
    }

    /**
     * AI 응답을 다양한 형식으로 파싱 시도
     */
    private List<String> parseAIResponse(String aiResult) {
        List<String> names = new ArrayList<>();

        // 1. JSON 배열 형식 시도: ["장소A", "장소B"]
        try {
            JsonNode jsonNode = objectMapper.readTree(aiResult);
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    names.add(node.asText().trim());
                }
                if (!names.isEmpty()) return names;
            }
        } catch (JsonProcessingException ignored) {
            // JSON 파싱 실패, 다른 형식 시도
        }

        // 2. JSON 객체에서 places/recommendations 키 찾기
        try {
            JsonNode jsonNode = objectMapper.readTree(aiResult);
            JsonNode placesNode = jsonNode.has("places") ? jsonNode.get("places") :
                    jsonNode.has("recommendations") ? jsonNode.get("recommendations") : null;
            if (placesNode != null && placesNode.isArray()) {
                for (JsonNode node : placesNode) {
                    if (node.isTextual()) {
                        names.add(node.asText().trim());
                    } else if (node.has("name")) {
                        names.add(node.get("name").asText().trim());
                    }
                }
                if (!names.isEmpty()) return names;
            }
        } catch (JsonProcessingException ignored) {
        }

        // 3. 번호 목록 형식: "1. 장소A\n2. 장소B"
        Pattern numberedPattern = Pattern.compile("^\\d+\\.\\s*(.+)$", Pattern.MULTILINE);
        Matcher numberedMatcher = numberedPattern.matcher(aiResult);
        while (numberedMatcher.find()) {
            names.add(numberedMatcher.group(1).trim());
        }
        if (!names.isEmpty()) return names;

        // 4. 대시 목록 형식: "- 장소A\n- 장소B"
        Pattern dashPattern = Pattern.compile("^[-•]\\s*(.+)$", Pattern.MULTILINE);
        Matcher dashMatcher = dashPattern.matcher(aiResult);
        while (dashMatcher.find()) {
            names.add(dashMatcher.group(1).trim());
        }
        if (!names.isEmpty()) return names;

        // 5. 쉼표 구분: "장소A, 장소B, 장소C"
        if (aiResult.contains(",")) {
            names = Arrays.stream(aiResult.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !s.matches("^\\d+$"))
                    .collect(Collectors.toList());
            if (!names.isEmpty()) return names;
        }

        // 6. 줄바꿈 구분
        names = Arrays.stream(aiResult.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && s.length() > 1 && !s.matches("^\\d+$"))
                .collect(Collectors.toList());

        return names;
    }

    /**
     * AI 추천 결과가 부족할 때 평점 기반으로 보충
     */
    private List<PlaceResponseDto> supplementWithRatingBased(
            List<PlaceResponseDto> current,
            List<PlaceResponseDto> placeList,
            ScheduleCreateRequestDto dto,
            int targetCount) {

        Set<String> existingNames = current.stream()
                .map(PlaceResponseDto::getName)
                .collect(Collectors.toSet());

        Set<String> selectedNames = dto.getSelectedPlace().stream()
                .map(SelectedPlace::getName)
                .collect(Collectors.toSet());

        List<PlaceResponseDto> additional = placeList.stream()
                .filter(p -> !existingNames.contains(p.getName()))
                .filter(p -> !selectedNames.contains(p.getName()))
                .sorted(Comparator.comparing(PlaceResponseDto::getRating,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(targetCount - current.size())
                .toList();

        List<PlaceResponseDto> result = new ArrayList<>(current);
        result.addAll(additional);
        return result;
    }

    /**
     * AI 추천 완전 실패 시 평점 기반 fallback
     */
    private List<PlaceResponseDto> getFallbackRecommendations(
            List<PlaceResponseDto> placeList,
            ScheduleCreateRequestDto dto,
            int count) {

        Set<String> selectedNames = dto.getSelectedPlace().stream()
                .map(SelectedPlace::getName)
                .collect(Collectors.toSet());

        return placeList.stream()
                .filter(p -> !selectedNames.contains(p.getName()))
                .sorted(Comparator.comparing(PlaceResponseDto::getRating,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .toList();
    }

    private String buildPrompt(ScheduleCreateRequestDto dto, List<PlaceResponseDto> placeList, String themeDescription) {
        StringBuilder sb = new StringBuilder();
        int recommendCount = dto.getTotalPlaceCount() - dto.getSelectedPlace().size();

        sb.append("다음은 여행 장소 후보 목록입니다.\n");
        sb.append("사용자가 이미 선택한 장소는 제외하고, 나머지 후보 중에서 적절한 장소를 ")
                .append(recommendCount).append("개 추천해주세요.\n\n");

        if (!dto.getSelectedPlace().isEmpty()) {
            sb.append("이미 선택된 장소 목록 (추천에서 제외):\n");
            for (SelectedPlace place : dto.getSelectedPlace()) {
                sb.append("- ").append(place.getName()).append("\n");
            }
        }

        sb.append("요청 정보:\n");
        sb.append("- 테마: ").append(themeDescription).append("\n");
        sb.append("- 출발 위치 위도: ").append(dto.getPointCoordinate().getLatitude()).append("\n");
        sb.append("- 출발 위치 경도: ").append(dto.getPointCoordinate().getLongitude()).append("\n");
        sb.append("- 스케줄 시작 시간: ").append(dto.getScheduleStartTime()).append("\n");
        sb.append("- 스케줄 종료 시간: ").append(dto.getScheduleEndTime()).append("\n");

        boolean includeLunch = isTimeBetween(dto.getScheduleStartTime().toLocalTime(), dto.getScheduleEndTime().toLocalTime(), LocalTime.of(11, 30), LocalTime.of(13, 30));
        boolean includeDinner = isTimeBetween(dto.getScheduleStartTime().toLocalTime(), dto.getScheduleEndTime().toLocalTime(), LocalTime.of(17, 0), LocalTime.of(19, 0));

        sb.append("\n요청 조건:\n");
        if (includeLunch && includeDinner) {
            sb.append("- 점심시간과 저녁시간이 모두 포함되어 있으므로 음식점을 최소 2개 포함해주세요.\n");
        } else if (includeLunch || includeDinner) {
            sb.append("- ").append(includeLunch ? "점심시간" : "저녁시간").append("이 포함되어 있으므로 음식점을 최소 1개 포함해주세요.\n");
        } else {
            sb.append("- 음식점은 선택적으로 포함해도 됩니다.\n");
        }

        sb.append("\n장소 목록:\n");
        for (int i = 0; i < placeList.size(); i++) {
            PlaceResponseDto place = placeList.get(i);
            boolean isRestaurant = place.getCategory() != null && place.getCategory().startsWith("food-");

            sb.append(i + 1).append(". 이름: ").append(place.getName())
                    .append(", 평점: ").append(place.getRating())
                    .append(", 리뷰 수: ").append(place.getUserRatingsTotal())
                    .append(", 위도: ").append(place.getLatitude())
                    .append(", 경도: ").append(place.getLongitude());

            if (place.getCategory() != null) {
                sb.append(", 카테고리: ").append(isRestaurant ? "음식점 (" + place.getCategory() + ")" : place.getCategory());
            }

            sb.append("\n");
        }

        sb.append("\n장소 이름만 쉼표로 구분해서 반환해주세요. 예: 장소A, 장소B, 장소C");

        return sb.toString();
    }

    private boolean isTimeBetween(LocalTime start, LocalTime end, LocalTime rangeStart, LocalTime rangeEnd) {
        return !(end.isBefore(rangeStart) || start.isAfter(rangeEnd));
    }
}
