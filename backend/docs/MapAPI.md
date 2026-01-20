## 📚 목차
- [Home](../README.md)
- [User API](UserAPI.md) [(Example)](UserAPIDetail.md)
- [Auth API](AuthAPI.md)
- [Friendship API](FriendshipAPI.md)
- **Map API** [(Example)](MapAPIDetail.md)
- [Schedule API](ScheduleAPI.md) [(Example)](ScheduleAPIDetail.md)
- [Path API](PathAPI.md) [(Example)](PathAPIDetail.md)
- [Board API](BoardAPI.md) [(Example)](BoardAPIDetail.md)
- [Comment API](CommentAPI.md) [(Example)](CommentAPIDetail.md)
- [Group API](GroupAPI.md) [(Example)](GroupAPIDetail.md)
- [GroupBoard API](GroupBoardAPI.md) [(Example)](GroupBoardAPIDetail.md)
- [GroupComment API](GroupCommentAPI.md) [(Example)](GroupCommentAPIDetail.md)

# MAP API
## API 바로가기
| API 호출                             | 설명             |
|------------------------------------|----------------|
| [GET /map/category](#카테고리-조회)      | 카테고리 조회        |
| [GET /map](#지도-출력)                 | 지도 출력          |
| [GET /map/detail](#장소-세부-정보-출력)    | 장소 세부 정보 출력 ✏️ |
| [GET /map/autocomplete](#장소명-자동완성) | 장소명 자동완성       |

---

## API 상세

### 카테고리 조회

**GET** `/map/category`
> 카테고리 목록을 조회합니다.  
> 세부 카테고리 코드는 퀴리 파라미터에 부모 카테고리 코드를 넣어서 조회할 수 있습니다.

## [Example](MapAPIDetail.md#카테고리-조회)

#### 요청 쿼리 파라미터
| 파라미터     | 필수 여부 | 설명      | 값 예시 |
|----------|-------|---------|------|
| category | X     | 카테고리 코드 | cafe |

#### category 종류
| 카테고리 코드                   | 설명    | 부모 카테고리 코드 |
|---------------------------|-------|------------|
| tour                      | 관광지   | X          |
| tour-nature               | 자연    | tour       |
| tour-tradition            | 역사    | tour       |
| tour-park                 | 공원    | tour       |
| tour-theme-park           | 테마파크  | tour       |
| food                      | 음식점   | X          |
| food-korean               | 한식    | food       |
| food-western              | 양식    | food       |
| food-japanese             | 일식    | food       |
| food-chinese              | 중식    | food       |
| food-other                | 기타    | food       |
| cafe                      | 카페    | X          |
| convenience-store         | 편의점   | X          |
| shopping                  | 쇼핑    | X          |
| shopping-permanent-market | 상설시장  | shopping   |
| shopping-department-store | 백화점   | shopping   |
| culture                   | 문화시설  | X          |
| event                     | 공연/행사 | X          |

#### 응답 바디 파라미터
| 파라미터 | 설명      | 값 예시              |
|------|---------|-------------------|
| code | 카테고리 코드 | tour, tour-park 등 |
| name | 카테고리 이름 | 관광지, 공원 등         |

---

### 지도 출력

**GET** `/map`

> 사용자가 선택한 방법으로 주변의 장소를 조회합니다.

## [Example](MapAPIDetail.md#지도-출력)

#### 요청 쿼리 파라미터
| 파라미터      | 필수 여부                                  | 설명             | 값 예시                                                                             |
|-----------|----------------------------------------|----------------|----------------------------------------------------------------------------------|
| search    | O                                      | 장소 선택 방법       | location(위치), destination(도착지), middle-point(중간지점)                               |
| sort      | O                                      | 정렬 방법          | title_asc(가나다 오름차순), rating_asc(평점 오름차순), user_ratings_total_dsc(총 평점 개수 내림차순) 등 |
| latitude  | search=location일 때만 O                  | 위도             | 37.6092635094031                                                                 |
| longitude | search=location일 때만 O                  | 경도             | 127.06471287129368                                                               |
| start     | search=destination일 때만 O               | 출발 장소 이름, 주소   | 올림픽공원, 서울 송파구 방이동 88 등                                                           |
| end       | search=destination일 때만 O               | 도착 장소 이름, 주소   | 시청역, 서울 중구 세종대로 지하 101 등                                                         |
| name      | search=middle-point일 때만 O(최소 2개 입력 필요) | 각 출발 장소 이름, 주소 | 한성대, 서울 성북구 삼선교로16길 116 등                                                        |
| category  | X                                      | 카테고리 코드(필수X)   | tour, food-korean, cafe 등                                                        |

#### 응답 바디 파라미터
| 파라미터             | 설명      | 값 예시                                  |
|------------------|---------|---------------------------------------|
| start            | 출발지     | {name, address, latitude, longitude}  |
| name             | 장소 이름   | "한성대학교"                               |
| address          | 장소 주소   | "서울 성북구 삼선교로16길 116"                  |
| latitude         | 장소 위도   | "37.5825624632779"                    |
| longitude        | 장소 경도   | "127.010225523923"                    |
| end              | 도착지     | {name, address, latitude, longitude}  |
| list             | 주변 장소   | [{address,...},...]                   |
| contentId        | 장소 ID   | "google_ChIJv3WOusKifDURkNGSz-MjBAw"  |
| category         | 카테고리    | "cafe"                                |
| thumbnail        | 썸네일 이미지 | https://lh3.googleusercontent.com/... |
| rating           | 평점      | "4.4"                                 |
| userRatingsTotal | 총 평점 개수 | "7"                                   |

---

### 장소 세부 정보 출력

**GET** `/map/detail`

> 특정한 장소의 세부 정보를 출력합니다.

## [Example](MapAPIDetail.md#장소-세부-정보-출력)

#### 요청 쿼리 파라미터
| 파라미터      | 필수 여부 | 설명    | 값 예시    |
|-----------|-------|-------|---------|
| contentId | O     | 장소 ID | 2559938 |

#### 응답 바디 파라미터 ✏️
| 파라미터                    | 설명        | 값 예시                                                            |
|-------------------------|-----------|-----------------------------------------------------------------|
| address                 | 장소 주소     | "종로구 관훈동 18"                                                    |
| contentId               | 장소 ID     | "google_ChIJv3WOusKifDURkNGSz-MjBAw"                            |
| thumbnails              | 이미지 리스트   | ["https://lh3.googleusercontent.com...",...]                    |
| latitude                | 장소 위도     | "37.5735896"                                                    |
| longitude               | 장소 경도     | "126.9856075"                                                   |
| name                    | 장소 이름     | "다경향실"                                                          |
| rating                  | 평점        | "4.4"                                                           |
| userRatingsTotal        | 총 평점 개수   | "7"                                                             |
| phoneNumber             | 전화번호      | "02-723-3651"                                                   |
| url                     | 주소        | null                                                            |
| reviews                 | 리뷰        | [{author,language,rating,...}]                                  |
| author                  | 리뷰 작성자    | "정창욱"                                                           |
| language                | 리뷰 언어     | "ko"                                                            |
| rating                  | 리뷰 평점     | "5"                                                             |
| relativeTimeDescription | 리뷰 대략 작성일 | "7년 전"                                                          |
| text                    | 리뷰 내용     | "87년에 문을 연 인사동의 터줏대감. 중국의 대익차대리점을 겸하고있어서 정통보이차와 국산차를 모두 맛볼수있다." |
| time                    | 리뷰 작성일    | "2017-06-23T13:45:39"                                           |

---

### 장소명 자동완성

**GET** `/map/autocomplete`

> 장소명을 자동완성해 목록을 보여줍니다.  
> 서울 지역의 장소만 보여줍니다.

## [Example](MapAPIDetail.md#장소명-자동완성)

#### 요청 쿼리 파라미터
| 파라미터 | 필수 여부 | 설명  | 값 예시 |
|------|-------|-----|------|
| name | O     | 장소명 | 한성대  |

#### 응답 바디 파라미터
| 파라미터      | 설명                    | 값 예시               |
|-----------|-----------------------|--------------------|
| id        | 장소 ID(contentId와는 별개) | 11272875           |
| placeName | 장소명                   | 한성대학교              |
| address   | 주소                    | 서울 성북구 삼선교로16길 116 |