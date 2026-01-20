const dummyPosts = [
  // 맛집
  ...Array.from({ length: 30 }, (_, i) => ({
    id: `food-${i + 1}`,
    category: "맛집",
    title: `홍대에서 꼭 가봐야 할 맛집 TOP ${i + 1}`,
    description: `현지인 추천! 줄 서서 먹는 인기 맛집 ${i + 1}`,
    writer: `미식가${i + 1}`,
    time: `${Math.floor(Math.random() * 24) + 1}시간 전`,
    content: `분위기 좋고 음식 맛있는 곳 찾고 있다면 여기! 가격도 괜찮고 재방문 의사 있습니다. (${i + 1})`,
    image: `food${(i % 5) + 1}.jpg`,
  })),

  // 카페
  ...Array.from({ length: 30 }, (_, i) => ({
    id: `cafe-${i + 1}`,
    category: "카페",
    title: `인스타 감성 뿜뿜 카페 추천 ${i + 1}`,
    description: `감성 충전 가능한 예쁜 카페 리스트 ${i + 1}`,
    writer: `카페러버${i + 1}`,
    time: `${Math.floor(Math.random() * 24) + 1}시간 전`,
    content: `조용하고 분위기 좋아서 공부나 수다 떨기 딱 좋아요. 디저트도 훌륭하고 커피 맛도 일품! (${i + 1})`,
    image: `cafe${(i % 5) + 1}.jpg`,
  })),

  // 놀거리
  ...Array.from({ length: 30 }, (_, i) => ({
    id: `play-${i + 1}`,
    category: "놀거리",
    title: `서울 근교 당일치기 놀거리 추천 ${i + 1}`,
    description: `주말 나들이 코스로 딱 좋은 핫플 모음 ${i + 1}`,
    writer: `플레이어${i + 1}`,
    time: `${Math.floor(Math.random() * 24) + 1}시간 전`,
    content: `친구들이랑 가면 진짜 재밌어요! 가족, 연인과 가도 좋고 스트레스 확 풀리는 명소예요. (${i + 1})`,
    image: `play${(i % 5) + 1}.jpg`,
  })),

  // 일정/코스
  ...Array.from({ length: 30 }, (_, i) => ({
    id: `course-${i + 1}`,
    category: "일정/코스",
    title: `데이트에 딱! 알차게 짠 하루 코스 ${i + 1}`,
    description: `가성비 최고 여행 코스 소개합니다 ${i + 1}`,
    writer: `여행러${i + 1}`,
    time: `${Math.floor(Math.random() * 24) + 1}시간 전`,
    content: `서울 도심 속에서 즐기는 반나절 코스! 먹고, 놀고, 쉴 수 있는 완벽한 루트예요. (${i + 1})`,
    image: `course${(i % 5) + 1}.jpg`,
  })),
];

export default dummyPosts;
