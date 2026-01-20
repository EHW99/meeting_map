## 📚 목차
- [Home](../README.md)
- [User API](UserAPI.md) [(Example)](UserAPIDetail.md)
- [Auth API](AuthAPI.md)
- [Friendship API](FriendshipAPI.md)
- [Map API](MapAPI.md) [(Example)](MapAPIDetail.md)
- [Schedule API](ScheduleAPI.md) [(Example)](ScheduleAPIDetail.md)
- [Path API](PathAPI.md) [(Example)](PathAPIDetail.md)
- [Board API](BoardAPI.md) [(Example)](BoardAPIDetail.md)
- [Comment API](CommentAPI.md) [(Example)](CommentAPIDetail.md)
- [Group API](GroupAPI.md) [(Example)](GroupAPIDetail.md)
- [GroupBoard API](GroupBoardAPI.md) **(Example)**
- [GroupComment API](GroupCommentAPI.md) [(Example)](GroupCommentAPIDetail.md)

# GroupBoard API 예시
## API 바로가기
| API 호출                                                       | 설명           |
|--------------------------------------------------------------|--------------|
| [GET /groups/{groupNo}/boards](#그룹-전체-게시글-조회)                | 그룹 전체 게시글 조회 |
| [GET /groups/{groupNo}/boards/{groupBoardNo}](#그룹-게시글-상세-조회) | 그룹 게시글 상세 조회 |
| [POST /groups/{groupNo}/boards](#그룹-게시글-등록)                  | 그룹 게시글 등록    |
| [PUT /groups/{groupNo}/boards/{groupBoardNo}](#그룹-게시글-수정)    | 그룹 게시글 수정    |
| [DELETE /groups/{groupNo}/boards/{groupBoardNo}](#그룹-게시글-삭제) | 그룹 게시글 삭제    |

---

## API 상세

### 그룹 전체 게시글 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/groups/3/boards`, { withCredentials: true })
```

#### Response 예시
```json
{
    "content": [
        {
            "groupBoardNo": 3,
            "groupBoardTitle": "groupboardtest",
            "userId": "user2",
            "userNick": "사용자2",
            "groupNo": 3,
            "groupTitle": "testgroup",
            "groupBoardWriteDate": "2025-05-24T21:14:59",
            "groupBoardUpdateDate": "2025-05-24T21:14:59",
            "commentCount": 0
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "numberOfElements": 1,
    "empty": false
}
```

---

### 그룹 게시글 상세 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/groups/3/boards/3`, { withCredentials: true })
```

#### Response 예시
```json
{
    "groupBoardNo": 3,
    "groupBoardTitle": "groupboardtest",
    "groupBoardContent": "groupboardtestcontent",
    "userId": "user2",
    "userNick": "사용자2",
    "groupNo": 3,
    "groupTitle": "testgroup",
    "groupBoardWriteDate": "2025-05-24T21:14:59",
    "groupBoardUpdateDate": "2025-05-24T21:14:59"
}
```

---

### 그룹 게시글 등록

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/groups/3/boards`, {
    "groupBoardTitle": "groupboardtest",
    "groupBoardContent": "groupboardtestcontent"
}, { withCredentials: true })
```

---

### 그룹 게시글 수정

#### Request 예시
```javascript
axios.put(`${API_BASE_URL}/groups/3/boards/3`, {
    "groupBoardTitle": "groupboardtest",
    "groupBoardContent": "groupboardtestcontent"
}, { withCredentials: true })
```

---

### 그룹 게시글 삭제

#### Request 예시
```javascript
axios.delete(`${API_BASE_URL}/groups/3/boards/3`, { withCredentials: true })
```