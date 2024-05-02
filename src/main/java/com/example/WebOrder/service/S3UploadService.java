package com.example.WebOrder.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class S3UploadService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3Client;

    public S3UploadService(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String upload(MultipartFile image) throws IOException { // dirName의 디렉토리가 S3 Bucket 내부에 생성됨
        String originName = image.getOriginalFilename(); //원본 이미지 이름
        String ext = originName.substring(originName.lastIndexOf(".")); //확장자
        String changedName = changeImageName(originName); //새로 생성된 이미지 이름
        ObjectMetadata metadata = new ObjectMetadata(); //메타데이터
        metadata.setContentType("image/" + ext);

        PutObjectResult putObjectResult =
                amazonS3Client.putObject(
                        new PutObjectRequest(bucket, changedName, image.getInputStream(), metadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, changedName).toString();
    }

    public void delete(String fileName) throws IOException {
        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (SdkClientException e) {
            throw new IOException("삭제 오류");
        }
    }

    private String changeImageName(String originName) { //이미지 이름 중복 방지를 위해 랜덤으로 생성
        String random = UUID.randomUUID().toString();
        return random + originName;
    }

}