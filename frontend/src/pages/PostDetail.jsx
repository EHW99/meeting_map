import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import { FaArrowLeft, FaThumbsUp, FaThumbsDown } from 'react-icons/fa';
import './PostDetail.css';
import { useAppContext } from '../AppContext'; // AppContext import

const API_BASE = 'http://localhost:8080/api';


const PostDetail = () => {
    const [selectedPost, setSelectedPost] = useState(null);
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    const { boardNo } = useParams();
    const navigate = useNavigate();
    const { user } = useAppContext();  // AppContext에서 user 상태 가져오기
    const [editingCommentId, setEditingCommentId] = useState(null); // 수정할 댓글 ID
    const [editingContent, setEditingContent] = useState(''); // 수정할 댓글 내용

    // 게시글과 댓글을 동시에 불러오는 함수
    const fetchPostDetail = async () => {
        try {
            // 게시글 상세 조회
            const res = await axios.get(`${API_BASE}/boards/${boardNo}`);
            setSelectedPost(res.data);

            // 댓글 조회
            const commentRes = await axios.get(`${API_BASE}/boards/${boardNo}/comments`);
            // 댓글에 대해 '시간 차이'만 계산해서 저장
            const commentsWithTimeAgo = commentRes.data.map((comment) => ({
                ...comment,
                timeAgo: timeAgo(comment.commentWriteDate)
            }));
            setComments(commentsWithTimeAgo || []);
        } catch (err) {
            console.error('게시글 또는 댓글 조회 실패:', err);
        }
    };

    // 시간 계산 함수 (timeAgo)
    const timeAgo = (dateString) => {
        const now = new Date();
        const date = new Date(dateString);
        const diffInSeconds = Math.ceil((now - date) / 1000); // ceil을 사용하여 항상 1초 이상 차이로 처리

        const seconds = diffInSeconds;
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);
        const months = Math.floor(days / 30);  // 평균적인 계산
        const years = Math.floor(days / 365);

        if (seconds < 60) {
            return `${seconds}초 전`;
        } else if (minutes < 60) {
            return `${minutes}분 전`;
        } else if (hours < 24) {
            return `${hours}시간 전`;
        } else if (days < 30) {
            return `${days}일 전`;
        } else if (months < 12) {
            return `${months}달 전`;
        } else {
            return `${years}년 전`;
        }
    };
    // 좋아요 버튼 클릭
    const toggleLike = async () => {
        if (!user) {
            alert('좋아요를 누르려면 로그인해야 합니다.');
            return;
        }
        try {
            // 인증된 요청을 보내기 위해 withCredentials 추가
            const res = await axios.post(`${API_BASE}/boards/${boardNo}/like`, {}, { withCredentials: true });
            fetchPostDetail(); // 좋아요 후 게시글 새로고침
        } catch (err) {
            console.error('좋아요 실패:', err);
        }
    };

    // 싫어요 버튼 클릭
    const toggleHate = async () => {
        if (!user) {
            alert('싫어요를 누르려면 로그인해야 합니다.');
            return;
        }
        try {
            // 인증된 요청을 보내기 위해 withCredentials 추가
            const res = await axios.post(`${API_BASE}/boards/${boardNo}/hate`, {}, { withCredentials: true });
            fetchPostDetail(); // 싫어요 후 게시글 새로고침
        } catch (err) {
            console.error('싫어요 실패:', err);
        }
    };

    // 댓글 작성
    const postComment = async () => {
        if (!user) {
            alert('댓글 작성은 로그인 후 가능합니다.');
            return;
        }
        if (!newComment.trim()) return;
        try {
            // 인증된 요청을 보내기 위해 withCredentials 추가
            await axios.post(`${API_BASE}/boards/${boardNo}/comments`, { commentContent: newComment }, { withCredentials: true });
            setNewComment('');
            fetchPostDetail(); // 댓글 작성 후 게시글과 댓글 새로고침
        } catch (err) {
            console.error('댓글 작성 실패:', err);
        }
    };

    // 댓글 수정
    const updateComment = async (commentNo) => {
        console.log('수정할 댓글:', editingContent);  // 로그 추가
        try {
            await axios.put(`${API_BASE}/comments/${commentNo}`, { commentContent: editingContent }, { withCredentials: true });
            setEditingCommentId(null);
            setEditingContent('');
            fetchPostDetail(); // 수정 후 게시글과 댓글 새로고침
        } catch (err) {
            console.error('댓글 수정 실패:', err);
        }
    };

    // 댓글 삭제
    const deleteComment = async (commentNo) => {
        const confirmed = window.confirm('정말로 이 댓글을 삭제하시겠습니까?');
        if (!confirmed) return;

        try {
            // withCredentials 옵션 추가
            await axios.delete(`${API_BASE}/comments/${commentNo}`, { withCredentials: true });
            fetchPostDetail(); // 삭제 후 게시글과 댓글 새로고침
        } catch (err) {
            console.error('댓글 삭제 실패:', err);
        }
    };

    // 초기 데이터 로드
    useEffect(() => {
        fetchPostDetail();
    }, [boardNo]);

    if (!selectedPost) {
        return <div>로딩 중...</div>;  // 데이터가 없을 경우 로딩 화면
    }

    // 게시글 삭제
    const deletePost = async (boardNo) => {
        if (window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
            try {
                const res = await axios.delete(`${API_BASE}/boards/${boardNo}`, { withCredentials: true });
                alert('게시글이 삭제되었습니다.');
                navigate('/board'); // 게시판 목록으로 이동
            } catch (err) {
                console.error('게시글 삭제 실패:', err);
                alert('삭제 실패');
            }
        }
    };

    // 게시글 수정
    const handleEditClick = () => {
        if (selectedPost.userId === user.userId) {
            navigate(`/edit/${selectedPost.boardNo}`);
        } else {
            alert('본인 게시글만 수정할 수 있습니다.');
        }
    };

    return (
        <div className="post-detail-page">
            <button onClick={() => navigate('/board')} className='board-content-back-button'><FaArrowLeft /> 뒤로가기</button>

            <h2 className='board-detail-title'>{selectedPost.boardTitle}</h2>

            <div className='board-content'>
                {/* 이미지 출력 영역 */}
                {selectedPost.boardFiles && selectedPost.boardFiles.length > 0 && (
                    <div className="board-images">
                        {selectedPost.boardFiles.map((file, index) => (
                            <img
                                key={index}
                                src={file.fileUrl}
                                alt={`첨부 이미지 ${index + 1}`}
                                className="board-image"
                            />
                        ))}
                    </div>)}

                {selectedPost.boardContent}
            </div>
            <p>설명 : {selectedPost.boardDescription}</p>
            <div className='board-content-bottom'>
                <p className='board-content-writer'>글쓴이 : {selectedPost.userNick}</p>
                <p>{new Date(selectedPost.boardWriteDate).toLocaleString()}</p>
            </div>


            <div className="like-hate-buttons">
                <button onClick={toggleLike} className='board-like-button'><FaThumbsUp /> 좋아요 ({selectedPost.boardLike})</button>
                <button onClick={toggleHate} className='board-hate-button'><FaThumbsDown /> 싫어요 ({selectedPost.boardHate})</button>
            </div>

            {user?.userId === selectedPost.userId && ( // 로그인한 사용자와 게시글 작성자 일치
                <div className="edit-delete-buttons">
                    <button onClick={() => navigate(`/edit/${selectedPost.boardNo}`)} className="edit-button">수정</button>
                    <button onClick={() => deletePost(selectedPost.boardNo)} className="delete-button">삭제</button>
                </div>
            )}

            <div className="comment-section">
                <textarea
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="댓글을 입력하세요"
                />
                <button onClick={postComment} className='board-comment-write-button'>댓글 등록</button>

                {comments.length > 0 ? (
                    comments.map((comment) => (
                        <div key={comment.commentNo} className="comment-item">
                            <p><strong>{comment.userNick}</strong> · {comment.commentContent}</p>
                            <p className="comment-date">
                                {comment.timeAgo} {/* 이미 계산된 timeAgo 사용 */}
                            </p>
                            {editingCommentId === comment.commentNo ? (
                                <>
                                    <textarea
                                        className='board-content-textarea'
                                        value={editingContent}
                                        onChange={(e) => setEditingContent(e.target.value)}
                                    />
                                    <button onClick={() => updateComment(comment.commentNo)}>수정 완료</button>
                                    <button onClick={() => setEditingCommentId(null)}>취소</button>
                                </>
                            ) : (
                                <>
                                    {user?.userId === comment.userId && (
                                        <>
                                            <button className='board-comment-area-button' onClick={() => {
                                                setEditingCommentId(comment.commentNo);
                                                setEditingContent(comment.commentContent);
                                            }}>수정</button>
                                            <button className='board-comment-area-button' onClick={() => deleteComment(comment.commentNo)}>삭제</button>
                                        </>
                                    )}
                                </>
                            )}
                        </div>
                    ))
                ) : (
                    <p>댓글이 없습니다.</p>
                )}
            </div>
        </div>
    );
};

export default PostDetail;
