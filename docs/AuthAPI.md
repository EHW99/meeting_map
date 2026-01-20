# Auth API
## API 바로가기
| API 호출                       | 설명      |
|------------------------------|---------|
| [POST /auth/login](#로그인)     | 로그인     |
| [POST /auth/kakao](#카카오-로그인) | 카카오 로그인 |
| [POST /auth/logout](#로그아웃)   | 로그아웃    |

## API 목록

### 로그인

**POST** `/auth/login`

> 로그인을 진행합니다.

#### 요청 바디 파라미터
| 파라미터       | 설명      | 값 예시              |
|------------|---------|-------------------|
| userId     | 회원 ID   | "exampleUser"     |
| userPasswd | 회원 비밀번호 | "examplePassword" |

**※ 로그인 이후 로그인이 필요한 api로 axios 요청시 `withCredentials: true`만 추가로 넣어주면 됩니다.**

---

### 카카오 로그인

**POST** `/auth/kakao`

> 카카오 로그인을 진행합니다.

**로그인 이후 로그인이 필요한 api로 axios 요청시 `withCredentials: true`만 추가로 넣어주면 됩니다.**

---

### 로그아웃

**POST** `/auth/logout`

> 로그아웃을 진행합니다.