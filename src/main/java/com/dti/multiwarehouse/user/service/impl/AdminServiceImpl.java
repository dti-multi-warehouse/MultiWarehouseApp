package com.dti.multiwarehouse.user.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dti.multiwarehouse.exceptions.ResourceNotFoundException;
import com.dti.multiwarehouse.user.dto.UserProfileDTO;
import com.dti.multiwarehouse.user.dto.WarehouseAdminRequest;
import com.dti.multiwarehouse.user.dto.WarehouseAdminResponse;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.repository.UserRepository;
import com.dti.multiwarehouse.user.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Page<UserProfileDTO> searchUser(String role, String username, String email, Pageable pageable) {
        return userRepository. findAllByUsernameEmailAndRole(role, username, email, pageable)
                .map(this::mapToUserProfileDTO);
    }

    @Override
    public List<UserProfileDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserProfileDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarehouseAdminResponse> getWarehouseAdmins() {
        return userRepository.findByRole("warehouse_admin").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseAdminResponse getWarehouseAdminById(Long id) {
        User warehouseAdmin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse admin not found"));

        if (!warehouseAdmin.getRole().equals("warehouse_admin")) {
            throw new ResourceNotFoundException("User is not a warehouse admin");
        }

        return mapToResponse(warehouseAdmin);
    }

    @Override
    public WarehouseAdminResponse createWarehouseAdmin(WarehouseAdminRequest request) {
        User warehouseAdmin = new User();
        if (request.getUsername() != null){
            warehouseAdmin.setUsername(request.getUsername());
        }
        warehouseAdmin.setEmail(request.getEmail());
        warehouseAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        warehouseAdmin.setRole("warehouse_admin");
        warehouseAdmin.setVerified(true);

        if (request.getAvatar() != null) {
            warehouseAdmin.setAvatar(uploadAvatar(request.getAvatar()));
        }

        User savedWarehouseAdmin = userRepository.save(warehouseAdmin);
        return mapToResponse(savedWarehouseAdmin);
    }

    @Override
    public WarehouseAdminResponse updateWarehouseAdmin(Long id, WarehouseAdminRequest request) {
        User warehouseAdmin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse admin not found"));

        if (request.getUsername() != null){
            warehouseAdmin.setUsername(request.getUsername());
        }
        if (request.getEmail() != null){
            warehouseAdmin.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            warehouseAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getAvatar() != null) {
            warehouseAdmin.setAvatar(uploadAvatar(request.getAvatar()));
        }

        User updatedWarehouseAdmin = userRepository.save(warehouseAdmin);
        return mapToResponse(updatedWarehouseAdmin);
    }

    @Override
    public void deleteWarehouseAdmin(Long id) {
        User warehouseAdmin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse admin not found"));
        userRepository.delete(warehouseAdmin);
    }

    private String uploadAvatar(MultipartFile avatar) {
        try {
            Map uploadResult = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }

    private WarehouseAdminResponse mapToResponse(User user) {
        WarehouseAdminResponse response = new WarehouseAdminResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setWarehouseId(user.getWarehouseAdmins().getFirst().getWarehouse().getId());
        response.setWarehouseName(user.getWarehouseAdmins().getFirst().getWarehouse().getName());
        return response;
    }

    private UserProfileDTO mapToUserProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setSocial(user.isSocial());
        dto.setVerified(user.isVerified());
        dto.setAvatar(user.getAvatar());
        dto.setRole(user.getRole());
        return dto;
    }
}