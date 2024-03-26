package com.example.WebOrder.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
public class OrderPasswordService {
    @Value("${qrcode.url}")
    private String url;

    public Boolean isPasswordValid(Long userId, String password){
        return true;
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
}

