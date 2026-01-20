## 📚 목차
- [Home](../README.md)
- [User API](UserAPI.md) [(Example)](UserAPIDetail.md)
- [Auth API](AuthAPI.md)
- [Friendship API](FriendshipAPI.md)
- [Map API](MapAPI.md) [(Example)](MapAPIDetail.md)
- **Schedule API** [(Example)](ScheduleAPIDetail.md)
- [Path API](PathAPI.md) [(Example)](PathAPIDetail.md)
- [Board API](BoardAPI.md) [(Example)](BoardAPIDetail.md)
- [Comment API](CommentAPI.md) [(Example)](CommentAPIDetail.md)
- [Group API](GroupAPI.md) [(Example)](GroupAPIDetail.md)
- [GroupBoard API](GroupBoardAPI.md) [(Example)](GroupBoardAPIDetail.md)
- [GroupComment API](GroupCommentAPI.md) [(Example)](GroupCommentAPIDetail.md)

# Schedule API
## API 바로가기
| API 호출                                    | 설명               |
|-------------------------------------------|------------------|
| [GET /schedules](#자신이-만든-스케줄-조회)          | 자신이 만든 스케줄 조회 ✏️ |
| [POST /schedules/create](#스케줄-생성)         | 스케줄 생성           |
| [POST /schedules](#스케줄-저장)                | 스케줄 저장 ✏️        |
| [PUT /schedules](#스케줄-수정)                 | 스케줄 수정           |
| [DELETE /schedules/{scheduleNo}](#스케줄-삭제) | 스케줄 삭제           |

---

## API 상세

### 자신이 만든 스케줄 조회

**GET** `/schedules`

> 로그인한 회원이 만든 스케줄을 조회합니다.  
> 스케줄의 상세 정보까지 한번에 조회합니다.

## [Example](ScheduleAPIDetail.md#자신이-만든-스케줄-조회)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 응답 파라미터
| 파라미터                | 설명         | 값 예시                   |
|---------------------|------------|------------------------|
| scheduleNo          | 스케줄 번호     | 1                      |
| scheduleName        | 스케줄 이름     | "스터디 회의"               |
| scheduleAbout       | 스케줄 설명     | "다음 주 프로젝트 스터디 회의 진행"  |
| scheduleCreatedDate | 스케줄 생성 시간  | "2025-03-04T10:00:00"  |
| userId              | 스케줄 생성자 ID | "user1"                |
| details             | 스케줄 상세     | {scheduleDetailNo,...} |
| scheduleDetailNo    | 스케줄 상세 번호  | 1                      |
| scheduleContent     | 스케줄 내용     | "스터디 장소 도착"            |
| scheduleAddress     | 장소 주소      | "서울특별시 마포구 와우산로 94"    |
| latitude            | 장소 위도      | 37.550900              |
| longitude           | 장소 경도      | 126.925300             |
| scheduleStartTime   | 스케줄 시작 시간  | "2025-03-04T13:00:00"  |
| scheduleEndTime     | 스케줄 종료 시간  | "2025-03-04T13:30:00"  |
| scheduleNo          | 포함된 스케줄 번호 | 1                      |

---

### 스케줄 생성

**POST** `/schedules/create`

> 사용자로부터 입력받은 데이터를 통해 스케줄을 생성합니다.

## [Example](ScheduleAPIDetail.md#스케줄-생성)

#### 요청 파라미터 ✏️
| 파라미터                     | 필수 여부                  | 설명                                                            | 값 예시                           |
|--------------------------|------------------------|---------------------------------------------------------------|--------------------------------|
| selectedPlace            | 추가(AI) 추천 여부가 false면 O | 선택한 장소들                                                       | [{contentId, ...}, ...]        |
| contentId                |                        | 장소 ID                                                         | 126508                         |
| address                  |                        | 장소 주소                                                         | "서울특별시 종로구 사직로 161 (세종로)"      |
| latitude                 |                        | 장소 위도                                                         | "37.5760836609"                |
| longitude                |                        | 장소 경도                                                         | "126.9767375783"               |
| name                     |                        | 장소 이름                                                         | "경복궁"                          |
| category                 |                        | 카테고리                                                          | "tour-tradition"               |
| stayMinutes              |                        | 장소별 머무는 시간(분 단위)                                              | 60                             |
| scheduleStartTime        | O                      | 스케줄 시작 시간(input type="datetime-local")                        | "2025-06-01T10:00:00"          |
| scheduleEndTime          | O                      | 스케줄 종료 시간(input type="datetime-local")                        | "2025-06-01T18:00:00"          |
| transport                | O                      | 이동 수단                                                         | "pedestrian", "car", "transit" |
| additionalRecommendation | O                      | 추가 추천 여부                                                      | true/false                     |
| aiRecommendation         | X                      | AI 추천 여부, 추가 추천 여부보다 우선순위가 높음                                 | true/false                     |
| totalPlaceCount          | 추가(AI) 추천 여부가 true면 O  | 선택한 장소를 포함한 스케줄에 포함될 총 장소 개수                                  | 4                              |
| theme                    | 추가(AI) 추천 여부가 true면 O  | 추천에 사용될 테마                                                    | "tour" 등                       |
| stayMinutesMean          | 추가(AI) 추천 여부가 true면 O  | 추천될 장소들의 평균 머무는 시간(분 단위)                                      | 60                             |
| pointCoordinate          | 추가(AI) 추천 여부가 true면 O  | 추천을 시작할 장소 위치(출발지-목적지 기반 검색이면 목적지 좌표, 중간 지점 기반 검색이면 중간 지점 좌표) | {latitude, longitude}          |

#### theme 종류
| theme    | 설명   | 카테고리                             |
|----------|------|----------------------------------|
| tour     | 관광   | tour                             |
| nature   | 자연힐링 | tour-nature                      |
| history  | 역사탐방 | tour-tradition                   |
| food     | 음식투어 | food                             |
| shopping | 쇼핑   | shopping                         |
| date     | 데이트  | tour-park, tour-theme-park, cafe |

---

### 스케줄 저장

**POST** `/schedules`

> 생성한 스케줄을 저장합니다.  
> /schedules/create로 반환된 데이터의 schedules 부분을 details에 넣어 scheduleName, scheduleAbout과 함께 보내면 됩니다.

## [Example](ScheduleAPIDetail.md#스케줄-저장)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 파라미터
| 파라미터          | 필수 여부 | 설명        | 값 예시                         |
|---------------|-------|-----------|------------------------------|
| scheduleName  | O     | 스케줄 이름    | "스터디 회의"                     |
| scheduleAbout | O     | 스케줄 설명    | "다음 주 프로젝트 스터디 회의 진행"        |
| details       | O     | 스케줄 세부 내용 | [{scheduleContent:...}, ...] |

#### 응답 헤더 ✏️
| 헤더       | 설명          | 값 예시             |
|----------|-------------|------------------|
| Location | 스케줄이 생성된 주소 | /api/schedules/7 |

---

### 스케줄 수정

**PUT** `/schedules`

> 스케줄을 수정해서 저장합니다.

## [Example](ScheduleAPIDetail.md#스케줄-수정)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 파라미터
| 파라미터          | 필수 여부 | 설명        | 값 예시                         |
|---------------|-------|-----------|------------------------------|
| scheduleName  | O     | 스케줄 이름    | "스터디 회의"                     |
| scheduleAbout | O     | 스케줄 설명    | "다음 주 프로젝트 스터디 회의 진행"        |
| details       | O     | 스케줄 세부 내용 | [{scheduleContent:...}, ...] |

---

### 스케줄 삭제

**DELETE** `/schedules/{scheduleNo}`

> 스케줄을 삭제합니다.

## [Example](ScheduleAPIDetail.md#스케줄-삭제)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 파라미터
| 파라미터       | 설명     | 값 예시 |
|------------|--------|------|
| scheduleNo | 스케줄 번호 | 1    |