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
- [GroupBoard API](GroupBoardAPI.md) [(Example)](GroupBoardAPIDetail.md)
- [GroupComment API](GroupCommentAPI.md) **(Example)**

# GroupComment API 예시
## API 바로가기
| API 호출                                                                                    | 설명           |
|-------------------------------------------------------------------------------------------|--------------|
| [GET /groups/{groupNo}/boards/{groupBoardNo}/comments](#특정-게시글-댓글-조회)                     | 특정 게시글 댓글 조회 |
| [POST /groups/{groupNo}/boards/{groupBoardNo}/comments](#그룹-게시글-댓글-등록)                    | 그룹 게시글 댓글 등록 |
| [PUT /groups/{groupNo}/boards/{groupBoardNo}/comments/{groupCommentNo}](#그룹-게시글-댓글-수정)    | 그룹 게시글 댓글 수정 |
| [DELETE /groups/{groupNo}/boards/{groupBoardNo}/comments/{groupCommentNo}](#그룹-게시글-댓글-삭제) | 그룹 게시글 댓글 삭제 |

---

## API 상세

### 특정 게시글 댓글 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/groups/3/boards/3/comments?page=0&size=10&sortBy=groupCommentWriteDate&direction=desc`, { withCredentials: true })
```

#### Response 예시
```json
[
    {
        "groupCommentNo": 3,
        "userId": "user2",
        "userNick": "사용자2",
        "groupCommentContent": "testcomment",
        "groupCommentWriteDate": "2025-05-24T21:44:09",
        "userImg": null,
        "groupBoardNo": 3
    }
]
```

---

### 그룹 게시글 댓글 등록

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/groups/3/boards/3/comments`, {
    "groupCommentContent": "testcomment"
}, { withCredentials: true })
```

#### Response 예시
```json
{
    "groupCommentNo": 3,
    "userId": "user2",
    "userNick": "사용자2",
    "groupCommentContent": "testcomment",
    "groupCommentWriteDate": "2025-05-24T21:44:08.9765259",
    "userImg": null,
    "groupBoardNo": 3
}
```

---

### 그룹 게시글 댓글 수정

#### Request 예시
```javascript
axios.put(`${API_BASE_URL}/groups/3/boards/3/comments/3`, {
    "groupCommentContent": "testcomment"
}, { withCredentials: true })
```

---

### 그룹 게시글 댓글 삭제

#### Request 예시
```javascript
axios.delete(`${API_BASE_URL}/groups/3/boards/3/comments/3`, { withCredentials: true })
```