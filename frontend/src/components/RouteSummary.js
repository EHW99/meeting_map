import React, { useState } from 'react';
import './RouteSummary.css';

// 교통수단별 아이콘 및 색상
const getModeIcon = (mode) => {
  switch (mode) {
    case '도보': return '🚶';
    case '지하철': return '🚇';
    case '버스': return '🚌';
    default: return '📍';
  }
};

const getModeClass = (mode) => {
  switch (mode) {
    case '도보': return 'walk';
    case '지하철': return 'subway';
    case '버스': return 'bus';
    default: return '';
  }
};

// 시간 포맷팅
const formatTime = (minutes) => {
  if (minutes >= 60) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return mins > 0 ? `${hours}시간 ${mins}분` : `${hours}시간`;
  }
  return `${minutes}분`;
};

// 거리 포맷팅
const formatDistance = (meters) => {
  if (meters >= 1000) {
    return `${(meters / 1000).toFixed(1)}km`;
  }
  return `${meters}m`;
};

const RouteSummary = ({ routes = [], transportMode, selectedIdx, onSelect }) => {
  const [expandedRoute, setExpandedRoute] = useState(null);

  const toggleExpand = (key) => {
    setExpandedRoute(expandedRoute === key ? null : key);
  };

  return (
    <div className="route-list">
      {routes.map((routeGroup, groupIdx) => (
        <div key={groupIdx} className="route-group">
          <h4 className="route-group-title">
            <span className="route-from-icon">📍</span>
            {routeGroup.from}에서 출발
          </h4>

          {transportMode === 'transit' ? (
            (() => {
              const allPlans = routeGroup.routes
                .filter(route => route.message === null)
                .flatMap((route, idx) =>
                  route.plan.map((p, subIdx) => ({
                    ...p,
                    groupIdx,
                    idx,
                    subIdx,
                    key: `${groupIdx}-${idx}-${subIdx}`
                  }))
                );

              const sortedPlans = allPlans
                .sort((a, b) => a.totalTime - b.totalTime)
                .slice(0, 6);

              return (
                <div className="transit-routes">
                  {sortedPlans.map((plan, index) => {
                    const isSelected = selectedIdx === plan.key;
                    const isExpanded = expandedRoute === plan.key;

                    return (
                      <div
                        key={plan.key}
                        className={`transit-card ${isSelected ? 'selected' : ''}`}
                      >
                        {/* 요약 정보 헤더 */}
                        <div
                          className="transit-card-header"
                          onClick={() => onSelect(plan.groupIdx, plan.idx, plan.subIdx)}
                        >
                          <div className="transit-rank">
                            {index === 0 ? '🏆' : `${index + 1}`}
                          </div>
                          <div className="transit-summary">
                            <div className="transit-main-info">
                              <span className="transit-time">{formatTime(plan.totalTime)}</span>
                              <span className="transit-divider">•</span>
                              <span className="transit-fare">{plan.fare.toLocaleString()}원</span>
                            </div>
                            <div className="transit-sub-info">
                              <span>도보 {formatTime(plan.totalWalkTime)}</span>
                              <span className="transit-divider">•</span>
                              <span>환승 {plan.transferCount}회</span>
                            </div>
                          </div>

                          {/* 경로 미리보기 (아이콘만) */}
                          <div className="transit-preview">
                            {plan.detail && plan.detail.slice(0, 5).map((d, i) => (
                              <React.Fragment key={i}>
                                <span
                                  className={`mode-badge ${getModeClass(d.mode)}`}
                                  style={d.routeColor && d.mode !== '도보' ? { backgroundColor: `#${d.routeColor}` } : {}}
                                >
                                  {getModeIcon(d.mode)}
                                </span>
                                {i < Math.min(plan.detail.length - 1, 4) && (
                                  <span className="route-arrow">→</span>
                                )}
                              </React.Fragment>
                            ))}
                            {plan.detail && plan.detail.length > 5 && (
                              <span className="more-steps">+{plan.detail.length - 5}</span>
                            )}
                          </div>
                        </div>

                        {/* 확장 버튼 */}
                        <button
                          className="transit-expand-btn"
                          onClick={(e) => {
                            e.stopPropagation();
                            toggleExpand(plan.key);
                          }}
                        >
                          {isExpanded ? '접기 ▲' : '상세보기 ▼'}
                        </button>

                        {/* 상세 경로 정보 (확장 시) */}
                        {isExpanded && plan.detail && (
                          <div className="transit-detail">
                            <div className="transit-timeline">
                              {plan.detail.map((step, stepIdx) => (
                                <div key={stepIdx} className={`timeline-step ${getModeClass(step.mode)}`}>
                                  <div className="timeline-marker">
                                    <span
                                      className={`marker-icon ${getModeClass(step.mode)}`}
                                      style={step.routeColor && step.mode !== '도보' ? { backgroundColor: `#${step.routeColor}` } : {}}
                                    >
                                      {getModeIcon(step.mode)}
                                    </span>
                                    {stepIdx < plan.detail.length - 1 && (
                                      <div
                                        className="timeline-line"
                                        style={step.routeColor && step.mode !== '도보' ? { backgroundColor: `#${step.routeColor}` } : {}}
                                      />
                                    )}
                                  </div>
                                  <div className="timeline-content">
                                    <div className="step-header">
                                      <span className="step-mode">{step.mode}</span>
                                      <span className="step-time">{formatTime(step.time)}</span>
                                      {step.distance > 0 && (
                                        <span className="step-distance">{formatDistance(step.distance)}</span>
                                      )}
                                    </div>
                                    <div className="step-description">{step.timeline}</div>
                                    <div className="step-locations">
                                      <span className="step-start">{step.start?.name}</span>
                                      <span className="step-arrow">→</span>
                                      <span className="step-end">{step.end?.name}</span>
                                    </div>
                                  </div>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              );
            })()
          ) : (
            /* 자동차/도보 경로 */
            <div className="route-button-group">
              {routeGroup.routes.map((route, idx) => (
                <button
                  key={`${groupIdx}-${idx}`}
                  className={`route-button ${transportMode} ${selectedIdx === `${groupIdx}-${idx}` ? 'selected' : ''}`}
                  onClick={() => onSelect(groupIdx, idx)}
                >
                  <span className="route-icon">
                    {transportMode === 'car' ? '🚗' : '🚶'}
                  </span>
                  <span className="route-info">
                    <span className="route-time">{formatTime(route.time)}</span>
                    <span className="route-meta">
                      {route.fare ? `요금 ${route.fare.toLocaleString()}원 • ` : ''}
                      거리 {formatDistance(route.distance)}
                    </span>
                  </span>
                </button>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default RouteSummary;
