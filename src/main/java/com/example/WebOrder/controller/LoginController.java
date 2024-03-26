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
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/index")
    public String getIndex(){
        return "html/index";
    }
    @GetMapping("/login")
    public String getLoginForm(){
        log.info("로그인 폼 소환");
        return "html/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid LoginFormDto dto){
        log.info("로그인 시도");
        if (loginService.isLoginAttemptValid(dto)){
            log.info("로그인 성공");
            return "redirect:/index";
        }
        else {
            log.info("로그인 실패");
            return "redirect:/login?error=true";
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
        if (loginService.usernameExists(dto.getUsername())){
            log.info("username 중복");
            bindingResult.addError(new FieldError("UserFormDto", "username", "유저네임이 중복되었습니다."));
            return "redirect:/register?error=true";
        }
        else if (bindingResult.hasErrors()){
            return "redirect:/register?pwderror=true";
        }
        else {
            loginService.createUser(dto);
            log.info("회원가입 성공");
            return "redirect:/login";
        }
    }
}
