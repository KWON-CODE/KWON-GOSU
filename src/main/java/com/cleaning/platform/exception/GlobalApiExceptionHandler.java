package com.cleaning.platform.exception;

import com.cleaning.platform.controller.ApiRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(annotations = ApiRestController.class)
public class GlobalApiExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        ex.printStackTrace();
        Map<String, String> errorBody = Map.of(
                "error", "NotAcceptable",
                "message", "서버가 클라이언트가 요청한 미디어 타입으로 응답할 수 없습니다. Accept 헤더를 확인하세요."
        );
        return new ResponseEntity<>(errorBody, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        Map<String, String> errorBody = Map.of(
                "error", ex.getClass().getSimpleName(),
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}