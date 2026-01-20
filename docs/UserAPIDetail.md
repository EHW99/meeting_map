# User API 예시
## API 바로가기
| API 호출                               | 설명        |
|--------------------------------------|-----------|
| [POST /user/check-id](#아이디-중복-검사)    | 아이디 중복 검사 |
| [POST /user/register](#회원가입)         | 회원가입      |
| [GET /user](#회원-정보-조회)               | 회원 정보 조회  |
| [GET /user/list](#전체-회원-조회)          | 전체 회원 조회  |
| [PUT /user](#회원-정보-변경)               | 회원 정보 변경  |
| [GET /user/boards](#작성한-글-조회)        | 작성한 글 조회  |
| [GET /user/boards/liked](#좋아요한-글-조회) | 좋아요한 글 조회 |
| [GET /user/groups](#속한-그룹-조회)        | 속한 그룹 조회  |
| [POST /user/password](#비밀번호-변경)      | 비밀번호 변경   |
| [DELETE /user](#회원-탈퇴)               | 회원 탈퇴     |

---

## API 목록

### 아이디 중복 검사

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/user/check-id`, {
    "userId": "exampleUser"
})
```

#### Response 예시
```json
{
  "available": true
}
```

---

### 회원가입

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/user/register`, {
  "userId": "exampleUser",
  "userEmail": "hong@example.com",
  "userPasswd": "password123",
  "userNick": "홍길동",
  "userAddress": "서울특별시 중구 세종대로 110"
})
```

#### Response 예시
```json
{
  "userId": "exampleUser",
  "userEmail": "hong@example.com",
  "userNick": "홍길동",
  "userImg": null,
  "userAddress": "서울특별시 중구 세종대로 110",
  "userType": 1
}
```

---

### 회원 정보 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/user`, { withCredentials: true })
```

#### Response 예시
```json
{
  "userId": "exampleUser",
  "userEmail": "hong@example.com",
  "userNick": "홍길동",
  "userImg": "hong.png",
  "userAddress": "서울특별시 중구 세종대로 110",
  "userType": 1,
  "onlyFriendsCanSeeActivity": false,
  "emailNotificationAgree": false,
  "pushNotificationAgree": false
}
```

---

### 전체 회원 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/user`, { withCredentials: true })
```

#### Response 예시
```json
[
  {
    "userId": "exampleUser1",
    "userEmail": "hong@example.com",
    "userNick": "홍길동",
    "userImg": "hong.png",
    "userAddress": "서울특별시 중구 세종대로 110",
    "userType": 1,
    "onlyFriendsCanSeeActivity": false,
    "emailNotificationAgree": false,
    "pushNotificationAgree": false
  },
  {
    "userId": "exampleUser2",
    "userEmail": "lee@example.com",
    "userNick": "이순신",
    "userImg": "lee.png",
    "userAddress": "서울특별시 종로구 세종대로 172",
    "userType": 0,
    "onlyFriendsCanSeeActivity": false,
    "emailNotificationAgree": false,
    "pushNotificationAgree": false
  }
]
```

---

### 회원 정보 변경

#### Request 예시 1 (이메일, 닉네임, 주소 변경)
```javascript
const userData = {
    "userEmail": "hong1@example.com",
    "userNick": "홍길동",
    "userAddress": "서울특별시 용산구 남산공원길 105"
};

const formData = new FormData();

// JSON 데이터를 Blob으로 만들어서 추가
formData.append(
    "user",
    new Blob([JSON.stringify(jsonData)], { type: "application/json" })
);

axios.put(`${API_BASE_URL}/user`, formData, {
    headers: {
        "Content-Type": "multipart/form-data",
    },
    withCredentials: true
})
```

#### Request 예시 2 (프로필 사진 변경)
```javascript
import React, { useState } from "react";

const [file, setFile] = useState();

const formData = new FormData();

// 파일이 있으면 formData에 추가
if (file) {
    formData.append("profileImage", file);
}

axios.put(`${API_BASE_URL}/user`, formData, {
    headers: {
        "Content-Type": "multipart/form-data",
    },
    withCredentials: true
})
```

#### Request 예시 3 (각종 설정 변경)
```javascript
const userData = {
    "emailNotificationAgree": true
};

const formData = new FormData();

axios.put(`${API_BASE_URL}/user`, formData, {
    headers: {
        "Content-Type": "multipart/form-data",
    },
    withCredentials: true
})
```

---

### 작성한 글 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/user/boards`, { withCredentials: true })
```

#### Response 예시
```json
[
    {
        "boardNo": 16,
        "userId": "user1",
        "userNick": "사용자1",
        "userType": 1,
        "userTypeName": "User",
        "boardTitle": "자유게시판 글 1",
        "boardDescription": null,
        "boardViewCount": 50,
        "boardWriteDate": "2025-03-04T00:00:00",
        "boardUpdateDate": "2025-03-04T00:00:00",
        "boardLike": 0,
        "boardHate": 0,
        "categoryNo": 2,
        "categoryName": "자유",
        "commentCount": 0,
        "userImg": "https://capstone-meetingmap.s3.eu-north-1.amazonaws.com/8c0405c9-6369-4dec-ae70-4e197217fbb4_ai-generated-9510467_640.jpg",
        "thumbnailUrl": null
    },
    {
        "boardNo": 6,
        "userId": "user1",
        "userNick": "사용자1",
        "userType": 1,
        "userTypeName": "User",
        "boardTitle": "Q&A 질문 1",
        "boardDescription": null,
        "boardViewCount": 50,
        "boardWriteDate": "2025-03-04T00:00:00",
        "boardUpdateDate": "2025-03-04T00:00:00",
        "boardLike": 0,
        "boardHate": 0,
        "categoryNo": 1,
        "categoryName": "Q&A",
        "commentCount": 0,
        "userImg": "https://capstone-meetingmap.s3.eu-north-1.amazonaws.com/8c0405c9-6369-4dec-ae70-4e197217fbb4_ai-generated-9510467_640.jpg", 
        "thumbnailUrl": null
    }
]
```

---

### 좋아요한 글 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/user/boards/liked`, { withCredentials: true })
```

#### Response 예시
```json
[
    {
        "boardNo": 37,
        "userId": "user1",
        "userNick": "사용자1",
        "userType": 1,
        "userTypeName": "User",
        "boardTitle": "test글",
        "boardDescription": "test글입니다",
        "boardViewCount": 3,
        "boardWriteDate": "2025-05-20T23:21:49",
        "boardUpdateDate": "2025-05-21T17:43:30",
        "boardLike": 1,
        "boardHate": 0,
        "categoryNo": 1,
        "categoryName": "Q&A",
        "commentCount": 2,
        "userImg": "https://capstone-meetingmap.s3.eu-north-1.amazonaws.com/8c0405c9-6369-4dec-ae70-4e197217fbb4_ai-generated-9510467_640.jpg", 
        "thumbnailUrl": "https://capstone-meetingmap.s3.eu-north-1.amazonaws.com/8cfed205-448c-49a0-baf2-486a0bc008ae_Seoul_Forest_서울숲.jpg"
    }
]
```

---

### 저장한 글 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/user/boards/scraped`, { withCredentials: true })
```

#### Response 예시
```json
[
  {
    "scrapNo": 1,
    "boardNo": 37,
    "userId": "user1",
    "userNick": "사용자1",
    "userType": 1,
    "userTypeName": "User",
    "boardTitle": "test글",
    "boardDescription": "test글입니다",
    "boardViewCount": 3,
    "boardWriteDate": "2025-05-20T23:21:49",
    "boardUpdateDate": "2025-05-21T17:43:30",
    "boardLike": 1,
    "boardHate": 0,
    "categoryNo": 1,
    "categoryName": "Q&A",
    "commentCount": 2,
    "userImg": "https://capstone-meetingmap.s3.eu-north-1.amazonaws.com/8c0405c9-6369-4dec-ae70-4e197217fbb4_ai-generated-9510467_640.jpg",
    "thumbnailUrl": "https://capstone-meetingmap.s3.eu-north-1.amazonaws.com/8cfed205-448c-49a0-baf2-486a0bc008ae_Seoul_Forest_서울숲.jpg"
  }
]
```

---

### 속한 그룹 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/user/groups`, { withCredentials: true })
```

#### Response 예시
```json
[
    {
        "groupNo": 2,
        "groupTitle": "헬스 커뮤니티",
        "groupDescription": "헬스를 사랑하는 자들의 모임",
        "groupCreateDate": "2025-05-24T01:02:54",
        "groupCreatedUserId": "user1"
    }
]
```

---

### 비밀번호 변경

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/user/password`, {
    "userPasswd": "12345678"
}, { withCredentials: true })
```

---

### 회원 탈퇴

#### Request 예시
```javascript
axios.delete(`${API_BASE_URL}/user`, {
    "userPasswd": "12345678"
}, { withCredentials: true })
```