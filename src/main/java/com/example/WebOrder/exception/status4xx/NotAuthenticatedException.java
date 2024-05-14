package com.example.WebOrder.exception.status4xx;

// 인증을 하지 않을 시 호출하는 Exception 페이지
public class NotAuthenticatedException extends RuntimeException{
    public NotAuthenticatedException(String message){
        super(message);
    }
}
