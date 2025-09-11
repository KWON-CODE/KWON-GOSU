package com.cleaning.platform.service;



import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String verificationUrl = "http://localhost:8080/users/verify?token=" + token;

        String htmlContent = "<h1>[Clean-Platform] 이메일 인증</h1>"
                + "<p>회원가입을 완료하려면 아래 링크를 클릭하세요.</p>"
                + "<a href='" + verificationUrl + "'>인증 링크</a>";

        helper.setText(htmlContent, true); // true: HTML 형식으로 전송
        helper.setTo(toEmail);
        helper.setSubject("[Clean-Platform] 회원가입 인증 이메일");
        helper.setFrom("odin053@daum.net");

        mailSender.send(mimeMessage);
    }
}