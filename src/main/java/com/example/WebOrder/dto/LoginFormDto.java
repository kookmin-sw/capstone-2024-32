package com.example.WebOrder.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginFormDto {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
