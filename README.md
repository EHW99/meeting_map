# MeetingMap

경로 기반 장소 추천 + 자동 스케줄링 서비스

## 프로젝트 소개

출발지와 도착지를 입력하면 경로를 따라 다양한 장소를 자동 추천하고, 시각적 UI를 통해 스케줄을 빠르게 구성할 수 있는 플랫폼입니다.

**배포 URL**: https://meeting-map.kro.kr

## 기술 스택

| 구분 | 기술 |
|------|------|
| Backend | Spring Boot, Java 17, JPA, MySQL |
| Frontend | React 19, JavaScript |
| API | Kakao Maps, TMap, Google Places, OpenAI, Tour API |
| Infra | AWS S3 |

## 시연

<details>
<summary>회원가입 / 로그인</summary>
<img src="asset/Animation5.gif" width="600"/>
</details>

<details>
<summary>메인페이지</summary>
<img src="asset/Animation4.gif" width="600"/>
</details>

<details>
<summary>지도 - 출발지/도착지 경로</summary>
<img src="asset/Animation1.gif" width="600"/>
</details>

<details>
<summary>지도 - 다중 입력 후 중간지점</summary>
<img src="asset/Animation2.gif" width="600"/>
</details>

<details>
<summary>스케줄 생성</summary>
<img src="asset/Animation9.gif" width="600"/>
</details>

<details>
<summary>게시판 / 글쓰기</summary>
<img src="asset/Animation10.gif" width="600"/>
</details>

<details>
<summary>마이페이지</summary>
<img src="asset/Animation11.gif" width="600"/>
</details>

<details>
<summary>그룹</summary>
<img src="asset/Animation12.gif" width="600"/>
</details>

## 주요 기능

### 메인 페이지
- 출발지/도착지 입력 시 중간지점 자동 탐색 (최대 4개)
- 오늘의 추천 게시글
- 랜덤 장소 추천

### 지도 / 카테고리
- 선택 위치 기반 장소 표시
- 카테고리별 필터링 (관광지, 음식점, 카페 등)
- 차량 / 대중교통 / 도보 경로 지원

### 스케줄 생성
- AI 기반 추천 스케줄
- 직접 장소 추가/삭제하여 일정 구성
- 지도 위 경로 및 마커 표시

### 게시판
- 카테고리별 게시판 (맛집, 카페, 놀거리, 일정/코스)
- 글쓰기, 댓글, 좋아요, 저장 기능

### 마이페이지
- 내가 쓴 글, 좋아요한 글, 저장한 글 관리
- 친구 목록 및 개인 일정 조회
- 프로필 설정

### 그룹
- 그룹 생성 및 멤버 관리
- 그룹 내 스케줄 공유
- 그룹 게시판

### 인증
- 일반 회원가입 / 로그인
- 카카오 로그인

## 로컬 실행 방법

### Backend
```bash
cd backend
# .env.example을 참고하여 .env 파일 생성
./gradlew bootRun
```

### Frontend
```bash
cd frontend
npm install
# .env.example을 참고하여 .env 파일 생성
npm start
```

## API 문서

| API | 설명 |
|-----|------|
| [UserAPI](docs/UserAPI.md) | 회원 관련 |
| [AuthAPI](docs/AuthAPI.md) | 인증 (로그인/로그아웃) |
| [BoardAPI](docs/BoardAPI.md) | 게시판 |
| [CommentAPI](docs/CommentAPI.md) | 댓글 |
| [MapAPI](docs/MapAPI.md) | 지도/장소 |
| [PathAPI](docs/PathAPI.md) | 경로 탐색 |
| [ScheduleAPI](docs/ScheduleAPI.md) | 스케줄 |
| [GroupAPI](docs/GroupAPI.md) | 그룹 |
| [GroupBoardAPI](docs/GroupBoardAPI.md) | 그룹 게시판 |
| [FriendshipAPI](docs/FriendshipAPI.md) | 친구 |
