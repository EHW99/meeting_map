# Group API 상세
## API 바로가기
| API 호출                                                              | 설명                 |
|---------------------------------------------------------------------|--------------------|
| [GET /groups/{groupNo}](#그룹-조회)                                     | 그룹 조회              |
| [GET /groups/{groupNo}/members](#그룹-멤버-조회)                          | 그룹 멤버 조회           |
| [POST /groups](#그룹-생성)                                              | 그룹 생성              |
| [PUT /groups/{groupNo}](#그룹-수정)                                     | 그룹 수정              |
| [DELETE /groups/{groupNo}](#그룹-삭제)                                  | 그룹 삭제              |
| [DELETE /groups/{groupNo}/members/{deleteUserId}](#그룹-멤버-강제-탈퇴)     | 그룹 멤버 강제 탈퇴        |
| [POST /groups/{groupNo}/invitations](#그룹-초대)                        | 그룹 초대              |
| [GET /groups/invitations](#그룹-초대-목록-조회)                             | 그룹 초대 목록 조회        |
| [POST /groups/invitations/{invitationNo}/{status}](#그룹-초대-수락거절)     | 그룹 초대 수락/거절        |
| [POST /groups/{groupNo}/schedules](#그룹-내-스케줄-공유)                    | 그룹 내 스케줄 공유        |
| [GET /groups/{groupNo}/schedules](#그룹-내-공유된-스케줄-조회)                 | 그룹 내 공유된 스케줄 조회    |
| [DELETE /groups/{groupNo}/schedules/{scheduleNo}](#공유-스케줄-삭제)       | 공유 스케줄 삭제          |

---

## API 상세

### 그룹 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/groups/3`, { withCredentials: true })
```

#### Response 예시
```json
{
    "groupNo": 3,
    "groupTitle": "testgroup",
    "groupDescription": "testgroupdesc",
    "groupCreateDate": "2025-05-24T19:16:10",
    "groupCreatedUserId": "user1"
}
```

---

### 그룹 멤버 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/groups/3/members`, { withCredentials: true })
```

#### Response 예시
```json
[
    {
        "userId": "user1",
        "userEmail": "user1@example.com",
        "userNick": "사용자1",
        "userImg": "https://capstone-meetingmap.s3.eu-north-1.amazonaws.com/8c0405c9-6369-4dec-ae70-4e197217fbb4_ai-generated-9510467_640.jpg",
        "userAddress": "서울 종로구 사직로 161",
        "userType": 1
    }
]
```

---

### 소속되어있는 전체 그룹 멤버 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/groups/members`, { withCredentials: true })
```

#### Response 예시
```json
[
    {
        "userId": "user1",
        "userEmail": "user1@example.com",
        "userNick": "사용자1",
        "userImg": "https://capstone-meetingmap.s3.eu-north-1.amazonaws.com/8c0405c9-6369-4dec-ae70-4e197217fbb4_ai-generated-9510467_640.jpg",
        "userAddress": "서울 종로구 사직로 161",
        "userType": 1,
        "groupNo": 2,
        "groupTitle": "testgroup"
    },
    {
        "userId": "user1",
        "userEmail": "user1@example.com",
        "userNick": "사용자1",
        "userImg": "https://capstone-meetingmap.s3.eu-north-1.amazonaws.com/8c0405c9-6369-4dec-ae70-4e197217fbb4_ai-generated-9510467_640.jpg",
        "userAddress": "서울 종로구 사직로 161",
        "userType": 1,
        "groupNo": 3,
        "groupTitle": "testgroup"
    },
    {
        "userId": "user2",
        "userEmail": "user2@example.com",
        "userNick": "사용자2",
        "userImg": null,
        "userAddress": null,
        "userType": 1,
        "groupNo": 3,
        "groupTitle": "testgroup"
    }
]
```

---

### 그룹 생성

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/groups`, {
    "groupTitle": "testgroup",
    "groupDescription": "testgroupdesc"
}, { withCredentials: true })
```

---

### 그룹 수정

#### Request 예시
```javascript
axios.put(`${API_BASE_URL}/groups/1`, {
    "groupTitle": "testgroupmodify",
    "groupDescription": "testgroupdescmodify"
}, { withCredentials: true })
```

---

### 그룹 삭제

#### Request 예시
```javascript
axios.delete(`${API_BASE_URL}/groups/1`, { withCredentials: true })
```

---

### 그룹 멤버 강제 탈퇴

#### Request 예시
```javascript
axios.delete(`${API_BASE_URL}/groups/1/members/user2`, { withCredentials: true })
```

---

### 그룹 초대

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/groups/invitations`, {
    "userNick": "회원2",
    "userEmail": "user2@example.com",
    "groupTitle": "헬스 커뮤니티"
}, { withCredentials: true })
```

---

### 그룹 초대 목록 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/groups/invitations`, { withCredentials: true })
```

#### Response 예시
```json
[
    {
        "invitationNo": 2,
        "groupNo": 3,
        "groupTitle": "testgroup",
        "senderId": "user1",
        "senderNick": "사용자1",
        "receiverId": "user2",
        "status": "WAITING",
        "invitedDate": "2025-05-24T19:40:27"
    }
]
```

---

### 그룹 초대 수락/거절

#### Request 예시 1 (초대 수락)
```javascript
axios.post(`${API_BASE_URL}/groups/invitations/2/accept`, { withCredentials: true })
```

#### Request 예시 2 (초대 거절)
```javascript
axios.post(`${API_BASE_URL}/groups/invitations/2/reject`, { withCredentials: true })
```

---

### 그룹 내 스케줄 공유

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/groups/2/schedules`, {
    "scheduleNo": 7
}, { withCredentials: true })
```

---

### 그룹 내 공유된 스케줄 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/groups/2/schedules`, { withCredentials: true })
```

#### Response 예시
```json
[
    {
        "scheduleNo": 7,
        "scheduleName": "test",
        "scheduleAbout": "testabout",
        "scheduleCreatedDate": "2025-05-10T22:25:11",
        "userId": "user1",
        "details": [
          {
            "scheduleDetailNo": 20,
            "scheduleContent": "일상비일상의틈 방문",
            "scheduleAddress": "서울특별시 강남구 강남대로 426 (역삼동) ",
            "latitude": 37.500641,
            "longitude": 127.026785,
            "scheduleStartTime": "2025-05-07T10:00:00",
            "scheduleEndTime": "2025-05-07T11:00:00",
            "scheduleNo": 7
          },
          {
            "scheduleDetailNo": 21,
            "scheduleContent": "메가박스 코엑스 방문",
            "scheduleAddress": "대한민국 서울특별시 강남구 봉은사로 524",
            "latitude": 37.512657,
            "longitude": 127.058652,
            "scheduleStartTime": "2025-05-07T11:47:00",
            "scheduleEndTime": "2025-05-07T12:47:00",
            "scheduleNo": 7
          },
          {
            "scheduleDetailNo": 22,
            "scheduleContent": "우텐더 방문",
            "scheduleAddress": "서울특별시 강남구 압구정로42길 25-10 1~2층",
            "latitude": 37.527049,
            "longitude": 127.035809,
            "scheduleStartTime": "2025-05-07T13:26:00",
            "scheduleEndTime": "2025-05-07T14:26:00",
            "scheduleNo": 7
          }
        ]
    }
]
```

---

### 공유 스케줄 삭제

#### Request 예시
```javascript
axios.delete(`${API_BASE_URL}/groups/2/schedules/7`, { withCredentials: true })
```