import React from 'react';
import { useLocation } from 'react-router-dom';
import MainSection from '../components/MainSection.jsx';

export default function Home() {
  const location = useLocation();
  const message = location.state?.message;
  
  return <MainSection />;
}
