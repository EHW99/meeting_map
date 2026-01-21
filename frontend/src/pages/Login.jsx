import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import './Login.css';
import { useAppContext } from "../AppContext";
import { API_BASE_URL } from '../constants';

const predefinedUsers = ['user1','user2','user3','user4','user5','user6','user7','user8','user9','user10','admin1','admin2','admin3'];

function Login() {
  const [userId, setUserId] = useState('');
  const [userPasswd, setUserPasswd] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser } = useAppContext();
  const { from = "/" } = location.state || {};
  const userIdInputRef = useRef(null);

  useEffect(() => {
    userIdInputRef.current?.focus();
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
      setUser(response.data);
      alert("로그인 성공");
      navigate(from);
    } catch (error) {
      setErrorMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleLogin();
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        {/* Left Side - Brand */}
        <div className="auth-brand">
          <Link to="/" className="brand-logo">MeetingMap</Link>
          <h1 className="brand-title">만남의 중심을 찾아보세요</h1>
          <p className="brand-subtitle">친구들과의 최적의 만남 장소를 쉽고 빠르게 찾아드립니다</p>
        </div>

        {/* Right Side - Form */}
        <div className="auth-form-wrapper">
          <div className="auth-form">
            <h2 className="form-title">로그인</h2>
            <p className="form-subtitle">MeetingMap에 오신 것을 환영합니다</p>

            <div className="form-group">
              <label className="form-label" htmlFor="userId">아이디</label>
              <input
                id="userId"
                type="text"
                className="form-input"
                placeholder="아이디를 입력하세요"
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
                onKeyDown={handleKeyDown}
                ref={userIdInputRef}
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="password">비밀번호</label>
              <div className="input-with-icon">
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  className="form-input"
                  placeholder="비밀번호를 입력하세요"
                  value={userPasswd}
                  onChange={(e) => setUserPasswd(e.target.value)}
                  onKeyDown={handleKeyDown}
                />
                <button
                  type="button"
                  className="toggle-password"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={showPassword ? "비밀번호 숨기기" : "비밀번호 보기"}
                >
                  {showPassword ? (
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24"/>
                      <line x1="1" y1="1" x2="23" y2="23"/>
                    </svg>
                  ) : (
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                  )}
                </button>
              </div>
            </div>

            {errorMessage && (
              <div className="error-message">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="12" y1="8" x2="12" y2="12"/>
                  <line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                {errorMessage}
              </div>
            )}

            <button className="btn-primary" onClick={handleLogin}>
              로그인
            </button>

            <div className="divider">
              <span>또는</span>
            </div>

            <a
              href={`https://kauth.kakao.com/oauth/authorize?client_id=${process.env.REACT_APP_KAKAO_CLIENT_ID}&redirect_uri=${process.env.REACT_APP_KAKAO_REDIRECT_URI}&response_type=code&state=${encodeURIComponent(from)}`}
              className="btn-kakao"
            >
              <img src="/images/kakao_login_medium_narrow.png" alt="카카오 로그인" />
            </a>

            <p className="form-footer">
              계정이 없으신가요?{' '}
              <Link to="/register" className="link-primary">회원가입</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
