import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import './Map.css';
import RouteSummary from '../components/RouteSummary';
import { drawPolyline, drawTransitPlan, clearPolylines } from '../components/RouteDrawer';
import { API_BASE_URL } from '../constants.js';
import useAutocomplete from '../hooks/useAutocomplete';
import useKakaoMap from '../hooks/useKakaoMap';

export const categoryList = [
  { code: 'tour', name: '관광지', icon: '🏛️' },
  { code: 'food', name: '음식점', icon: '🍽️' },
  { code: 'cafe', name: '카페', icon: '☕' },
  { code: 'convenience-store', name: '편의점', icon: '🏪' },
  { code: 'shopping', name: '쇼핑', icon: '🛍️' },
  { code: 'culture', name: '문화시설', icon: '🎭' },
  { code: 'event', name: '공연/행사', icon: '🎪' }
];

export const categoryDetailCodes = {
  tour: ['tour-nature', 'tour-tradition', 'tour-park', 'tour-theme-park'],
  food: ['food-korean', 'food-western', 'food-japanese', 'food-chinese', 'food-other'],
  cafe: ['cafe'],
  'convenience-store': ['convenience-store'],
  shopping: ['shopping-permanent-market', 'shopping-department-store'],
  culture: ['culture'],
  event: ['event']
};

