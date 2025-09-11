package com.cleaning.platform.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDto {

    @Size(min = 2, max = 25, message = "이름은 2자 이상 25자 이하로 입력해주세요.")
    @NotEmpty(message = "닉네임을 입력해주세요.")
    private String username;

    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotEmpty(message = "비밀번호를 다시 입력해주세요.")
    private String passwordConfirm;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {
        if (password == null || passwordConfirm == null) {
            return false;
        }
        return password.equals(passwordConfirm);
    }

    @NotEmpty(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotEmpty(message = "전화번호를 입력해주세요.")
    private String phoneNumber;
    private String zipcode;
    private String mainAddress;
    private String detailAddress;
}