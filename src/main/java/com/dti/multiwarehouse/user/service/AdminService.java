package com.dti.multiwarehouse.user.service;

import com.dti.multiwarehouse.user.dto.UserProfileDTO;
import com.dti.multiwarehouse.user.dto.WarehouseAdminRequest;
import com.dti.multiwarehouse.user.dto.WarehouseAdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    Page<UserProfileDTO> searchUser(String role, String username, String email, Pageable pageable);
    List<UserProfileDTO> getAllUsers();
    List<WarehouseAdminResponse> getWarehouseAdmins();
    WarehouseAdminResponse getWarehouseAdminById(Long id);
    WarehouseAdminResponse createWarehouseAdmin(WarehouseAdminRequest request);
    WarehouseAdminResponse updateWarehouseAdmin(Long id, WarehouseAdminRequest request);
    void deleteWarehouseAdmin(Long id);
}
