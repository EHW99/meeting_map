import React, { useState } from 'react';
import './CategorySidebar.css';

const CategorySidebar = ({ places = [], onClose, onAddPlace, addedList = [], className = '' }) => {
  const [isOpen, setIsOpen] = useState(true); // ✅ 상태 정의
  const toggleSidebar = () => setIsOpen(prev => !prev); // ✅ 토글 함수 정의

  return (
    <div className={`category-sidebar ${className}`}>
      <div className="category-header">
        <h3>카테고리</h3>
        <button className="close-button" onClick={toggleSidebar}>
          {isOpen ? '닫기' : '열기'}
        </button>
      </div>

      {isOpen && (
        <div className="place-list">
          {places.length === 0 ? (
            <p>장소 정보를 불러오는 중입니다...</p>
          ) : (
            places.map((place, index) => (
              <div key={index} className="place-item">
                <div className="place-thumbnail-container">
                  {/* ✅ imageUrl 우선 사용, 없으면 thumbnail 사용 */}
                  {place.imageUrl || place.thumbnail ? (
                    <img
                      src={place.imageUrl || place.thumbnail}
                      alt={place.name}
                      className="place-thumbnail"
                    />
                  ) : (
                    <span className="no-image-text">이미지 없음</span>
                  )}
                </div>
                <div className="place-content">
                  <h4 className="place-title">{place.name}</h4>
                  <p className="place-desc">{place.address || '상세 주소 없음'}</p>
                  <button
                    disabled={addedList.some(p => p.name === place.name) || addedList.length >= 8}
                    onClick={() => onAddPlace(place)}
                  >
                    추가
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
};

export default CategorySidebar;