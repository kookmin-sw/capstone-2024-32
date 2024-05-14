package com.example.WebOrder.exception.status4xx;

// 인증은 했으나, 접근 권한이 없는 행동을 할 때 뜨는 Exception
public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message){
        super(message);
    }
}
