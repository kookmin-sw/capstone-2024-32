package com.example.WebOrder.service;

import com.example.WebOrder.dto.LoginFormDto;
import com.example.WebOrder.dto.UserFormDto;
import com.example.WebOrder.entity.User;
import com.example.WebOrder.exception.status4xx.NotAuthenticatedException;
import com.example.WebOrder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class LoginService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderPasswordService orderPasswordService;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, OrderPasswordService orderPasswordService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderPasswordService = orderPasswordService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username " + username);
        return userRepository.findByUsername(username).get();
    }

    public Boolean isLoginAttemptValid(LoginFormDto dto){
        Optional<User> findUser = userRepository.findByUsername(dto.getUsername());

        if (findUser.isEmpty()) throw new UsernameNotFoundException("유저가 존재하지 않습니다.");

        User user = findUser.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new NotAuthenticatedException("비밀번호가 일치하지 않습니다.");

        return true;
    }

    public Boolean usernameExists(String username){
        return userRepository.existsByUsername(username);
    }

    public Boolean isCurrentUserAuthenticated(Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            return Objects.equals(user.getId(), userId);
        }
        return false;
    }


    public User createUser(UserFormDto dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setSeats(new ArrayList<>());

        user = userRepository.save(user);

        orderPasswordService.updateEntranceCode(user.getId());
        return user;
    }


    // 현재 유저의 Username을 얻어내는 메소드
    public String getUsernameOfCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) throw new NotAuthenticatedException("로그인하지 않은 상태입니다!");
        User user = (User) authentication.getPrincipal();

        return user.getUsername();
    }

    // 현재 유저의 패스워드와 매개변수로 주어진 password가 일치하면 true, 아니면 false를 리턴하는 메소드
    public Boolean isPasswordCorrect(String password){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) throw new NotAuthenticatedException("로그인하지 않은 상태입니다!");
        User user = (User) authentication.getPrincipal();

        if (!passwordEncoder.matches(password, user.getPassword())) return false;
        return true;
    }

    //현재 유저의 entity를 가져오기
    public User getCurrentUserEntity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) throw new NotAuthenticatedException("로그인하지 않은 상태입니다!");
        return (User) authentication.getPrincipal();
    }

    public Boolean userExistsByUserId(Long userId){
        return userRepository.existsById(userId);
    }


}

