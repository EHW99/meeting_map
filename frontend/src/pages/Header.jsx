import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import './Header.css';
import { useAppContext } from '../AppContext';
import { API_BASE_URL } from '../constants';

function Header() {
  const { user, setUser } = useAppContext();
  const [errorMessage, setErrorMessage] = useState('');
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  if (location.pathname === '/login' || location.pathname === '/register') return null;

  const handleLogout = async () => {
    try {
      await axios.post(
        `${API_BASE_URL}/auth/logout`, null,
        { withCredentials: true }
      );
      setUser(null);
      navigate('/');
    } catch (error) {
      setErrorMessage("로그아웃에 실패했습니다.");
    }
  };

  const navItems = [
    { path: '/', label: '홈' },
    { path: '/map', label: '지도' },
    { path: '/schedule', label: '일정' },
    { path: '/board', label: '게시판' },
    { path: '/group', label: '그룹' },
  ];

  const isActive = (path) => {
    if (path === '/') return location.pathname === '/';
    return location.pathname.startsWith(path);
  };

  return (
    <header className="header">
      <div className="header-container">
        {/* Logo */}
        <Link to="/" className="header-logo">
          <span className="logo-text">MeetingMap</span>
        </Link>

        {/* Desktop Navigation */}
        <nav className="header-nav">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`nav-link ${isActive(item.path) ? 'active' : ''}`}
            >
              {item.label}
            </Link>
          ))}
        </nav>

        {/* Auth Buttons / User Info */}
        <div className="header-auth">
          {user ? (
            <div className="user-menu">
              <Link to="/mypage" className="user-profile">
                <span className="user-avatar">
                  {user.userImg ? (
                    <img src={user.userImg} alt="프로필" />
                  ) : (
                    <span className="avatar-placeholder">
                      {(user.userNick || user.nickname || 'U').charAt(0)}
                    </span>
                  )}
                </span>
                <span className="user-name">{user.userNick || user.nickname}</span>
              </Link>
              <button className="btn-logout" onClick={handleLogout}>
                로그아웃
              </button>
            </div>
          ) : (
            <div className="auth-buttons">
              <button className="btn-login" onClick={() => navigate('/login')}>
                로그인
              </button>
              <button className="btn-register" onClick={() => navigate('/register')}>
                회원가입
              </button>
            </div>
          )}
        </div>

        {/* Mobile Menu Button */}
        <button
          className="mobile-menu-btn"
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          aria-label="메뉴"
        >
          <span className={`hamburger ${mobileMenuOpen ? 'open' : ''}`}>
            <span></span>
            <span></span>
            <span></span>
          </span>
        </button>
      </div>

      {/* Mobile Navigation */}
      <div className={`mobile-nav ${mobileMenuOpen ? 'open' : ''}`}>
        <nav className="mobile-nav-links">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`mobile-nav-link ${isActive(item.path) ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              {item.label}
            </Link>
          ))}
          {user ? (
            <>
              <Link
                to="/mypage"
                className="mobile-nav-link"
                onClick={() => setMobileMenuOpen(false)}
              >
                마이페이지
              </Link>
              <button
                className="mobile-nav-link logout"
                onClick={() => {
                  handleLogout();
                  setMobileMenuOpen(false);
                }}
              >
                로그아웃
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="mobile-nav-link"
                onClick={() => setMobileMenuOpen(false)}
              >
                로그인
              </Link>
              <Link
                to="/register"
                className="mobile-nav-link"
                onClick={() => setMobileMenuOpen(false)}
              >
                회원가입
              </Link>
            </>
          )}
        </nav>
      </div>

      {/* Mobile Overlay */}
      {mobileMenuOpen && (
        <div
          className="mobile-overlay"
          onClick={() => setMobileMenuOpen(false)}
        />
      )}
    </header>
  );
}

export default Header;
