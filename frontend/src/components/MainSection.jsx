import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './MainSection.css';
import dummyPosts from '../data/dummyPosts';
import { API_BASE_URL } from '../constants';

const images = [
  '/images/bg1.jpg',
  '/images/bg2.jpg',
  '/images/bg3.jpg',
  '/images/bg4.jpg'
];

const randomPlaces = [
  '남산타워', '경복궁', '홍대 걷고 싶은 거리', '롯데월드타워', '서울숲',
  '청계천', '이태원 거리', '코엑스 몰', '광장시장', '올림픽공원'
];

const placeBackgrounds = {
  '남산타워': '/images/placeBG/1.jpg',
  '경복궁': '/images/placeBG/2.jpg',
  '홍대 걷고 싶은 거리': '/images/placeBG/3.jpg',
  '롯데월드타워': '/images/placeBG/4.jpg',
  '서울숲': '/images/placeBG/5.jpg',
  '청계천': '/images/placeBG/6.jpg',
  '이태원 거리': '/images/placeBG/7.jpg',
  '코엑스 몰': '/images/placeBG/8.jpg',
  '광장시장': '/images/placeBG/9.jpg',
  '올림픽공원': '/images/placeBG/10.jpg'
};

export default function MainSection() {
  const [currentImage, setCurrentImage] = useState(0);
  const [mode, setMode] = useState('route');
  const [departures, setDepartures] = useState(['', '']);
  const [departure, setDeparture] = useState('');
  const [destination, setDestination] = useState('');
  const [randomPlace, setRandomPlace] = useState('');
  const [suggestions, setSuggestions] = useState({});
  const [focusedInput, setFocusedInput] = useState(null);
  const [topPosts, setTopPosts] = useState([]);
  const navigate = useNavigate();
  const timeoutRef = useRef(null);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentImage((prev) => (prev + 1) % images.length);
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const random = randomPlaces[Math.floor(Math.random() * randomPlaces.length)];
    setRandomPlace(random);
  }, []);

  useEffect(() => {
    const updatePosts = () => {
      const shuffled = [...dummyPosts].sort(() => 0.5 - Math.random());
      setTopPosts(shuffled.slice(0, 3));
    };
    updatePosts();
    const interval = setInterval(updatePosts, 5000);
    return () => clearInterval(interval);
  }, []);

  const fetchSuggestions = async (query, index = null) => {
    if (!query.trim()) return;
    try {
      const res = await axios.get(`${API_BASE_URL}/map/autocomplete?name=${query}`);
      const key = index !== null ? index : 'single';
      const data = res.data.slice(0, 10);
      setSuggestions((prev) => ({ ...prev, [key]: data }));
    } catch (err) {
      const key = index !== null ? index : 'single';
      setSuggestions((prev) => ({ ...prev, [key]: [] }));
    }
  };

  const handleDepartureChange = (index, value) => {
    const updated = [...departures];
    updated[index] = value;
    setDepartures(updated);
    setFocusedInput(index);
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    timeoutRef.current = setTimeout(() => fetchSuggestions(value, index), 300);
  };

  const handleSingleDepartureChange = (value) => {
    setDeparture(value);
    setFocusedInput('single');
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    timeoutRef.current = setTimeout(() => fetchSuggestions(value), 300);
  };

  const handleSelectSuggestion = (text, index = null) => {
    if (index !== null) {
      const updated = [...departures];
      updated[index] = text;
      setDepartures(updated);
      setSuggestions((prev) => ({ ...prev, [index]: [] }));
    } else {
      setDeparture(text);
      setSuggestions((prev) => ({ ...prev, single: [] }));
    }
  };

  const handleGetCurrentLocation = (index = null, isDestination = false) => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
        const { latitude, longitude } = position.coords;
        const geocoder = new window.kakao.maps.services.Geocoder();
        geocoder.coord2Address(longitude, latitude, (result, status) => {
          if (status === window.kakao.maps.services.Status.OK) {
            const address = result[0].road_address?.address_name || result[0].address.address_name;
            if (isDestination) setDestination(address);
            else if (index !== null) {
              const updated = [...departures];
              updated[index] = address;
              setDepartures(updated);
            } else setDeparture(address);
          }
        });
      });
    } else {
      alert('위치 정보를 가져올 수 없습니다.');
    }
  };

  const handleStart = () => {
    if (mode === 'midpoint') {
      const filtered = departures.filter((d) => d.trim() !== '');
      if (filtered.length < 2) return alert('출발지를 2개 이상 입력해야 합니다.');
      navigate(`/map?search=middle-point&sort=rating_dsc&${filtered.map((f) => (`name=${f}`)).join('&')}`);
    } else {
      if (!departure.trim() || !destination.trim()) return alert('출발지와 도착지를 모두 입력해주세요.');
      navigate(`/map?search=destination&sort=rating_dsc&start=${departure}&end=${destination}`);
    }
  };

  const addDepartureInput = () => {
    if (departures.length < 4) setDepartures([...departures, '']);
  };

  const removeDepartureInput = (index) => {
    if (index >= 2) {
      const updated = departures.filter((_, i) => i !== index);
      setDepartures(updated);
    }
  };

  const handleRandomPlaceClick = async () => {
    try {
      const res = await axios.get(
        `${API_BASE_URL}/map/autocomplete?name=${encodeURIComponent(randomPlace)}`
      );
      if (!res.data || res.data.length === 0) {
        return alert('장소 정보를 찾을 수 없습니다.');
      }
      const place = res.data[0];
      const imageUrl = placeBackgrounds[randomPlace];

      navigate('/map?search=random-place', {
        state: {
          fromRandomPlace: true,
          selectedPlace: {
            name: place.placeName,
            address: place.address_name || place.road_address_name,
            latitude: place.y,
            longitude: place.x,
            imageUrl
          }
        }
      });
    } catch (err) {
      console.error('장소 조회 실패:', err);
      alert('장소 정보를 불러오는 데 실패했습니다.');
    }
  };

  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="hero-section">
        {images.map((image, index) => (
          <div
            key={index}
            className={`hero-background ${index === currentImage ? 'visible' : ''}`}
            style={{ backgroundImage: `url(${image})` }}
          />
        ))}
        <div className="hero-overlay" />

        <div className="hero-content">
          <h1 className="hero-title">만남의 중심을 찾아보세요</h1>
          <p className="hero-subtitle">친구들과의 최적의 만남 장소를 쉽고 빠르게 찾아드립니다</p>

          <div className="search-container">
            {/* Mode Toggle */}
            <div className="mode-toggle">
              <button
                className={`mode-btn ${mode === 'route' ? 'active' : ''}`}
                onClick={() => setMode('route')}
              >
                경로 찾기
              </button>
              <button
                className={`mode-btn ${mode === 'midpoint' ? 'active' : ''}`}
                onClick={() => setMode('midpoint')}
              >
                중간지점 찾기
              </button>
            </div>

            {mode === 'midpoint' ? (
              <div className="search-form">
                {departures.map((value, index) => (
                  <div className="input-group" key={index}>
                    <div className="input-wrapper">
                      <input
                        type="text"
                        className="search-input"
                        placeholder={`출발지 ${index + 1}`}
                        value={value}
                        onChange={(e) => handleDepartureChange(index, e.target.value)}
                      />
                      <button
                        className="location-btn"
                        onClick={() => handleGetCurrentLocation(index)}
                        title="현재 위치"
                      >
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <circle cx="12" cy="12" r="10"/>
                          <circle cx="12" cy="12" r="3"/>
                          <line x1="12" y1="2" x2="12" y2="6"/>
                          <line x1="12" y1="18" x2="12" y2="22"/>
                          <line x1="2" y1="12" x2="6" y2="12"/>
                          <line x1="18" y1="12" x2="22" y2="12"/>
                        </svg>
                      </button>
                      {index >= 2 && (
                        <button className="remove-btn" onClick={() => removeDepartureInput(index)}>
                          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <line x1="18" y1="6" x2="6" y2="18"/>
                            <line x1="6" y1="6" x2="18" y2="18"/>
                          </svg>
                        </button>
                      )}
                    </div>
                    {suggestions[index] && suggestions[index].length > 0 && (
                      <ul className="suggestions-list">
                        {suggestions[index].map((s, i) => (
                          <li key={i} onClick={() => handleSelectSuggestion(s.placeName, index)}>
                            {s.placeName}
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                ))}
                <div className="search-actions">
                  {departures.length < 4 && (
                    <button className="add-btn" onClick={addDepartureInput}>
                      + 출발지 추가
                    </button>
                  )}
                  <button className="search-btn" onClick={handleStart}>
                    중간지점 찾기
                  </button>
                </div>
              </div>
            ) : (
              <div className="search-form">
                <div className="input-group">
                  <div className="input-wrapper">
                    <input
                      type="text"
                      className="search-input"
                      placeholder="출발지 입력"
                      value={departure}
                      onChange={(e) => handleSingleDepartureChange(e.target.value)}
                    />
                    <button
                      className="location-btn"
                      onClick={() => handleGetCurrentLocation()}
                      title="현재 위치"
                    >
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="12" cy="12" r="10"/>
                        <circle cx="12" cy="12" r="3"/>
                        <line x1="12" y1="2" x2="12" y2="6"/>
                        <line x1="12" y1="18" x2="12" y2="22"/>
                        <line x1="2" y1="12" x2="6" y2="12"/>
                        <line x1="18" y1="12" x2="22" y2="12"/>
                      </svg>
                    </button>
                  </div>
                  {suggestions.single && suggestions.single.length > 0 && (
                    <ul className="suggestions-list">
                      {suggestions.single.map((s, i) => (
                        <li key={i} onClick={() => handleSelectSuggestion(s.placeName)}>
                          {s.placeName}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
                <div className="input-group">
                  <div className="input-wrapper">
                    <input
                      type="text"
                      className="search-input"
                      placeholder="도착지 입력"
                      value={destination}
                      onChange={(e) => {
                        setDestination(e.target.value);
                        setFocusedInput('destination');
                        if (timeoutRef.current) clearTimeout(timeoutRef.current);
                        timeoutRef.current = setTimeout(() => fetchSuggestions(e.target.value, 'destination'), 300);
                      }}
                    />
                    <button
                      className="location-btn"
                      onClick={() => handleGetCurrentLocation(null, true)}
                      title="현재 위치"
                    >
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="12" cy="12" r="10"/>
                        <circle cx="12" cy="12" r="3"/>
                        <line x1="12" y1="2" x2="12" y2="6"/>
                        <line x1="12" y1="18" x2="12" y2="22"/>
                        <line x1="2" y1="12" x2="6" y2="12"/>
                        <line x1="18" y1="12" x2="22" y2="12"/>
                      </svg>
                    </button>
                  </div>
                  {suggestions.destination && suggestions.destination.length > 0 && (
                    <ul className="suggestions-list">
                      {suggestions.destination.map((s, i) => (
                        <li key={i} onClick={() => {
                          setDestination(s.placeName);
                          setSuggestions((prev) => ({ ...prev, destination: [] }));
                        }}>
                          {s.placeName}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
                <button className="search-btn" onClick={handleStart}>
                  경로 찾기
                </button>
              </div>
            )}
          </div>

          {/* Slider Dots */}
          <div className="hero-dots">
            {images.map((_, idx) => (
              <button
                key={idx}
                className={`dot ${idx === currentImage ? 'active' : ''}`}
                onClick={() => setCurrentImage(idx)}
                aria-label={`슬라이드 ${idx + 1}`}
              />
            ))}
          </div>
        </div>
      </section>

      {/* Recommendations Section */}
      <section className="recommendations-section">
        <div className="container">
          <div className="recommendations-grid">
            {/* Today's Recommendations */}
            <div className="recommendation-block">
              <h2 className="section-title">
                <span className="title-icon">#</span>
                오늘의 추천
              </h2>
              <div className="cards-row">
                {topPosts.map((post) => (
                  <article
                    key={post.id}
                    className="recommendation-card"
                    onClick={() => navigate(`/board?postId=${post.id}`)}
                  >
                    <div className="card-image">
                      <img src={`/images/${post.image}`} alt={post.title} />
                    </div>
                    <div className="card-body">
                      <h3 className="card-title">{post.title}</h3>
                      <p className="card-description">{post.description}</p>
                    </div>
                  </article>
                ))}
              </div>
            </div>

            {/* Random Place */}
            <div className="recommendation-block random-block">
              <h2 className="section-title">
                <span className="title-icon">#</span>
                랜덤 장소 추천
              </h2>
              <div
                className="random-place-card"
                onClick={handleRandomPlaceClick}
                style={{
                  backgroundImage: `url(${placeBackgrounds[randomPlace] || ''})`
                }}
              >
                <div className="random-place-overlay" />
                <div className="random-place-content">
                  <h3 className="random-place-name">{randomPlace}</h3>
                  <button
                    className="refresh-btn"
                    onClick={(e) => {
                      e.stopPropagation();
                      setRandomPlace(randomPlaces[Math.floor(Math.random() * randomPlaces.length)]);
                    }}
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M23 4v6h-6"/>
                      <path d="M1 20v-6h6"/>
                      <path d="M3.51 9a9 9 0 0114.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0020.49 15"/>
                    </svg>
                    다시 추천받기
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
