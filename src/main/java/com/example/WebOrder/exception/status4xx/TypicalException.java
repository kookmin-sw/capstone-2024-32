package com.example.WebOrder.exception.status4xx;

// 분류하기 어렵지만 어쨌든 사용자가 뭔가 잘못 입력했을 시 띄우는 Exception
public class TypicalException extends RuntimeException{
    public TypicalException (String message){
        super(message);
    }
}
