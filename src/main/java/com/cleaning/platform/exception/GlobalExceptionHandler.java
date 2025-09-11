package com.cleaning.platform.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice("com.cleaning.platform.controller")
public class GlobalExceptionHandler {


    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {

        model.addAttribute("errorMessage", e.getMessage());

        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", "예기치 않은 오류가 발생했습니다.");
        return "error";
    }
}