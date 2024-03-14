package com.example.WebOrder.service;

import com.example.WebOrder.dto.LoginDto;
import com.example.WebOrder.dto.RegisterDto;
import com.example.WebOrder.entity.User;
import com.example.WebOrder.repository.UserRepository;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).get();
    }

    public Boolean isLoginAttemptValid(LoginDto dto){
        Optional<User> optionalUser = userRepository.findByUsername(dto.getUsername());

        if (optionalUser.isEmpty()) throw new UsernameNotFoundException("유저가 존재하지 않습니다.");

        User user = optionalUser.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new RuntimeException("비밀번호가 일치하지 않습니다.");

        return true;
    }

    public User createUser(RegisterDto dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    public Boolean usernameExists(String username){
        return userRepository.existsByUsername(username);
    }
}
