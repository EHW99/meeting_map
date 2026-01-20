# Board API
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

**GET** `/boards`

> 게시글 전체를 조회합니다.  
> category 쿼리 파라미터로 특정 카테고리만 조회하거나 page, size 등으로 일부만 조회할 수 있습니다.  
> keyword 쿼리 파라미터로 boardTitle과 boardDescription에서 해당 키워드와 일치하는 게시글만 볼 수 있습니다.  
> 파라미터를 사용하지 않을 경우 page는 0, size는 100, sortBy는 boardWriteDate, direction은 desc이 기본값입니다.

## [Example](BoardAPIDetail.md#게시글-조회)

#### 요청 쿼리 파라미터
| 파라미터      | 필수 여부 | 설명          | 값 예시             |
|-----------|-------|-------------|------------------|
| category  | X     | 카테고리 번호     | 1                |
| keyword   | X     | 검색할 키워드     | "질문"             |
| page      | X     | 페이지 번호      | 0                |
| size      | X     | 한 번에 출력할 개수 | 10               |
| sortBy    | X     | 정렬 기준       | boardWriteDate 등 |
| direction | X     | 정렬 방향       | asc, desc        |

#### 응답 바디 파라미터
| 파라미터             | 설명                  | 값 예시                      |
|------------------|---------------------|---------------------------|
| content          | 게시글 목록              | [{boardNo,userId,...}]    |
| boardNo          | 게시글 번호              | 1                         |
| userId           | 작성한 회원 ID           | "user1"                   |
| userNick         | 작성한 회원 닉네임          | "걷는남자"                    |
| userType         | 작성한 회원 종류           | 0(관리자), 1(일반 사용자)         |
| userTypeName     | 작성한 회원 종류 이름        | Admin(관리자), User(일반 사용자)  |
| boardTitle       | 게시글 제목              | "서울숲 산책 코스 추천"            |
| boardDescription | 게시글 설명              | "조용하고 분위기 있어서 좋았어요."      |
| boardViewCount   | 게시글 조회수             | 1                         |
| boardWriteDate   | 게시글 작성일             | "2025-05-09T23:21:49"     |
| boardUpdateDate  | 게시글 수정일             | "2025-05-09T23:51:40"     |
| boardLike        | 게시글 좋아요 수           | 0                         |
| boardHate        | 게시글 싫어요 수           | 0                         |
| categoryNo       | 게시글 카테고리 번호         | 1                         |
| categoryName     | 게시글 카테고리 이름         | "자유게시판"                   |
| commentCount     | 게시글 댓글 수            | 0                         |
| userImg          | 회원 프로필 사진 주소        | null                      |
| thumbnailUrl     | 썸네일 주소              | null                      |
| pageable         | 페이징                 | {pageNumber,pageSize,...} |
| pageNumber       | 페이지 번호              | 0                         |
| pageSize         | 한 번에 출력할 개수         | 10                        |
| sort             | 정렬 정보               | {empty,sorted,unsorted}   |
| empty            | 정렬 기준이 빈 값인지        | false                     |
| sorted           | 정렬이 적용되었는지          | true                      |
| unsorted         | 정렬이 미적용되었는지         | false                     |
| offset           | 전체 페이지 인덱스          | 0                         |
| paged            | 페이징이 적용되었는지         | true                      |
| unpaged          | 페이징이 미적용되었는지        | false                     |
| last             | 마지막 페이지인지           | false                     |
| totalElements    | 전체 데이터 수            | 14                        |
| totalPages       | 전체 페이지 수            | 2                         |
| first            | 처음 페이지인지            | true                      |
| numberOfElements | 현재 페이지에 들어 있는 데이터 수 | 10                        |
| size             | 요청한 페이지 크기          | 10                        |
| number           | 현재 페이지 번호           | 0                         |
| empty            | 현재 페이지가 비어있는지       | false                     |

---

### 카테고리 조회

**GET** `/boards/category`

> 카테고리 목록을 조회합니다.

## [Example](BoardAPIDetail.md#카테고리-조회)

#### 응답 바디 파라미터
| 파라미터         | 설명      | 값 예시    |
|--------------|---------|---------|
| categoryNo   | 카테고리 번호 | 1       |
| categoryName | 카테고리 이름 | "자유게시판" |

---

### 게시글 상세 조회

**GET** `/boards/{boardNo}`

> 특정 게시글의 상세 정보를 조회합니다.  
> 댓글 정보 조회는 [Comment API](CommentAPI.md)에서 할 수 있습니다.

## [Example](BoardAPIDetail.md#게시글-상세-조회)

