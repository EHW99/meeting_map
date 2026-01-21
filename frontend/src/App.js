import React from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Header from './pages/Header';
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
  const location = useLocation();
  const isMobile = useIsMobile();

  // Login/Register pages don't show Header
  const showHeader = location.pathname !== '/login' && location.pathname !== '/register';
  // Map page needs special layout
  const isMapPage = location.pathname === '/map';

  return (
    <div className="app">
      <AppProvider>
        {showHeader && <Header />}

        <main className={`main-content ${!showHeader ? 'no-header' : ''} ${isMapPage ? 'map-page' : ''}`}>
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
            <Route path="/write" element={<PostWrite />} />
            <Route path="/boards/:boardNo" element={<PostDetail />} />
            <Route path="/edit/:boardNo" element={<PostUpdate />} />
          </Routes>
        </main>

        {isMobile && showHeader && <MobileBottomNav />}
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
