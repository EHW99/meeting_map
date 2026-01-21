import { useState, useEffect, useCallback, useRef } from 'react';

/**
 * 마커 클러스터러 훅
 * 많은 수의 마커를 효율적으로 관리하고 클러스터링합니다.
 * @param {object} mapObj - Kakao Map 객체
 * @param {object} options - 클러스터러 옵션
 * @returns {object} { clusterer, addMarkers, clearMarkers, getMarkers }
 */
const useMarkerClusterer = (mapObj, options = {}) => {
  const [clusterer, setClusterer] = useState(null);
  const markersRef = useRef([]);

  const {
    averageCenter = true,
    minLevel = 6,
    disableClickZoom = false,
    gridSize = 60,
    styles = null
  } = options;

  // 기본 클러스터 스타일
  const defaultStyles = [
    {
      width: '36px',
      height: '36px',
      background: 'rgba(245, 158, 11, 0.8)',
      borderRadius: '18px',
      color: '#fff',
      textAlign: 'center',
      fontWeight: 'bold',
      lineHeight: '36px',
      fontSize: '14px'
    },
    {
      width: '46px',
      height: '46px',
      background: 'rgba(217, 119, 6, 0.85)',
      borderRadius: '23px',
      color: '#fff',
      textAlign: 'center',
      fontWeight: 'bold',
      lineHeight: '46px',
      fontSize: '15px'
    },
    {
      width: '56px',
      height: '56px',
      background: 'rgba(180, 83, 9, 0.9)',
      borderRadius: '28px',
      color: '#fff',
      textAlign: 'center',
      fontWeight: 'bold',
      lineHeight: '56px',
      fontSize: '16px'
    }
  ];

  // 클러스터러 초기화
  useEffect(() => {
    if (!mapObj || !window.kakao?.maps?.MarkerClusterer) return;

    const newClusterer = new window.kakao.maps.MarkerClusterer({
      map: mapObj,
      averageCenter,
      minLevel,
      disableClickZoom,
      gridSize,
      calculator: [10, 30],
      styles: styles || defaultStyles
    });

    // 클릭 이벤트 (disableClickZoom이 true일 때 수동 줌)
    if (disableClickZoom) {
      window.kakao.maps.event.addListener(newClusterer, 'clusterclick', (cluster) => {
        const level = mapObj.getLevel() - 1;
        mapObj.setLevel(level, { anchor: cluster.getCenter() });
      });
    }

    setClusterer(newClusterer);

    return () => {
      newClusterer.clear();
    };
  }, [mapObj, averageCenter, minLevel, disableClickZoom, gridSize]);

  // 마커 추가
  const addMarkers = useCallback((places, onMarkerClick = null) => {
    if (!clusterer || !places || places.length === 0) return [];

    // 기존 마커 제거
    clusterer.clear();
    markersRef.current.forEach(marker => marker.setMap(null));
    markersRef.current = [];

    const markers = places
      .filter(place => place.latitude && place.longitude)
      .map(place => {
        const lat = parseFloat(place.latitude);
        const lng = parseFloat(place.longitude);

        if (isNaN(lat) || isNaN(lng)) return null;

        const marker = new window.kakao.maps.Marker({
          position: new window.kakao.maps.LatLng(lat, lng),
          title: place.name
        });

        // 마커 클릭 이벤트
        if (onMarkerClick) {
          window.kakao.maps.event.addListener(marker, 'click', () => {
            onMarkerClick(place, marker);
          });
        }

        return marker;
      })
      .filter(Boolean);

    markersRef.current = markers;
    clusterer.addMarkers(markers);

    return markers;
  }, [clusterer]);

  // 마커 제거
  const clearMarkers = useCallback(() => {
    if (clusterer) {
      clusterer.clear();
    }
    markersRef.current.forEach(marker => marker.setMap(null));
    markersRef.current = [];
  }, [clusterer]);

  // 현재 마커 목록 반환
  const getMarkers = useCallback(() => {
    return markersRef.current;
  }, []);

  // 클러스터러 다시 그리기
  const redraw = useCallback(() => {
    if (clusterer) {
      clusterer.redraw();
    }
  }, [clusterer]);

  return {
    clusterer,
    addMarkers,
    clearMarkers,
    getMarkers,
    redraw
  };
};

export default useMarkerClusterer;
