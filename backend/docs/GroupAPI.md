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
- **Group API** [(Example)](GroupAPIDetail.md)
- [GroupBoard API](GroupBoardAPI.md) [(Example)](GroupBoardAPIDetail.md)
- [GroupComment API](GroupCommentAPI.md) [(Example)](GroupCommentAPIDetail.md)

# Group API
## API 바로가기
| API 호출                                                          | 설명                    |
|-----------------------------------------------------------------|-----------------------|
| [GET /groups/{groupNo}](#그룹-조회)                                 | 그룹 조회                 |
| [GET /groups/{groupNo}/members](#그룹-멤버-조회)                      | 그룹 멤버 조회              |
| [GET /groups/members](#소속되어있는-전체-그룹-멤버-조회)                      | 소속되어있는 전체 그룹 멤버 조회 ✏️ |
| [POST /groups](#그룹-생성)                                          | 그룹 생성                 |
| [PUT /groups/{groupNo}](#그룹-수정)                                 | 그룹 수정                 |
| [DELETE /groups/{groupNo}](#그룹-삭제)                              | 그룹 삭제                 |
| [DELETE /groups/{groupNo}/members/{deleteUserId}](#그룹-멤버-강제-탈퇴) | 그룹 멤버 강제 탈퇴           |
| [POST /groups/invitations](#그룹-초대)                              | 그룹 초대 ✏️              |
| [GET /groups/invitations](#그룹-초대-목록-조회)                         | 그룹 초대 목록 조회           |
| [POST /groups/invitations/{invitationNo}/{status}](#그룹-초대-수락거절) | 그룹 초대 수락/거절           |
| [POST /groups/{groupNo}/schedules](#그룹-내-스케줄-공유)                | 그룹 내 스케줄 공유           |
| [GET /groups/{groupNo}/schedules](#그룹-내-공유된-스케줄-조회)             | 그룹 내 공유된 스케줄 조회 ✏️    |
| [DELETE /groups/{groupNo}/schedules/{scheduleNo}](#공유-스케줄-삭제)   | 공유 스케줄 삭제             |

---

## API 상세

### 그룹 조회

**GET** `/groups/{groupNo}`

> 특정 그룹을 조회합니다.
> 해당 그룹의 멤버만 조회할 수 있습니다.

## [Example](GroupAPIDetail.md#그룹-조회)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명    | 값 예시 |
|---------|-------|------|
| groupNo | 그룹 번호 | 1    |

#### 응답 바디 파라미터
| 파라미터               | 설명            | 값 예시                  |
|--------------------|---------------|-----------------------|
| groupNo            | 그룹 ID         | 1                     |
| groupTitle         | 그룹명           | "헬스 커뮤니티"             |
| groupDescription   | 그룹 설명         | "헬스를 사랑하는 자들의 모임"     |
| groupCreateDate    | 그룹 생성일        | "2025-05-24T01:02:54" |
| groupCreatedUserId | 그룹을 생성한 회원 ID | "user1"               |

---

### 그룹 멤버 조회

**GET** `/groups/{groupNo}/members`

> 특정 그룹의 멤버를 조회합니다.  
> 해당 그룹의 멤버만 조회할 수 있습니다.

## [Example](GroupAPIDetail.md#그룹-멤버-조회)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명    | 값 예시 |
|---------|-------|------|
| groupNo | 그룹 번호 | 1    |

#### 응답 바디 파라미터
| 파라미터        | 설명        | 값 예시                |
|-------------|-----------|---------------------|
| userId      | 회원 ID     | "exampleUser"       |
| userEmail   | 회원 이메일    | "hong@example.com"  |
| userNick    | 회원 닉네임    | "홍길동"               |
| userImg     | 회원 프로필 사진 | "hong.png"          |
| userAddress | 회원 주소     | "서울특별시 중구 세종대로 110" |
| userType    | 회원 유형     | 1(일반 사용자)           |

---

### 소속되어있는 전체 그룹 멤버 조회

**GET** `/groups/members`

> 회원이 소속되어있는 전체 그룹의 멤버를 조회합니다.

## [Example](GroupAPIDetail.md#소속되어있는-전체-그룹-멤버-조회)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 응답 바디 파라미터
| 파라미터        | 설명        | 값 예시                |
|-------------|-----------|---------------------|
| userId      | 회원 ID     | "exampleUser"       |
| userEmail   | 회원 이메일    | "hong@example.com"  |
| userNick    | 회원 닉네임    | "홍길동"               |
| userImg     | 회원 프로필 사진 | "hong.png"          |
| userAddress | 회원 주소     | "서울특별시 중구 세종대로 110" |
| userType    | 회원 유형     | 1(일반 사용자)           |
| groupNo     | 그룹 번호     | 1                   |
| groupTitle  | 그룹명       | "헬스 커뮤니티"           |

---

### 그룹 생성

**POST** `/groups`
> 그룹을 생성합니다.

## [Example](GroupAPIDetail.md#그룹-생성)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 바디 파라미터
| 파라미터             | 설명    | 값 예시              |
|------------------|-------|-------------------|
| groupTitle       | 그룹명   | "헬스 커뮤니티"         |
| groupDescription | 그룹 설명 | "헬스를 사랑하는 자들의 모임" |

#### 응답 헤더
| 헤더       | 설명         | 값 예시          |
|----------|------------|---------------|
| Location | 그룹이 생성된 주소 | /api/groups/1 |

---

### 그룹 수정

**PUT** `/groups/{groupNo}`
> 그룹을 수정합니다.  
> 그룹 생성자만 수정할 수 있습니다.

## [Example](GroupAPIDetail.md#그룹-수정)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명    | 값 예시 |
|---------|-------|------|
| groupNo | 그룹 번호 | 1    |

#### 요청 바디 파라미터
| 파라미터             | 설명    | 값 예시              |
|------------------|-------|-------------------|
| groupTitle       | 그룹명   | "헬스 커뮤니티"         |
| groupDescription | 그룹 설명 | "헬스를 사랑하는 자들의 모임" |

---

### 그룹 삭제

**DELETE** `/groups/{groupNo}`
> 그룹을 삭제합니다.  
> 그룹 생성자만 삭제할 수 있습니다.  
> 삭제하면 그룹 멤버, 초대, 게시판, 댓글 정보까지 같이 삭제됩니다.

## [Example](GroupAPIDetail.md#그룹-삭제)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명    | 값 예시 |
|---------|-------|------|
| groupNo | 그룹 번호 | 1    |

---

### 그룹 멤버 강제 탈퇴

**DELETE** `/groups/{groupNo}/members/{deleteUserId}`
> 그룹의 멤버를 강제로 탈퇴시킵니다.  
> 그룹 생성자만 탈퇴시킬 수 있습니다.  

## [Example](GroupAPIDetail.md#그룹-멤버-강제-탈퇴)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터         | 설명         | 값 예시    |
|--------------|------------|---------|
| groupNo      | 그룹 번호      | 1       |
| deleteUserId | 탈퇴시킬 회원 ID | "user2" |

---

### 그룹 초대

**POST** `/groups/invitations`
> 특정 회원을 그룹에 초대합니다.

## [Example](GroupAPIDetail.md#그룹-초대)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 바디 파라미터
| 파라미터       | 설명     | 값 예시      |
|------------|--------|-----------|
| userNick   | 회원 닉네임 | "회원2"     |
| userEmail  | 회원 이메일 | "회원 이메일"  |
| groupTitle | 그룹명    | "헬스 커뮤니티" |

---

### 그룹 초대 목록 조회

**GET** `/groups/invitations`
> 그룹에 초대된 정보를 조회합니다.

## [Example](GroupAPIDetail.md#그룹-초대-목록-조회)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 응답 바디 파라미터
| 파라미터         | 설명            | 값 예시                              |
|--------------|---------------|-----------------------------------|
| invitationNo | 초대 번호         | 2                                 |
| groupNo      | 그룹 번호         | 1                                 |
| groupTitle   | 그룹명           | 헬스 커뮤니티                           |
| senderId     | 초대를 보낸 회원 ID  | "user1"                           |
| senderNick   | 초대를 보낸 회원 닉네임 | "사용자1"                            |
| receiverId   | 초대를 받은 회원 ID  | "user2"                           |
| status       | 상태            | "WAITING", "ACCEPTED", "REJECTED" |
| invitedDate  | 초대를 보낸 시간     | "2025-05-24T19:40:27"             |

---

### 그룹 초대 수락/거절

**POST** `/groups/invitations/{invitationNo}/{status}`
> 그룹 초대를 수락하거나 거절합니다.

## [Example](GroupAPIDetail.md#그룹-초대-수락거절)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터         | 설명    | 값 예시               |
|--------------|-------|--------------------|
| invitationNo | 초대 번호 | 2                  |
| status       | 상태    | "accept", "reject" |

---

### 그룹 내 스케줄 공유

**POST** `/groups/{groupNo}/schedules`
> 그룹에 회원 본인이 만든 스케줄을 공유합니다.  
> 그룹 내 스케줄은 최대 4개까지 공유 가능합니다.

## [Example](GroupAPIDetail.md#그룹-내-스케줄-공유)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명    | 값 예시               |
|---------|-------|--------------------|
| groupNo | 그룹 번호 | 2                  |

#### 요청 바디 파라미터
| 파라미터       | 설명     | 값 예시 |
|------------|--------|------|
| scheduleNo | 스케줄 번호 | 7    |

---

### 그룹 내 공유된 스케줄 조회

**GET** `/groups/{groupNo}/schedules`
> 그룹에 공유된 스케줄들을 조회합니다.  
> 스케줄의 상세 정보까지 한번에 조회합니다.  
> 그룹의 모든 멤버가 조회 가능합니다.

## [Example](GroupAPIDetail.md#그룹-내-공유된-스케줄-조회)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명    | 값 예시               |
|---------|-------|--------------------|
| groupNo | 그룹 번호 | 2                  |

#### 응답 바디 파라미터
| 파라미터                | 설명         | 값 예시                   |
|---------------------|------------|------------------------|
| scheduleNo          | 스케줄 번호     | 1                      |
| scheduleName        | 스케줄 이름     | "스터디 회의"               |
| scheduleAbout       | 스케줄 설명     | "다음 주 프로젝트 스터디 회의 진행"  |
| scheduleCreatedDate | 스케줄 생성 시간  | "2025-03-04T10:00:00"  |
| userId              | 스케줄 생성자 ID | "user1"                |
| details             | 스케줄 상세     | {scheduleDetailNo,...} |
| scheduleDetailNo    | 스케줄 상세 번호  | 1                      |
| scheduleContent     | 스케줄 내용     | "스터디 장소 도착"            |
| scheduleAddress     | 장소 주소      | "서울특별시 마포구 와우산로 94"    |
| latitude            | 장소 위도      | 37.550900              |
| longitude           | 장소 경도      | 126.925300             |
| scheduleStartTime   | 스케줄 시작 시간  | "2025-03-04T13:00:00"  |
| scheduleEndTime     | 스케줄 종료 시간  | "2025-03-04T13:30:00"  |
| scheduleNo          | 포함된 스케줄 번호 | 1                      |

---

### 공유 스케줄 삭제

**DELETE** `/groups/{groupNo}/schedules/{scheduleNo}`
> 그룹에 공유된 스케줄을 삭제합니다.  
> 그룹장만 스케줄 삭제가 가능합니다.  
> 스케줄 연결을 끊는 개념으로 스케줄 자체가 사라지지는 않습니다.

## [Example](GroupAPIDetail.md#공유-스케줄-삭제)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터       | 설명     | 값 예시 |
|------------|--------|------|
| groupNo    | 그룹 번호  | 2    |
| scheduleNo | 스케줄 번호 | 7    |