package com.dti.multiwarehouse.cloudImageStorage.service.impl;

import com.cloudinary.Cloudinary;
import com.dti.multiwarehouse.cloudImageStorage.service.CloudImageStorageService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CloudinaryServiceImpl implements CloudImageStorageService {
    @Resource
    private Cloudinary cloudinary;
    private final List<String> ALLOWED_FORMAT = List.of("jpg", "jpeg", "png");

    @Override
    public String uploadImage(MultipartFile file, String folderName) throws IOException {
        HashMap<Object, Object> options = new HashMap<>();
        options.put("folder", folderName);
        options.put("allowed_format", ALLOWED_FORMAT);
        Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
        String publicId = (String) uploadedFile.get("public_id");
        return cloudinary.url().secure(true).generate(publicId);
    }
}
