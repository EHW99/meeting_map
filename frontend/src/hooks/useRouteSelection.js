import { useState, useCallback } from 'react';
import { drawPolyline, drawTransitPlan, clearPolylines } from '../components/RouteDrawer';

/**
 * 경로 선택 및 폴리라인 그리기 훅
 * @param {object} mapObj - Kakao Map 객체
 * @returns {object} 경로 선택 관련 상태 및 함수
 */
const useRouteSelection = (mapObj) => {
  const [polylines, setPolylines] = useState([]);
  const [transferMarkers, setTransferMarkers] = useState([]);
  const [selectedRouteIdx, setSelectedRouteIdx] = useState(null);

  // 폴리라인 및 마커 초기화
  const clearRouteDisplay = useCallback(() => {
    clearPolylines(polylines);
    setPolylines([]);

    transferMarkers.forEach(marker => marker.setMap(null));
    setTransferMarkers([]);

    setSelectedRouteIdx(null);
  }, [polylines, transferMarkers]);

  // 경로 클릭 핸들러
  const handleRouteClick = useCallback((routeList, transportMode, groupIdx, routeIdx, planIdx = 0) => {
    if (!mapObj) return;

    const pathType = transportMode === 'walk' ? 'pedestrian' : transportMode === 'transit' ? 'transit' : 'car';
    const selectedKey = `${groupIdx}-${routeIdx}-${planIdx}`;

    // 같은 경로 다시 클릭 시 선택 해제
    if (selectedRouteIdx === selectedKey) {
      clearRouteDisplay();
      return;
    }

    // 기존 폴리라인 및 마커 제거
    clearPolylines(polylines);
    transferMarkers.forEach(marker => marker.setMap(null));

    const selectedGroup = routeList[groupIdx];
    if (!selectedGroup) return;

    const selected = selectedGroup.routes[routeIdx];
    if (!selected) return;

    if (transportMode === 'transit') {
      // 대중교통 경로 그리기
      const lines = drawTransitPlan(mapObj, selected.plan[planIdx]);
      setPolylines(lines);

      // 환승 지점 마커 추가
      const markers = [];
      if (selected.plan[planIdx]?.detail) {
        selected.plan[planIdx].detail.forEach(d => {
          if (d.start?.y && d.start?.x) {
            const marker = new window.kakao.maps.Marker({
              map: mapObj,
              position: new window.kakao.maps.LatLng(d.start.y, d.start.x)
            });
            markers.push(marker);
          }
        });
      }
      setTransferMarkers(markers);
    } else {
      // 자동차/도보 경로 그리기
      if (selected.coordinates) {
        const color = pathType === 'pedestrian' ? '#4D524C' : '#007bff';
        const style = pathType === 'pedestrian' ? 'dashed' : 'solid';
        const line = drawPolyline(mapObj, selected.coordinates, color, style);
        setPolylines([line]);
      }
      setTransferMarkers([]);
    }

    setSelectedRouteIdx(selectedKey);
  }, [mapObj, polylines, transferMarkers, selectedRouteIdx, clearRouteDisplay]);

  // 초기 경로 그리기 (첫 번째 경로)
  const drawInitialRoute = useCallback((routeData, transportMode) => {
    if (!mapObj || !routeData) return;

    clearPolylines(polylines);
    setPolylines([]);

    if (transportMode !== 'transit' && routeData.coordinates) {
      const pathType = transportMode === 'walk' ? 'pedestrian' : 'car';
      const color = pathType === 'pedestrian' ? '#4D524C' : '#007bff';
      const style = pathType === 'pedestrian' ? 'dashed' : 'solid';
      const line = drawPolyline(mapObj, routeData.coordinates, color, style);
      setPolylines([line]);
    }
  }, [mapObj, polylines]);

  return {
    polylines,
    transferMarkers,
    selectedRouteIdx,
    handleRouteClick,
    clearRouteDisplay,
    drawInitialRoute,
    setPolylines,
    setTransferMarkers,
    setSelectedRouteIdx
  };
};

export default useRouteSelection;
