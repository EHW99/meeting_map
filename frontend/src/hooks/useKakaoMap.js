import { useState, useEffect, useCallback } from 'react';

/**
 * Kakao Map 초기화 훅
 * @param {string} containerId - 지도 컨테이너 DOM ID
 * @param {object} options - 지도 옵션 (center, level)
 * @param {number} timeoutMs - 타임아웃 (기본 5초)
 * @returns {object} { mapObj, mapLoadError, resetMap }
 */
const useKakaoMap = (containerId, options = {}, timeoutMs = 5000) => {
  const [mapObj, setMapObj] = useState(null);
  const [mapLoadError, setMapLoadError] = useState(false);

  const {
    centerLat = 37.554722,
    centerLng = 126.970833,
    level = 5
  } = options;

  useEffect(() => {
    let attempts = 0;
    const maxAttempts = timeoutMs / 100;
    let intervalId = null;

    const initializeMap = () => {
      attempts++;

      if (window.kakao && window.kakao.maps) {
        clearInterval(intervalId);
        const container = document.getElementById(containerId);

        if (!container) {
          // 컨테이너가 아직 없으면 다시 시도
          if (attempts < maxAttempts) {
            return;
          }
          setMapLoadError(true);
          return;
        }

        const map = new window.kakao.maps.Map(container, {
          center: new window.kakao.maps.LatLng(centerLat, centerLng),
          level: level,
        });
        setMapObj(map);
        setMapLoadError(false);
      } else if (attempts >= maxAttempts) {
        clearInterval(intervalId);
        setMapLoadError(true);
      }
    };

    intervalId = setInterval(initializeMap, 100);

    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [containerId, centerLat, centerLng, level, timeoutMs]);

  // 지도 재초기화 함수
  const resetMap = useCallback(() => {
    setMapObj(null);
    setMapLoadError(false);
  }, []);

  return {
    mapObj,
    mapLoadError,
    resetMap
  };
};

export default useKakaoMap;
