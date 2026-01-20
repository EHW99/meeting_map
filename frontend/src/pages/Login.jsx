import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';
import './Login.css';
import { useAppContext } from "../AppContext"; // ✅ context 불러오기
import { API_BASE_URL } from '../constants';

const predefinedUsers = ['user1','user2','user3','user4','user5','user6','user7','user8','user9','user10','admin1','admin2','admin3'];

function Login() {
  const [userId, setUserId] = useState('');
  const [userPasswd, setUserPasswd] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser } = useAppContext(); // ✅ context에 로그인 정보 저장할 함수
  const { from = "/" } = location.state || {};
  const userIdInputRef = useRef(null);  // 1. ref 생성

  useEffect(() => {
    userIdInputRef.current?.focus();    // 2. 컴포넌트 마운트 시 포커스
  }, []);

  const handleLogin = async () => {
    if (!userId || !userPasswd) {
      setErrorMessage("아이디와 비밀번호를 모두 입력해주세요.");
      return;
    }

    const finalPassword = predefinedUsers.includes(userId) ? '123456' : userPasswd;

    try {
      const response = await axios.post(
        `${API_BASE_URL}/auth/login`,
        { userId, userPasswd: finalPassword },
        {
          withCredentials: true,
          headers: { "Content-Type": "application/json" }
        }
      );
      setUser(response.data); // ✅ context에도 저장!
      alert("로그인 성공");
      navigate(from); // 🔸 원래 가려던 페이지로 이동!
    } catch (error) {
      setErrorMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
    }
  };

  return (
    <div className="login-container">
      <div className="login-form">
        <h2>로그인</h2>

        <label>아이디:</label>
        <input
          type="text"
          placeholder="아이디"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
        />

        <label>비밀번호:</label>
        <div className="input-with-toggle">
          <input
            type={showPassword ? "text" : "password"}
            placeholder="비밀번호"
            value={userPasswd}
            onChange={(e) => setUserPasswd(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleLogin()}
            ref={userIdInputRef}
          />
          <span className="eye-icon" onClick={() => setShowPassword(!showPassword)}>
            {showPassword ? '👁‍🗨' : '👁'}
          </span>
        </div>

        {errorMessage && <div className="error">{errorMessage}</div>}

        <button onClick={handleLogin}>로그인</button>

        <div style={{ width: '100%', marginBottom: '12px' }}>
  <a
    href={`https://kauth.kakao.com/oauth/authorize?client_id=${process.env.REACT_APP_KAKAO_CLIENT_ID}&redirect_uri=${process.env.REACT_APP_KAKAO_REDIRECT_URI}&response_type=code&state=${encodeURIComponent(from)}`}
    style={{
      display: 'block',
      width: '100%',
      height: '44px',
      backgroundColor: '#FEE500',
      borderRadius: '6px',
      overflow: 'hidden'
    }}
  >
    <img
      src="/images/kakao_login_medium_narrow.png"
      alt="카카오 로그인"
      style={{
        width: '100%',
        height: '100%',
        objectFit: 'contain',
        display: 'block'
      }}
    />
  </a>
  {/*<a
      href={`https://kauth.kakao.com/oauth/authorize?client_id=d88db5d8494588ec7e3f5e9aa95b78d8&redirect_uri=https://meeting-map.kro.kr/auth/kakao/callback&response_type=code&state=${encodeURIComponent(from)}`}
      style={{
        display: 'block',
        width: '100%',
        height: '44px',
        backgroundColor: '#FEE500',
        borderRadius: '6px',
        overflow: 'hidden'
      }}
      >
      <img
        src="/images/kakao_login_medium_narrow.png"
        alt="카카오 로그인"
        style={{
          width: '100%',
          height: '100%',
          objectFit: 'contain',
          display: 'block'
        }}
      />
    </a>
    배포용*/}
</div>

  {/* ⬇ 회원가입은 그 아래로 이동 */}
  <div className="register-link" onClick={() => navigate("/register")}>
    회원가입
  </div>
</div>
</div>
  );
}

export default Login;
