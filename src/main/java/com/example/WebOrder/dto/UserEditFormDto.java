package com.example.WebOrder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEditFormDto {
    private String username;
    private String currentPassword; //현재 비밀번호 (권한 재확인)
    private String changedPassword; //바꾸고자 하는 비밀번호
    private String name;

}
