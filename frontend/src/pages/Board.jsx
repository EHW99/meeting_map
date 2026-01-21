import React, { useState, useEffect } from 'react';
import axios from 'axios';
import DOMPurify from 'dompurify';
import './Board.css';
import { useAppContext } from '../AppContext';
import { useNavigate } from 'react-router-dom';
import { API_BASE_URL } from '../constants';

const categories = [
  { no: null, name: '전체', icon: '📋' },
  { no: 0, name: '공지사항', icon: '📢' },
  { no: 1, name: 'Q&A', icon: '❓' },
  { no: 2, name: '자유게시판', icon: '💬' }
];

const Board = () => {
  const [posts, setPosts] = useState([]);
  const [popularPosts, setPopularPosts] = useState([]);
  const [categoryNo, setCategoryNo] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(12);
  const [totalPages, setTotalPages] = useState(1);
  const { user } = useAppContext();
  const navigate = useNavigate();

  const fetchPosts = async () => {
    try {
      const params = {
        page,
        size,
        sortBy: 'boardWriteDate',
        direction: 'desc',
      };
      if (categoryNo !== null) params.category = categoryNo;
      if (searchQuery.trim()) params.keyword = searchQuery.trim();

      const res = await axios.get(`${API_BASE_URL}/boards`, { params });
      setPosts(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error('게시글 조회 실패:', err);
    }
  };

  const fetchPopularPosts = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/boards`, {
        params: {
          page: 0,
          size: 5,
          sortBy: 'boardLike',
          direction: 'desc',
        },
      });
      setPopularPosts(res.data.content);
    } catch (err) {
      console.error('인기글 조회 실패:', err);
    }
  };

  const handleCategoryChange = (newCategoryNo) => {
    setCategoryNo(newCategoryNo);
    setSearchQuery('');
    setPage(0);
  };

  const handleSearch = () => {
    setPage(0);
    fetchPosts();
  };

  useEffect(() => {
    fetchPosts();
  }, [categoryNo, page]);

  useEffect(() => {
    fetchPopularPosts();
  }, []);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = now - date;
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) {
      const diffHours = Math.floor(diffTime / (1000 * 60 * 60));
      if (diffHours === 0) {
        const diffMins = Math.floor(diffTime / (1000 * 60));
        return `${diffMins}분 전`;
      }
      return `${diffHours}시간 전`;
    } else if (diffDays < 7) {
      return `${diffDays}일 전`;
    }
    return date.toLocaleDateString('ko-KR');
  };

  const getCategoryLabel = (catNo) => {
    const cat = categories.find(c => c.no === catNo);
    return cat ? cat.name : '';
  };

  return (
    <div className="board-page">
      {/* Header Section */}
      <div className="board-header">
        <div className="board-header-content">
          <h1 className="board-title-main">게시판</h1>
          <p className="board-subtitle">MeetingMap 커뮤니티에서 자유롭게 소통하세요</p>
        </div>
      </div>

      {/* Main Container */}
      <div className="board-container">
        {/* Top Bar: Tabs + Search */}
        <div className="board-top-bar">
          <div className="category-tabs">
            {categories.map((cat) => (
              <button
                key={cat.name}
                className={`category-tab ${categoryNo === cat.no ? 'active' : ''}`}
                onClick={() => handleCategoryChange(cat.no)}
              >
                <span className="tab-icon">{cat.icon}</span>
                <span className="tab-name">{cat.name}</span>
              </button>
            ))}
          </div>

          <div className="search-box">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="11" cy="11" r="8"/>
              <path d="M21 21l-4.35-4.35"/>
            </svg>
            <input
              type="text"
              placeholder="검색어를 입력하세요"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
            />
          </div>
        </div>

        {/* Popular Posts Highlight */}
        {popularPosts.length > 0 && categoryNo === null && (
          <div className="popular-section">
            <h2 className="section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
              </svg>
              인기 게시글
            </h2>
            <div className="popular-list">
              {popularPosts.slice(0, 5).map((post, index) => (
                <div
                  key={post.boardNo}
                  className="popular-item"
                  onClick={() => navigate(`/boards/${post.boardNo}`)}
                >
                  <span className="popular-rank">{index + 1}</span>
                  <span className="popular-title">{DOMPurify.sanitize(post.boardTitle)}</span>
                  <span className="popular-likes">❤️ {post.boardLike}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Posts Grid */}
        <div className="posts-grid">
          {posts.map((post) => (
            <article
              key={post.boardNo}
              className="post-card"
              onClick={() => navigate(`/boards/${post.boardNo}`)}
            >
              <div className="card-thumbnail">
                {post.thumbnailUrl ? (
                  <img src={post.thumbnailUrl} alt={post.boardTitle} />
                ) : (
                  <div className="no-thumbnail">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                      <circle cx="8.5" cy="8.5" r="1.5"/>
                      <polyline points="21,15 16,10 5,21"/>
                    </svg>
                  </div>
                )}
                {post.categoryNo !== undefined && (
                  <span className="card-category">{getCategoryLabel(post.categoryNo)}</span>
                )}
              </div>

              <div className="card-content">
                <h3 className="card-title">
                  {DOMPurify.sanitize(post.boardTitle)}
                  {post.commentCount > 0 && (
                    <span className="comment-count">[{post.commentCount}]</span>
                  )}
                </h3>
                <p className="card-description">{DOMPurify.sanitize(post.boardDescription)}</p>
                <div className="card-meta">
                  <div className="meta-author">
                    <div className="author-avatar">
                      {post.userNick?.charAt(0) || 'U'}
                    </div>
                    <span>{DOMPurify.sanitize(post.userNick)}</span>
                  </div>
                  <span className="meta-date">{formatDate(post.boardWriteDate)}</span>
                </div>
              </div>
            </article>
          ))}
        </div>

        {/* Empty State */}
        {posts.length === 0 && (
          <div className="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
              <polyline points="14,2 14,8 20,8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <polyline points="10,9 9,9 8,9"/>
            </svg>
            <p>게시글이 없습니다</p>
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="pagination">
            <button
              className="page-btn prev"
              onClick={() => setPage(page - 1)}
              disabled={page === 0}
            >
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="15 18 9 12 15 6"/>
              </svg>
            </button>

            <div className="page-numbers">
              {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                let pageNum;
                if (totalPages <= 5) {
                  pageNum = i;
                } else if (page < 3) {
                  pageNum = i;
                } else if (page > totalPages - 4) {
                  pageNum = totalPages - 5 + i;
                } else {
                  pageNum = page - 2 + i;
                }
                return (
                  <button
                    key={pageNum}
                    className={`page-num ${page === pageNum ? 'active' : ''}`}
                    onClick={() => setPage(pageNum)}
                  >
                    {pageNum + 1}
                  </button>
                );
              })}
            </div>

            <button
              className="page-btn next"
              onClick={() => setPage(page + 1)}
              disabled={page === totalPages - 1}
            >
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </button>
          </div>
        )}
      </div>

      {/* FAB - Write Post Button */}
      <button className="fab-write" onClick={() => navigate('/write')}>
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/>
          <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>
        </svg>
        <span>글쓰기</span>
      </button>
    </div>
  );
};

export default Board;