#### 요청 경로 파라미터
| 파라미터    | 설명     | 값 예시 |
|---------|--------|------|
| boardNo | 게시글 번호 | 37   |

#### 응답 바디 파라미터
| 파라미터                | 설명           | 값 예시                                                                                     |
|---------------------|--------------|------------------------------------------------------------------------------------------|
| boardNo             | 게시글 번호       | 1                                                                                        |
| userId              | 작성한 회원 ID    | "user1"                                                                                  |
| userNick            | 작성한 회원 닉네임   | "걷는남자"                                                                                   |
| userType            | 작성한 회원 종류    | 0(관리자), 1(일반 사용자)                                                                        |
| userTypeName        | 작성한 회원 종류 이름 | Admin(관리자), User(일반 사용자)                                                                 |
| boardTitle          | 게시글 제목       | "서울숲 산책 코스 추천"                                                                           |
| boardDescription    | 게시글 설명       | "조용하고 분위기 있어서 좋았어요."                                                                     |
| boardContent        | 게시글 내용       | "데이트할 때 뭔가 특별한 걸 하고 싶지 않을 때, 서울숲 산책이 최고인 것 같아요. 조용하고 공원도 넓고, 근처에 카페랑 식당도 많아서 코스 짜기 좋아요." |
| boardViewCount      | 게시글 조회수      | 1                                                                                        |
| boardWriteDate      | 게시글 작성일      | "2025-05-09T23:21:49"                                                                    |
| boardUpdateDate     | 게시글 수정일      | "2025-05-09T23:51:40"                                                                    |
| boardLike           | 게시글 좋아요 수    | 0                                                                                        |
| boardHate           | 게시글 싫어요 수    | 0                                                                                        |
| categoryNo          | 게시글 카테고리 번호  | 1                                                                                        |
| categoryName        | 게시글 카테고리 이름  | "자유게시판"                                                                                  |
| schedule            | 스케줄          | {scheduleNo, scheduleAbout, ...}                                                         |
| scheduleNo          | 스케줄 번호       | 1                                                                                        |
| scheduleName        | 스케줄 이름       | "스터디 회의"                                                                                 |
| scheduleAbout       | 스케줄 설명       | "다음 주 프로젝트 스터디 회의 진행"                                                                    |
| scheduleCreatedDate | 스케줄 생성 시간    | "2025-03-04T10:00:00"                                                                    |
| userId              | 스케줄 생성자 ID   | "user1"                                                                                  |
| details             | 스케줄 상세       | {scheduleDetailNo,...}                                                                   |
| scheduleDetailNo    | 스케줄 상세 번호    | 1                                                                                        |
| scheduleContent     | 스케줄 내용       | "스터디 장소 도착"                                                                              |
| scheduleAddress     | 장소 주소        | "서울특별시 마포구 와우산로 94"                                                                      |
| latitude            | 장소 위도        | 37.550900                                                                                |
| longitude           | 장소 경도        | 126.925300                                                                               |
| scheduleStartTime   | 스케줄 시작 시간    | "2025-03-04T13:00:00"                                                                    |
| scheduleEndTime     | 스케줄 종료 시간    | "2025-03-04T13:30:00"                                                                    |
| scheduleNo          | 포함된 스케줄 번호   | 1                                                                                        |
| boardFiles          | 게시글 첨부파일들    | [{fileNo,fileName,fileUrl},...]                                                          |
| fileNo              | 파일 번호        | 1                                                                                        |
| fileName            | 파일명          | "Seoul_Forest_서울숲.jpg"                                                                   |
| fileUrl             | 파일 주소        | "/uploads/53220240-9d46-42bb-b325-b9b0342b7f26_Seoul_Forest_서울숲.jpg"                     |

---

### 게시글 등록

**POST** `/boards`

> 게시글을 등록합니다.  
> 회원 본인이 만든 스케줄도 등록할 수 있습니다.  
> 파일은 첨부파일 형태로 받아서 등록할 수 있습니다.  
> json은 Blob 객체로 받아서 type: "application/json"을 명시해주어야 합니다.

