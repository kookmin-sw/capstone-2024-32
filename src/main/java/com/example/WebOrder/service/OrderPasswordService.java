package com.example.WebOrder.service;

import com.example.WebOrder.entity.User;
import com.example.WebOrder.exception.status4xx.NoEntityException;
import com.example.WebOrder.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class OrderPasswordService {
    @Value("${qrcode.url}")
    private String url;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserRepository userRepository;

    public OrderPasswordService(SimpMessagingTemplate simpMessagingTemplate, UserRepository userRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
    }

    public String generateQRCode(Long userId, Long seatId) throws WriterException, IOException {
        /*
        //url encoding
        String encodedUserId = Base64.getUrlEncoder().encodeToString(String.valueOf(userId).getBytes());
        String encodedURL = url + "/guest/" + encodedUserId + "/" + seatId;
         */

        /*
        //URL 인코딩하지 않는 버전
         */
        String encodedURL = url + "/guest/" + userId + "/" + seatId + "/login";

        //QR 코드 생성.
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(encodedURL, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        return Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
    }

    //인증번호 가져오기
    public String getEntranceCode(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) throw new NoEntityException("해당하는 유저가 존재하지 않습니다!");
        User user = optionalUser.get();

        return user.getEntranceCode();
    }

    //인증번호 랜덤으로 바꾸기
    public String updateEntranceCode(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) throw new NoEntityException("해당하는 유저가 존재하지 않습니다!");
        User user = optionalUser.get();

        Random random = new Random(System.nanoTime());

        // 0부터 9999까지의 랜덤한 숫자 생성
        int randomNumber = random.nextInt(10000);

        // 스트링으로 변환하여 저장 후 리턴함.
        String randomNumberString = String.format("%04d", randomNumber);

        user.setEntranceCode(randomNumberString);
        userRepository.save(user);

        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/topic/entranceCode", randomNumberString);
        return randomNumberString;
    }

    //인증번호 비교하기
    //맞으면 true, 틀리면 false
    public Boolean authenticateByEntranceCode(Long userId, String entranceCode){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) throw new NoEntityException("해당하는 유저가 존재하지 않습니다!");
        User user = optionalUser.get();

        boolean result = user.getEntranceCode().equals(entranceCode);

        // 틀리면 인증번호 바꿈.
        if (!result) updateEntranceCode(user.getId());
        return result;
    }

    // url만 바꿔서 다른 user의 주문 페이지에 접근하는 것을 막기 위한 메소드 2개
    // 쿠키 발급
    public Cookie getCookieAfterEntranceCode(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) throw new NoEntityException("해당하는 유저가 존재하지 않습니다!");
        User user = optionalUser.get();

        Cookie cookie = new Cookie("entrancetoken", user.getEntranceCode());
        cookie.setPath("/");
        cookie.setMaxAge(300); //5분 지속.
        return cookie;
    }
    // 쿠키 검사
    // Cookie에 담긴 "entrancetoken"을 확인하여 현재 인증번호와 일치하면 true, 아니라면 false.
    public Boolean isAuthenticatedByRequest(Long userId, HttpServletRequest request){
        String entranceToken = null;
        if (request.getCookies() == null) return false;
        for (Cookie cookie : request.getCookies()){
            if (cookie.getName().equals("entrancetoken"))
                entranceToken = cookie.getValue();
        }

        if (entranceToken == null) return false;

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) return false;
        User user = optionalUser.get();

        if (!user.getEntranceCode().equals(entranceToken)) return false;
        return true;
    }
}

