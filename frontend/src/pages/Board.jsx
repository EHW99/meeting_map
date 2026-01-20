import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { FaSearch } from 'react-icons/fa';
import './Board.css';
import { useAppContext } from '../AppContext';
import { useNavigate } from 'react-router-dom';
import { API_BASE_URL } from '../constants';

const Board = () => {
  const [posts, setPosts] = useState([]);
  const [popularPosts, setPopularPosts] = useState([]);
  const [selectedPost, setSelectedPost] = useState(null);
  const [categoryNo, setCategoryNo] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingContent, setEditingContent] = useState('');
  const { user } = useAppContext();
  const navigate = useNavigate();
  // 게시글 목록 조회
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

  // 인기 게시글 조회
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

  // 게시글 상세조회
  const fetchPostDetail = async (boardNo) => {
    try {
      console.log('boardNo:', boardNo);
      const res = await axios.get(`${API_BASE_URL}/boards/${boardNo}`);
      setSelectedPost(res.data);
      fetchComments(boardNo); // 댓글도 같이 조회
    } catch (err) {
      console.error('게시글 상세 조회 실패:', err);
    }
  };

  const handleCategoryChange = (newCategoryNo) => {
    setCategoryNo(newCategoryNo);
    setSelectedPost(null);
    setSearchQuery('');
    setPage(0);
  };

  const handleSearch = () => {
    setPage(0);
    setSelectedPost(null);
    fetchPosts();
  };

  useEffect(() => {
    fetchPosts();
  }, [categoryNo, page]);

  useEffect(() => {
    fetchPopularPosts();
  }, []);


  // 댓글 목록 조회
  const fetchComments = async (boardNo) => {
    try {
      const res = await axios.get(`${API_BASE_URL}/boards/${boardNo}/comments`, {
        withCredentials: true
      });
      setComments(res.data || []);
    } catch (err) {
      console.error('댓글 조회 실패:', err);
      setComments([]); // 오류 나도 안전하게 초기화
    }
  };





  return (
    <div className="board-page">


      <div className="board-left-menu">
        <h1 className="menu-title">BOARD</h1>
        <nav className="menu-buttons">
          <button onClick={() => handleCategoryChange(null)}>전체</button>
          <button onClick={() => handleCategoryChange(0)}>공지사항</button>
          <button onClick={() => handleCategoryChange(1)}>Q&A</button>
          <button onClick={() => handleCategoryChange(2)}>자유게시판</button>
        </nav>
      </div>

      <div className="main-content">
        <div className="search-bar">
          <input
            type="text"
            placeholder="검색어를 입력하세요"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          />
          <FaSearch className="search-icon" onClick={handleSearch} />
        </div>

        <div className="post-list">
          {posts.map((post) => (
            <div
              key={post.boardNo}
              className="post-item"
              onClick={() => navigate(`/boards/${post.boardNo}`)} // 상세 페이지로 이동
            >
              {/* 썸네일 이미지 추가 */}
              <div className="thumbnail-container">
                {post.thumbnailUrl ? (
                  <img
                    src={post.thumbnailUrl}
                    alt={post.boardTitle}
                    className="thumbnail-image"
                  />
                ) : (
                  <span className="no-thumbnail">이미지 없음</span> // 이미지 없을 경우 대체 텍스트
                )}
              </div>

              <div className="post-content">
                <div className='board-title-comment-count'>
                <h2 className="board-title">{post.boardTitle}</h2>
                <p className='board-title-comment-count-count'>({post.commentCount})</p>
                </div>

                <div className="board-title-bottom">
                  <p className="description">{post.boardDescription}</p>
                  <p className="timestamp">
                    {post.userNick} · {new Date(post.boardWriteDate).toLocaleDateString()}
                  </p>
                </div>
              </div>
            </div>
          ))}
        </div>

        {!selectedPost && (
          <div className="write-post-button">
            <button onClick={() => navigate('/write')}>게시글 작성</button>
          </div>
        )}

        <div className="pagination-buttons">
          {/* 왼쪽 (이전) 버튼 */}
          <button
            className={`page-nav ${page === 0 ? 'disabled' : ''}`}
            onClick={() => setPage(page - 1)}
            disabled={page === 0}
          >
            &lt; {/* 왼쪽 화살표 */}
          </button>

          {/* 페이지 번호 버튼들 */}
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={() => setPage(i)}
              className={page === i ? 'active-page' : ''}
            >
              {i + 1}
            </button>
          ))}

          {/* 오른쪽 (다음) 버튼 */}
          <button
            className={`page-nav ${page === totalPages - 1 ? 'disabled' : ''}`}
            onClick={() => setPage(page + 1)}
            disabled={page === totalPages - 1}
          >
            &gt; {/* 오른쪽 화살표 */}
          </button>
        </div>



      </div>
    </div>
  );
};

export default Board;
