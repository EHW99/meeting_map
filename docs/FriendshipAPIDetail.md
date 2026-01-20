# Friendship API
## API 바로가기
| API 호출                                     | 설명          |
|--------------------------------------------|-------------|
| [GET /user/friends](#친구-목록-조회)             | 게시글 조회      |
| [GET /user/friends/sent](#보낸-친구-요청-조회)     | 보낸 친구 요청 조회 |
| [GET /user/friends/received](#받은-친구-요청-조회) | 받은 친구 요청 조회 |
| [POST /user/friends/add](#친구-추가)           | 친구 추가       |
| [POST /user/friends/approve](#친구-요청-수락)    | 친구 요청 수락    |
| [DELETE /user/friends](#친구-삭제)             | 친구 삭제       |

---

## API 상세

### 친구 목록 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/user/friends`, { withCredentials: true })
```

#### Response 예시
```json
[
  {
    "friendshipNo": 1,
    "userId": "user1",
    "opponentId": "user2",
    "opponentNick": "사용자2",
    "status": "ACCEPTED",
    "counterpartFriendshipNo": 2,
    "from": true
  },
  {
    "friendshipNo": 3,
    "userId": "user1",
    "opponentId": "user3",
    "opponentNick": "사용자3",
    "status": "ACCEPTED",
    "counterpartFriendshipNo": 4,
    "from": true
  }
]
```

---

### 보낸 친구 요청 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/user/friends/sent`, { withCredentials: true })
```

#### Response 예시
```json
[
  {
    "friendshipNo": 7,
    "userId": "user1",
    "opponentId": "user5",
    "opponentNick": "사용자5",
    "status": "WAITING",
    "counterpartFriendshipNo": 8,
    "from": true
  }
]
```

---

### 받은 친구 요청 조회

#### Request 예시
```javascript
axios.get(`${API_BASE_URL}/user/friends/received`, { withCredentials: true })
```

#### Response 예시
```json
[
  {
    "friendshipNo": 8,
    "userId": "user5",
    "opponentId": "user1",
    "opponentNick": "사용자1",
    "status": "WAITING",
    "counterpartFriendshipNo": 7,
    "from": false
  }
]
```

---

### 친구 추가

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/user/friends/add`, {
    "opponentId": "user2",
}, { withCredentials: true })
```

---

### 친구 요청 수락
#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/user/friends/approve`, {
        "friendshipNo": 8,
    }, { withCredentials: true })
```

---

### 친구 삭제
#### Request 예시
```javascript
axios.delete(`${API_BASE_URL}/user/friends`, {
        "friendshipNo": 8,
    }, { withCredentials: true })
```