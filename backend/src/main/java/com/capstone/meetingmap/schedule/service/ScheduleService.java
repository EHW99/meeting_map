package com.capstone.meetingmap.schedule.service;

import com.capstone.meetingmap.api.openai.service.OpenAIService;
import com.capstone.meetingmap.api.tmap.dto.PedestrianRouteRequest;
import com.capstone.meetingmap.api.tmap.dto.RouteRequest;
import com.capstone.meetingmap.api.tmap.dto.RouteResponse;
import com.capstone.meetingmap.api.tmap.dto.SimpleTransitRouteResponse;
import com.capstone.meetingmap.api.tmap.service.TMapApiService;
import com.capstone.meetingmap.group.repository.GroupScheduleRepository;
import com.capstone.meetingmap.map.dto.PlaceResponseDto;
import com.capstone.meetingmap.map.service.MapService;
import com.capstone.meetingmap.schedule.dto.*;
import com.capstone.meetingmap.schedule.entity.Schedule;
import com.capstone.meetingmap.schedule.entity.ScheduleDetail;
import com.capstone.meetingmap.schedule.repository.ScheduleDetailRepository;
import com.capstone.meetingmap.schedule.repository.ScheduleRepository;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.user.repository.UserRepository;
import com.capstone.meetingmap.util.ParseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final TMapApiService tMapApiService;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleDetailRepository scheduleDetailRepository;
    private final UserRepository userRepository;
    private final MapService mapService;
    private final OpenAIService openAIService;
    private final GroupScheduleRepository groupScheduleRepository;

    public ScheduleService(TMapApiService tMapApiService,
                           ScheduleRepository scheduleRepository,
                           ScheduleDetailRepository scheduleDetailRepository,
                           UserRepository userRepository,
                           MapService mapService,
                           OpenAIService openAIService,
                           GroupScheduleRepository groupScheduleRepository) {
        this.tMapApiService = tMapApiService;
        this.scheduleRepository = scheduleRepository;
        this.scheduleDetailRepository = scheduleDetailRepository;
        this.userRepository = userRepository;
        this.mapService = mapService;
        this.openAIService = openAIService;
        this.groupScheduleRepository = groupScheduleRepository;
    }

    // 회원이 만든 스케줄 가져오기
    public List<ScheduleResponseDto> getSchedulesByUserId(String userId) {
        List<Schedule> scheduleList = scheduleRepository.findAllByUser_UserId(userId);

        return scheduleList.stream().map(ScheduleResponseDto::fromEntity).collect(Collectors.toList());
    }

    // 세부 스케줄과 함께 스케줄 추가
    @Transactional
    public Integer saveScheduleWithDetails(String userId, ScheduleSaveRequestDto scheduleSaveRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        Schedule schedule = scheduleSaveRequestDto.toEntity(scheduleSaveRequestDto, user);
        scheduleRepository.save(schedule);

        return schedule.getScheduleNo();
    }

    // 스케줄 수정
    @Transactional
    public void updateSchedule(String userId, Integer scheduleNo, ScheduleSaveRequestDto requestDto) {
        if (!scheduleRepository.existsByScheduleNoAndUser_UserId(scheduleNo, userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 만든 스케줄만 수정 가능합니다");

        Schedule schedule = scheduleRepository.findById(scheduleNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "스케줄을 찾을 수 없습니다"));

        schedule.getDetails().clear(); // 기존 상세일정 삭제

        List<ScheduleDetail> scheduleDetails = requestDto.getDetails().stream().map(dto -> dto.toEntity(schedule)).toList();

        //일정 저장
        schedule.setScheduleWithoutUserId(requestDto.getScheduleName(), requestDto.getScheduleAbout(), scheduleDetails);
    }

    // 스케줄 삭제
    @Transactional
    public void deleteSchedule(String userId, Integer scheduleNo) {
        if (!scheduleRepository.existsByScheduleNoAndUser_UserId(scheduleNo, userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 만든 스케줄만 삭제 가능합니다");

        // 공유된 스케줄도 전부 삭제
        groupScheduleRepository.deleteByScheduleScheduleNo(scheduleNo);

        scheduleRepository.deleteById(scheduleNo);
    }

    // 스케줄 생성
    public ScheduleCreateResponseDto createSchedule(ScheduleCreateRequestDto dto) {

        List<ScheduleDetailCreateDto> detailCreateDtoList = new ArrayList<>();

        if (dto.getAiRecommendation() != null && dto.getAiRecommendation()) { // AI 추천 받기를 체크한 경우
            // 추가 장소를 PointCoordinate와 테마 기준으로 AI에 정보를 제공해 선택(정렬은 user_ratings_total 기준 정렬)
            int remainingCount = dto.getTotalPlaceCount() - dto.getSelectedPlace().size();

            // AI에게 추천을 맡길 총 장소
            List<PlaceResponseDto> mergedResponse = new ArrayList<>();

            List<String> categoryList = new ArrayList<>();
            switch (dto.getTheme()) {
                case "tour" -> categoryList.add("tour");
                case "nature" -> categoryList.add("tour-nature");
                case "history" -> categoryList.add("tour-tradition");
                case "food" -> categoryList.add("food");
                case "shopping" -> categoryList.add("shopping");
                case "date" -> {
                    categoryList.add("tour-park");
                    categoryList.add("cafe");
                    categoryList.add("tour-theme-park");
                }
            }

            for (String category : categoryList) {
                List<PlaceResponseDto> placeResponse = mapService.getAllPlaces("user_ratings_total_dsc", dto.getPointCoordinate().getLatitude(), dto.getPointCoordinate().getLongitude(), category);
                mergedResponse.addAll(placeResponse);
            }

            if (!dto.getTheme().equals("food")) {
                List<PlaceResponseDto> foodPlaceResponse = mapService.getAllPlaces("user_ratings_total_dsc", dto.getPointCoordinate().getLatitude(), dto.getPointCoordinate().getLongitude(), "food");
                mergedResponse.addAll(foodPlaceResponse);
            }

            String themeDescription = switch (dto.getTheme()) {
                case "history" -> "역사적 가치가 높고 전통적인 분위기가 있는 장소";
                case "nature" -> "자연 경관이 아름답고 휴식하기 좋은 장소";
                case "food" -> "맛이 좋고 리뷰 수가 많은 현지 인기 맛집";
                case "date" -> "분위기 좋고 둘이 걷기 좋은 장소";
                default -> "추천할만한 장소";
            };

            // AI 호출: AI 서비스에 필요한 정보(현재 위치, 테마, 이미 선택한 장소, 시간 등)를 보내 추천 장소 요청
            List<PlaceResponseDto> aiRecommendedPlaces = openAIService.getRecommendedPlaces(dto, mergedResponse, themeDescription);

            // AI 추천 장소 중 필요한 수량만큼만 선택
            List<PlaceResponseDto> selectedAiPlaces = aiRecommendedPlaces.stream()
                    .limit(remainingCount)
                    .toList();

            // 기존에 선택된 장소 + AI 추천 장소 합치기
            List<SelectedPlace> additionalPlaceList = selectedAiPlaces.stream()
                    .map(placeDto -> placeDto.toSelectedPlace(dto.getStayMinutesMean()))
                    .toList();

            dto.getSelectedPlace().addAll(additionalPlaceList);
        } else if (dto.getAdditionalRecommendation()) { // 추가 추천 받기를 체크한 경우
            // 추가 장소를 PointCoordinate를 기준으로 테마, 평점 기준으로 필터링해 선택(정렬은 user_ratings_total 기준 정렬)
            int remainingCount = dto.getTotalPlaceCount() - dto.getSelectedPlace().size();
            // 추가로 선택된 총 장소
            List<PlaceResponseDto> mergedResponse = new ArrayList<>();

            // 음식점을 1개도 고르지 않은 경우 처리
            if (dto.getSelectedPlace().stream().noneMatch(place -> place.getCategory() != null && place.getCategory().startsWith("food-"))) {
                List<PlaceResponseDto> response = mapService.getAllPlaces("user_ratings_total_dsc", dto.getPointCoordinate().getLatitude(), dto.getPointCoordinate().getLongitude(), "food");
                // 점심식사 가능한 시간
                if (dto.getScheduleStartTime().toLocalTime().isBefore(LocalTime.of(11, 30)) && dto.getScheduleEndTime().toLocalTime().isAfter(LocalTime.of(13, 30))) {
                    mergedResponse.add(response.get(0));
                    remainingCount--;
                }
                // 저녁식사 가능한 시간
                if (dto.getScheduleStartTime().toLocalTime().isBefore(LocalTime.of(17, 0)) && dto.getScheduleEndTime().toLocalTime().isAfter(LocalTime.of(19, 0))) {
                    mergedResponse.add(response.get(1));
                    remainingCount--;
                }
            }
            List<String> categoryList = new ArrayList<>();
            switch (dto.getTheme()) {
                case "tour" -> categoryList.add("tour");
                case "nature" -> categoryList.add("tour-nature");
                case "history" -> categoryList.add("tour-tradition");
                case "food" -> categoryList.add("food");
                case "shopping" -> categoryList.add("shopping");
                case "date" -> {
                    categoryList.add("tour-park");
                    categoryList.add("cafe");
                    categoryList.add("tour-theme-park");
                }
            }

            for (String category : categoryList) {
                List<PlaceResponseDto> placeResponse = mapService.getAllPlaces("user_ratings_total_dsc", dto.getPointCoordinate().getLatitude(), dto.getPointCoordinate().getLongitude(), category);
                mergedResponse.addAll(placeResponse);
            }

            Set<String> alreadySelectedNames = dto.getSelectedPlace().stream()
                    .map(SelectedPlace::getName)
                    .collect(Collectors.toSet());

            // 중복 제거
            mergedResponse = mergedResponse.stream()
                    .filter(placeDto -> !alreadySelectedNames.contains(placeDto.getName()))
                    .collect(Collectors.toList());

            if (dto.getMinimumRating() != null) {
                // 최소 평점 이상만 추출
                mergedResponse = mergedResponse.stream()
                        .filter(placeDto -> ParseUtil.parseDoubleSafe(placeDto.getRating()) >= dto.getMinimumRating())
                        .toList();
            }

            List<PlaceResponseDto> subMergedResponse;
            if (mergedResponse.size() > dto.getTotalPlaceCount()) {
                // subList로 앞부분만 추출해서 새로운 리스트로 만듬
                subMergedResponse = new ArrayList<>(mergedResponse.subList(0, dto.getTotalPlaceCount()));
            } else {
                subMergedResponse = new ArrayList<>(mergedResponse);
            }

            // 추가 검색 결과를 평점 개수 많은 기준으로 정렬
            subMergedResponse.sort(Comparator.comparing(PlaceResponseDto::getUserRatingsTotal).reversed());

            // dto에 추가로 선택된 총 장소 추가
            List<SelectedPlace> additionalPlaceList = new ArrayList<>();
            for (PlaceResponseDto placeDto : mergedResponse) {
                additionalPlaceList.add(placeDto.toSelectedPlace(dto.getStayMinutesMean()));
            }
            dto.getSelectedPlace().addAll(additionalPlaceList);

        }

        // 스케줄 순서대로 정렬되게 할 장소들 리스트
        List<SelectedPlace> sortedSelectedPlaceList = new ArrayList<>();

        // 처음 제시된 장소(추가 추천을 사용할 경우 가장 높은 평점을 가진 장소)로 시작지점 설정
        SelectedPlace firstPlace = null;
        for (SelectedPlace selectedPlace : dto.getSelectedPlace()) {
            // 점심/저녁 식사 시간부터 시작할 경우 음식점인 경우만 시작 장소로 결정
            if ((dto.getScheduleStartTime().toLocalTime().isAfter(LocalTime.of(11, 30)) &&
                    dto.getScheduleEndTime().toLocalTime().isBefore(LocalTime.of(13, 30))) ||
                    (dto.getScheduleStartTime().toLocalTime().isAfter(LocalTime.of(17, 0)) &&
                            dto.getScheduleEndTime().toLocalTime().isBefore(LocalTime.of(19, 0)))) {
                if (selectedPlace.getCategory().contains("food")) {
                    firstPlace = selectedPlace;
                    break;
                }
            } else { // 그 외의 경우 음식점이 아닌 경우만 시작 장소로 결정
                if (!selectedPlace.getCategory().contains("food")) {
                    firstPlace = selectedPlace;
                    break;
                }
            }
        }

        // 알고리즘 변수
        Set<SelectedPlace> visited = new HashSet<>();
        LocalDateTime currentTime = dto.getScheduleStartTime();

        // 첫 장소를 정렬 리스트에 넣기
        sortedSelectedPlaceList.add(firstPlace);

        // 시작 지점 선택
        SelectedPlace currentPlace = firstPlace;

        // 시작 지점 방문 처리
        visited.add(currentPlace);

        // 시작 시간 임시 저장
        LocalDateTime startTime = currentTime;

        // 시작 장소 방문 후 머문 시간을 반영해 변경
        currentTime = currentTime.plusMinutes(currentPlace.getStayMinutes());

        //detail dto에 반영
        detailCreateDtoList.add(ScheduleDetailCreateDto.builder()
                .scheduleContent(currentPlace.getName() + " 방문")
                .scheduleAddress(currentPlace.getAddress())
                .latitude(new BigDecimal(currentPlace.getLatitude()))
                .longitude(new BigDecimal(currentPlace.getLongitude()))
                .scheduleStartTime(startTime)
                .scheduleEndTime(currentTime)
                .build());

        // 식사 여부
        boolean hadLunch = false;
        boolean hadDinner = false;

        while (true) {

            // 방문해야 할 장소
            List<SelectedPlace> candidates = new ArrayList<>();

            if (dto.getTheme().equals("food")) {
                for (SelectedPlace place : dto.getSelectedPlace()) {
                    if (visited.contains(place)) { // 이미 방문한 장소면 continue
                        continue;
                    }
                    candidates.add(place);
                }
            } else {
                // 음식점 필터: 점심 or 저녁시간이 되었거나 지났으면 true
                boolean isLunchTime = currentTime.toLocalTime().isAfter(LocalTime.of(11, 30));
                boolean isDinnerTime = currentTime.toLocalTime().isAfter(LocalTime.of(17, 0));

                boolean hadLunchFlag = false;
                boolean hadDinnerFlag = false;
                for (SelectedPlace place : dto.getSelectedPlace()) {
                    if (visited.contains(place)) { // 이미 방문한 장소면 continue
                        continue;
                    }
                    if (isLunchTime && !hadLunch) { // 점심시간이거나 지났는데 음식점 방문을 안했으면 음식점 추가
                        if (place.getCategory().startsWith("food-")) {
                            candidates.add(place);
                            hadLunchFlag = true;
                        }
                    } else if (isDinnerTime && !hadDinner) { // 저녁시간이거나 지났는데 음식점 방문을 안했으면 음식점 추가
                        if (place.getCategory().startsWith("food-")) {
                            candidates.add(place);
                            hadDinnerFlag = true;
                        }
                    } else { // 그 외 시간이면 음식점을 제외하고 추가
                        if (!place.getCategory().startsWith("food-")) {
                            candidates.add(place);
                        }
                    }
                }
                if (hadLunchFlag) hadLunch = true;
                if (hadDinnerFlag) hadDinner = true;
            }

            // 방문해야 할 장소가 없거나 총 장소 개수만큼 방문했으면 반복 종료
            if (candidates.isEmpty() || visited.size() >= dto.getTotalPlaceCount()) break;

            // NN 알고리즘: 가장 가까운 장소 선택
            // tmap api로 거리/시간 계산
            NearestInfo nearestInfo;
            if (dto.getTransport().equals("pedestrian")) {
                List<RouteResponse> routeResponseList = new ArrayList<>();
                for (SelectedPlace candidate : candidates) {
                    RouteResponse routeResponse = tMapApiService.getPedestrianRoutes(PedestrianRouteRequest.builder()
                            .startX(ParseUtil.parseDoubleSafe(currentPlace.getLongitude()))
                            .startY(ParseUtil.parseDoubleSafe(currentPlace.getLatitude()))
                            .endX(ParseUtil.parseDoubleSafe(candidate.getLongitude()))
                            .endY(ParseUtil.parseDoubleSafe(candidate.getLatitude()))
                            .build());
                    routeResponseList.add(routeResponse);
                }
                nearestInfo = tMapApiService.findNearest(routeResponseList, candidates); // 다음 장소 결정
            } else if (dto.getTransport().equals("car")) {
                List<RouteResponse> routeResponseList = new ArrayList<>();
                for (SelectedPlace candidate : candidates) {
                    RouteResponse routeResponse = tMapApiService.getCarRoutes(RouteRequest.builder()
                            .startX(ParseUtil.parseDoubleSafe(currentPlace.getLongitude()))
                            .startY(ParseUtil.parseDoubleSafe(currentPlace.getLatitude()))
                            .endX(ParseUtil.parseDoubleSafe(candidate.getLongitude()))
                            .endY(ParseUtil.parseDoubleSafe(candidate.getLatitude()))
                            .build());
                    routeResponseList.add(routeResponse);
                }
                nearestInfo = tMapApiService.findNearest(routeResponseList, candidates); // 다음 장소 결정
            } else if (dto.getTransport().equals("transit")) {
                List<SimpleTransitRouteResponse> simpleTransitRouteResponseList = new ArrayList<>();
                for (SelectedPlace candidate : candidates) {
                    SimpleTransitRouteResponse response = tMapApiService.getSimpleTransitRoutes(RouteRequest.builder()
                            .startX(ParseUtil.parseDoubleSafe(currentPlace.getLongitude()))
                            .startY(ParseUtil.parseDoubleSafe(currentPlace.getLatitude()))
                            .endX(ParseUtil.parseDoubleSafe(candidate.getLongitude()))
                            .endY(ParseUtil.parseDoubleSafe(candidate.getLatitude()))
                            .build());

                    // 에러가 있다면
                    if (response.getResult() != null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, response.getResult().getMessage());
                    }

                    simpleTransitRouteResponseList.add(response);
                }
                nearestInfo = tMapApiService.findNearestTransit(simpleTransitRouteResponseList, candidates); // 다음 장소 결정
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이동 수단을 찾지 못했습니다");
            }
            SelectedPlace next = nearestInfo.getNearest();

            // 이동 시간 결정(30분 단위로 올림)
            int travelMinutes = ((nearestInfo.getMinTime() + 29) / 30) * 30;

            // 이동 후 머문 시간이 종료 시간을 넘으면 반복 종료
            if (currentTime.plusMinutes(travelMinutes + next.getStayMinutes()).isAfter(dto.getScheduleEndTime())) {
                break;
            }

            // 이동 시간 반영
            currentTime = currentTime.plusMinutes(travelMinutes);

            // 정렬 리스트에 넣기
            sortedSelectedPlaceList.add(next);

            // 방문 처리
            visited.add(next);

            // 현재 장소를 변경
            currentPlace = next;

            // 시간 임시 저장
            LocalDateTime startTime2 = currentTime;

            // 장소 방문 후 머문 시간을 반영해 변경
            currentTime = currentTime.plusMinutes(currentPlace.getStayMinutes());

            //detail dto에 반영
            detailCreateDtoList.add(ScheduleDetailCreateDto.builder()
                    .scheduleContent(currentPlace.getName() + " 방문")
                    .scheduleAddress(currentPlace.getAddress())
                    .latitude(new BigDecimal(currentPlace.getLatitude()))
                    .longitude(new BigDecimal(currentPlace.getLongitude()))
                    .scheduleStartTime(startTime2)
                    .scheduleEndTime(currentTime)
                    .build());
        }

        return ScheduleCreateResponseDto.builder()
                .places(sortedSelectedPlaceList)
                .schedules(detailCreateDtoList)
                .build();
    }
}