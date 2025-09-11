package com.cleaning.platform.service;

import com.cleaning.platform.domain.Address;
import com.cleaning.platform.domain.ServiceProvider;
import com.cleaning.platform.domain.UserType;
import com.cleaning.platform.domain.Users;
import com.cleaning.platform.dto.ProviderDto;
import com.cleaning.platform.dto.ProviderRegistrationDto;
import com.cleaning.platform.dto.UserDto;
import com.cleaning.platform.repository.ServiceProviderRepository;
import com.cleaning.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.cleaning.platform.dto.UserSearchDto; // import 추가
import java.util.stream.Collectors; // import 추가

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ServiceProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final NtsApiService ntsApiService;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       ServiceProviderRepository providerRepository,
                       PasswordEncoder passwordEncoder,
                       FileService fileService,
                       NtsApiService ntsApiService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
        this.ntsApiService = ntsApiService;
        this.emailService = emailService;
    }

    @Transactional
    public void registerUser(UserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Address address = new Address(dto.getZipcode(), dto.getMainAddress(), dto.getDetailAddress());

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        String token = UUID.randomUUID().toString();


        Users user = Users.builder()
                .id("U-" + UUID.randomUUID().toString().substring(0, 7))
                .username(dto.getUsername())
                .password(encodedPassword)
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(address)
                .enabled(false)
                .verificationToken(token)
                .build();
        user.setUserType(UserType.GENERAL);
        userRepository.save(user);


        try {
            System.out.println(">>> [LOG] 이메일 발송을 시도합니다. To: " + user.getEmail());
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
            System.out.println(">>> [LOG] 이메일 발송 요청이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            System.err.println(">>> [ERROR] 이메일 발송 중 오류가 발생했습니다!");
            e.printStackTrace(); // 에러의 상세 내용을 콘솔에 출력
        }
    }

    @Transactional
    public void registerProvider(ProviderRegistrationDto dto) throws IOException {

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (dto.getBusinessRegistrationNumber() != null && !dto.getBusinessRegistrationNumber().isEmpty()) {

            if (!ntsApiService.validateBusinessRegistrationNumber(dto.getBusinessRegistrationNumber())) {
                throw new IllegalArgumentException("유효하지 않거나 휴/폐업 상태의 사업자등록번호입니다.");
            }

            if (providerRepository.findByBusinessRegistrationNumber(dto.getBusinessRegistrationNumber()).isPresent()) {
                throw new IllegalArgumentException("이미 등록된 사업자등록번호입니다.");
            }
        } else {

            throw new IllegalArgumentException("사업자등록번호를 입력해주세요.");
        }

        Address address = new Address(dto.getZipcode(), dto.getMainAddress(), dto.getDetailAddress());
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        String token = UUID.randomUUID().toString();


        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + dto.getEmail());
        }

        Users user = Users.builder()
                .id("U-" + java.util.UUID.randomUUID().toString().substring(0, 7))
                .username(dto.getUsername())
                .password(encodedPassword)
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(address)
                .enabled(false)
                .verificationToken(token)
                .build();
        user.setUserType(UserType.PROVIDER);


        MultipartFile profileImageFile = dto.getProfileImage();
        String storedFilename = null;
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            storedFilename = fileService.uploadFile(profileImageFile);
        }

        ServiceProvider provider = ServiceProvider.builder()
                .id("P-" + java.util.UUID.randomUUID().toString().substring(0, 7))
                .users(user)
                .providerName(dto.getProviderName())
                .profileImageName(storedFilename)
                .businessRegistrationNumber(dto.getBusinessRegistrationNumber())
                .contactPerson(dto.getContactPerson())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getEmail())
                .description(dto.getDescription())
                .providerType(dto.getProviderType())
                .build();


        userRepository.save(user);
        providerRepository.save(provider);

        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    public void updateUser(String userId, UserDto dto) {
        Users user = findUserByIdOrThrow(userId);
        Address address = new Address(dto.getZipcode(), dto.getMainAddress(), dto.getDetailAddress());
        user.update(dto.getUsername(), dto.getPhoneNumber(), address);
    }

    @Transactional
    public void updateUserByEmail(String email, UserDto dto) {
        Users user = findUserByEmailOrThrow(email);
        Address address = new Address(dto.getZipcode(), dto.getMainAddress(), dto.getDetailAddress());
        user.update(dto.getUsername(), dto.getPhoneNumber(), address);
    }

    public List<Users> findAllUsers() {
        return userRepository.findAll();
    }
   

    public List<UserSearchDto> findAllUsersForSearch() {
        return userRepository.findAll().stream()
                .map(UserSearchDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Users findUserByIdOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다. ID: " + id));
    }


    @Transactional(readOnly = true)
    public Users findUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다. Email: " + email));
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findAllProviders() {
        return providerRepository.findAllWithUsers();
    }

    @Transactional(readOnly = true)
    public Optional<ServiceProvider> findProviderByUserEmail(String userEmail) {
        return providerRepository.findByUsersEmail(userEmail);
    }

    @Transactional(readOnly = true)
    public ServiceProvider findProviderById(String id) {
        return providerRepository.findWithServicesById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean isUsernameDuplicated(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isEmailDuplicated(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public boolean verifyEmail(String token) {
        Optional<Users> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isEmpty()) {
            return false;
        }

        Users user = userOptional.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return true;
    }



}