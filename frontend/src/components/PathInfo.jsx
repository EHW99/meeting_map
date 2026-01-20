// ✅ components/PathInfo.jsx — 교통 정보 요약 박스
import React from 'react';
import './PathInfo.css';

export default function PathInfo({ mode, summary }) {
  if (!summary) return null;

  const getLabel = (mode) => {
    if (mode === 'car') return '자동차';
    if (mode === 'walk') return '도보';
    return '대중교통';
  };

  return (
    <div className="path-info-box">
      <h4>{getLabel(mode)} 경로 정보</h4>
      <ul>
        {summary.totalTime && <li>소요 시간: {summary.totalTime}분</li>}
        {summary.totalDistance && <li>총 거리: {(summary.totalDistance / 1000).toFixed(1)}km</li>}
        {summary.totalTransfer !== undefined && <li>환승 횟수: {summary.totalTransfer}회</li>}
      </ul>
    </div>
  );
}