## [Example](BoardAPIDetail.md#게시글-등록)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 바디 파라미터(**※반드시 FormData로 보낼 것**)
| 파라미터             | 설명          | 값 예시                                                                                     |
|------------------|-------------|------------------------------------------------------------------------------------------|
| json             | 요청 json     | {boardTitle,boardDescription,boardContent,categoryNo}                                    |
| boardTitle       | 게시글 제목      | "서울숲 산책 코스 추천"                                                                           |
| boardDescription | 게시글 설명      | "조용하고 분위기 있어서 좋았어요."                                                                     |
| boardContent     | 게시글 내용      | "데이트할 때 뭔가 특별한 걸 하고 싶지 않을 때, 서울숲 산책이 최고인 것 같아요. 조용하고 공원도 넓고, 근처에 카페랑 식당도 많아서 코스 짜기 좋아요." |
| categoryNo       | 게시글 카테고리 번호 | 1                                                                                        |
| scheduleNo       | 스케줄 번호      | 7                                                                                        |
| files            | 파일          | 파일                                                                                       |

#### 응답 헤더
| 헤더       | 설명          | 값 예시           |
|----------|-------------|----------------|
| Location | 게시글이 생성된 주소 | /api/boards/38 |

---

### 게시글 수정

**PUT** `/boards/{boardNo}`
> 게시글을 수정합니다.  
> 삭제할 파일이 있다면 deleteFileNos에 파일 번호를 리스트로 넣어서 보내면 됩니다.  
> files에 파일을 넣으면 새롭게 파일이 업로드되니 기존 파일을 그대로 유지하려면 files는 비어있어야 합니다.  
> json은 Blob 객체로 받아서 type: "application/json"을 명시해주어야 합니다.

## [Example](BoardAPIDetail.md#게시글-수정)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 바디 파라미터(**※boardNo를 제외한 나머지는 반드시 FormData로 보낼 것**)
| 파라미터             | 설명          | 값 예시                                                                                     |
|------------------|-------------|------------------------------------------------------------------------------------------|
| boardNo          | 게시글 번호      | 1                                                                                        |
| json             | 요청 json     | {boardNo,boardTitle,boardDescription,boardContent,categoryNo}                            |
| boardTitle       | 게시글 제목      | "서울숲 산책 코스 추천"                                                                           |
| boardDescription | 게시글 설명      | "조용하고 분위기 있어서 좋았어요."                                                                     |
| boardContent     | 게시글 내용      | "데이트할 때 뭔가 특별한 걸 하고 싶지 않을 때, 서울숲 산책이 최고인 것 같아요. 조용하고 공원도 넓고, 근처에 카페랑 식당도 많아서 코스 짜기 좋아요." |
| categoryNo       | 게시글 카테고리 번호 | 1                                                                                        |
| scheduleNo       | 스케줄 번호      | 7                                                                                        |
| files            | 파일          | 파일                                                                                       |
| deleteFileNos    | 삭제할 파일 번호들  | 1                                                                                        |

---

### 게시글 삭제

**DELETE** `/boards/{boardNo}`
> 게시글을 삭제합니다.  
> 게시글이 삭제되면 댓글과 좋아요, 싫어요, 파일까지 전부 삭제됩니다.

## [Example](BoardAPIDetail.md#게시글-삭제)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명     | 값 예시 |
|---------|--------|------|
| boardNo | 게시글 번호 | 1    |

---

### 좋아요 토글

**POST** `/boards/{boardNo}/like`
> 게시글에 좋아요를 추가합니다.  
> 1 게시글 당 좋아요 또는 싫어요 1개만 가능합니다.  
> 좋아요를 요청한 상태에서 한번 더 요청하면 좋아요가 취소됩니다.

## [Example](BoardAPIDetail.md#좋아요-토글)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명     | 값 예시 |
|---------|--------|------|
| boardNo | 게시글 번호 | 1    |

---

### 싫어요 토글

**POST** `/boards/{boardNo}/hate`
> 게시글에 싫어요를 추가합니다.  
> 1 게시글 당 좋아요 또는 싫어요 1개만 가능합니다.  
> 싫어요를 요청한 상태에서 한번 더 요청하면 싫어요가 취소됩니다.

## [Example](BoardAPIDetail.md#싫어요-토글)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명     | 값 예시 |
|---------|--------|------|
| boardNo | 게시글 번호 | 1    |

---

### 저장(스크랩) 토글

**POST** `/boards/{boardNo}/scrap`
> 게시글을 저장(스크랩)합니다.  
> 저장을 요청한 상태에서 한번 더 요청하면 저장이 취소됩니다.

## [Example](BoardAPIDetail.md#저장스크랩-토글)

#### 요청 쿠키
| 쿠키          | 설명  | 값 예시            |
|-------------|-----|-----------------|
| accessToken | jwt | eyJhbGciOiJI... |
- 로그인을 진행하면 자동으로 쿠키가 등록되어 보내집니다.

#### 요청 경로 파라미터
| 파라미터    | 설명     | 값 예시 |
|---------|--------|------|
| boardNo | 게시글 번호 | 1    |
