package com.opex.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String userId) {
        if (file.isEmpty() || !file.getContentType().equalsIgnoreCase("application/pdf")) {
            throw new RuntimeException("Il file deve essere un PDF non vuoto.");
        }

        String fileName = userId + "/" + UUID.randomUUID().toString() + ".pdf";

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return fileName; // Restituisce la chiave del file (o l'URL se configurato)
        } catch (IOException e) {
            throw new RuntimeException("Errore durante l'upload del file su S3", e);
        }
    }
}
