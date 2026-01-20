package com.capstone.meetingmap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    //ResponseStatusException을 처리하는 핸들러
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CustomErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }

    //IllegalStateException을 처리하는 핸들러 (회원가입 중 중복 사용자 처리, 409 Conflict)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalStateException(IllegalStateException e) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // MethodArgumentNotValidException을 처리하는 앤들러 (@Valid 실패 시 발생하는 예외)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        if (e.hasErrors()) {
            errorResponse.setMessage(e.getAllErrors().get(0).getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errorResponse);
    }

    //기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다: " + ex.getMessage());
    }
}
