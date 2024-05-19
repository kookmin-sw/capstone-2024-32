package com.example.WebOrder.service;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class UrlEncodeService {
    public static String encodeBase64UrlSafe(String input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input.getBytes());
    }

    public static String decodeBase64UrlSafe(String encodedString) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedString);
        return new String(decodedBytes);
    }
}
