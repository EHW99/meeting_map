import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';
import './Header.css';
import { useAppContext } from '../AppContext'
import { API_BASE_URL } from '../constants';

function Header({ toggleSidebar }) {
  const { user, setUser } = useAppContext();
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();
  const location = useLocation();

  if (location.pathname === '/login' || location.pathname === '/register') return null;

  const handleLogout = async () => {
    try {
      await axios.post(
        `${API_BASE_URL}/auth/logout`, null, 
        {
          withCredentials: true
        }
      );
      setUser(null);
      navigate('/');
    } catch (error) {
      setErrorMessage("로그아웃에 실패했습니다.");
    }
  };


  return (
    <>
      {/* 햄버거 메뉴 버튼 */}
      {toggleSidebar && (
        <button className="hamburger" onClick={toggleSidebar}>
          ☰
        </button>
      )}

      {/* 우측 상단 버튼 영역 */}
      <div className="header-buttons">
        {user ? (
          <>
            <span className="nickname">{user.userNick || user.nickname}</span>
            <button onClick={handleLogout}>로그아웃</button>
          </>
        ) : (
          <>
            <button onClick={() => navigate('/login')}>로그인</button>
            <button onClick={() => navigate('/register')}>회원가입</button>
          </>
        )}
      </div>
    </>
  );
}


export default Header;
