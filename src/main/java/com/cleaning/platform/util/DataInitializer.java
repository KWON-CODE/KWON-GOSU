package com.cleaning.platform.util;

import com.cleaning.platform.domain.UserType;
import com.cleaning.platform.domain.Users;
import com.cleaning.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 관리자 이메일
        String adminEmail = "admin@admin.com";

        // DB에 해당 이메일을 가진 사용자가 없는 경우에만 관리자 계정을 생성
        if (!userRepository.findByEmail(adminEmail).isPresent()) {
            Users admin = Users.builder()
                    .id("admin-user") // 고정 ID 사용
                    .username("관리자")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("00000000")) // 비밀번호 암호화
                    .phoneNumber("010-0000-0000")
                    .build();
            admin.setUserType(UserType.ADMIN); // 사용자 유형을 ADMIN으로 설정
            userRepository.save(admin);

            System.out.println(">>>>>>>>>> 초기 관리자 계정이 생성되었습니다: " + adminEmail);
        }
    }
}