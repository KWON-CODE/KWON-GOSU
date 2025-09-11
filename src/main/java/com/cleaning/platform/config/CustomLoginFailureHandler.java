// CustomLoginFailureHandler.java
package com.cleaning.platform.config; // 혹은 적절한 패키지 경로

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;

        if (exception instanceof BadCredentialsException) {
            errorMessage = "이메일 또는 비밀번호가 올바르지 않습니다.";
        } else if (exception instanceof DisabledException) {
            // 계정이 비활성화된 경우 (enabled = false)
            errorMessage = "이메일 인증을 완료해주세요.";
        } else {
            errorMessage = "로그인에 실패했습니다. 관리자에게 문의하세요.";
        }

        HttpSession session = request.getSession();
        session.setAttribute("errorMessage", errorMessage);

        response.sendRedirect("/users/login");
    }
}