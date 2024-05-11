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

    private String address;

    private String description;

    private String profileImageUrl;

    public static ProfileDto fromEntity(User user) {
        ProfileDto dto = new ProfileDto();
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setAddress(user.getAddress());
        dto.setDescription(user.getDescription());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        return dto;
    }
}
