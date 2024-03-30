package com.example.WebOrder.controller;

import com.example.WebOrder.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;

import java.io.IOException;

//스프링 내부 구현을 이용하기 위한 LoginHandler
@Slf4j
@Controller
public class LoginHandler implements AuthenticationFailureHandler, AuthenticationSuccessHandler {

    //시큐리티 내부 구현 Method
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("로그인 성공 : {}", ((User) authentication.getPrincipal()).getUsername());
        response.sendRedirect("/home");
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("로그인 실패: {}", request.getParameter("username"));
        response.sendRedirect("/login?error");
    }
}
