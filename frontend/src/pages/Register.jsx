import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
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
      await axios.post("http://localhost:8080/api/user/register", form, {
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

  return (
    <div className="register-container">
      <form className="register-form">
        <h2>회원가입</h2>

        <label>아이디:</label>
        <input name="userId" placeholder="아이디" onChange={handleChange} />
        {idError && <div className="error">{idError}</div>}

        <label>이메일:</label>
        <input name="userEmail" placeholder="이메일" onChange={handleChange} />
        {emailError && <div className="error">{emailError}</div>}

        <label>비밀번호:</label>
        <div className="input-with-toggle">
          <input
            name="userPasswd"
            type={showPassword ? 'text' : 'password'}
            placeholder="비밀번호"
            onChange={handleChange}
          />
          <span className="eye-icon" onClick={() => setShowPassword(!showPassword)}>
            {showPassword ? '👁‍🗨' : '👁'}
          </span>
        </div>

        <label>비밀번호 확인:</label>
        <div className="input-with-toggle">
          <input
            name="confirmPasswd"
            type={showConfirm ? 'text' : 'password'}
            placeholder="비밀번호 확인"
            onChange={handleChange}
          />
          <span className="eye-icon" onClick={() => setShowConfirm(!showConfirm)}>
            {showConfirm ? '👁‍🗨' : '👁'}
          </span>
        </div>

        <label>닉네임:</label>
        <input name="userNick" placeholder="닉네임" onChange={handleChange} />
        {nickError && <div className="error">{nickError}</div>}

        <label>주소:</label>
        <input name="userAddress" placeholder="주소 (지하철역)" onChange={handleChange} />

        {globalError && <div className="error">{globalError}</div>}

        <button type="button" onClick={handleRegister}>회원가입</button>

        <div className="register-link" onClick={() => navigate("/login")}>
          이미 계정이 있으신가요? 로그인
        </div>
      </form>
    </div>
  );
}

export default Register;
