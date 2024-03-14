package com.example.WebOrder.controller;

import com.example.WebOrder.dto.LoginDto;
import com.example.WebOrder.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String getLoginForm(HttpServletResponse response, LoginDto dto){

        if (!loginService.isLoginAttemptValid(dto)){
            return "redirect:/login?error=true";
        }
    }
}
