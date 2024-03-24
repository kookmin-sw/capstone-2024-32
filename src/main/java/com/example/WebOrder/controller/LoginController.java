package com.example.WebOrder.controller;

import com.example.WebOrder.dto.LoginFormDto;
import com.example.WebOrder.dto.UserFormDto;
import com.example.WebOrder.entity.User;
import com.example.WebOrder.service.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class LoginController {
    private final LoginService loginService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(LoginService loginService, PasswordEncoder passwordEncoder) {
        this.loginService = loginService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/index")
    public String getIndex(){
        return "/html/index";
    }
    @GetMapping("/login")
    public String getLoginForm(@ModelAttribute("loginFormDto") LoginFormDto dto){
        log.info("로그인 폼 소환");
        return "html/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute ("loginFormDto") LoginFormDto dto, BindingResult bindingResult){
        log.info("로그인 시도");
        if (loginService.isLoginAttemptValid(dto)){
            log.info("로그인 성공");
            return "html/index";
        }
        else {
            log.info("로그인 실패");
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "html/loginForm";
        }
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("userFormDto") UserFormDto dto) {
        log.info("회원가입 폼 소환");
        return "html/registerForm";
    }

    @PostMapping("/register")
    public String register(@Valid UserFormDto dto, BindingResult bindingResult){
        log.info("회원가입 시도");
        if (bindingResult.hasErrors()) {
            log.info("회원가입 실패");
            return "html/registerForm";
        }

        if (loginService.findByUsername(dto.getUsername()).isPresent()){
            log.info("username 중복");
            return "redirect:/register?error=true";
        }

        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordNotSame", "비밀번호를 다시 확인해주세요.");
            return "html/registerForm";
        }
        else {
            log.info("회원가입 성공");
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setName(dto.getName());
            loginService.join(user);
            return "redirect:/";
        }
    }
}
