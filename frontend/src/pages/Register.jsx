import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { API_BASE_URL } from '../constants';
import './Register.css';

function Register() {
  const [form, setForm] = useState({
    userId: '',
    userEmail: '',
    userPasswd: '',
    confirmPasswd: '',
    userNick: '',
    userAddress: ''
  });

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const [globalError, setGlobalError] = useState('');
  const [idError, setIdError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [nickError, setNickError] = useState('');

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });

    if (name === 'userId') setIdError('');
    if (name === 'userEmail') setEmailError('');
    if (name === 'userNick') setNickError('');
    setGlobalError('');
  };

  const handleRegister = async () => {
    const { userId, userEmail, userPasswd, confirmPasswd, userNick } = form;

    if (!userId || !userEmail || !userPasswd || !confirmPasswd || !userNick) {
      setGlobalError("모든 항목을 입력해주세요.");
      return;
    }

    if (userPasswd !== confirmPasswd) {
      setGlobalError("비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      await axios.post(`${API_BASE_URL}/user/register`, form, {
        headers: { "Content-Type": "application/json" },
        withCredentials: true
      });
      alert("회원가입 성공");
      navigate("/login");
    } catch (err) {
      const msg = err?.response?.data?.message || "";
      let handled = false;

      if (msg.includes("아이디")) {
        setIdError("이미 존재하는 아이디입니다.");
        handled = true;
      } else if (msg.includes("이메일")) {
        setEmailError("이미 존재하는 이메일입니다.");
        handled = true;
      } else if (msg.includes("닉네임")) {
        setNickError("이미 존재하는 닉네임입니다.");
        handled = true;
      }

      if (!handled) {
        setGlobalError(msg || "회원가입에 실패했습니다.");
      }
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleRegister();
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        {/* Left Side - Brand */}
        <div className="auth-brand">
          <Link to="/" className="brand-logo">MeetingMap</Link>
          <h1 className="brand-title">새로운 만남의 시작</h1>
          <p className="brand-subtitle">MeetingMap과 함께 친구들과의 완벽한 만남 장소를 찾아보세요</p>
        </div>

        {/* Right Side - Form */}
        <div className="auth-form-wrapper register-wrapper">
          <div className="auth-form">
            <h2 className="form-title">회원가입</h2>
            <p className="form-subtitle">계정을 만들어 서비스를 이용해보세요</p>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label" htmlFor="userId">아이디</label>
                <input
                  id="userId"
                  name="userId"
                  type="text"
                  className={`form-input ${idError ? 'error' : ''}`}
                  placeholder="아이디를 입력하세요"
                  onChange={handleChange}
                  onKeyDown={handleKeyDown}
                />
                {idError && <span className="field-error">{idError}</span>}
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="userNick">닉네임</label>
                <input
                  id="userNick"
                  name="userNick"
                  type="text"
                  className={`form-input ${nickError ? 'error' : ''}`}
                  placeholder="닉네임을 입력하세요"
                  onChange={handleChange}
                  onKeyDown={handleKeyDown}
                />
                {nickError && <span className="field-error">{nickError}</span>}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="userEmail">이메일</label>
              <input
                id="userEmail"
                name="userEmail"
                type="email"
                className={`form-input ${emailError ? 'error' : ''}`}
                placeholder="이메일을 입력하세요"
                onChange={handleChange}
                onKeyDown={handleKeyDown}
              />
              {emailError && <span className="field-error">{emailError}</span>}
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label" htmlFor="userPasswd">비밀번호</label>
                <div className="input-with-icon">
                  <input
                    id="userPasswd"
                    name="userPasswd"
                    type={showPassword ? 'text' : 'password'}
                    className="form-input"
                    placeholder="비밀번호"
                    onChange={handleChange}
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

              <div className="form-group">
                <label className="form-label" htmlFor="confirmPasswd">비밀번호 확인</label>
                <div className="input-with-icon">
                  <input
                    id="confirmPasswd"
                    name="confirmPasswd"
                    type={showConfirm ? 'text' : 'password'}
                    className="form-input"
                    placeholder="비밀번호 확인"
                    onChange={handleChange}
                    onKeyDown={handleKeyDown}
                  />
                  <button
                    type="button"
                    className="toggle-password"
                    onClick={() => setShowConfirm(!showConfirm)}
                    aria-label={showConfirm ? "비밀번호 숨기기" : "비밀번호 보기"}
                  >
                    {showConfirm ? (
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
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="userAddress">주소 (선택)</label>
              <input
                id="userAddress"
                name="userAddress"
                type="text"
                className="form-input"
                placeholder="주소 또는 근처 지하철역"
                onChange={handleChange}
                onKeyDown={handleKeyDown}
              />
            </div>

            {globalError && (
              <div className="error-message">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="12" y1="8" x2="12" y2="12"/>
                  <line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                {globalError}
              </div>
            )}

            <button className="btn-primary" onClick={handleRegister}>
              회원가입
            </button>

            <p className="form-footer">
              이미 계정이 있으신가요?{' '}
              <Link to="/login" className="link-primary">로그인</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Register;
