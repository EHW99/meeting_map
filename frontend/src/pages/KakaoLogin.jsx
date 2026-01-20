import React, { useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAppContext } from "../AppContext"; // ✅ context 불러오기
import { useLocation } from "react-router-dom";
import { API_BASE_URL } from "../constants.js";

export default function KakaoLogin() {
  const navigate = useNavigate();
  const location = useLocation();

  const { setUser } = useAppContext(); // ✅ context에 로그인 정보 저장할 함수

  useEffect(() => {
    // URLSearchParams로 쿼리 파라미터 추출
    const queryParams = new URLSearchParams(location.search);
    const code = queryParams.get("code"); // 'code' 파라미터 추출
    const state = queryParams.get("state") || "/";  // state 파라미터 받기, 없으면 "/"로 기본 지정

    if (code) {
      // 카카오 코드 서버로 전송
      axios
              .get(`${API_BASE_URL}/auth/kakao?code=${code}`, {
                withCredentials: true,
              })
              .then((response) => {
                axios
                        .get(`${API_BASE_URL}/user`, {
                          withCredentials: true,
                        })
                        .then((response2) => {
                          setUser(response2.data);
                          navigate(state);  // state 값으로 이동!
                        })
                        .catch((error) => {
                          console.error("사용자 조회에 실패했습니다.", error);
                        });
              })
              .catch((error) => {
                console.error("카카오 로그인 오류:", error);
              });
    }
  }, [location.search, navigate]);

  return (
          <div>
            <h1>카카오 로그인 처리 중...</h1>
          </div>
  );
}