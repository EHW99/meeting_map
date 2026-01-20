// 📁 src/App.js
import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Header from './pages/Header'; // ✅ 추가
import Home from './pages/Home';
import Group from './pages/Group';
import Board from './pages/Board';
import Schedule from './pages/Schedule';
import Mypage from './pages/Mypage';
import Login from './pages/Login';
import Register from './pages/Register';
import Map from './pages/Map';
import PostWrite from './pages/PostWrite';
import PostDetail from './pages/PostDetail';
import PostUpdate from './pages/PostUpdate';
import './App.css';
import { AppProvider } from "./AppContext";
import KakaoLogin from './pages/KakaoLogin';
import MobileBottomNav from './components/MobileBottomNav';
import useIsMobile from './utils/useIsMobile';

function AppContent() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const location = useLocation();
  const isMobile = useIsMobile();

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);
  // 로그인/회원가입 페이지가 아닐 때만 Header 표시
  const showHeader = location.pathname !== '/login' && location.pathname !== '/register';

  return (
    <div className="App">
      <AppProvider>

      {showHeader && <Header toggleSidebar={toggleSidebar} />}
      {!isMobile && <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />} {/* ✅ PC만 */}

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/group" element={<Group />} />
        <Route path="/board" element={<Board />} />
        <Route path="/schedule" element={<Schedule />} />
        <Route path="/mypage" element={<Mypage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/map" element={<Map />} />
        <Route path="/auth/kakao/callback" element={<KakaoLogin />} />
        <Route path="/write" element={<PostWrite />} /> {/* 게시글 작성 페이지 */}
        <Route path="/boards/:boardNo" element={<PostDetail />} /> {/* 라우트 설정 */}
        <Route path="/edit/:boardNo" element={<PostUpdate />} />
      </Routes>
      {isMobile && <MobileBottomNav />} {/* ✅ 모바일에서만 표시 */}
      </AppProvider>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
