package com.capstone.meetingmap.api.openai.service;

import com.capstone.meetingmap.api.openai.dto.ChatCompletionResponse;
import com.capstone.meetingmap.map.dto.PlaceResponseDto;
import com.capstone.meetingmap.schedule.dto.ScheduleCreateRequestDto;
import com.capstone.meetingmap.schedule.dto.SelectedPlace;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {
    private final RestClient openAiRestClient;

    public OpenAIService(RestClient openAiRestClient) {
        this.openAiRestClient = openAiRestClient;
    }

    public String getChatCompletion(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(Map.of(
                        "role", "user",
                        "content", prompt
                )),
                "max_tokens", 500
        );

        ChatCompletionResponse response = openAiRestClient.post()
                .uri("/v1/chat/completions")
                .body(requestBody)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("OpenAI API에서 결과를 찾을 수 없습니다.");
        }

        return response.getChoices().get(0).getMessage().getContent();
    }

    public List<PlaceResponseDto> getRecommendedPlaces(ScheduleCreateRequestDto dto, List<PlaceResponseDto> placeList, String themeDescription) {
        String prompt = buildPrompt(dto, placeList, themeDescription);
        String aiResult = getChatCompletion(prompt);

        // 장소 이름 파싱
        List<String> recommendedNames = Arrays.stream(aiResult.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // 이름 기준으로 placeList에서 찾아서 반환
        return placeList.stream()
                .filter(place -> recommendedNames.contains(place.getName()))
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
