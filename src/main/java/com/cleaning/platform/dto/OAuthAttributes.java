package com.cleaning.platform.dto;

import com.cleaning.platform.domain.SocialProvider;
import com.cleaning.platform.domain.UserType;
import com.cleaning.platform.domain.Users;
import lombok.Builder;
import lombok.Getter;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;


@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Users toEntity(SocialProvider provider,  PasswordEncoder passwordEncoder) {
        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);


        return Users.builder()
                .id("U-" + UUID.randomUUID().toString().substring(0, 7))
                .username(name)
                .email(email)
                .password(encodedPassword) // 5. 암호화된 비밀번호 설정
                .provider(provider)
                .userType(UserType.GENERAL) // 6. 기본 권한 설정
                .enabled(true) // 7. 계정 즉시 활성화
                .build();
    }
}