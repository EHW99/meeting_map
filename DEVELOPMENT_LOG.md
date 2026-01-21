# MeetingMap 개발 기록

> 이 문서는 프로젝트 개발 및 리팩토링 과정을 기록합니다.

---

## 2026-01-21 작업 내역

### 1. 프론트엔드 전체 UI 리디자인

#### 변경 사항
- **디자인 시스템 구축**
  - `frontend/src/styles/design-tokens.css`: CSS 변수 (색상, 타이포그래피, 간격 등)
  - `frontend/src/styles/base.css`: Pretendard 폰트 적용, 기본 스타일 리셋
  - Primary 컬러: Amber (#F59E0B)

- **페이지별 리디자인**
  | 페이지 | 주요 변경 |
  |--------|----------|
  | Header | 햄버거 메뉴 → 고정 네비게이션 바 |
  | Login/Register | 2분할 레이아웃, 모던한 폼 디자인 |
  | Map | 좌측 사이드바 + 우측 지도 레이아웃, 검색 패널 추가 |
  | Board | 카드 그리드 레이아웃, 상단 탭 메뉴 |
  | Schedule | 스텝 기반 UI, 시각적 개선 |
  | Group | 카드 기반 그룹 목록 |
  | Mypage | 프로필 카드, 탭 기반 콘텐츠 |

#### 기술적 세부사항
- 총 30개 파일 변경 (+7,355 / -3,500 lines)
- Pretendard 웹폰트 적용
- CSS Grid/Flexbox 기반 반응형 레이아웃

---

### 2. API 설정 통합 및 정리

#### 문제점
- 외부 API 키가 여러 곳에 분산되어 관리되고 있었음
- 일부 API 키가 코드에 하드코딩되어 있었음
- `.env` 파일과 `application-*.properties` 간 불일치

#### 해결
| API | 변경 전 | 변경 후 |
|-----|---------|---------|
| Google API | `application-dev.properties`에 하드코딩 | `.env`로 통합 |
| 프론트엔드 API URL | 3개 파일에 `localhost:8080` 하드코딩 | `constants.js`의 환경변수 사용 |

#### 수정된 파일
- `backend/.env`: Google API 키 추가
- `backend/src/main/resources/application-dev.properties`: 하드코딩 키 제거
- `frontend/src/pages/Register.jsx`: API_BASE_URL 사용
- `frontend/src/pages/PostWrite.jsx`: API_BASE_URL 사용
- `frontend/src/pages/PostUpdate.jsx`: API_BASE_URL 사용

---

### 3. Map 페이지 검색 기능 추가

#### 문제점
- URL 파라미터 없이 Map 페이지 직접 접근 시 출발지/도착지 입력 불가
- 자동완성 기능이 URL 인코딩 문제로 동작하지 않음

#### 해결
- 검색 패널 UI 추가 (경로 검색 / 중간지점 모드)
- 출발지/도착지 입력 필드 + 자동완성
- `encodeURIComponent()` 적용으로 한글 검색 지원

---

### 4. TMap API 연동 수정

#### 문제점
- TMap API 403 Forbidden 에러 (구독 미완료)
- 대중교통 API 별도 구독 필요

#### 해결
- SK Open API 포털에서 TMap 경로 API 구독
- SK Open API 포털에서 대중교통 길찾기 API 구독
- `.env` 파일에 새 API 키 적용

#### 현재 상태
| API | 상태 |
|-----|------|
| TMap 자동차/보행자 경로 | ✅ 정상 |
| TMap 대중교통 경로 | ✅ 정상 |

---

### 5. 보안 이슈 수정 ✅

#### 수정 완료된 이슈
| # | 이슈 | 심각도 | 상태 | 수정 내용 |
|---|------|--------|------|----------|
| 1 | Login.jsx에 테스트 비밀번호 하드코딩 | 🔴 높음 | ✅ 완료 | `predefinedUsers` 배열과 비밀번호 자동 대입 로직 제거 |
| 2 | CORS 설정 중복 | 🔴 높음 | ✅ 완료 | 프로덕션 도메인 추가, 허용 메서드 명시적 지정 |
| 3 | API 키 로그 노출 | 🟡 중간 | ✅ 완료 | 민감한 헤더/파라미터 마스킹 처리 |
| 4 | TMap API 에러 처리 미흡 | 🟡 중간 | ✅ 완료 | HTTP 상태별 에러 핸들링 + 로깅 추가 |

#### 수정된 파일
- `frontend/src/pages/Login.jsx`: 테스트 계정 하드코딩 제거
- `backend/.../config/CorsMvcConfig.java`: 프로덕션 도메인 + 메서드 명시
- `backend/.../config/SecurityConfig.java`: 허용 메서드 명시적 지정
- `backend/.../config/ExternalApiRestClientConfig.java`: API 키 마스킹 로직
- `backend/.../api/tmap/service/TMapApiService.java`: 에러 핸들링 강화

---

### 6. OpenAI API 응답 안정화 ✅

#### 문제점
- AI 응답이 예상과 다른 형식으로 오면 파싱 실패
- 쉼표 구분자만 가정하여 다양한 응답 형식 처리 불가
- AI 추천 실패 시 전체 일정 생성이 실패

#### 해결
```java
// 다중 형식 파싱 지원
private List<String> parseAIResponse(String aiResult) {
    // 1. JSON 배열: ["장소A", "장소B"]
    // 2. JSON 객체: {"places": [...]}
    // 3. 번호 목록: "1. 장소A\n2. 장소B"
    // 4. 대시 목록: "- 장소A\n- 장소B"
    // 5. 쉼표 구분: "장소A, 장소B"
    // 6. 줄바꿈 구분
}

// 평점 기반 fallback 추가
private List<PlaceResponseDto> getFallbackRecommendations(...) {
    // AI 실패 시 평점 순으로 추천
}
```

#### 수정된 파일
- `backend/.../api/openai/service/OpenAIService.java`: 다중 형식 파싱 + fallback 로직

---

### 7. 프론트엔드 에러 처리 강화 ✅

#### 문제점
- Kakao Map SDK 로딩 시 무한 폴링 (타임아웃 없음)
- API 에러 시 console.error만 출력, 사용자 피드백 없음
- 네트워크 오류 시 앱이 멈춘 것처럼 보임

#### 해결
| 개선 항목 | 구현 내용 |
|----------|----------|
| 지도 로딩 타임아웃 | 5초 후 에러 플레이스홀더 표시 |
| 로딩 오버레이 | 검색/일정생성 중 스피너 표시 |
| 에러 배너 | 상단에 에러 메시지 + 닫기 버튼 |
| 에러 유형 분류 | 타임아웃, 400, 500 등 상황별 메시지 |

#### 수정된 파일
- `frontend/src/pages/Map.jsx`: 로딩/에러 상태 + UI 컴포넌트
- `frontend/src/pages/Map.css`: 로딩 오버레이, 에러 배너 스타일
- `frontend/src/pages/Schedule.jsx`: 글로벌 에러 배너, 로딩 상태
- `frontend/src/pages/Schedule.css`: 에러/로딩 UI 스타일

---

## 코드 품질 분석 결과

### 발견된 문제점 (향후 개선 예정)

#### 백엔드
1. ~~**OpenAI API 응답 파싱 불안정**~~ - ✅ 해결됨
2. ~~**AI 추천 실패 시 fallback 없음**~~ - ✅ 해결됨
3. **Tour API 응답 파싱 복잡** - 빈 문자열/배열 혼재

#### 프론트엔드
1. ~~**Kakao Map SDK 로딩 무한 폴링**~~ - ✅ 해결됨
2. ~~**API 에러 시 사용자 피드백 없음**~~ - ✅ 해결됨
3. **자동완성 캐싱 없음** - 중복 API 호출
4. **중복 코드** - Map.jsx와 Schedule.jsx 간 경로 그리기 로직

#### 미활용 기능
- Kakao Map 카테고리 검색
- Kakao Map 로드뷰 API
- Kakao Map 클러스터링

---

## 커밋 히스토리

| 날짜 | 커밋 | 설명 |
|------|------|------|
| 2026-01-21 | `d59241e` | Redesign frontend UI and fix API configurations |
| 2026-01-21 | `72b0d53` | Enhance README for portfolio presentation |
| - | `470c89c` | Simplify README and use collapsible sections for GIFs |
| - | `03e422a` | Add project documentation and demo assets |
| - | `7a1876d` | Improve frontend security: env variables and XSS protection |
| - | `cadc65b` | Make S3 service optional for local development |

---

## 다음 작업 예정

### 완료된 작업 ✅
- [x] 보안 이슈 수정 완료
- [x] OpenAI API 응답 안정화 (다중 형식 파싱 + fallback)
- [x] 에러 처리 및 사용자 피드백 강화 (Map, Schedule)

### 중간 우선순위
- [ ] 대중교통 경로 시각화 개선
- [ ] 자동완성 캐싱 추가
- [ ] Kakao Map 추가 기능 활용

### 낮은 우선순위
- [ ] 중복 코드 리팩토링 (공통 Hook 추출)
- [ ] Swagger/OpenAPI 문서화
- [ ] 테스트 코드 작성

---

## 기술 스택

### Backend
- Java 17, Spring Boot 3.x
- Spring Security + JWT
- JPA/Hibernate + MySQL
- RestClient (외부 API 호출)

### Frontend
- React 18
- React Router v6
- Axios
- Kakao Maps SDK

### External APIs
- Kakao Maps API (지도, 검색, OAuth)
- TMap API (경로 안내)
- Google Places API (장소 정보)
- Tour API (관광 정보)
- OpenAI API (AI 추천)

### Infrastructure
- AWS S3 (파일 저장)
- AWS RDS (MySQL)

---

*이 문서는 개발 진행에 따라 지속적으로 업데이트됩니다.*
