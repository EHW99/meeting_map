import axios from "axios";
import { createContext, useContext, useEffect, useState } from "react";
import { API_BASE_URL } from "./constants";

const AppContext = createContext();

export function AppProvider({ children }) {
  const [user, setUser] = useState(undefined);

  // 새로고침 시 로그인 정보 복구
  useEffect(() => {
    axios
      .get(`${API_BASE_URL}/user`, { withCredentials: true })
      .then((response) => {
        setUser(response.data); // 세션이 유효하면 유저 정보 저장
      })
      .catch(() => {
        setUser(null); // 세션 만료 시 로그아웃 상태
      });
  }, []);

  return (
    <AppContext.Provider value={{ user, setUser }}>
      {children}
    </AppContext.Provider>
  );
}

// 어디서든 이걸로 user, setUser 꺼내쓸 수 있음
export function useAppContext() {
  return useContext(AppContext);
}
