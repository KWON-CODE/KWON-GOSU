package com.cleaning.platform.service;

import com.cleaning.platform.domain.UserType; // UserType import
import com.cleaning.platform.domain.Users;
import com.cleaning.platform.domain.Users;
import com.cleaning.platform.repository.ServiceProviderRepository;
import com.cleaning.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;
    public UserSecurityService(UserRepository userRepository
                       ) {
        this.userRepository = userRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. : " + email));

        List<GrantedAuthority> authorities = new ArrayList<>();

        System.out.println("==============================================================");
        System.out.println("[로그인 시 DB에서 가져온 PW]: " + users.getPassword());
        System.out.println("==============================================================");


        if (users.getUserType() == UserType.PROVIDER) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PROVIDER"));
        } else if (users.getUserType() == UserType.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                users.getEmail(),
                users.getPassword(),
                users.isEnabled(), // <- 1. 활성화 상태
                true,              // <- 2. 계정 만료 여부 (true: 만료 안됨)
                true,              // <- 3. 비밀번호 만료 여부 (true: 만료 안됨)
                true,              // <- 4. 계정 잠김 여부 (true: 잠기지 않음)
                authorities
        );
    }
}