import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import { useAppContext } from '../AppContext'; // AppContext import
import './PostUpdate.css';

const API_BASE = 'http://localhost:8080/api';

const PostUpdate = () => {
  const { boardNo } = useParams();
  const { user } = useAppContext();
  const navigate = useNavigate();

  const [boardTitle, setBoardTitle] = useState('');
  const [boardDescription, setBoardDescription] = useState('');
  const [boardContent, setBoardContent] = useState('');
  const [categoryNo, setCategoryNo] = useState(0);  // 카테고리 기본값
  const [files, setFiles] = useState([]);  // 파일 상태 추가
  const [selectedImages, setSelectedImages] = useState([]);

  const fetchPostDetail = async () => {
    try {
      const res = await axios.get(`${API_BASE}/boards/${boardNo}`);
      const { boardTitle, boardDescription, boardContent, categoryNo } = res.data;
      setBoardTitle(boardTitle);
      setBoardDescription(boardDescription);
      setBoardContent(boardContent);
      setCategoryNo(categoryNo);
    } catch (err) {
      console.error('게시글 조회 실패:', err);
      alert('게시글을 불러오는 데 실패했습니다.');
    }
  };

  useEffect(() => {
    fetchPostDetail();
  }, [boardNo]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!user) {
      alert('로그인 후 게시글을 수정할 수 있습니다.');
      return;
    }

    // FormData 객체 생성
    const formData = new FormData();

    // 게시글 데이터를 FormData에 추가
    const postData = {
      boardTitle,
      boardDescription,
      boardContent,
      categoryNo,
    };

    // JSON 데이터를 Blob으로 만들어서 추가
    formData.append(
      "json",
      new Blob([JSON.stringify(postData)], { type: "application/json" })
    );

    // 여러 파일 모두 formData에 추가
    files.forEach((file) => {
      formData.append("files", file);
    });

    // 폼 데이터를 서버로 보내기
    try {
      await axios.put(`${API_BASE}/boards/${boardNo}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data", // 반드시 multipart/form-data로 설정
        },
        withCredentials: true, // 쿠키 전달
      });

      alert('게시글 수정이 완료되었습니다.');
      navigate(`/boards/${boardNo}`); // 수정된 게시글 상세 페이지로 이동
    } catch (err) {
      console.error('게시글 수정 실패:', err);
      alert('게시글 수정에 실패했습니다.');
    }
  };

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    const imagePreviews = files.map((file) => ({
      file,
      preview: URL.createObjectURL(file),
    }));
    setSelectedImages(imagePreviews);
    setFiles(files);
  };

  return (
    <div className="post-update-page">
      <form onSubmit={handleSubmit} className="post-update-form">
        <div className="board-write-top">
          <button type="button" onClick={() => navigate(-1)} className="back-button">뒤로가기</button>
          <h2 className="board-write-top-h2">게시글 수정</h2>
        </div>

        <div className="form-group">
          <label>제목</label>
          <input
            type="text"
            value={boardTitle}
            onChange={(e) => setBoardTitle(e.target.value)}
            required
          />
        </div>

        <label className="board-write-label">카테고리</label>
        <select value={categoryNo} onChange={(e) => setCategoryNo(Number(e.target.value))} disabled>
          <option value={0}>공지사항</option>
          <option value={1}>Q&A</option>
          <option value={2}>자유게시판</option>
        </select>

        <div className="form-group">
          <label>설명</label>
          <input
            type="text"
            value={boardDescription}
            onChange={(e) => setBoardDescription(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label>내용</label>
          <textarea
            value={boardContent}
            onChange={(e) => setBoardContent(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label>이미지</label>
          <label htmlFor="image-upload" className="custom-file-upload" style={{ color: "white" }}>
            이미지 선택(파일명은 50자 이내여야 합니다.)
          </label>
          <input
            id="image-upload"
            type="file"
            accept="image/*"
            multiple
            onChange={handleFileChange}
            style={{ display: "none" }}
          />
          <div className="image-preview-container">
            {selectedImages.map((img, index) => (
              <img
                key={index}
                src={img.preview}
                alt={`preview-${index}`}
                className="image-preview"
              />
            ))}
          </div>
        </div>

        <div className="form-group">
          <button type="submit">게시글 수정</button>
        </div>
      </form>
    </div>
  );
};

export default PostUpdate;