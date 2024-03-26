package com.example.WebOrder.dto;

import com.example.WebOrder.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProfileDto {

    private String username;

    private String name;

    public static ProfileDto fromEntity(User user) {
        ProfileDto dto = new ProfileDto();
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());

        return dto;
    }
}