const Map = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // Kakao Map 초기화 (공통 훅 사용)
  const { mapObj, mapLoadError } = useKakaoMap('map');

  const [departure, setDeparture] = useState(null);
  const [destination, setDestination] = useState(null);
  const [transportMode, setTransportMode] = useState('car');
  const [routeList, setRouteList] = useState([]);
  const [selectedRouteIdx, setSelectedRouteIdx] = useState(null);
  const [polylines, setPolylines] = useState([]);
  const [showSidebar, setShowSidebar] = useState(false);
  const [categoryMarkers, setCategoryMarkers] = useState([]);
  const [selectedPlaces, setSelectedPlaces] = useState([]);
  const [addedList, setAddedList] = useState([]);
  const [sort, setSort] = useState();
  const [search, setSearch] = useState();
  const [departures, setDepartures] = useState([]);
  const [start, setStart] = useState();
  const [end, setEnd] = useState();
  const [middlePoint, setMiddlePoint] = useState();
  const [transferMarkers, setTransferMarkers] = useState();
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [showRoutePanel, setShowRoutePanel] = useState(false);

  // Loading and error states
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  // Search panel states
  const [searchMode, setSearchMode] = useState('route'); // 'route' or 'midpoint'
  const [searchDeparture, setSearchDeparture] = useState('');
  const [searchDestination, setSearchDestination] = useState('');
  const [searchDepartures, setSearchDepartures] = useState(['', '']);

  // 자동완성 훅 사용 (캐싱 포함)
  const { suggestions, fetchSuggestions, clearSuggestions } = useAutocomplete(300, 5);

  const handleRemovePlace = (indexToRemove) => {
    setAddedList(prev => prev.filter((_, i) => i !== indexToRemove));
  };

  // Search panel helper functions
  const handleSearchDepartureChange = (value) => {
    setSearchDeparture(value);
    fetchSuggestions(value, 'departure');
  };

  const handleSearchDestinationChange = (value) => {
    setSearchDestination(value);
    fetchSuggestions(value, 'destination');
  };

  const handleMultiDepartureChange = (index, value) => {
    const updated = [...searchDepartures];
    updated[index] = value;
    setSearchDepartures(updated);
    fetchSuggestions(value, `multi-${index}`);
  };

  const handleSelectSuggestion = (text, key) => {
    if (key === 'departure') {
      setSearchDeparture(text);
    } else if (key === 'destination') {
      setSearchDestination(text);
    } else if (key.startsWith('multi-')) {
      const index = parseInt(key.split('-')[1]);
      const updated = [...searchDepartures];
      updated[index] = text;
      setSearchDepartures(updated);
    }
    clearSuggestions(key);
  };

  const handleGetCurrentLocation = (key) => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
        const { latitude, longitude } = position.coords;
        const geocoder = new window.kakao.maps.services.Geocoder();
        geocoder.coord2Address(longitude, latitude, (result, status) => {
          if (status === window.kakao.maps.services.Status.OK) {
            const address = result[0].road_address?.address_name || result[0].address.address_name;
            if (key === 'departure') setSearchDeparture(address);
            else if (key === 'destination') setSearchDestination(address);
            else if (key.startsWith('multi-')) {
              const index = parseInt(key.split('-')[1]);
              const updated = [...searchDepartures];
              updated[index] = address;
              setSearchDepartures(updated);
            }
          }
        });
      });
    } else {
      alert('위치 정보를 가져올 수 없습니다.');
    }
  };

  const addSearchDepartureInput = () => {
    if (searchDepartures.length < 4) {
      setSearchDepartures([...searchDepartures, '']);
    }
  };

  const removeSearchDepartureInput = (index) => {
    if (index >= 2) {
      setSearchDepartures(searchDepartures.filter((_, i) => i !== index));
    }
  };

  const handleSearchSubmit = () => {
    if (searchMode === 'midpoint') {
      const filtered = searchDepartures.filter(d => d.trim() !== '');
      if (filtered.length < 2) {
        alert('출발지를 2개 이상 입력해야 합니다.');
        return;
      }
      navigate(`/map?search=middle-point&sort=rating_dsc&${filtered.map(f => `name=${encodeURIComponent(f)}`).join('&')}`);
    } else {
      if (!searchDeparture.trim() || !searchDestination.trim()) {
        alert('출발지와 도착지를 모두 입력해주세요.');
        return;
      }
      navigate(`/map?search=destination&sort=rating_dsc&start=${encodeURIComponent(searchDeparture)}&end=${encodeURIComponent(searchDestination)}`);
    }
  };

  // 맵 로드 에러 시 에러 메시지 설정
  useEffect(() => {
    if (mapLoadError) {
      setError('지도를 불러오는데 실패했습니다. 페이지를 새로고침 해주세요.');
    }
  }, [mapLoadError]);

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const search = searchParams.get("search") || "";
    const sort = searchParams.get("sort") || "";
    const departure = searchParams.get("start") || "";
    const destination = searchParams.get("end") || "";
    const departures = searchParams.getAll("name") || "";

    setSearch(search);
    setSort(sort);
    setDeparture(departure);
    setDestination(destination);
    setDepartures(departures);
  }, [location.search]);

  useEffect(() => {
    if (
      mapObj &&
      location.state?.fromRandomPlace &&
      location.state?.selectedPlace
    ) {
      const place = location.state.selectedPlace;
      clearPolylines(polylines);
      categoryMarkers.forEach(m => m.setMap(null));

      const lat = parseFloat(place.latitude);
      const lng = parseFloat(place.longitude);
      const marker = new window.kakao.maps.Marker({
        map: mapObj,
        position: new window.kakao.maps.LatLng(lat, lng),
        title: place.name
      });

      mapObj.setCenter(new window.kakao.maps.LatLng(lat, lng));
      setCategoryMarkers([marker]);
      setSelectedPlaces([place]);
      setShowSidebar(true);
    }
  }, [mapObj, location.state]);


  useEffect(() => {
    if (!mapObj) return;
    const fetchData = async () => {
      if (!search || !sort) return;
      if (search === 'random-place') return;

      const allPlaces = [];
      const markers = [];

      setIsLoading(true);
      setError(null);

      try {
        const res = await axios.get(
          `${API_BASE_URL}/map?search=${search}&sort=${sort}${departure ? `&start=${departure}` : ''}${destination ? `&end=${destination}` : ''}${departures.length ? `&${departures.map((d) => `name=${d}`).join('&')}` : ''}`,
          { timeout: 30000 } // 30초 타임아웃
        );

        const start = res.data?.start || null;
        const end = res.data?.end || null;
        const middlePoint = res.data?.middlePoint || null;
        const items = res.data?.list || [];

        if (items.length === 0) {
          setError('검색 결과가 없습니다. 다른 조건으로 검색해보세요.');
        }

        for (const place of items) {
          if (!place.longitude || !place.latitude) continue;

          const lat = parseFloat(place.latitude);
          const lng = parseFloat(place.longitude);
          if (isNaN(lat) || isNaN(lng)) continue;

          const marker = new window.kakao.maps.Marker({
            map: mapObj,
            position: new window.kakao.maps.LatLng(lat, lng),
            title: place.name,
          });
          markers.push(marker);
        }

        allPlaces.push(...items);
        setStart(start);
        setEnd(end);
        setMiddlePoint(middlePoint);
        setCategoryMarkers(markers);
        setSelectedPlaces(allPlaces.slice(0, 50));
        setShowSidebar(true);
        setShowRoutePanel(true);
      } catch (err) {
        console.error('장소 검색 실패:', err);
        if (err.code === 'ECONNABORTED') {
          setError('요청 시간이 초과되었습니다. 다시 시도해주세요.');
        } else if (err.response?.status === 400) {
          setError('잘못된 요청입니다. 출발지/도착지를 확인해주세요.');
        } else if (err.response?.status === 500) {
          setError('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        } else {
          setError('장소 검색 중 오류가 발생했습니다.');
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [departure, destination, departures, sort, mapObj, search]);


  useEffect(() => {
    if (!mapObj || (!start && !end)) return;

    if (transferMarkers) {
      transferMarkers.forEach((marker) => {
        marker.setMap(null);
      });
      setTransferMarkers([]);
    }

    const bounds = new window.kakao.maps.LatLngBounds();

    if (Array.isArray(start)) {
      start.forEach((s) => {
        const startPosition = new window.kakao.maps.LatLng(s.latitude, s.longitude);
        new window.kakao.maps.Marker({ map: mapObj, position: startPosition });
        bounds.extend(startPosition);
      })
    } else {
      const startPosition = new window.kakao.maps.LatLng(start.latitude, start.longitude);
      const endPosition = new window.kakao.maps.LatLng(end.latitude, end.longitude);

      const startImage = new window.kakao.maps.MarkerImage(
        'https://cdn-icons-png.flaticon.com/512/447/447031.png',
        new window.kakao.maps.Size(33, 44)
      );

      const endImage = new window.kakao.maps.MarkerImage(
        'https://cdn-icons-png.flaticon.com/512/684/684908.png',
        new window.kakao.maps.Size(33, 44)
      );

      new window.kakao.maps.Marker({ map: mapObj, position: startPosition, title: start.name, image: startImage });
      new window.kakao.maps.Marker({ map: mapObj, position: endPosition, title: end.name, image: endImage });

      bounds.extend(startPosition);
      bounds.extend(endPosition);
    }

    mapObj.setBounds(bounds);
    loadRoutes();
  }, [mapObj, start, end, transportMode]);

  const loadRoutes = async () => {
    clearPolylines(polylines);
    setPolylines([]);
    setSelectedRouteIdx(null);
    if (Array.isArray(start)) {
      try {
        const pathType = transportMode === 'walk' ? 'pedestrian' : transportMode === 'transit' ? 'transit' : 'car';
        const res = await axios.get(`${API_BASE_URL}/path/${pathType}?${start.map((d) => (`name=${d.name}`)).join('&')}`);
        const result = res.data.map((routes, idx) => ({
          from: start[idx].name,
          routes: [routes],
        }));
        setRouteList(result);
        if (transportMode !== 'transit') {
          const line = drawPolyline(mapObj, result[0].routes[0].coordinates, (pathType === 'pedestrian' ? '#4D524C' : '#007bff'));
          setPolylines([line]);
        }
      } catch (err) {
        console.error('경로 API 오류:', err);
      }
    } else {
      try {
        const pathType = transportMode === 'walk' ? 'pedestrian' : transportMode === 'transit' ? 'transit' : 'car';
        const res = await axios.get(`${API_BASE_URL}/path/${pathType}?start=${start.name}&end=${end.name}`);
        const result = [{
          from: start.name,
          routes: [res.data[0]],
        }];
        setRouteList(result);
        if (transportMode !== 'transit') {
          const line = drawPolyline(mapObj, result[0].routes[0].coordinates, (pathType === 'pedestrian' ? '#4D524C' : '#007bff'), (pathType === 'pedestrian' ? 'dashed' : 'solid'));
          setPolylines([line]);
        }
      } catch (err) {
        console.error('경로 API 오류:', err);
      }
    }
  };

  const handleRouteClick = (groupIdx, routeIdx, planIdx = 0) => {
    const pathType = transportMode === 'walk' ? 'pedestrian' : transportMode === 'transit' ? 'transit' : 'car';
    const selectedKey = `${groupIdx}-${routeIdx}-${planIdx}`;
    if (selectedRouteIdx === selectedKey) {
      clearPolylines(polylines);
      setPolylines([]);
      setSelectedRouteIdx(null);
      return;
    }
    clearPolylines(polylines);
    if (transferMarkers) {
      transferMarkers.forEach((marker) => {
        marker.setMap(null);
      });
      setTransferMarkers([]);
    }
    const selectedGroup = routeList[groupIdx];
    const selected = selectedGroup.routes[routeIdx];
    if (transportMode === 'transit') {
      const lines = drawTransitPlan(mapObj, selected.plan[planIdx]);
      setPolylines(lines);
      const markers = [];
      selected.plan[planIdx].detail.forEach(d => {
        const marker = new window.kakao.maps.Marker({ map: mapObj, position: new window.kakao.maps.LatLng(d.start.y, d.start.x) });
        markers.push(marker);
      });
      setTransferMarkers(markers);
    } else {
      const line = drawPolyline(mapObj, selected.coordinates, (pathType === 'pedestrian' ? '#4D524C' : '#007bff'), (pathType === 'pedestrian' ? 'dashed' : 'solid'));
      setPolylines([line]);
    }
    setSelectedRouteIdx(selectedKey);
  };

  const handleCategoryClick = async (code) => {
    const detailCodes = categoryDetailCodes[code];
    if (!detailCodes) return;

    // search/sort 파라미터가 없으면 API 호출하지 않음
    if (!search || !sort) {
      alert('출발지와 도착지를 먼저 설정해주세요.');
      return;
    }

    setSelectedCategory(code);
    const allPlaces = [];
    const markers = [];

    categoryMarkers.forEach(marker => marker.setMap(null));

    for (const detailCode of detailCodes) {
      try {
        const res = await axios.get(`${API_BASE_URL}/map?search=${search}&sort=${sort}${(departure ? `&start=${departure}` : ``)}${(destination ? `&end=${destination}` : ``)}${(departures.length ? `&${departures.map((d) => (`name=${d}`)).join('&')}` : ``)}&category=${detailCode}`);
        const items = res.data?.list || [];

        for (const place of items) {
          if (!place.longitude || !place.latitude) continue;

          const lat = parseFloat(place.latitude);
          const lng = parseFloat(place.longitude);
          const marker = new window.kakao.maps.Marker({
            map: mapObj,
            position: new window.kakao.maps.LatLng(lat, lng),
            title: place.name
          });
          markers.push(marker);
        }

        allPlaces.push(...items);
      } catch (err) {
        console.error(`❌ ${detailCode} 요청 실패:`, err);
      }
    }

    setCategoryMarkers(markers);
    setSelectedPlaces(allPlaces.slice(0, 50));
    setShowSidebar(true);
  };

  const handleAddPlace = (place) => {
    if (addedList.find(p => p.name === place.name)) return;
    if (addedList.length >= 8) return;
    setAddedList([...addedList, place]);
  };

  const handleCreateSchedule = () => {
    if (end) {
      navigate('/schedule', {
        state: {
          addedList,
          end: { latitude: end.latitude, longitude: end.longitude }
        }
      });
    } else {
      navigate('/schedule', {
        state: {
          addedList,
          end: { latitude: middlePoint.latitude, longitude: middlePoint.longitude }
        }
      });
    }
  };

  return (
    <div className="map-layout">
      {/* Left Sidebar */}
      <aside className="map-sidebar">
        {/* Search Panel - Show when no search params */}
        {(!search || !sort) && (
          <div className="sidebar-section search-panel">
            <h3 className="section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="11" cy="11" r="8"/>
                <line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
              장소 검색
            </h3>

            {/* Mode Toggle */}
            <div className="search-mode-toggle">
              <button
                className={`mode-btn ${searchMode === 'route' ? 'active' : ''}`}
                onClick={() => setSearchMode('route')}
              >
                경로 찾기
              </button>
              <button
                className={`mode-btn ${searchMode === 'midpoint' ? 'active' : ''}`}
                onClick={() => setSearchMode('midpoint')}
              >
                중간지점
              </button>
            </div>

            {searchMode === 'route' ? (
              <div className="search-form">
                {/* Departure Input */}
                <div className="search-input-group">
                  <label>출발지</label>
                  <div className="input-with-btn">
                    <input
                      type="text"
                      placeholder="출발지 입력"
                      value={searchDeparture}
                      onChange={(e) => handleSearchDepartureChange(e.target.value)}
                    />
                    <button
                      className="location-btn"
                      onClick={() => handleGetCurrentLocation('departure')}
                      title="현재 위치"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="12" cy="12" r="10"/>
                        <circle cx="12" cy="12" r="3"/>
                      </svg>
                    </button>
                  </div>
                  {suggestions.departure && suggestions.departure.length > 0 && (
                    <ul className="suggestions-dropdown">
                      {suggestions.departure.map((s, i) => (
                        <li key={i} onClick={() => handleSelectSuggestion(s.placeName, 'departure')}>
                          {s.placeName}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>

                {/* Destination Input */}
                <div className="search-input-group">
                  <label>도착지</label>
                  <div className="input-with-btn">
                    <input
                      type="text"
                      placeholder="도착지 입력"
                      value={searchDestination}
                      onChange={(e) => handleSearchDestinationChange(e.target.value)}
                    />
                    <button
                      className="location-btn"
                      onClick={() => handleGetCurrentLocation('destination')}
                      title="현재 위치"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="12" cy="12" r="10"/>
                        <circle cx="12" cy="12" r="3"/>
                      </svg>
                    </button>
                  </div>
                  {suggestions.destination && suggestions.destination.length > 0 && (
                    <ul className="suggestions-dropdown">
                      {suggestions.destination.map((s, i) => (
                        <li key={i} onClick={() => handleSelectSuggestion(s.placeName, 'destination')}>
                          {s.placeName}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>

                <button className="search-submit-btn" onClick={handleSearchSubmit}>
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                  </svg>
                  경로 찾기
                </button>
              </div>
            ) : (
              <div className="search-form">
                {/* Multiple Departures */}
                {searchDepartures.map((value, index) => (
                  <div className="search-input-group" key={index}>
                    <label>출발지 {index + 1}</label>
                    <div className="input-with-btn">
                      <input
                        type="text"
                        placeholder={`출발지 ${index + 1}`}
                        value={value}
                        onChange={(e) => handleMultiDepartureChange(index, e.target.value)}
                      />
                      <button
                        className="location-btn"
                        onClick={() => handleGetCurrentLocation(`multi-${index}`)}
                        title="현재 위치"
                      >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <circle cx="12" cy="12" r="10"/>
                          <circle cx="12" cy="12" r="3"/>
                        </svg>
                      </button>
                      {index >= 2 && (
                        <button className="remove-input-btn" onClick={() => removeSearchDepartureInput(index)}>
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <line x1="18" y1="6" x2="6" y2="18"/>
                            <line x1="6" y1="6" x2="18" y2="18"/>
                          </svg>
                        </button>
                      )}
                    </div>
                    {suggestions[`multi-${index}`] && suggestions[`multi-${index}`].length > 0 && (
                      <ul className="suggestions-dropdown">
                        {suggestions[`multi-${index}`].map((s, i) => (
                          <li key={i} onClick={() => handleSelectSuggestion(s.placeName, `multi-${index}`)}>
                            {s.placeName}
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                ))}

                <div className="search-actions">
                  {searchDepartures.length < 4 && (
                    <button className="add-departure-btn" onClick={addSearchDepartureInput}>
                      + 출발지 추가
                    </button>
                  )}
                </div>

                <button className="search-submit-btn" onClick={handleSearchSubmit}>
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                  </svg>
                  중간지점 찾기
                </button>
              </div>
            )}
          </div>
        )}

        {/* Location Info */}
        <div className="sidebar-section location-info">
          <h3 className="section-title">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
              <circle cx="12" cy="10" r="3"/>
            </svg>
            경로 정보
          </h3>
          <div className="location-details">
            {Array.isArray(start) ? (
              <div className="location-item">
                <span className="location-label">출발지</span>
                <ul className="location-list">
                  {start.map((s, index) => (
                    <li key={index}>{s.name}</li>
                  ))}
                </ul>
              </div>
            ) : start && (
              <div className="location-item">
                <span className="location-label">출발</span>
                <span className="location-value">{start.name}</span>
              </div>
            )}

            {middlePoint && (
              <div className="location-item middle">
                <span className="location-label">중간지점</span>
                <span className="location-value">{middlePoint.address}</span>
              </div>
            )}

            {end && (
              <div className="location-item">
                <span className="location-label">도착</span>
                <span className="location-value">{end.name}</span>
              </div>
            )}
          </div>
        </div>

        {/* Category Filter */}
        <div className="sidebar-section">
          <h3 className="section-title">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="3" width="7" height="7"/>
              <rect x="14" y="3" width="7" height="7"/>
              <rect x="14" y="14" width="7" height="7"/>
              <rect x="3" y="14" width="7" height="7"/>
            </svg>
            카테고리
          </h3>
          <div className="category-grid">
            {categoryList.map(cat => (
              <button
                key={cat.code}
                className={`category-chip ${selectedCategory === cat.code ? 'active' : ''}`}
                onClick={() => handleCategoryClick(cat.code)}
              >
                <span className="category-icon">{cat.icon}</span>
                <span className="category-name">{cat.name}</span>
              </button>
            ))}
          </div>
        </div>

        {/* Place Results */}
        {showSidebar && selectedPlaces.length > 0 && (
          <div className="sidebar-section place-results">
            <h3 className="section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M4 6h16M4 12h16M4 18h16"/>
              </svg>
              검색 결과
              <span className="result-count">{selectedPlaces.length}개</span>
            </h3>
            <div className="place-list">
              {selectedPlaces.map((place, index) => (
                <div key={index} className="place-card">
                  <div className="place-image">
                    {place.imageUrl || place.thumbnail ? (
                      <img src={place.imageUrl || place.thumbnail} alt={place.name} />
                    ) : (
                      <div className="no-image">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                          <circle cx="8.5" cy="8.5" r="1.5"/>
                          <polyline points="21,15 16,10 5,21"/>
                        </svg>
                      </div>
                    )}
                  </div>
                  <div className="place-info">
                    <h4 className="place-name">{place.name}</h4>
                    <p className="place-address">{place.address || '상세 주소 없음'}</p>
                    <button
                      className="btn-add"
                      disabled={addedList.some(p => p.name === place.name) || addedList.length >= 8}
                      onClick={() => handleAddPlace(place)}
                    >
                      {addedList.some(p => p.name === place.name) ? '추가됨' : '+ 추가'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Selected Places */}
        <div className="sidebar-section selected-places">
          <div className="section-header">
            <h3 className="section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M9 11l3 3L22 4"/>
                <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
              </svg>
              선택한 장소
              <span className="selected-count">{addedList.length}/8</span>
            </h3>
            {addedList.length > 0 && (
              <button className="btn-schedule" onClick={handleCreateSchedule}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                  <line x1="16" y1="2" x2="16" y2="6"/>
                  <line x1="8" y1="2" x2="8" y2="6"/>
                  <line x1="3" y1="10" x2="21" y2="10"/>
                </svg>
                스케줄 생성
              </button>
            )}
          </div>
          {addedList.length === 0 ? (
            <p className="empty-state">장소를 선택하면 여기에 표시됩니다</p>
          ) : (
            <ul className="selected-list">
              {addedList.map((place, index) => (
                <li key={index} className="selected-item">
                  <span className="item-number">{index + 1}</span>
                  <div className="item-info">
                    <span className="item-name">{place.name}</span>
                    <span className="item-address">{place.address}</span>
                  </div>
                  <button className="btn-remove" onClick={() => handleRemovePlace(index)}>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <line x1="18" y1="6" x2="6" y2="18"/>
                      <line x1="6" y1="6" x2="18" y2="18"/>
                    </svg>
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>
      </aside>

      {/* Map Area */}
      <main className="map-main">
        {/* Loading Overlay */}
        {isLoading && (
          <div className="map-loading-overlay">
            <div className="loading-spinner"></div>
            <p>검색 중...</p>
          </div>
        )}

        {/* Error Banner */}
        {error && !isLoading && (
          <div className="map-error-banner">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            <span>{error}</span>
            <button className="error-close-btn" onClick={() => setError(null)}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
        )}

        {/* Map Container or Error Placeholder */}
        {mapLoadError ? (
          <div className="map-error-placeholder">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
              <circle cx="12" cy="10" r="3"/>
              <line x1="2" y1="2" x2="22" y2="22" strokeWidth="2"/>
            </svg>
            <h3>지도를 불러올 수 없습니다</h3>
            <p>페이지를 새로고침 하거나 인터넷 연결을 확인해주세요.</p>
            <button className="btn-reload" onClick={() => window.location.reload()}>
              새로고침
            </button>
          </div>
        ) : (
          <div id="map" className="map-container"></div>
        )}

        {/* Route Panel - Bottom of Map */}
        {showRoutePanel && (start || end) && (
          <div className="route-panel">
            <div className="route-panel-header">
              <h3>경로 옵션</h3>
              <div className="transport-tabs">
                <button
                  className={`transport-tab ${transportMode === 'car' ? 'active' : ''}`}
                  onClick={() => setTransportMode('car')}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M5 17h14v-2H5v2zm0-4h14l-1.5-6h-11L5 13zm2 2a1 1 0 110-2 1 1 0 010 2zm10 0a1 1 0 110-2 1 1 0 010 2z"/>
                  </svg>
                  차량
                </button>
                <button
                  className={`transport-tab ${transportMode === 'transit' ? 'active' : ''}`}
                  onClick={() => setTransportMode('transit')}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="4" y="3" width="16" height="16" rx="2"/>
                    <path d="M8 21h8M12 17v4"/>
                    <path d="M8 7h8M8 11h8"/>
                  </svg>
                  대중교통
                </button>
                <button
                  className={`transport-tab ${transportMode === 'walk' ? 'active' : ''}`}
                  onClick={() => setTransportMode('walk')}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="4" r="2"/>
                    <path d="M15 7l-3 3-3-3M9 10l-2 11M15 10l2 11"/>
                  </svg>
                  도보
                </button>
              </div>
              <button className="btn-close-panel" onClick={() => setShowRoutePanel(false)}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="18 15 12 9 6 15"/>
                </svg>
              </button>
            </div>
            <div className="route-panel-content">
              <RouteSummary
                routes={routeList}
                transportMode={transportMode}
                selectedIdx={selectedRouteIdx}
                onSelect={handleRouteClick}
              />
            </div>
          </div>
        )}

        {/* Mini Route Toggle Button (when panel is closed) */}
        {!showRoutePanel && (start || end) && (
          <button className="btn-show-route" onClick={() => setShowRoutePanel(true)}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
            경로 보기
          </button>
        )}
      </main>
    </div>
  );
};

export default Map;
