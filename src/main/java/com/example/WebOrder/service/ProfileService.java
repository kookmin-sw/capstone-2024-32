package com.example.WebOrder.service;

import com.example.WebOrder.dto.ProfileDto;
import com.example.WebOrder.dto.UserEditFormDto;
import com.example.WebOrder.entity.User;
import com.example.WebOrder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

// 프로필 관련 서비스
// LoginService와는 다르다.
// 마이페이지에서 자신의 회원정보를 가져오거나 수정하거나, 또는 타인의 프로필을 볼 때 필요한 서비스
@Service
@Slf4j
public class ProfileService {
    private final UserRepository userRepository;
    private final LoginService loginService;
    private final PasswordEncoder passwordEncoder;
    public ProfileService(UserRepository userRepository, LoginService loginService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.loginService = loginService;
        this.passwordEncoder = passwordEncoder;
    }

    // 접속한 유저가 자신의 프로필을 보고자 할 때 사용하는 메소드.
    public ProfileDto getMyProfile(){
        String username = loginService.getUsernameOfCurrentUser();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) throw new RuntimeException("엔티티없음");
        User user = optionalUser.get();

        ProfileDto dto = ProfileDto.fromEntity(user);
        return dto;
    }
    
    // userId에 해당하는 user의 프로필을 가져오는 메소드
    public ProfileDto getUserProfileById(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) throw new RuntimeException("엔티티없음");
        User user = optionalUser.get();

        return ProfileDto.fromEntity(user);
    }

    // username에 해당하는 user의 프로필을 가져오는 메소드
    public ProfileDto getUserProfileByUsername(String username){
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) throw new RuntimeException("엔티티없음");
        User user = optionalUser.get();

        return ProfileDto.fromEntity(user);
    }

    // 자신의 프로필을 수정하는 메소드
    public Long editMyProfile(UserEditFormDto dto, String fileName){
        String username = loginService.getUsernameOfCurrentUser();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) throw new RuntimeException("엔티티없음");
        User user = optionalUser.get();

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) throw new RuntimeException("권한없음");

        user.setName(dto.getName());
        if (!Objects.equals(dto.getChangedPassword(), "")) {
            user.setPassword(passwordEncoder.encode(dto.getChangedPassword()));
        }
        user.setAddress(dto.getAddress());
        user.setDescription(dto.getDescription());
        user.setProfileImageUrl(fileName);

        return userRepository.save(user).getId();
    }



}
