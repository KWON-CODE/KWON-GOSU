package com.cleaning.platform.service;

import com.cleaning.platform.domain.SocialProvider;
import com.cleaning.platform.domain.Users;
import com.cleaning.platform.dto.OAuthAttributes;
import com.cleaning.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName =
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        SocialProvider provider = SocialProvider.valueOf(registrationId.toUpperCase());

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Users user = saveOrUpdate(attributes, provider);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private Users saveOrUpdate(OAuthAttributes attributes, SocialProvider provider) {
        Optional<Users> userOptional = userRepository.findByEmail(attributes.getEmail());
        Users user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.update(attributes.getName());
        } else {
            user = attributes.toEntity(provider, passwordEncoder);
        }
        return userRepository.save(user);
    }
}