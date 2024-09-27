package com.dti.multiwarehouse.user.controller;

import com.dti.multiwarehouse.exceptions.ApplicationException;
import com.dti.multiwarehouse.exceptions.ResourceNotFoundException;
import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.user.dto.UserProfileDTO;
import com.dti.multiwarehouse.user.dto.WarehouseAdminRequest;
import com.dti.multiwarehouse.user.dto.WarehouseAdminResponse;
import com.dti.multiwarehouse.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserProfileDTO> users = adminService.getAllUsers();
            return Response.success("Users retrieved successfully", users);
        } catch (Exception e) {
            return Response.failed("Failed to retrieve users.");
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<UserProfileDTO>> searchUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfileDTO> users = adminService.searchUser(role, username, email, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/warehouse-admins")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getWarehouseAdmins() {
        try {
            List<WarehouseAdminResponse> warehouseAdmins = adminService.getWarehouseAdmins();
            return Response.success("Warehouse admins retrieved successfully", warehouseAdmins);
        } catch (Exception e) {
            return Response.failed("Failed to retrieve warehouse admins.");
        }
    }

    @GetMapping("/warehouse-admins/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getWarehouseAdminById(@PathVariable Long id) {
        try {
            WarehouseAdminResponse warehouseAdmin = adminService.getWarehouseAdminById(id);
            return Response.success("Warehouse admin retrieved successfully", warehouseAdmin);
        } catch (ResourceNotFoundException e) {
            return Response.failed("Warehouse admin not found.");
        }
    }

    @PostMapping("/warehouse-admins")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createWarehouseAdmin(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {

        try {
            WarehouseAdminRequest request = new WarehouseAdminRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setPassword(password);
            request.setAvatar(avatar);

            WarehouseAdminResponse response = adminService.createWarehouseAdmin(request);
            return Response.success("Warehouse admin created successfully", response);
        } catch (ApplicationException e) {
            return Response.failed("Failed to create warehouse admin.");
        }
    }

    @PutMapping("/warehouse-admins/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateWarehouseAdmin(
            @PathVariable Long id,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {

        try {
            WarehouseAdminRequest request = new WarehouseAdminRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setPassword(password);
            request.setAvatar(avatar);

            WarehouseAdminResponse response = adminService.updateWarehouseAdmin(id, request);
            return Response.success("Warehouse admin updated successfully", response);
        } catch (ResourceNotFoundException e) {
            return Response.failed("Warehouse admin not found.");
        }
    }

    @DeleteMapping("/warehouse-admins/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteWarehouseAdmin(@PathVariable Long id) {
        try {
            adminService.deleteWarehouseAdmin(id);
            return Response.success("Warehouse admin deleted successfully");
        } catch (ResourceNotFoundException e) {
            return Response.failed("Warehouse admin not found.");
        }
    }
}
