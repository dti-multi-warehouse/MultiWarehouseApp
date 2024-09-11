package com.dti.multiwarehouse.cloudImageStorage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudImageStorageService {
    String uploadImage(MultipartFile file, String folderName) throws IOException;
    void deleteImage(String imageName) throws IOException;
}
