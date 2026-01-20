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
- **GroupBoard API** [(Example)](GroupBoardAPIDetail.md)
- [GroupComment API](GroupCommentAPI.md) [(Example)](GroupCommentAPIDetail.md)

# GroupBoard API
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

**GET** `/groups/{groupNo}/boards`

> 해당 그룹의 게시글 목록을 조회합니다.  
> 해당 그룹의 멤버만 조회할 수 있습니다.  
> page, size 등으로 일부만 조회할 수 있습니다.  
> 파라미터를 사용하지 않을 경우 page는 0, size는 10, sortBy는 groupBoardWriteDate, direction은 desc이 기본값입니다.

## [Example](GroupBoardAPIDetail.md#그룹-전체-게시글-조회)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명    | 값 예시 |
|---------|-------|------|
| groupNo | 그룹 번호 | 3    |

#### 요청 쿼리 파라미터
| 파라미터      | 필수 여부 | 설명          | 값 예시                  |
|-----------|-------|-------------|-----------------------|
| keyword   | X     | 검색할 키워드     | "질문"                  |
| page      | X     | 페이지 번호      | 0                     |
| size      | X     | 한 번에 출력할 개수 | 10                    |
| sortBy    | X     | 정렬 기준       | groupBoardWriteDate 등 |
| direction | X     | 정렬 방향       | asc, desc             |

#### 응답 바디 파라미터
| 파라미터                 | 설명                  | 값 예시                      |
|----------------------|---------------------|---------------------------|
| content              | 그룹 게시글 목록           | [{boardNo,userId,...}]    |
| groupBoardNo         | 그룹 게시글 번호           | 3                         |
| groupBoardTitle      | 그룹 게시글 제목           | "테스트 글입니다"                |
| userId               | 작성한 회원 ID           | "user1"                   |
| userNick             | 작성한 회원 닉네임          | "걷는남자"                    |
| groupNo              | 그룹 번호               | 3                         |
| groupTitle           | 그룹명                 | "헬스 커뮤니티"                 |
| groupBoardWriteDate  | 그룹 게시글 작성일          | "2025-05-24T21:14:59"     |
| groupBoardUpdateDate | 그룹 게시글 수정일          | "2025-05-24T21:14:59"     |
| commentCount         | 게시글 댓글 수            | 0                         |
| pageable             | 페이징                 | {pageNumber,pageSize,...} |
| pageNumber           | 페이지 번호              | 0                         |
| pageSize             | 한 번에 출력할 개수         | 10                        |
| sort                 | 정렬 정보               | {empty,sorted,unsorted}   |
| empty                | 정렬 기준이 빈 값인지        | false                     |
| sorted               | 정렬이 적용되었는지          | true                      |
| unsorted             | 정렬이 미적용되었는지         | false                     |
| offset               | 전체 페이지 인덱스          | 0                         |
| paged                | 페이징이 적용되었는지         | true                      |
| unpaged              | 페이징이 미적용되었는지        | false                     |
| last                 | 마지막 페이지인지           | false                     |
| totalElements        | 전체 데이터 수            | 14                        |
| totalPages           | 전체 페이지 수            | 2                         |
| first                | 처음 페이지인지            | true                      |
| numberOfElements     | 현재 페이지에 들어 있는 데이터 수 | 10                        |
| size                 | 요청한 페이지 크기          | 10                        |
| number               | 현재 페이지 번호           | 0                         |
| empty                | 현재 페이지가 비어있는지       | false                     |

---

### 그룹 게시글 상세 조회

**GET** `/groups/{groupNo}/boards/{groupBoardNo}`

> 해당 그룹의 특정 게시글 상세 내용을 조회합니다.  
> 해당 그룹의 멤버만 조회할 수 있습니다.
> 댓글 정보 조회는 [GroupComment API](GroupCommentAPI.md)에서 할 수 있습니다.

## [Example](GroupBoardAPIDetail.md#그룹-게시글-상세-조회)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터         | 설명        | 값 예시 |
|--------------|-----------|------|
| groupNo      | 그룹 번호     | 3    |
| groupBoardNo | 그룹 게시글 번호 | 3    |

#### 응답 바디 파라미터
| 파라미터                 | 설명                  | 값 예시                   |
|----------------------|---------------------|------------------------|
| content              | 그룹 게시글 목록           | [{boardNo,userId,...}] |
| groupBoardNo         | 그룹 게시글 번호           | 3                      |
| groupBoardTitle      | 그룹 게시글 제목           | "테스트 글입니다"             |
| groupBoardContent    | 그룹 게시글 내용           | "테스트 내용입니다"            |
| userId               | 작성한 회원 ID           | "user1"                |
| userNick             | 작성한 회원 닉네임          | "걷는남자"                 |
| groupNo              | 그룹 번호               | 3                      |
| groupTitle           | 그룹명                 | "헬스 커뮤니티"              |
| groupBoardWriteDate  | 그룹 게시글 작성일          | "2025-05-24T21:14:59"  |
| groupBoardUpdateDate | 그룹 게시글 수정일          | "2025-05-24T21:14:59"  |

---

### 그룹 게시글 등록

**POST** `/groups/{groupNo}/boards`

> 해당 그룹에 게시글을 작성합니다.  
> 해당 그룹의 멤버만 작성할 수 있습니다.

## [Example](GroupBoardAPIDetail.md#그룹-게시글-등록)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명    | 값 예시 |
|---------|-------|------|
| groupNo | 그룹 번호 | 3    |

#### 요청 바디 파라미터
| 파라미터              | 설명        | 값 예시        |
|-------------------|-----------|-------------|
| groupBoardTitle   | 그룹 게시글 제목 | "테스트 글입니다"  |
| groupBoardContent | 그룹 게시글 내용 | "테스트 내용입니다" |

#### 응답 헤더
| 헤더       | 설명          | 값 예시                   |
|----------|-------------|------------------------|
| Location | 게시글이 생성된 주소 | /api/groups/3/boards/3 |

---

### 그룹 게시글 수정

**PUT** `/groups/{groupNo}/boards/{groupBoardNo}`

> 해당 그룹의 게시글을 수정합니다.  
> 해당 게시글 작성자만 수정할 수 있습니다.

## [Example](GroupBoardAPIDetail.md#그룹-게시글-수정)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터         | 설명        | 값 예시 |
|--------------|-----------|------|
| groupNo      | 그룹 번호     | 3    |
| groupBoardNo | 그룹 게시글 번호 | 3    |

#### 요청 바디 파라미터
| 파라미터              | 설명        | 값 예시        |
|-------------------|-----------|-------------|
| groupBoardTitle   | 그룹 게시글 제목 | "테스트 글입니다"  |
| groupBoardContent | 그룹 게시글 내용 | "테스트 내용입니다" |

---

### 그룹 게시글 삭제

**DELETE** `/groups/{groupNo}/boards/{groupBoardNo}`

> 해당 그룹의 게시글을 삭제합니다.  
> 그룹장이나 해당 게시글 작성자만 삭제할 수 있습니다.

## [Example](GroupBoardAPIDetail.md#그룹-게시글-삭제)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터         | 설명        | 값 예시 |
|--------------|-----------|------|
| groupNo      | 그룹 번호     | 3    |
| groupBoardNo | 그룹 게시글 번호 | 3    |