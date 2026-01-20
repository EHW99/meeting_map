## 📚 목차
- [Home](../README.md)
- [User API](UserAPI.md) [(Example)](UserAPIDetail.md)
- [Auth API](AuthAPI.md)
- [Friendship API](FriendshipAPI.md)
- [Map API](MapAPI.md) [(Example)](MapAPIDetail.md)
- [Schedule API](ScheduleAPI.md) [(Example)](ScheduleAPIDetail.md)
- **Path API** [(Example)](PathAPIDetail.md)
- [Board API](BoardAPI.md) [(Example)](BoardAPIDetail.md)
- [Comment API](CommentAPI.md) [(Example)](CommentAPIDetail.md)
- [Group API](GroupAPI.md) [(Example)](GroupAPIDetail.md)
- [GroupBoard API](GroupBoardAPI.md) [(Example)](GroupBoardAPIDetail.md)
- [GroupComment API](GroupCommentAPI.md) [(Example)](GroupCommentAPIDetail.md)

# Path API
## API 바로가기
| API 호출                                   | 설명               |
|------------------------------------------|------------------|
| [POST /path/pedestrian](#스케줄로-보행자-길찾기)   | 스케줄로 보행자 길찾기     |
| [POST /path/car](#스케줄로-자동차-길찾기)          | 스케줄로 자동차 길찾기     |
| [POST /path/transit](#스케줄로-대중교통-길찾기)     | 스케줄로 대중교통 길찾기    |
| [GET /path/pedestrian](#장소-이름으로-보행자-길찾기) | 장소 이름으로 보행자 길찾기  |
| [GET /path/car](#장소-이름으로-자동차-길찾기)        | 장소 이름으로 자동차 길찾기  |
| [GET /path/transit](#장소-이름으로-대중교통-길찾기)   | 장소 이름으로 대중교통 길찾기 |

---

## API 상세

### 스케줄로 보행자 길찾기

**POST** `/path/pedestrian`

> /schedules/create로부터 받은 schedules 리스트를 그대로 입력받아 보행자 경로를 반환합니다.  
> 또는 이미 만들어진 스케줄의 details 리스트도 입력 가능합니다.

## [Example](PathAPIDetail.md#스케줄로-보행자-길찾기)

#### 응답 파라미터
| 파라미터        | 설명                           | 값 예시                                         |
|-------------|------------------------------|----------------------------------------------|
| origin      | 경로 시작 장소                     | {name,x,y}                                   |
| name        | 장소 이름                        | "경복궁"                                        |
| x           | x 좌표(longitude)              | 126.9767375783                               |
| y           | y 좌표(latitude)               | 37.5760836609                                |
| destination | 경로 끝 장소                      | {name,x,y}                                   |
| distance    | 경로 총 이동 거리(미터 단위)            | 2184                                         |
| time        | 경로 총 이동 시간(분 단위)             | 29                                           |
| fare        | 경로까지의 총 택시 비용(보행자는 무조건 null) | null                                         |
| coordinates | 경로 폴리라인 좌표                   | [[126.9768181264162,37.57608131977726], ...] |

---

### 스케줄로 자동차 길찾기

**POST** `/path/car`

> /schedules/create로부터 받은 schedules 리스트를 그대로 입력받아 자동차 경로를 반환합니다.  
> 또는 이미 만들어진 스케줄의 details 리스트도 입력 가능합니다.

## [Example](PathAPIDetail.md#스케줄로-자동차-길찾기)

#### 응답 파라미터
| 파라미터        | 설명                | 값 예시                                          |
|-------------|-------------------|-----------------------------------------------|
| origin      | 경로 시작 장소          | {name,x,y}                                    |
| name        | 장소 이름             | "경복궁"                                         |
| x           | x 좌표(longitude)   | 126.9767375783                                |
| y           | y 좌표(latitude)    | 37.5760836609                                 |
| destination | 경로 끝 장소           | {name,x,y}                                    |
| distance    | 경로 총 이동 거리(미터 단위) | 3963                                          |
| time        | 경로 총 이동 시간(분 단위)  | 15                                            |
| fare        | 경로까지의 총 택시 비용     | 7600                                          |
| coordinates | 경로 폴리라인 좌표        | [[126.97629597137697,37.57538972416959], ...] |

---

### 스케줄로 대중교통 길찾기

**POST** `/path/transit`

> /schedules/create로부터 받은 schedules 리스트를 그대로 입력받아 대중교통 경로를 반환합니다.  
> 또는 이미 만들어진 스케줄의 details 리스트도 입력 가능합니다.

## [Example](PathAPIDetail.md#스케줄로-대중교통-길찾기)

#### 응답 파라미터
| 파라미터          | 설명                  | 값 예시                                              |
|---------------|---------------------|---------------------------------------------------|
| origin        | 경로 시작 장소            | {name,x,y}                                        |
| name          | 장소 이름               | "경복궁"                                             |
| x             | x 좌표(longitude)     | 126.9767375783                                    |
| y             | y 좌표(latitude)      | 37.5760836609                                     |
| destination   | 경로 끝 장소             | {name,x,y}                                        |
| plan          | 이동 루트               | [{totalDistance,totalTime,...},{...})]            |
| totalDistance | 경로 총 이동 거리(미터 단위)   | 1553                                              |
| totalTime     | 경로 총 이동 시간(분 단위)    | 27                                                |
| totalWalkTime | 경로 총 도보 이동 시간(분 단위) | 21                                                |
| transferCount | 환승 횟수               | 0                                                 |
| fare          | 총 요금                | 1500                                              |
| pathType      | 이동 방법 타입            | 1(지하철), 2(버스), 3(버스+지하철)                          |
| detail        | 세부 경로 이동 방법         | [{mode,timeline,...},{...}]                       |
| mode          | 세부 경로 이동 방식         | "도보", "버스", "지하철"                                 |
| timeline      | 세부 경로 이동에 관한 설명     | "경복궁까지 도보로 이동"                                    |
| distance      | 세부 경로 이동 거리(미터 단위)  | 292                                               |
| time          | 세부 경로 이동 시간(분 단위)   | 4                                                 |
| routeColor    | 세부 경로 색(색상 코드)      | "4D524C"(도보), (버스, 지하철은 고유의 색)                    |
| routeStyle    | 세부 경로 모양            | "dashed"(도보), "solid"(버스, 지하철)                    |
| start         | 세부 경로 시작 장소         | {name,x,y}                                        |
| end           | 세부 경로 끝 장소          | {name,x,y}                                        |
| coordinates   | 세부 경로 폴리라인 좌표       | [[126.97682,37.57608],[126.977425,37.575203],...] |

---

### 장소 이름으로 보행자 길찾기

**GET** `/path/pedestrian`
> 장소 이름(주소)들을 입력받아 보행자 경로를 반환합니다.  
> 장소 이름은 start와 end를 입력받으면 출발지부터 목적지까지의 경로,  
> name을 입력받으면 중간지점을 찾아 각 지점부터 중간지점까지의 경로들을 반환합니다.  
> 응답 파라미터는 [스케줄로 보행자 길찾기](#스케줄로-보행자-길찾기)와 같습니다.

## [Example](PathAPIDetail.md#장소-이름으로-보행자-길찾기)

#### 요청 쿼리 파라미터
| 파라미터  | 설명            | 값 예시            |
|-------|---------------|-----------------|
| start | 출발지 이름(주소)    | 한성대학교           |
| end   | 목적지 이름(주소)    | 서울 종로구 대학로 101  |
| name  | 여러 출발지 이름(주소) | 한성대학교, 시청역, ... |


---

### 장소 이름으로 자동차 길찾기

**GET** `/path/car`
> 장소 이름(주소)들을 입력받아 자동차 경로를 반환합니다.  
> 장소 이름은 start와 end를 입력받으면 출발지부터 목적지까지의 경로,  
> name을 입력받으면 중간지점을 찾아 각 지점부터 중간지점까지의 경로들을 반환합니다.  
> 응답 파라미터는 [스케줄로 자동차 길찾기](#스케줄로-자동차-길찾기)와 같습니다.

## [Example](PathAPIDetail.md#장소-이름으로-자동차-길찾기)

#### 요청 쿼리 파라미터
| 파라미터  | 설명            | 값 예시            |
|-------|---------------|-----------------|
| start | 출발지 이름(주소)    | 한성대학교           |
| end   | 목적지 이름(주소)    | 서울 종로구 대학로 101  |
| name  | 여러 출발지 이름(주소) | 한성대학교, 시청역, ... |

---

### 장소 이름으로 대중교통 길찾기

**GET** `/path/transit`

> 장소 이름(주소)들을 입력받아 대중교통 경로를 반환합니다.  
> 장소 이름은 start와 end를 입력받으면 출발지부터 목적지까지의 경로,  
> name을 입력받으면 중간지점을 찾아 각 지점부터 중간지점까지의 경로들을 반환합니다.  
> 응답 파라미터는 [스케줄로 대중교통 길찾기](#스케줄로-대중교통-길찾기)와 같습니다.

## [Example](PathAPIDetail.md#장소-이름으로-대중교통-길찾기)

#### 요청 쿼리 파라미터
| 파라미터  | 설명            | 값 예시            |
|-------|---------------|-----------------|
| start | 출발지 이름(주소)    | 한성대학교           |
| end   | 목적지 이름(주소)    | 서울 종로구 대학로 101  |
| name  | 여러 출발지 이름(주소) | 한성대학교, 시청역, ... |