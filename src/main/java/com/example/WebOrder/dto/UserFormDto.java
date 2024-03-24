package com.example.WebOrder.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserFormDto {
    @NotEmpty(message = "아이디는 필수 입력 값입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요.")
    private String password;

    @NotEmpty(message = "비밀번호를 한 번 더 입력해주세요.")
    private String passwordCheck;

    @NotEmpty(message = "이름은 필수 입력 값입니다.")
    private String name;
}
