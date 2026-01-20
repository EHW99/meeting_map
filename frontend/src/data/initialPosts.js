const initialPosts = [
  // 맛집 추천
  { id: 1, category: '맛집 추천', title: '홍대 맛집 투어', author: 'user1', createdAt: '2024-05-01', likes: 10, views: 120, comments: [], images: ['/images/food1.jpg'] },
  { id: 2, category: '맛집 추천', title: '이태원 핫플 맛집', author: 'user2', createdAt: '2024-05-02', likes: 15, views: 90, comments: [], images: ['/images/food2.jpg'] },
  { id: 3, category: '맛집 추천', title: '강남 최고의 스시집', author: 'user3', createdAt: '2024-05-03', likes: 7, views: 60, comments: [], images: ['/images/food3.jpg'] },
  { id: 4, category: '맛집 추천', title: '신촌 떡볶이 골목', author: 'user4', createdAt: '2024-05-04', likes: 20, views: 110, comments: [], images: ['/images/food4.jpg'] },
  { id: 5, category: '맛집 추천', title: '건대 맛집 로드', author: 'user5', createdAt: '2024-05-05', likes: 8, views: 80, comments: [], images: ['/images/food5.jpg'] },
  { id: 6, category: '맛집 추천', title: '서촌 맛집 탐방', author: 'user6', createdAt: '2024-05-06', likes: 12, views: 100, comments: [], images: ['/images/food6.jpg'] },

  // 카페 추천
  { id: 7, category: '카페 추천', title: '서울숲 감성 카페', author: 'user7', createdAt: '2024-05-01', likes: 9, views: 80, comments: [], images: ['/images/cafe1.jpg'] },
  { id: 8, category: '카페 추천', title: '홍대 루프탑 카페', author: 'user8', createdAt: '2024-05-02', likes: 13, views: 70, comments: [], images: ['/images/cafe2.jpg'] },
  { id: 9, category: '카페 추천', title: '압구정 감성 카페', author: 'user9', createdAt: '2024-05-03', likes: 6, views: 50, comments: [], images: ['/images/cafe3.jpg'] },
  { id: 10, category: '카페 추천', title: '성수동 핫플 카페', author: 'user10', createdAt: '2024-05-04', likes: 18, views: 90, comments: [], images: ['/images/cafe4.jpg'] },
  { id: 11, category: '카페 추천', title: '망원동 카페거리', author: 'user11', createdAt: '2024-05-05', likes: 7, views: 65, comments: [], images: ['/images/cafe5.jpg'] },
  { id: 12, category: '카페 추천', title: '가로수길 카페 투어', author: 'user12', createdAt: '2024-05-06', likes: 11, views: 75, comments: [], images: ['/images/cafe6.jpg'] },

  // 놀거리 추천
  { id: 13, category: '문화·여가 추천', title: '서울대공원 나들이', author: 'user13', createdAt: '2024-05-01', likes: 5, views: 100, comments: [], images: ['/images/play1.jpg'] },
  { id: 14, category: '문화·여가 추천', title: '롯데월드 어드벤처', author: 'user14', createdAt: '2024-05-02', likes: 11, views: 95, comments: [], images: ['/images/play2.jpg'] },
  { id: 15, category: '문화·여가 추천', title: '한강 자전거 여행', author: 'user15', createdAt: '2024-05-03', likes: 7, views: 70, comments: [], images: ['/images/play3.jpg'] },
  { id: 16, category: '문화·여가 추천', title: '잠실 롯데타워 전망대', author: 'user16', createdAt: '2024-05-04', likes: 14, views: 85, comments: [], images: ['/images/play4.jpg'] },
  { id: 17, category: '문화·여가 추천', title: '인사동 전통 거리', author: 'user17', createdAt: '2024-05-05', likes: 6, views: 60, comments: [], images: ['/images/play5.jpg'] },
  { id: 18, category: '문화·여가 추천', title: '명동 쇼핑거리', author: 'user18', createdAt: '2024-05-06', likes: 10, views: 80, comments: [], images: ['/images/play6.jpg'] },

  // 일정·코스 추천
  { id: 19, category: '일정 경험 추천', title: '서울 당일치기 코스', author: 'user19', createdAt: '2024-05-01', likes: 8, views: 60, comments: [], images: ['/images/course1.jpg'] },
  { id: 20, category: '일정 경험 추천', title: '주말 1박2일 코스', author: 'user20', createdAt: '2024-05-02', likes: 10, views: 80, comments: [], images: ['/images/course2.jpg'] },
  { id: 21, category: '일정 경험 추천', title: '서울 야경 명소 코스', author: 'user21', createdAt: '2024-05-03', likes: 12, views: 90, comments: [], images: ['/images/course3.jpg'] },
  { id: 22, category: '일정 경험 추천', title: '연인 핫플 데이트 코스', author: 'user22', createdAt: '2024-05-04', likes: 15, views: 100, comments: [], images: ['/images/course4.jpg'] },
  { id: 23, category: '일정 경험 추천', title: '서울 벚꽃 명소 코스', author: 'user23', createdAt: '2024-05-05', likes: 9, views: 70, comments: [], images: ['/images/course5.jpg'] },
  { id: 24, category: '일정 경험 추천', title: '힐링 산책 코스', author: 'user24', createdAt: '2024-05-06', likes: 11, views: 85, comments: [], images: ['/images/course6.jpg'] },
];

export default initialPosts;
