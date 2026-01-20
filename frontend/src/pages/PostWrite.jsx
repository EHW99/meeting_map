import React, { useState } from 'react';
import axios from 'axios';
import { useAppContext } from '../AppContext';
import { useNavigate } from 'react-router-dom';
import './PostWrite.css';

const API_BASE = 'http://localhost:8080/api';

const PostWrite = () => {
  const { user } = useAppContext();
  const navigate = useNavigate();

  const [boardTitle, setBoardTitle] = useState('');
  const [boardDescription, setBoardDescription] = useState('');
  const [boardContent, setBoardContent] = useState('');
  const [categoryNo, setCategoryNo] = useState(0);  // 카테고리 기본값을 설정
  const [files, setFiles] = useState([]);  // 파일 상태 추가
  const [selectedImages, setSelectedImages] = useState([]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!user) {
      alert('로그인 후 게시글을 작성할 수 있습니다.');
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
      await axios.post(`${API_BASE}/boards`, formData, {
        headers: {
          "Content-Type": "multipart/form-data", // 반드시 multipart/form-data로 설정
        },
        withCredentials: true, // 쿠키 전달
      });

      alert('게시글 작성이 완료되었습니다.');
      navigate('/board'); // 게시글 목록 페이지로 이동
    } catch (err) {
      console.error('게시글 작성 실패:', err);
      alert('게시글 작성에 실패했습니다.');
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
    <div className="post-write-page">


      <form onSubmit={handleSubmit} className="post-write-form">
        <div className='board-write-top'>
          <button onClick={() => navigate(-1)} className="back-button">뒤로가기</button>
          <h2 className='board-write-top-h2'>게시글 작성</h2>
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

        <label className='board-write-label'>카테고리</label>
        <select value={categoryNo} onChange={(e) => setCategoryNo(Number(e.target.value))}>
          {user?.userType === 0 ? (
            // 어드민일 경우 공지사항, Q&A, 자유게시판 모두 보이기
            <>
              <option value={0}>공지사항</option>
              <option value={1}>Q&A</option>
              <option value={2}>자유게시판</option>
            </>
          ) : (
            // 유저일 경우 자유게시판, Q&A만 보이기
            <>
              <option value={1}>Q&A</option>
              <option value={2}>자유게시판</option>
            </>
          )}
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

        </div>

        <div className="form-group">
          <button type="submit" className='board-write-all-button'>게시글 작성</button>
        </div>
      </form>
    </div>
  );
};

export default PostWrite;
