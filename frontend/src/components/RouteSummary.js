import React from 'react';
import './RouteSummary.css';

const RouteSummary = ({ routes = [], transportMode, selectedIdx, onSelect }) => {
  return (
    <div className="route-list">
      {routes.map((routeGroup, groupIdx) => (
        <div key={groupIdx} className="route-group">
          <h4>{routeGroup.from}에서 출발:</h4>
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

              const firstHalf = sortedPlans.slice(0, 3);
              const secondHalf = sortedPlans.slice(3);

              return (
                <>
                  <div className="route-button-group">
                    {firstHalf.map(p => (
                      <button
                        key={p.key}
                        className={`route-button transit ${selectedIdx === p.key ? 'selected' : ''}`}
                        onClick={() => onSelect(p.groupIdx, p.idx, p.subIdx)}
                      >
                        🚇 {
                          p.totalTime >= 60
                            ? `${Math.floor(p.totalTime / 60)}시간 ${p.totalTime % 60}분`
                            : `${p.totalTime}분`
                        } / 도보 {
                          p.totalWalkTime >= 60
                            ? `${Math.floor(p.totalWalkTime / 60)}시간 ${p.totalWalkTime % 60}분`
                            : `${p.totalWalkTime}분`
                        } / 환승 {p.transferCount}회 / 요금 {p.fare.toLocaleString()}원
                      </button>
                    ))}
                  </div>
                  <div className="route-button-group spaced">
                    {secondHalf.map(p => (
                      <button
                        key={p.key}
                        className={`route-button transit ${selectedIdx === p.key ? 'selected' : ''}`}
                        onClick={() => onSelect(p.groupIdx, p.idx, p.subIdx)}
                      >
                        🚇 {
                          p.totalTime >= 60
                            ? `${Math.floor(p.totalTime / 60)}시간 ${p.totalTime % 60}분`
                            : `${p.totalTime}분`
                        } / 도보 {
                          p.totalWalkTime >= 60
                            ? `${Math.floor(p.totalWalkTime / 60)}시간 ${p.totalWalkTime % 60}분`
                            : `${p.totalWalkTime}분`
                        } / 환승 {p.transferCount}회 / 요금 {p.fare.toLocaleString()}원
                      </button>
                    ))}
                  </div>
                </>
              );
            })()
          ) : (
            routeGroup.routes.map((route, idx) => (
              <button
                key={`${groupIdx}-${idx}`}
                className={`route-button ${transportMode} ${selectedIdx === `${groupIdx}-${idx}` ? 'selected' : ''}`}
                onClick={() => onSelect(groupIdx, idx)}
              >
                ⏱ {
                  route.time >= 60
                    ? `${Math.floor(route.time / 60)}시간 ${route.time % 60}분`
                    : `${route.time}분`
                }
                {route.fare ? ` / 요금 ${route.fare.toLocaleString()}원` : ``} / 거리 {(route.distance / 1000).toFixed(1)}km
              </button>
            ))
          )}
        </div>
      ))}
    </div>
  );
};

export default RouteSummary;
