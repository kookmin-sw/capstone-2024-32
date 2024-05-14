package com.example.WebOrder.exception.status4xx;

// 엔티티가 없을 시 호춣하는 Exception
public class NoEntityException extends RuntimeException{
    public NoEntityException(String message){
        super(message);
    }
}
