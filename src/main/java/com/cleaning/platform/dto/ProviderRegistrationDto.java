package com.cleaning.platform.dto;

import com.cleaning.platform.domain.Address;
import com.cleaning.platform.domain.ProviderType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProviderRegistrationDto {

    @Size(min = 2, max = 25) @NotEmpty(message = "사용자 이름을 입력해주세요.")
    private String username;

    @NotEmpty (message = "이메일을 입력해주세요.") 
    @Email
    private String email;

    @NotEmpty (message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20)
    private String password;

    @NotEmpty (message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    @NotEmpty(message = "휴대전화 번호를 입력해주세요.")
    private String phoneNumber;

   //  private String address;
   private String zipcode;
    private String mainAddress;
    private String detailAddress;


    @NotEmpty(message = " 서비스 제공자를 입력해주세요.")
    private String providerName;

    @NotEmpty(message = "제공자 연락처를 입력해주세요.")
    private String contactPhone;


    @Pattern(regexp = "^$|^[0-9]{10}$", message = "'-' 없이 10자리 숫자만 입력해주세요.")
    private String businessRegistrationNumber;

    @NotNull(message = "주요 서비스 유형을 선택해주세요.")
    private ProviderType providerType;

    private MultipartFile profileImage;

   private String contactPerson;
    private String description;

    public MultipartFile getProfileImage() {
        return profileImage;
     }

    public void setProfileImage(MultipartFile profileImage) {
        this.profileImage = profileImage; }

    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {

        return password != null && password.equals(passwordConfirm);
    }
}