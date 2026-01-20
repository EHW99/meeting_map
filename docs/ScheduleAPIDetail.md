# Schedule API 예시
## API 바로가기
| API 호출                                                 | 설명             |
|--------------------------------------------------------|----------------|
| [GET /schedules](#자신이-만든-스케줄-조회)                       | 자신이 만든 스케줄 조회  |
| [POST /schedule/create](#스케줄-생성)                       | 스케줄 생성         |
| [POST /schedules](#스케줄-저장)                             | 스케줄 저장         |
| [PUT /schedules](#스케줄-수정)                              | 스케줄 수정         |
| [DELETE /schedules{scheduleNo}](#스케줄-삭제)               | 스케줄 삭제         |

---

## API 상세

### 자신이 만든 스케줄 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/schedules`, { withCredentials: true })
```

#### Response 예시
```json
[
  {
    "scheduleNo": 1,
    "scheduleName": "스터디 회의",
    "scheduleAbout": "다음 주 프로젝트 스터디 회의 진행",
    "scheduleCreatedDate": "2025-03-04T10:00:00",
    "userId": "user1",
    "details": [
      {
        "scheduleDetailNo": 1,
        "scheduleContent": "스터디 장소 도착",
        "scheduleAddress": "서울특별시 마포구 와우산로 94",
        "latitude": 37.550900,
        "longitude": 126.925300,
        "scheduleStartTime": "2025-03-04T13:00:00",
        "scheduleEndTime": "2025-03-04T13:30:00",
        "scheduleNo": 1
      },
      {
        "scheduleDetailNo": 2,
        "scheduleContent": "팀 회의 시작",
        "scheduleAddress": "홍대입구역 근처 카페",
        "latitude": 37.550400,
        "longitude": 126.926000,
        "scheduleStartTime": "2025-03-04T13:30:00",
        "scheduleEndTime": "2025-03-04T15:00:00",
        "scheduleNo": 1
      }
    ]
  }
]
```

---

### 스케줄에 참여한 회원 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/api/schedules/1/members`)
```

#### Response 예시
```json
[
  {
    "userId": "user1",
    "userEmail": "user1@example.com",
    "userNick": "사용자1",
    "userImg": null,
    "userAddress": null,
    "userType": 1
  },
  {
    "userId": "user4",
    "userEmail": "user4@example.com",
    "userNick": "사용자4",
    "userImg": null,
    "userAddress": null,
    "userType": 1
  },
  {
    "userId": "user5",
    "userEmail": "user5@example.com",
    "userNick": "사용자5",
    "userImg": null,
    "userAddress": null,
    "userType": 1
  }
]
```

---

### 스케줄 생성

#### Request 예시 1 (추가 추천 O, AI 추천 X)
```javascript
axios.post(`${API_BASE_URL}/api/schedules/create`,
    {
        "selectedPlace": [
            {
                "contentId": "126508",
                "address": "서울특별시 종로구 사직로 161 (세종로) ",
                "latitude": "37.5760836609",
                "longitude": "126.9767375783",
                "name": "경복궁",
                "category": "tour-tradition",
                "stayMinutes": 60
            }
        ],
        "scheduleStartTime": "2025-06-01T10:00:00",
        "scheduleEndTime": "2025-06-01T18:00:00",
        "transport": "pedestrian",
        "additionalRecommendation": true,
        "aiRecommendation": false,
        "totalPlaceCount": 4,
        "theme": "tour",
        "stayMinutesMean": 60,
        "pointCoordinate": {
            "latitude": "37.57596445980707",
            "longitude": "126.97685309595215"
        }
    }
)
```

#### Response 예시 1 (추가 추천 O, AI 추천 X)
```json
{
  "places": [
    {
      "contentId": "126508",
      "address": "서울특별시 종로구 사직로 161 (세종로) ",
      "name": "경복궁",
      "latitude": "37.5760836609",
      "longitude": "126.9767375783",
      "category": "tour-tradition",
      "stayMinutes": 60
    },
    {
      "contentId": "126511",
      "address": "서울특별시 종로구 창경궁로 185 ",
      "name": "창경궁",
      "latitude": "37.5776782272",
      "longitude": "126.9938554166",
      "category": "tour-tradition",
      "stayMinutes": 60
    },
    {
      "contentId": "1325115",
      "address": "서울특별시 종로구 종로40가길 18 (종로5가) ",
      "name": "진옥화할매원조닭한마리",
      "latitude": "37.5704292825",
      "longitude": "127.0057128756",
      "category": "food-korean",
      "stayMinutes": 60
    },
    {
      "contentId": "129507",
      "address": "서울특별시 종로구 창신동 ",
      "name": "청계천",
      "latitude": "37.5697015781",
      "longitude": "127.0050907302",
      "category": "tour-park",
      "stayMinutes": 60
    }
  ],
  "schedules": [
    {
      "scheduleContent": "경복궁 방문",
      "scheduleAddress": "서울특별시 종로구 사직로 161 (세종로) ",
      "latitude": 37.5760836609,
      "longitude": 126.9767375783,
      "scheduleStartTime": "2025-06-01T10:00:00",
      "scheduleEndTime": "2025-06-01T11:00:00"
    },
    {
      "scheduleContent": "창경궁 방문",
      "scheduleAddress": "서울특별시 종로구 창경궁로 185 ",
      "latitude": 37.5776782272,
      "longitude": 126.9938554166,
      "scheduleStartTime": "2025-06-01T11:30:00",
      "scheduleEndTime": "2025-06-01T12:30:00"
    },
    {
      "scheduleContent": "진옥화할매원조닭한마리 방문",
      "scheduleAddress": "서울특별시 종로구 종로40가길 18 (종로5가) ",
      "latitude": 37.5704292825,
      "longitude": 127.0057128756,
      "scheduleStartTime": "2025-06-01T13:00:00",
      "scheduleEndTime": "2025-06-01T14:00:00"
    },
    {
      "scheduleContent": "청계천 방문",
      "scheduleAddress": "서울특별시 종로구 창신동 ",
      "latitude": 37.5697015781,
      "longitude": 127.0050907302,
      "scheduleStartTime": "2025-06-01T14:30:00",
      "scheduleEndTime": "2025-06-01T15:30:00"
    }
  ]
}
```

#### Request 예시 2 (AI 추천 O)
```javascript
axios.post(`${API_BASE_URL}/api/schedules/create`,
    {
        "selectedPlace": [
            {
                "contentId": "126508",
                "address": "서울특별시 종로구 사직로 161 (세종로) ",
                "latitude": "37.5760836609",
                "longitude": "126.9767375783",
                "name": "경복궁",
                "category": "tour-tradition",
                "stayMinutes": 60
            }
        ],
        "scheduleStartTime": "2025-06-01T10:00:00",
        "scheduleEndTime": "2025-06-01T18:00:00",
        "transport": "pedestrian",
        "additionalRecommendation": true,
        "aiRecommendation": true,
        "totalPlaceCount": 4,
        "theme": "tour",
        "stayMinutesMean": 60,
        "pointCoordinate": {
            "latitude": "37.57596445980707",
            "longitude": "126.97685309595215"
        }
    }
)
```

#### Response 예시 2 (AI 추천 O)
```json
{
  "places": [
    {
      "contentId": "126508",
      "address": "서울특별시 종로구 사직로 161 (세종로) ",
      "name": "경복궁",
      "latitude": "37.5760836609",
      "longitude": "126.9767375783",
      "category": "tour-tradition",
      "stayMinutes": 60
    },
    {
      "contentId": "129507",
      "address": "서울특별시 종로구 창신동 ",
      "name": "청계천",
      "latitude": "37.5697015781",
      "longitude": "127.0050907302",
      "category": "tour-park",
      "stayMinutes": 60
    },
    {
      "contentId": "1325115",
      "address": "서울특별시 종로구 종로40가길 18 (종로5가) ",
      "name": "진옥화할매원조닭한마리",
      "latitude": "37.5704292825",
      "longitude": "127.0057128756",
      "category": "food-korean",
      "stayMinutes": 60
    },
    {
      "contentId": "126943",
      "address": "서울특별시 종로구 북악산로 267 (평창동) ",
      "name": "북악스카이 팔각정",
      "latitude": "37.6020279351",
      "longitude": "126.9805642714",
      "category": "tour-park",
      "stayMinutes": 60
    }
  ],
  "schedules": [
    {
      "scheduleContent": "경복궁 방문",
      "scheduleAddress": "서울특별시 종로구 사직로 161 (세종로) ",
      "latitude": 37.5760836609,
      "longitude": 126.9767375783,
      "scheduleStartTime": "2025-06-01T10:00:00",
      "scheduleEndTime": "2025-06-01T11:00:00"
    },
    {
      "scheduleContent": "청계천 방문",
      "scheduleAddress": "서울특별시 종로구 창신동 ",
      "latitude": 37.5697015781,
      "longitude": 127.0050907302,
      "scheduleStartTime": "2025-06-01T12:00:00",
      "scheduleEndTime": "2025-06-01T13:00:00"
    },
    {
      "scheduleContent": "진옥화할매원조닭한마리 방문",
      "scheduleAddress": "서울특별시 종로구 종로40가길 18 (종로5가) ",
      "latitude": 37.5704292825,
      "longitude": 127.0057128756,
      "scheduleStartTime": "2025-06-01T13:30:00",
      "scheduleEndTime": "2025-06-01T14:30:00"
    },
    {
      "scheduleContent": "북악스카이 팔각정 방문",
      "scheduleAddress": "서울특별시 종로구 북악산로 267 (평창동) ",
      "latitude": 37.6020279351,
      "longitude": 126.9805642714,
      "scheduleStartTime": "2025-06-01T16:30:00",
      "scheduleEndTime": "2025-06-01T17:30:00"
    }
  ]
}
```

---

### 스케줄 저장

#### Request 예시 1
```javascript
axios.post(`${API_BASE_URL}/schedules`, { withCredentials: true },
    {
        "scheduleName": "test",
        "scheduleAbout": "testabout",
        "details": [
            {
                "scheduleContent": "경복궁 방문",
                "scheduleAddress": "서울특별시 종로구 사직로 161 (세종로) ",
                "latitude": 37.5760836609,
                "longitude": 126.9767375783,
                "scheduleStartTime": "2025-06-01T10:00:00",
                "scheduleEndTime": "2025-06-01T11:00:00"
            },
            {
                "scheduleContent": "창경궁 방문",
                "scheduleAddress": "서울특별시 종로구 창경궁로 185 ",
                "latitude": 37.5776782272,
                "longitude": 126.9938554166,
                "scheduleStartTime": "2025-06-01T11:30:00",
                "scheduleEndTime": "2025-06-01T12:30:00"
            },
            {
                "scheduleContent": "진옥화할매원조닭한마리 방문",
                "scheduleAddress": "서울특별시 종로구 종로40가길 18 (종로5가) ",
                "latitude": 37.5704292825,
                "longitude": 127.0057128756,
                "scheduleStartTime": "2025-06-01T13:00:00",
                "scheduleEndTime": "2025-06-01T14:00:00"
            },
            {
                "scheduleContent": "청계천 방문",
                "scheduleAddress": "서울특별시 종로구 창신동 ",
                "latitude": 37.5697015781,
                "longitude": 127.0050907302,
                "scheduleStartTime": "2025-06-01T14:30:00",
                "scheduleEndTime": "2025-06-01T15:30:00"
            }
        ]
    }
)
```

---

### 스케줄 수정

#### Request 예시 1
```javascript
axios.put(`${API_BASE_URL}/schedules`, { withCredentials: true },
    {
        "scheduleName": "test",
        "scheduleAbout": "testabout",
        "details": [
            {
                "scheduleContent": "경복궁 방문",
                "scheduleAddress": "서울특별시 종로구 사직로 161 (세종로) ",
                "latitude": 37.5760836609,
                "longitude": 126.9767375783,
                "scheduleStartTime": "2025-06-01T10:00:00",
                "scheduleEndTime": "2025-06-01T11:00:00"
            },
            {
                "scheduleContent": "창경궁 방문",
                "scheduleAddress": "서울특별시 종로구 창경궁로 185 ",
                "latitude": 37.5776782272,
                "longitude": 126.9938554166,
                "scheduleStartTime": "2025-06-01T11:30:00",
                "scheduleEndTime": "2025-06-01T12:30:00"
            },
            {
                "scheduleContent": "진옥화할매원조닭한마리 방문",
                "scheduleAddress": "서울특별시 종로구 종로40가길 18 (종로5가) ",
                "latitude": 37.5704292825,
                "longitude": 127.0057128756,
                "scheduleStartTime": "2025-06-01T13:00:00",
                "scheduleEndTime": "2025-06-01T14:00:00"
            },
            {
                "scheduleContent": "청계천 방문",
                "scheduleAddress": "서울특별시 종로구 창신동 ",
                "latitude": 37.5697015781,
                "longitude": 127.0050907302,
                "scheduleStartTime": "2025-06-01T14:30:00",
                "scheduleEndTime": "2025-06-01T15:30:00"
            }
        ]
    }
)
```

---

### 스케줄 삭제

#### Request 예시 1
```javascript
axios.delete(`${API_BASE_URL}/schedules/1`, { withCredentials: true })
```
