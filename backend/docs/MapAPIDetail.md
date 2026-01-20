## 📚 목차
- [Home](../README.md)
- [User API](UserAPI.md) [(Example)](UserAPIDetail.md)
- [Auth API](AuthAPI.md)
- [Friendship API](FriendshipAPI.md)
- [Map API](MapAPI.md) **(Example)**
- [Schedule API](ScheduleAPI.md) [(Example)](ScheduleAPIDetail.md)
- [Path API](PathAPI.md) [(Example)](PathAPIDetail.md)
- [Board API](BoardAPI.md) [(Example)](BoardAPIDetail.md)
- [Comment API](CommentAPI.md) [(Example)](CommentAPIDetail.md)
- [Group API](GroupAPI.md) [(Example)](GroupAPIDetail.md)
- [GroupBoard API](GroupBoardAPI.md) [(Example)](GroupBoardAPIDetail.md)
- [GroupComment API](GroupCommentAPI.md) [(Example)](GroupCommentAPIDetail.md)

# Schedule API 예시
## API 바로가기
| API 호출                             | 설명          |
|------------------------------------|-------------|
| [GET /map/category](#카테고리-조회)      | 카테고리 조회     |
| [GET /map](#지도-출력)                 | 지도 출력       |
| [GET /map/detail](#장소-세부-정보-출력)    | 장소 세부 정보 출력 |
| [GET /map/autocomplete](#장소명-자동완성) | 장소명 자동완성    |

---

## API 상세

### 카테고리 조회

#### Request 예시 1 (카테고리)
```javascript
axios.get(`${API_BASE_URL}/map/category`)
```

#### Response 예시 1 (카테고리)
```json
[
    {
        "code": "tour",
        "name": "관광지"
    },
    {
        "code": "food",
        "name": "음식점"
    },
    {
        "code": "cafe",
        "name": "카페"
    },
    {
        "code": "convenience-store",
        "name": "편의점"
    },
    {
        "code": "shopping",
        "name": "쇼핑"
    },
    {
        "code": "culture",
        "name": "문화시설"
    },
    {
        "code": "event",
        "name": "공연/행사"
    }
]
```

#### Request 예시 2 (세부 카테고리)
```javascript
axios.get(`${API_BASE_URL}/map/category?category=tour`)
```

#### Response 예시 2 (세부 카테고리)
```json
[
    {
        "code": "tour-nature",
        "name": "자연"
    },
    {
        "code": "tour-tradition",
        "name": "역사"
    },
    {
        "code": "tour-park",
        "name": "공원"
    },
    {
        "code": "tour-theme-park",
        "name": "테마파크"
    }
]
```

---

### 지도 출력

#### Request 예시 1 (출발지-도착지)
```javascript
axios.get(`${API_BASE_URL}/map?search=destination&sort=user_ratings_total_dsc&start=한성대학교&end=종로구 관훈동 18&category=cafe`)
```

#### Response 예시 1 (출발지-도착지)
```json
{
  "start": {
    "name": "한성대학교",
    "address": "서울 성북구 삼선교로16길 116",
    "latitude": "37.5825624632779",
    "longitude": "127.010225523923"
  },
  "end": {
    "name": "명신당필방",
    "address": "서울 종로구 인사동길 34",
    "latitude": "37.57356831591039",
    "longitude": "126.9856982140611"
  },
  "list": [
    {
      "address": "종로구 관훈동 18",
      "contentId": "google_ChIJv3WOusKifDURkNGSz-MjBAw",
      "category": "cafe",
      "thumbnail": "https://lh3.googleusercontent.com/place-photos/AJnk2cym7sgAzlB4C_F8_VbXTi9JhfjM5agM1d2rQ7DxsaV0jNN6fySIJfM-U-opSGaYCim7L47cDr2jU8RioU4X-cmsqtRyxRVcLxwvCBv0bUKoktNSDGC_4BNcIpMuPdKQAlkqCJLGWdyNy_CN2aA=s1600-w800",
      "latitude": "37.5735896",
      "longitude": "126.9856075",
      "name": "다경향실",
      "rating": "4.4",
      "userRatingsTotal": "7"
    },
    {
      "address": "서울특별시 종로구 사직로9길 22 (필운동) ",
      "contentId": "2783352",
      "category": "cafe",
      "thumbnail": "http://tong.visitkorea.or.kr/cms/resource/84/2790084_image2_1.jpg",
      "latitude": "37.5774250096",
      "longitude": "126.9677078075",
      "name": "스태픽스",
      "rating": "4.2",
      "userRatingsTotal": "412"
    }
  ]
}
```

#### Request 예시 2 (중간 지점)
```javascript
axios.get(`${API_BASE_URL}/map?search=middle-point&sort=rating_dsc&name=동작구민회관&name=녹번동근린공원&name=올림픽공원&category=food-korean`)
```
※name 파라미터는 여러 개 가능합니다.

#### Response 예시 2 (중간 지점)
```json
{
  "start": [
    {
      "name": "동작구민회관",
      "address": "서울 동작구 보라매로5길 28",
      "latitude": "37.4938972382326",
      "longitude": "126.922743463895"
    },
    {
      "name": "녹번동근린공원",
      "address": "",
      "latitude": "37.60353994592752",
      "longitude": "126.93185185285346"
    },
    {
      "name": "올림픽공원",
      "address": "서울 송파구 올림픽로 424",
      "latitude": "37.5205340628851",
      "longitude": "127.120812783275"
    }
  ],
  "middlePoint": {
    "address": "서울 용산구 이태원동 212-27",
    "latitude": "126.99180270000781",
    "longitude": "37.53932374901508"
  },
  "list": [
    {
      "address": "서울특별시 중구 명동8나길 28 (충무로1가) ",
      "contentId": "1489369",
      "category": "food-korean",
      "thumbnail": "http://tong.visitkorea.or.kr/cms/resource/38/3474938_image2_1.jpg",
      "latitude": "37.5614854780",
      "longitude": "126.9834734887",
      "name": "오다리집",
      "rating": "4.7",
      "userRatingsTotal": "3942"
    },
    {
      "address": "서울특별시 중구 세종대로 76 ",
      "contentId": "398344",
      "category": "food-korean",
      "thumbnail": "http://tong.visitkorea.or.kr/cms/resource/75/1290675_image2_1.jpg",
      "latitude": "37.5629101933",
      "longitude": "126.9768490516",
      "name": "현대칼국수",
      "rating": "4.4",
      "userRatingsTotal": "337"
    }
  ]
}
```

#### Request 예시 3 (위치)
```javascript
axios.get(`${API_BASE_URL}/map?search=location&sort=title_asc&latitude=37.5745839959&longitude=126.9857145803&category=food`)
```

#### Response 예시 3 (위치)
```json
{
  "current": {
    "address": "서울특별시 종로구 인사동10길 11-4",
    "latitude": "37.5745839959",
    "longitude": "126.9857145803"
  },
  "list": [
    {
      "address": "서울특별시 중구 남대문로 52-5 (명동2가) ",
      "contentId": "134746",
      "category": "food-chinese",
      "thumbnail": "http://tong.visitkorea.or.kr/cms/resource/96/3474896_image2_1.jpg",
      "latitude": "37.5621214856",
      "longitude": "126.9818402861",
      "name": "개화",
      "rating": "3.9",
      "userRatingsTotal": "867"
    },
    {
      "address": "서울특별시 중구 무교로 24 (무교동) 2층",
      "contentId": "133276",
      "category": "food-korean",
      "thumbnail": "http://tong.visitkorea.or.kr/cms/resource/18/3474918_image2_1.jpg",
      "latitude": "37.5681540761",
      "longitude": "126.9794958849",
      "name": "곰국시집",
      "rating": "4.1",
      "userRatingsTotal": "849"
    }
  ]
}
```

---

### 장소 세부 정보 출력

#### Request 예시 1 (TourAPI로 검색된 장소)
```javascript
axios.get(`${API_BASE_URL}/map/detail?contentId=2559938`)
```

#### Response 예시 1 (TourAPI로 검색된 장소)
```json
{
  "address": "서울특별시 강남구 봉은사로 524 (삼성동) 지하1층",
  "contentId": "2559938",
  "thumbnails": [
    "http://tong.visitkorea.or.kr/cms/resource/26/2559926_image2_1.jpg"
  ],
  "latitude": "37.5129952125",
  "longitude": "127.0571487082",
  "name": "브이알존 코엑스 직영점",
  "rating": "4.0",
  "userRatingsTotal": "4",
  "phoneNumber": "",
  "url": "",
  "reviews": [
    {
      "author": "Brandon Y",
      "language": "ko",
      "rating": "5",
      "relativeTimeDescription": "2년 전",
      "text": "직원분들이 너무 친절해서 좋았어요. 아이도 재밌어합니다~",
      "time": "2022-05-28T18:58:27"
    },
    {
      "author": "Teguh FM",
      "language": "en",
      "rating": "5",
      "relativeTimeDescription": "5달 전",
      "text": "맛있는 비건 음식. 분위기도 좋고 서비스도 아주 좋았어요.",
      "time": "2024-12-24T15:54:33"
    },
    {
      "author": "Neal Lee",
      "language": "en",
      "rating": "1",
      "relativeTimeDescription": "7달 전",
      "text": "이 장소는 작고 그다지 흥미롭지 않습니다.  장비가 오래되었습니다.  방문하지 않고 돈을 낭비하지 않았으면 좋았을 텐데요.",
      "time": "2024-10-21T16:37:46"
    },
    {
      "author": "ゴリラーマン金太郎",
      "language": "ja",
      "rating": "5",
      "relativeTimeDescription": "6달 전",
      "text": "크리스마스 트리와 도서관 최고로 아름다운",
      "time": "2024-11-21T17:06:16"
    }
  ]
}
```

#### Request 예시 2 (구글로 검색된 장소)
```javascript
axios.get(`${API_BASE_URL}/map/detail?contentId=google_ChIJh18aOsSifDURnxMisYWvnUM`)
```

#### Response 예시 2 (구글로 검색된 장소)
```json
{
  "address": "종로구 화동 북촌로5가길 24",
  "contentId": "google_ChIJh18aOsSifDURnxMisYWvnUM",
  "thumbnails": [
    "https://lh3.googleusercontent.com/place-photos/AJnk2cyTwVpDznD82TegtGdVvDgJaKTdoVcKJZDZ9UTaVkFKl6LlhBGUCkr0_43srlhmDnMaopkpsyBu5d-mICTobawgA09piS_8oIOyBihjle90qN2lpeJ9cwb0W4Cg3SaMQbSuyVrKT0Pr3ZaVL8I=s1600-w800",
    "https://lh3.googleusercontent.com/place-photos/AJnk2czTJ7wh19HwaVDMPsxbrgsXjz3LuD91QWExgS41eadzZ886g7gkhl_nYGaXh7AKDFOCXBXo_bKubt1yTEqvUGlpEQ5SJj3oP3yxEB_punTBRm23FE-Xpa8F5E9anSEpm3L-qvEVwLThFX3HCA=s1600-w800",
    "https://lh3.googleusercontent.com/place-photos/AJnk2cy31G4a3FNqtaLRJs0kOrXBztAlrWq3BzPtOMlOwe2hrW3w25XT8j6KyB9Ems83-0eIx0cs_3Uti9LuhWw2I5zEUuB00-TE5ceEZoChX5j0F5cfrttPPCrcaphg0oskJsOuuL_teplJulfG=s1600-w800",
    "https://lh3.googleusercontent.com/place-photos/AJnk2cza-aTmvrp4NNvQGorfcbDY0sobeiK2K4EtaVuaG9T7wHUHLzobUkELKDvvjb6Y8dDFAVzYuwp7Uv0Ej46tQKpP18Lr4_pWkYhyHSMDjcHFm9GUQYRSjwqaKcz2jkoWAFbze31q9k0xmkiNY5E=s1600-w800",
    "https://lh3.googleusercontent.com/place-photos/AJnk2cxbUL57GZshQNyD004BMzneaMoMvWohdciqXg4fRUMKiygbcq-RlZxOYEoP4bZtBnAnXvADvcaOvLdDbhiB_s2HCe8Rir_ZW70Y8wd1G3vdyXv2EYs2YC6LwSV8YLNAbzAtHyBpjuSgajm62A=s1600-w800",
    "https://lh3.googleusercontent.com/place-photos/AJnk2czAky9mWlIEee2TMdqkWn_7cJ9-eEBtHLjlkuyE3GPTlBnYWuv2hemqCi1fbBB46wR0ENpwWG4rwsdq9Rfz8YGxBKlHwbQqDNzxYypdGjW2JkYGEmhXlFAE0uInkwLgh4FmwToTqouK-_4RWsM=s1600-w800",
    "https://lh3.googleusercontent.com/place-photos/AJnk2czH1ugZgh_pezCjhgFuajPlPnexcAozjjgWVcrkwu9lkgdBFf2WdC6chyChFqUYi1oQbOFz2F2t627s0Ky2oRUsUSf6eXZqWHpcwQgHr11chVgxjTB46Lj2vg3HYrguAxq9wpbYCIwLZeQv=s1600-w800",
    "https://lh3.googleusercontent.com/place-photos/AJnk2cyr-UYGiaCBVlPPIGv8_qKcfjpkyA7AOFESSoX-jg9TnYsatBkQaOdkxJTnhwqrXJInGH_nqRU08IGIm3X5TGsLplIOAm0kGcXmHtRNH8hRTN-508VnJyhuKmtb5RFDrfAfLK_SzpvKC61hxg=s1600-w800",
    "https://lh3.googleusercontent.com/place-photos/AJnk2cwL3Pb2UjhnW6xhOKZ2wEswt4WyO6BQXepGXtoPMSv7hze3dGSdVvDfVOEarWc3Yz-ENk9814nxkQbVWa1j8V9PERkpm4WHwFLB1Ho25jZNQ3Kd_f4ROhhYFRPNO6V3XteOb1cmrBNYZemp_w=s1600-w800",
    "https://lh3.googleusercontent.com/place-photos/AJnk2cyioKj6GYOSl4DBbdycAcJN5V9nRxcTfM0RRVnjVnhzO9vzPAoSHZ7XgOKBBUHPOkZ2nQvNyzBDXy7iUCzZriu6fFCP8wcAbrFHqHvHZFjvDHe2bIsBOOmT2p7loYliQEOKu4hqydIHqh0-MA=s1600-w800"
  ],
  "latitude": "37.5805437",
  "longitude": "126.9819587",
  "name": "엔젤524",
  "rating": "3.8",
  "userRatingsTotal": "32",
  "phoneNumber": "02-720-3359",
  "url": null,
  "reviews": [
    {
      "author": "황혜영",
      "language": "ko",
      "rating": "5",
      "relativeTimeDescription": "7년 전",
      "text": "추억이 가득한 장소",
      "time": "2017-05-28T17:47:30"
    },
    {
      "author": "백승환",
      "language": "ko",
      "rating": "5",
      "relativeTimeDescription": "8년 전",
      "text": "깨끗한 인테리어",
      "time": "2017-01-08T12:57:46"
    },
    {
      "author": "클라이밍 암벽등반 영상 사진 채널",
      "language": "ko",
      "rating": "4",
      "relativeTimeDescription": "8년 전",
      "text": "식당 많은 곳",
      "time": "2016-12-09T12:40:59"
    },
    {
      "author": "Jow S",
      "language": "ko",
      "rating": "3",
      "relativeTimeDescription": "8년 전",
      "text": "굳~~**",
      "time": "2016-10-05T22:48:20"
    },
    {
      "author": "Michael Hsu",
      "language": "zh-Hant",
      "rating": "3",
      "relativeTimeDescription": "8년 전",
      "text": "쇼핑하기 좋은 거리는 매우 짧고 좁고, 차들이 항상 지나가기 때문에 마음 편히 쇼핑을 할 수 없습니다.",
      "time": "2016-09-18T18:56:30"
    }
  ]
}
```

---

### 장소명 자동완성

#### Request 예시 1
```javascript
axios.get(`${API_BASE_URL}/map/autocomplete?name=한성대`)
```

#### Response 예시 1
```json
[
  {
    "id": "11272875",
    "placeName": "한성대학교",
    "address": "서울 성북구 삼선교로16길 116"
  },
  {
    "id": "21160826",
    "placeName": "한성대입구역 4호선",
    "address": "서울 성북구 삼선교로 지하 1"
  },
  {
    "id": "423761522",
    "placeName": "한성대학교 상상관",
    "address": "서울 성북구 삼선교로16길 116"
  },
  {
    "id": "145744318",
    "placeName": "다이소 한성대입구역2호점",
    "address": "서울 성북구 동소문로 14"
  },
  {
    "id": "17567225",
    "placeName": "한성대학교 낙산관",
    "address": "서울 성북구 삼선교로16길 118"
  },
  {
    "id": "1852838507",
    "placeName": "한성대양꼬치",
    "address": "서울 성북구 동소문로6길 14-31"
  },
  {
    "id": "655977017",
    "placeName": "한성대학교 주차장",
    "address": "서울 성북구 삼선교로16길 116"
  },
  {
    "id": "1369203836",
    "placeName": "조선부뚜막 한성대점",
    "address": "서울 성북구 동소문로6길 4-23"
  },
  {
    "id": "17563225",
    "placeName": "한성대학교 미래관",
    "address": "서울 성북구 삼선교로16길 116"
  },
  {
    "id": "1650877136",
    "placeName": "올리브영 한성대입구역점",
    "address": "서울 성북구 삼선교로 12"
  }
]
```