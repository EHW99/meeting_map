package com.capstone.meetingmap.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//세션 로그인을 위한 유틸
public class SessionUtil {

    private static final String USER_ID = "loggedInUser";

    private SessionUtil() {} // 인스턴스 생성 방지

    //로그인 된 회원 id 세션에서 가져오기
    public static String getLoggedInUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession session = request.getSession(false);
        return (session != null) ? (String) session.getAttribute(USER_ID) : null;
    }

    //회원 id를 세션에 저장
    public static void setLoggedInUserId(String userId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        session.setAttribute(USER_ID, userId);
    }

    //로그아웃 등으로 인한 세션 초기화
    public static void clearSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
