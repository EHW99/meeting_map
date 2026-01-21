import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Schedule.css';
import RouteSummary from '../components/RouteSummary';
import { drawPolyline, drawTransitPlan, clearPolylines } from '../components/RouteDrawer';
import { categoryList, categoryDetailCodes } from './Map';
import { API_BASE_URL } from '../constants.js';
import { useAppContext } from '../AppContext';

const STEPS = [
  { id: 1, name: '장소 선택', icon: '📍' },
  { id: 2, name: '시간 설정', icon: '⏰' },
  { id: 3, name: '일정 확인', icon: '✅' }
];

const Schedule = () => {
  const navigate = useNavigate();
  const { user } = useAppContext();
  const location = useLocation();

  const [currentStep, setCurrentStep] = useState(1);
  const [mapObj, setMapObj] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [sidebarVisible, setSidebarVisible] = useState(false);
  const [selectedCategoryPlaces, setSelectedCategoryPlaces] = useState([]);

  const { addedList = [], end = {} } = location.state || {};
  const [scheduleItems, setScheduleItems] = useState(() => addedList);
  const [scheduleItemStayMinutes, setScheduleItemStayMinutes] = useState(Array(8).fill(60));
  const [scheduleName, setScheduleName] = useState('');
  const [scheduleAbout, setScheduleAbout] = useState('');
  const [scheduleDate, setScheduleDate] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [transport, setTransport] = useState('pedestrian');
  const [additionalRecommendation, setAdditionalRecommendation] = useState(false);
  const [totalPlaceCount, setTotalPlaceCount] = useState(() => Math.max(scheduleItems.length, 3));
  const [theme, setTheme] = useState('tour');
  const [stayMinutesMean, setStayMinutesMean] = useState(60);
  const [placeMarkers, setPlaceMarkers] = useState([]);
  const [recommendMode, setRecommendMode] = useState('normal');

  const [createdSchedule, setCreatedSchedule] = useState(null);
  const [createScheduleLoading, setCreateScheduleLoading] = useState(false);
  const [createScheduleError, setCreateScheduleError] = useState('');

  const [transportMode, setTransportMode] = useState('car');
  const [routeList, setRouteList] = useState([]);
  const [selectedRouteIdx, setSelectedRouteIdx] = useState(null);
  const [polylines, setPolylines] = useState([]);
  const [transferMarkers, setTransferMarkers] = useState([]);
  const [showRoutePanel, setShowRoutePanel] = useState(false);

  // Auth check
  useEffect(() => {
    if (user === null) {
      navigate('/login', { state: { from: '/schedule' } });
    }
  }, [user, navigate]);

  // Error timer
  useEffect(() => {
    if (createScheduleError) {
      const timer = setTimeout(() => setCreateScheduleError(''), 5000);
      return () => clearTimeout(timer);
    }
  }, [createScheduleError]);

  // Initialize map (wait for Kakao SDK to load)
  useEffect(() => {
    const interval = setInterval(() => {
      if (window.kakao && window.kakao.maps) {
        clearInterval(interval);
        const container = document.getElementById('schedule-map');
        if (!container) return;
        const map = new window.kakao.maps.Map(container, {
          center: new window.kakao.maps.LatLng(37.554722, 126.970833),
          level: 5,
        });
        setMapObj(map);
      }
    }, 100);

    return () => clearInterval(interval);
  }, []);

  // Center map on items
  useEffect(() => {
    if (!mapObj || !scheduleItems || scheduleItems.length === 0) return;
    if (location.state?.fromRandomPlace && scheduleItems[0]) {
      const centerLatLng = new window.kakao.maps.LatLng(
        parseFloat(scheduleItems[0].latitude),
        parseFloat(scheduleItems[0].longitude)
      );
      mapObj.setCenter(centerLatLng);
    }
  }, [mapObj, scheduleItems, location.state]);

  // Place markers
  useEffect(() => {
    if (!scheduleItems || !mapObj) return;

    placeMarkers.forEach(pm => {
      pm.marker.setMap(null);
      pm.infowindow.close();
    });

    const bounds = new window.kakao.maps.LatLngBounds();
    const markers = [];

    scheduleItems.forEach((item, index) => {
      const coords = new window.kakao.maps.LatLng(item.latitude, item.longitude);
      const marker = new window.kakao.maps.Marker({
        map: mapObj,
        position: coords,
        title: item.name
      });

      const infowindow = new window.kakao.maps.InfoWindow({
        content: `<div style="padding:5px;font-size:12px;">${index + 1}. ${item.name}</div>`
      });

      window.kakao.maps.event.addListener(marker, 'mouseover', () => infowindow.open(mapObj, marker));
      window.kakao.maps.event.addListener(marker, 'mouseout', () => infowindow.close());

      markers.push({ marker, infowindow });
      bounds.extend(coords);
    });

    setPlaceMarkers(markers);
    if (scheduleItems.length > 0) {
      mapObj.setBounds(bounds);
    }
  }, [scheduleItems, mapObj]);

  // Load places by category
  useEffect(() => {
    if (selectedCategory) {
      loadPlacesByCategory();
    }
  }, [selectedCategory]);

  // Load routes when schedule created
  useEffect(() => {
    if (createdSchedule) {
      loadRoutes();
    }
  }, [createdSchedule, transportMode]);

  const loadPlacesByCategory = async () => {
    const detailCodes = categoryDetailCodes[selectedCategory];
    if (!detailCodes) return;

    // end 좌표가 없으면 API 호출하지 않음
    if (!end || !end.latitude || !end.longitude) {
      console.warn('위치 정보가 없습니다. Map 페이지에서 경로를 설정해주세요.');
      return;
    }

    const allPlaces = [];
    for (const detailCode of detailCodes) {
      try {
        const res = await axios.get(`${API_BASE_URL}/map?search=location&sort=rating_dsc&latitude=${end.latitude}&longitude=${end.longitude}&category=${detailCode}`);
        allPlaces.push(...(res.data?.list || []));
      } catch (err) {
        console.error(`카테고리 요청 실패:`, err);
      }
    }
    setSelectedCategoryPlaces(allPlaces);
  };

  const loadRoutes = async () => {
    clearPolylines(polylines);
    setPolylines([]);
    setSelectedRouteIdx(null);

    try {
      const pathType = transportMode === 'walk' ? 'pedestrian' : transportMode === 'transit' ? 'transit' : 'car';
      const res = await axios.post(`${API_BASE_URL}/path/${pathType}`, createdSchedule);
      const result = res.data.map((routes) => ({
        from: routes?.origin?.name,
        routes: [routes],
      }));
      setRouteList(result);
      setShowRoutePanel(true);

      if (transportMode !== 'transit' && result[0]?.routes[0]?.coordinates) {
        const line = drawPolyline(mapObj, result[0].routes[0].coordinates, pathType === 'pedestrian' ? '#4D524C' : '#007bff', pathType === 'pedestrian' ? 'dashed' : 'solid');
        setPolylines([line]);
      }
    } catch (err) {
      console.error('경로 API 오류:', err);
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
    transferMarkers.forEach(m => m.setMap(null));
    setTransferMarkers([]);

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
      const line = drawPolyline(mapObj, selected.coordinates, pathType === 'pedestrian' ? '#4D524C' : '#007bff', pathType === 'pedestrian' ? 'dashed' : 'solid');
      setPolylines([line]);
    }
    setSelectedRouteIdx(selectedKey);
  };

  const addToSchedule = (place) => {
    if (scheduleItems.length >= 8) return;
    if (!scheduleItems.some(item => item.name === place.name)) {
      setScheduleItems([...scheduleItems, place]);
    }
  };

  const removeFromSchedule = (name) => {
    setScheduleItems(scheduleItems.filter(item => item.name !== name));
  };

  const handleCreateSchedule = async () => {
    if (!scheduleDate || !startTime || !endTime) {
      setCreateScheduleError('날짜와 시간을 모두 입력해주세요.');
      return;
    }

    setCreateScheduleLoading(true);
    setCreateScheduleError('');

    try {
      const body = {
        selectedPlace: scheduleItems.map((item, index) => ({
          contentId: item.contentId,
          address: item.address,
          name: item.name,
          latitude: item.latitude,
          longitude: item.longitude,
          category: item.category,
          stayMinutes: scheduleItemStayMinutes[index] || stayMinutesMean
        })),
        scheduleStartTime: `${scheduleDate}T${startTime}`,
        scheduleEndTime: `${scheduleDate}T${endTime}`,
        transport,
        additionalRecommendation: recommendMode === 'ai' ? true : additionalRecommendation,
        aiRecommendation: recommendMode === 'ai',
        totalPlaceCount,
        theme,
        stayMinutesMean,
        pointCoordinate: { latitude: end.latitude, longitude: end.longitude }
      };

      const res = await axios.post(`${API_BASE_URL}/schedules/create`, body, { withCredentials: true });
      const result = res.data;

      setScheduleItems(result.places);
      setCreatedSchedule(result.schedules);
      setTransportMode(transport === 'pedestrian' ? 'walk' : transport === 'transit' ? 'transit' : 'car');
      setCurrentStep(3);
    } catch (err) {
      setCreateScheduleError(err.response?.data?.message || '일정 생성에 실패했습니다.');
    } finally {
      setCreateScheduleLoading(false);
    }
  };

  const handleSaveSchedule = async () => {
    if (!scheduleName) {
      setCreateScheduleError('스케줄 이름을 입력해주세요.');
      return;
    }

    try {
      await axios.post(`${API_BASE_URL}/schedules`, {
        scheduleName,
        scheduleAbout,
        details: createdSchedule
      }, { withCredentials: true });
      alert('스케줄이 저장되었습니다!');
      navigate('/mypage');
    } catch (err) {
      console.error('스케줄 저장 실패:', err);
      setCreateScheduleError('스케줄 저장에 실패했습니다.');
    }
  };

  const canProceedToStep2 = scheduleItems.length > 0;
  const canProceedToStep3 = scheduleDate && startTime && endTime;

  return (
    <div className="schedule-page">
      {/* Progress Steps */}
      <div className="schedule-progress">
        <div className="progress-container">
          {STEPS.map((step, index) => (
            <React.Fragment key={step.id}>
              <div
                className={`progress-step ${currentStep >= step.id ? 'active' : ''} ${currentStep === step.id ? 'current' : ''}`}
                onClick={() => step.id < currentStep && setCurrentStep(step.id)}
              >
                <span className="step-icon">{step.icon}</span>
                <span className="step-name">{step.name}</span>
              </div>
              {index < STEPS.length - 1 && (
                <div className={`progress-line ${currentStep > step.id ? 'completed' : ''}`} />
              )}
            </React.Fragment>
          ))}
        </div>
      </div>

      <div className="schedule-layout">
        {/* Left: Map & Place List */}
        <div className="schedule-map-section">
          <div id="schedule-map" className="schedule-map"></div>

          {/* Location Info Overlay */}
          {scheduleItems.length > 0 && (
            <div className="schedule-location-info">
              <div className="location-badge start">
                <span className="badge-label">출발</span>
                <span className="badge-name">{scheduleItems[0]?.name}</span>
              </div>
              {scheduleItems.length > 1 && (
                <div className="location-badge end">
                  <span className="badge-label">도착</span>
                  <span className="badge-name">{scheduleItems[scheduleItems.length - 1]?.name}</span>
                </div>
              )}
            </div>
          )}

          {/* Route Panel */}
          {showRoutePanel && createdSchedule && (
            <div className="schedule-route-panel">
              <div className="route-panel-header">
                <h4>경로 안내</h4>
                <div className="transport-toggle">
                  <button className={transportMode === 'car' ? 'active' : ''} onClick={() => setTransportMode('car')}>🚗</button>
                  <button className={transportMode === 'transit' ? 'active' : ''} onClick={() => setTransportMode('transit')}>🚌</button>
                  <button className={transportMode === 'walk' ? 'active' : ''} onClick={() => setTransportMode('walk')}>🚶</button>
                </div>
                <button className="btn-close" onClick={() => setShowRoutePanel(false)}>×</button>
              </div>
              <div className="route-panel-body">
                <RouteSummary
                  routes={routeList}
                  transportMode={transportMode}
                  selectedIdx={selectedRouteIdx}
                  onSelect={handleRouteClick}
                />
              </div>
            </div>
          )}
        </div>

        {/* Right: Step Content */}
        <div className="schedule-content">
          {/* Step 1: Place Selection */}
          {currentStep === 1 && (
            <div className="step-content">
              <h2 className="step-title">장소 선택</h2>
              <p className="step-description">방문할 장소를 선택하세요 (최대 8개)</p>

              {/* Category Buttons */}
              <div className="category-filter">
                {categoryList.map(cat => (
                  <button
                    key={cat.code}
                    className={`category-chip ${selectedCategory === cat.code ? 'active' : ''}`}
                    onClick={() => {
                      setSelectedCategory(selectedCategory === cat.code ? null : cat.code);
                      setSidebarVisible(selectedCategory !== cat.code);
                    }}
                  >
                    {cat.name}
                  </button>
                ))}
              </div>

              {/* Category Places */}
              {sidebarVisible && selectedCategoryPlaces.length > 0 && (
                <div className="category-places">
                  <h4>추천 장소</h4>
                  <div className="places-scroll">
                    {selectedCategoryPlaces.slice(0, 10).map((place, idx) => (
                      <div key={idx} className="place-mini-card">
                        <span className="place-mini-name">{place.name}</span>
                        <button
                          className="btn-add-mini"
                          onClick={() => addToSchedule(place)}
                          disabled={scheduleItems.some(p => p.name === place.name) || scheduleItems.length >= 8}
                        >
                          +
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Selected Places */}
              <div className="selected-places-section">
                <h4>선택한 장소 ({scheduleItems.length}/8)</h4>
                {scheduleItems.length === 0 ? (
                  <p className="empty-hint">지도에서 장소를 추가하거나 위 카테고리에서 선택하세요</p>
                ) : (
                  <ul className="selected-places-list">
                    {scheduleItems.map((item, index) => (
                      <li key={index} className="selected-place-item">
                        <span className="place-number">{index + 1}</span>
                        <div className="place-details">
                          <span className="place-name">{item.name}</span>
                          <span className="place-category">{item.category || '장소'}</span>
                        </div>
                        <button className="btn-remove" onClick={() => removeFromSchedule(item.name)}>×</button>
                      </li>
                    ))}
                  </ul>
                )}
              </div>

              <button
                className="btn-next"
                onClick={() => setCurrentStep(2)}
                disabled={!canProceedToStep2}
              >
                다음 단계로
              </button>
            </div>
          )}

          {/* Step 2: Time Settings */}
          {currentStep === 2 && (
            <div className="step-content">
              <h2 className="step-title">시간 설정</h2>
              <p className="step-description">일정 날짜와 시간을 설정하세요</p>

              <div className="form-section">
                <div className="form-group">
                  <label>날짜</label>
                  <input type="date" value={scheduleDate} onChange={(e) => setScheduleDate(e.target.value)} />
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>시작 시간</label>
                    <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label>종료 시간</label>
                    <input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} />
                  </div>
                </div>

                <div className="form-group">
                  <label>이동 수단</label>
                  <div className="transport-options">
                    <label className={transport === 'pedestrian' ? 'active' : ''}>
                      <input type="radio" name="transport" value="pedestrian" checked={transport === 'pedestrian'} onChange={(e) => setTransport(e.target.value)} />
                      🚶 도보
                    </label>
                    <label className={transport === 'car' ? 'active' : ''}>
                      <input type="radio" name="transport" value="car" checked={transport === 'car'} onChange={(e) => setTransport(e.target.value)} />
                      🚗 자동차
                    </label>
                    <label className={transport === 'transit' ? 'active' : ''}>
                      <input type="radio" name="transport" value="transit" checked={transport === 'transit'} onChange={(e) => setTransport(e.target.value)} />
                      🚌 대중교통
                    </label>
                  </div>
                </div>

                <div className="form-group">
                  <label>추천 방식</label>
                  <div className="recommend-options">
                    <label className={recommendMode === 'normal' ? 'active' : ''}>
                      <input type="radio" name="recommend" value="normal" checked={recommendMode === 'normal'} onChange={(e) => setRecommendMode(e.target.value)} />
                      일반 추천
                    </label>
                    <label className={recommendMode === 'ai' ? 'active' : ''}>
                      <input type="radio" name="recommend" value="ai" checked={recommendMode === 'ai'} onChange={(e) => setRecommendMode(e.target.value)} />
                      ✨ AI 추천
                    </label>
                  </div>
                </div>

                {(additionalRecommendation || recommendMode === 'ai') && (
                  <div className="additional-options">
                    <div className="form-group">
                      <label>테마</label>
                      <select value={theme} onChange={(e) => setTheme(e.target.value)}>
                        <option value="tour">관광</option>
                        <option value="nature">자연 힐링</option>
                        <option value="history">역사 탐방</option>
                        <option value="food">음식 투어</option>
                        <option value="shopping">쇼핑</option>
                        <option value="date">데이트</option>
                      </select>
                    </div>
                    <div className="form-group">
                      <label>총 장소 수</label>
                      <input type="number" min={scheduleItems.length} max={7} value={totalPlaceCount} onChange={(e) => setTotalPlaceCount(Number(e.target.value))} />
                    </div>
                  </div>
                )}

                {recommendMode !== 'ai' && (
                  <label className="checkbox-label">
                    <input type="checkbox" checked={additionalRecommendation} onChange={(e) => setAdditionalRecommendation(e.target.checked)} />
                    추가 장소 추천 받기
                  </label>
                )}
              </div>

              {createScheduleError && <div className="error-message">{createScheduleError}</div>}

              <div className="btn-group">
                <button className="btn-back" onClick={() => setCurrentStep(1)}>이전</button>
                <button
                  className="btn-create"
                  onClick={handleCreateSchedule}
                  disabled={!canProceedToStep3 || createScheduleLoading}
                >
                  {createScheduleLoading ? '생성 중...' : '일정 생성하기'}
                </button>
              </div>
            </div>
          )}

          {/* Step 3: Schedule Result */}
          {currentStep === 3 && createdSchedule && (
            <div className="step-content">
              <h2 className="step-title">일정 완성!</h2>
              <p className="step-description">생성된 일정을 확인하고 저장하세요</p>

              <div className="schedule-save-form">
                <input
                  type="text"
                  className="schedule-name-input"
                  placeholder="스케줄 이름을 입력하세요"
                  value={scheduleName}
                  onChange={(e) => setScheduleName(e.target.value)}
                />
                <input
                  type="text"
                  className="schedule-about-input"
                  placeholder="간단한 설명 (선택사항)"
                  value={scheduleAbout}
                  onChange={(e) => setScheduleAbout(e.target.value)}
                />
              </div>

              <div className="schedule-result">
                <div className="result-date">
                  📅 {new Date(createdSchedule[0].scheduleStartTime).toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })}
                </div>
                <ul className="timeline">
                  {createdSchedule.map((item, index) => (
                    <li key={index} className="timeline-item">
                      <div className="timeline-marker">{index + 1}</div>
                      <div className="timeline-content">
                        <span className="timeline-time">
                          {new Date(item.scheduleStartTime).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })} -
                          {new Date(item.scheduleEndTime).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })}
                        </span>
                        <span className="timeline-place">{item.scheduleContent}</span>
                      </div>
                    </li>
                  ))}
                </ul>
              </div>

              {!showRoutePanel && (
                <button className="btn-show-route" onClick={() => setShowRoutePanel(true)}>
                  🗺️ 경로 보기
                </button>
              )}

              {createScheduleError && <div className="error-message">{createScheduleError}</div>}

              <div className="btn-group">
                <button className="btn-back" onClick={() => setCurrentStep(2)}>수정하기</button>
                <button className="btn-save" onClick={handleSaveSchedule}>저장하기</button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Schedule;
