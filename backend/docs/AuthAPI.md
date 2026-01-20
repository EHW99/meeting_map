## 📚 목차
- [Home](../README.md)
- [User API](UserAPI.md) [(Example)](UserAPIDetail.md)
- **Auth API**
- [Friendship API](FriendshipAPI.md)
- [Map API](MapAPI.md) [(Example)](MapAPIDetail.md)
- [Schedule API](ScheduleAPI.md) [(Example)](ScheduleAPIDetail.md)
- [Path API](PathAPI.md) [(Example)](PathAPIDetail.md)
- [Board API](BoardAPI.md) [(Example)](BoardAPIDetail.md)
- [Comment API](CommentAPI.md) [(Example)](CommentAPIDetail.md)
- [Group API](GroupAPI.md) [(Example)](GroupAPIDetail.md)
- [GroupBoard API](GroupBoardAPI.md) [(Example)](GroupBoardAPIDetail.md)
- [GroupComment API](GroupCommentAPI.md) [(Example)](GroupCommentAPIDetail.md)

# Auth API

## API 목록

### 로그인

**POST** `/auth/login`

# 구현 완료

**※ 로그인 이후 로그인이 필요한 api로 axios 요청시 `withCredentials: true`만 추가로 넣어주면 됩니다.**

---

### 카카오 로그인

**POST** `/auth/kakao`

# 구현 완료

**로그인 이후 로그인이 필요한 api로 axios 요청시 `withCredentials: true`만 추가로 넣어주면 됩니다.**

---

### 로그아웃

**POST** `/auth/logout`

# 구현 완료