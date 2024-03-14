package com.example.WebOrder.controller;

import com.example.WebOrder.dto.LoginDto;
import com.example.WebOrder.dto.RegisterDto;
import com.example.WebOrder.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
        return "/html/index";
    }
    @GetMapping("/login")
    public String getLoginForm(){
        log.info("로그인 폼 소환");
        return "/html/login";
    }

    @PostMapping("/login")
    public String login(LoginDto dto){
        log.info("로그인 시도");
        if (loginService.isLoginAttemptValid(dto)){
            log.info("로그인 성공");
            return "/html/index";
        }
        else {
            log.info("로그인 실패");
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/register")
    public String getRegisterForm(){
        log.info("회원가입 폼 소환");
        return "/html/register";
    }

    @PostMapping("/register")
    public String register(RegisterDto dto){
        log.info("회원가입 시도");
        if (loginService.usernameExists(dto.getUsername())){
            log.info("username 중복");
            return "redirect:/register?error=true";
        }
        else {
            log.info("회원가입 성공");
            loginService.createUser(dto);
            return "redirect:/login";
        }
    }
}
