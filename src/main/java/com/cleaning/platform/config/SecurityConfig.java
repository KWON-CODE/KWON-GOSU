package com.cleaning.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


import com.cleaning.platform.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final AuthenticationFailureHandler customLoginFailureHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/check-email",
                                "/","/users/login", "/users/new","/users/verify",
                                "/providers/new", "/api/users/check-email",
                                "/api/users/check-username","/home",
                                "/css/**", "/js/**", "/images/**","/ws/**", "/test-upload.html").permitAll()

                        .requestMatchers("/users", "/users/edit/**", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/my/**", "/provider/**").authenticated()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/users/login")
                        .defaultSuccessUrl("/", true)
                        .failureHandler(customLoginFailureHandler)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/users/login")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                );

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/ws/**")
                .ignoringRequestMatchers("/users/new", "/providers/new")
                .ignoringRequestMatchers("/api/**")
        );



        return http.build();
    }



}