## 📚 목차
- [Home](../README.md)
- [User API](UserAPI.md) [(Example)](UserAPIDetail.md)
- [Auth API](AuthAPI.md)
- [Friendship API](FriendshipAPI.md)
- [Map API](MapAPI.md) [(Example)](MapAPIDetail.md)
- [Schedule API](ScheduleAPI.md) [(Example)](ScheduleAPIDetail.md)
- [Path API](PathAPI.md) [(Example)](PathAPIDetail.md)
- [Board API](BoardAPI.md) **(Example)**
- [Comment API](CommentAPI.md) [(Example)](CommentAPIDetail.md)
- [Group API](GroupAPI.md) [(Example)](GroupAPIDetail.md)
- [GroupBoard API](GroupBoardAPI.md) [(Example)](GroupBoardAPIDetail.md)
- [GroupComment API](GroupCommentAPI.md) [(Example)](GroupCommentAPIDetail.md)

# Board API 예시
## API 바로가기
| API 호출                                    | 설명         |
|-------------------------------------------|------------|
| [GET /boards](#게시글-조회)                    | 게시글 조회     |
| [GET /boards/category](#카테고리-조회)          | 카테고리 조회    |
| [GET /boards/{boardNo}](#게시글-상세-조회)       | 게시글 상세 조회  |
| [POST /boards](#게시글-등록)                   | 게시글 등록     |
| [PUT /boards/{boardNo}](#게시글-수정)          | 게시글 수정     |
| [DELETE /boards/{boardNo}](#게시글-삭제)       | 게시글 삭제     |
| [POST /boards/{boardNo}/like](#좋아요-토글)    | 좋아요 토글     |
| [POST /boards/{boardNo}/hate](#싫어요-토글)    | 싫어요 토글     |
| [POST /boards/{boardNo}/scrap](#저장스크랩-토글) | 저장(스크랩) 토글 |

---

## API 상세

### 게시글 조회

#### Request 예시 1 (전체글 조회)
```javascript
axios.get(`${API_BASE_URL}/boards`)
```

#### Response 예시 1 (전체글 조회)
```json
{
    "content": [
        {
            "boardNo": 16,
            "userId": "user5",
            "userNick": "사용자5",
            "userType": 1,
            "userTypeName": "User",
            "boardTitle": "자유게시판 글 3",
            "boardDescription": null,
            "boardViewCount": 90,
            "boardWriteDate": "2025-03-04T00:00:00",
            "boardUpdateDate": "2025-03-04T00:00:00",
            "boardLike": 0,
            "boardHate": 0,
            "categoryNo": 2,
            "categoryName": "자유",
            "commentCount": 0,
            "userImg": null,
            "thumbnailUrl": null
        },
        {
            "boardNo": 15,
            "userId": "user4",
            "userNick": "사용자4",
            "userType": 1,
            "userTypeName": "User",
            "boardTitle": "자유게시판 글 2",
            "boardDescription": null,
            "boardViewCount": 80,
            "boardWriteDate": "2025-03-04T00:00:00",
            "boardUpdateDate": "2025-03-04T00:00:00",
            "boardLike": 0,
            "boardHate": 0,
            "categoryNo": 2,
            "categoryName": "자유",
            "commentCount": 0,
            "userImg": null,
            "thumbnailUrl": null
        },
        {
            "boardNo": 14,
            "userId": "user3",
            "userNick": "사용자3",
            "userType": 1,
            "userTypeName": "User",
            "boardTitle": "자유게시판 글 1",
            "boardDescription": null,
            "boardViewCount": 70,
            "boardWriteDate": "2025-03-04T00:00:00",
            "boardUpdateDate": "2025-03-04T00:00:00",
            "boardLike": 0,
            "boardHate": 0,
            "categoryNo": 2,
            "categoryName": "자유",
            "commentCount": 0,
            "userImg": null,
            "thumbnailUrl": null
        },
        {
            "boardNo": 13,
            "userId": "user8",
            "userNick": "사용자8",
            "userType": 1,
            "userTypeName": "User",
            "boardTitle": "Q&A 질문 3",
            "boardDescription": null,
            "boardViewCount": 120,
            "boardWriteDate": "2025-03-04T00:00:00",
            "boardUpdateDate": "2025-03-04T00:00:00",
            "boardLike": 0,
            "boardHate": 0,
            "categoryNo": 1,
            "categoryName": "Q&A",
            "commentCount": 0,
            "userImg": null,
            "thumbnailUrl": null
        },
        {
            "boardNo": 12,
            "userId": "user7",
            "userNick": "사용자7",
            "userType": 1,
            "userTypeName": "User",
            "boardTitle": "Q&A 질문 2",
            "boardDescription": null,
            "boardViewCount": 110,
            "boardWriteDate": "2025-03-04T00:00:00",
            "boardUpdateDate": "2025-03-04T00:00:00",
            "boardLike": 0,
            "boardHate": 0,
            "categoryNo": 1,
            "categoryName": "Q&A",
            "commentCount": 0,
            "userImg": null,
            "thumbnailUrl": null
        },
        {
            "boardNo": 11,
            "userId": "user6",
            "userNick": "사용자6",
            "userType": 1,
            "userTypeName": "User",
            "boardTitle": "Q&A 질문 1",
            "boardDescription": null,
            "boardViewCount": 100,
            "boardWriteDate": "2025-03-04T00:00:00",
            "boardUpdateDate": "2025-03-04T00:00:00",
            "boardLike": 0,
            "boardHate": 0,
            "categoryNo": 1,
            "categoryName": "Q&A",
            "commentCount": 0,
            "userImg": null,
            "thumbnailUrl": null
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 100,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalPages": 1,
    "totalElements": 6,
    "size": 100,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "numberOfElements": 100,
    "empty": false
}
```

#### Request 예시 2 (Q&A 글 중 "질문"이 포함된 10개만 작성일 기준 내림차순 조회)
```javascript
axios.get(`${API_BASE_URL}/boards?category=1&keyword=질문&page=0&size=10&sortBy=boardWriteDate&direction=desc`)
```

#### Response 예시 2 (Q&A 글 중 "질문"이 포함된 10개만 작성일 기준 내림차순 조회)
```json
{
    "content": [
        {
            "boardNo": 13,
            "userId": "user8",
            "userNick": "사용자8",
            "userType": 1,
            "userTypeName": "User",
            "boardTitle": "Q&A 질문 3",
            "boardDescription": null,
            "boardViewCount": 120,
            "boardWriteDate": "2025-03-04T00:00:00",
            "boardUpdateDate": "2025-03-04T00:00:00",
            "boardLike": 0,
            "boardHate": 0,
            "categoryNo": 1,
            "categoryName": "Q&A",
            "commentCount": 0,
            "userImg": null,
            "thumbnailUrl": null
        },
        {
            "boardNo": 12,
            "userId": "user7",
            "userNick": "사용자7",
            "userType": 1,
            "userTypeName": "User",
            "boardTitle": "Q&A 질문 2",
            "boardDescription": null,
            "boardViewCount": 110,
            "boardWriteDate": "2025-03-04T00:00:00",
            "boardUpdateDate": "2025-03-04T00:00:00",
            "boardLike": 0,
            "boardHate": 0,
            "categoryNo": 1,
            "categoryName": "Q&A",
            "commentCount": 0,
            "userImg": null,
            "thumbnailUrl": null
        },
        {
            "boardNo": 11,
            "userId": "user6",
            "userNick": "사용자6",
            "userType": 1,
            "userTypeName": "User",
            "boardTitle": "Q&A 질문 1",
            "boardDescription": null,
            "boardViewCount": 100,
            "boardWriteDate": "2025-03-04T00:00:00",
            "boardUpdateDate": "2025-03-04T00:00:00",
            "boardLike": 0,
            "boardHate": 0,
            "categoryNo": 1,
            "categoryName": "Q&A",
            "commentCount": 0,
            "userImg": null,
            "thumbnailUrl": null
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
    "last": false,
    "totalPages": 1,
    "totalElements": 3,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "numberOfElements": 10,
    "empty": false
}
```

---

### 카테고리 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/boards/category`)
```

#### Response 예시
```json
[
  {
    "categoryNo": 0,
    "categoryName": "공지"
  },
  {
    "categoryNo": 1,
    "categoryName": "Q&A"
  },
  {
    "categoryNo": 2,
    "categoryName": "자유"
  }
]
```

---

### 게시글 상세 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/boards/1`)
```

#### Response 예시
```json
{
    "boardNo": 1,
    "userId": "admin1",
    "userNick": "관리자1",
    "userType": 0,
    "userTypeName": "Admin",
    "boardTitle": "공지사항 1",
    "boardDescription": null,
    "boardContent": "공지사항 내용 1입니다.",
    "boardViewCount": 101,
    "boardWriteDate": "2025-03-04T00:00:00",
    "boardUpdateDate": "2025-03-04T00:00:00",
    "boardLike": 0,
    "boardHate": 0,
    "categoryNo": 0,
    "categoryName": "공지",
    "boardFiles": []
}
```

---

### 게시글 등록

#### Request 예시
```javascript
import React, { useState } from "react";

const [files, setFiles] = useState([]);

const jsonData = {
    "boardTitle": "test글",
    "boardDescription": "test글입니다",
    "boardContent": "test글 본문입니다",
    "categoryNo": 1
};

const formData = new FormData();

// JSON 데이터를 Blob으로 만들어서 추가
formData.append(
    "json",
    new Blob([JSON.stringify(jsonData)], { type: "application/json" })
);

// 여러 파일 모두 formData에 추가
files.forEach((file) => {
    formData.append("files", file);
});

axios.post(`${API_BASE_URL}/boards`, formData, {
    headers: {
        "Content-Type": "multipart/form-data",
    },
    withCredentials: true
})
```

---

### 게시글 수정

#### Request 예시
```javascript
import React, { useState } from "react";

const [files, setFiles] = useState([]);
const [deleteFileNos, setDeleteFileNos] = useState([1, 3]);

const jsonData = {
    "boardTitle": "test글",
    "boardDescription": "test글입니다",
    "boardContent": "test글 본문입니다",
    "categoryNo": 1
};

const formData = new FormData();

// JSON 데이터를 Blob으로 만들어서 추가
formData.append(
    "json",
    new Blob([JSON.stringify(jsonData)], { type: "application/json" })
);

// 여러 파일 모두 formData에 추가
files.forEach((file) => {
    formData.append("files", file);
});

// deleteFileNos 배열의 각 숫자를 문자열로 formData에 여러 번 추가
deleteFileNos.forEach((num) => {
    formData.append("deleteFileNos", num.toString())
});

axios.put(`${API_BASE_URL}/boards`, formData, {
    headers: {
        "Content-Type": "multipart/form-data",
    },
    withCredentials: true
})
```

---

### 게시글 삭제

#### Request 예시
```javascript
axios.delete(`${API_BASE_URL}/boards/1`, { withCredentials: true })
```

---

### 좋아요 토글

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/boards/1/like`, { withCredentials: true })
```

---

### 싫어요 토글

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/boards/1/hate`, { withCredentials: true })
```

---

### 저장(스크랩) 토글

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/boards/1/scrap`, { withCredentials: true })
```