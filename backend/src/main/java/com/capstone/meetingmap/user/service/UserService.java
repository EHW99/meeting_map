package com.capstone.meetingmap.user.service;

import com.capstone.meetingmap.api.amazon.service.S3Service;
import com.capstone.meetingmap.auth.dto.KakaoUserInfo;
import com.capstone.meetingmap.user.dto.*;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.user.repository.UserRepository;
import com.capstone.meetingmap.userrole.entity.UserRole;
import com.capstone.meetingmap.userrole.repository.UserRoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3Service s3Service;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, BCryptPasswordEncoder bCryptPasswordEncoder, S3Service s3Service) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.s3Service = s3Service;
    }

    // 중복 아이디 검증
    public UserCheckIdResponseDto existsByUserId(UserCheckIdRequestDto userCheckIdRequestDto) {
        String userId = userCheckIdRequestDto.getUserId();
        boolean isExists = userRepository.existsById(userId);
        return new UserCheckIdResponseDto(!isExists);
    }

    // 일반 회원가입
    @Transactional
    public UserResponseDto join(UserRegisterRequestDto userRegisterRequestDto) {
        String userId = userRegisterRequestDto.getUserId();
        String userPasswd = userRegisterRequestDto.getUserPasswd();
        boolean isExists = userRepository.existsById(userId);

        //중복 회원 검증
        if (isExists) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }

        String hashedUserPasswd = bCryptPasswordEncoder.encode(userPasswd); //비밀번호를 해시함수로 암호화
        UserRole userRole = userRoleRepository.findById(1).get(); //회원 유형을 1(일반사용자)로 고정

        User user = userRegisterRequestDto.toEntity(hashedUserPasswd, userRole); // 암호화된 비밀번호와 회원 유형을 넘겨서 User 엔티티 생성
        userRepository.save(user);

        return UserResponseDto.fromEntity(user); //User 엔티티를 UserResponseDto로 변환해 반환
    }

    // 카카오 회원가입
    @Transactional
    public User kakaoJoin(KakaoUserInfo kakaoUser) {
        String userId = "kakao_" + kakaoUser.getId();

        UserRole userRole = userRoleRepository.findById(1).get(); //회원 유형을 1(일반사용자)로 고정

        User user = User.builder()
                .userId(userId)
                .userEmail(kakaoUser.getKakaoAccount().getEmail())
                .userPasswd("")
                .userNick(kakaoUser.getKakaoAccount().getProfile().getNickname())
                .userImg(kakaoUser.getKakaoAccount().getProfile().getProfileImageUrl())
                .userRole(userRole)
                .onlyFriendsCanSeeActivity(false)
                .emailNotificationAgree(false)
                .pushNotificationAgree(false)
                .build();
        userRepository.save(user);

        return user;
    }

    // 특정 회원 조회
    public UserResponseDto findOne(String userId) {
        return userRepository.findById(userId)
                .map(UserResponseDto::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
    }

    // 모든 회원 조회
    public List<UserResponseDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 회원 정보 변경
    @Transactional
    public UserResponseDto updateUser(UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImage, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        // 프로필 사진 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String imageUrl = s3Service.upload(profileImage);
                user.setProfileImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (userUpdateRequestDto != null) {
            user.updateInfo(userUpdateRequestDto);
        }

        return UserResponseDto.fromEntity(user);
    }

    // 회원 비밀번호 변경
    @Transactional
    public void updatePassword(UserUpdatePasswdRequestDto userUpdatePasswdRequestDto, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        String hashedUserPasswd = bCryptPasswordEncoder.encode(userUpdatePasswdRequestDto.getUserPasswd()); //비밀번호를 해시함수로 암호화

        User newUser = userUpdatePasswdRequestDto.toEntity(hashedUserPasswd, user);

        userRepository.save(newUser);
    }

    // 회원 탈퇴
    @Transactional
    public void delete(UserDeleteRequestDto userDeleteRequestDto, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        // 카카오 사용자는 비밀번호 기반 탈퇴 불가
        if (userId.startsWith("kakao_")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카카오 계정은 카카오에서 연동 해제 후 탈퇴가 가능합니다.");
        }

        if (bCryptPasswordEncoder.matches(userDeleteRequestDto.getUserPasswd(), user.getUserPasswd())) {
            userRepository.delete(user);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 잘못되었습니다.");
        }
    }
}
