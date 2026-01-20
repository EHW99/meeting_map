import React from 'react';
import { Link } from 'react-router-dom';
import { FaHome, FaUsers, FaClipboard, FaMap, FaCalendarAlt, FaUser, FaSignInAlt } from "react-icons/fa";
import './MobileBottomNav.css';

const MobileBottomNav = () => {
  return (
    <div className="mobile-bottom-scroll">
      <div className="nav-scroll-container">
        <Link to="/group"><FaUsers /><span>GROUP</span></Link>
        <Link to="/board"><FaClipboard /><span>BOARD</span></Link>
        <Link to="/"><FaHome /><span>HOME</span></Link>
        <Link to="/map"><FaMap /><span>MAP</span></Link>
        <Link to="/schedule"><FaCalendarAlt /><span>SCHEDULE</span></Link>
        <Link to="/mypage"><FaUser /><span>MYPAGE</span></Link>
      </div>
    </div>
  );
};

export default MobileBottomNav;
