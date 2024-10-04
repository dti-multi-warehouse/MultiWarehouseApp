package com.dti.multiwarehouse.auth.service;

import com.dti.multiwarehouse.auth.dto.SocialLoginRequest;
import com.dti.multiwarehouse.auth.dto.LoginResponseDto;
import com.dti.multiwarehouse.auth.helper.Claims;
import com.dti.multiwarehouse.auth.repository.AuthRedisRepository;
import com.dti.multiwarehouse.exceptions.ResourceNotFoundException;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.repository.UserRepository;
import com.dti.multiwarehouse.user.service.AdminService;
import com.dti.multiwarehouse.user.service.UserService;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final JwtEncoder jwtEncoder;
    private final AuthRedisRepository authRedisRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AdminService adminService;
    private final WarehouseService warehouseService;

    @Override
    public LoginResponseDto generateToken(Authentication authentication) {
        logger.debug("Generating token for user: {}", authentication.getName());
        Optional<User> userOptional = userService.findByEmail(authentication.getName());

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();

        long userId = user.getId();
        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var existingKey = authRedisRepository.getJwtKey(authentication.getName());
        LoginResponseDto response = new LoginResponseDto();
        response.setUserId(Long.toString(userId));
        response.setEmail(authentication.getName());
        response.setRole(scope);

        //        if warehouse admin, find their warehouse and add to response
        if (Objects.equals(user.getRole(), "warehouse_admin")) {
            var admin = adminService.getWarehouseAdminById(userId);
            response.setWarehouseId(admin.getWarehouseId());
            response.setWarehouseName(admin.getWarehouseName());
        }

//        if admin, find the first warehouse and add to response
        if (Objects.equals(user.getRole(), "admin")) {
            var warehouse = warehouseService.findFirstWarehouse();
            response.setWarehouseId(warehouse.getId());
            response.setWarehouseName(warehouse.getName());
        }

        if (existingKey != null) {
            logger.info("Token already exists for user: {}. Returning existing token.", authentication.getName());
            response.setAccessToken(existingKey);
            return response;
        }

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("id", userId)
                .claim("role", user.getRole())
                .claim("is_social", user.isSocial());

//        if warehouse admin, find their warehouse and add to claims
        if (Objects.equals(user.getRole(), "warehouse_admin")) {
            var admin = adminService.getWarehouseAdminById(userId);
            claimsBuilder
                    .claim("warehouse_id", admin.getWarehouseId())
                    .claim("warehouse_name", admin.getWarehouseName());
        }

//        if admin, find the first warehouse and add to claims
        if (Objects.equals(user.getRole(), "admin")) {
            var warehouse = warehouseService.findFirstWarehouse();
            claimsBuilder
                    .claim("warehouse_id", warehouse.getId())
                    .claim("warehouse_name", warehouse.getName());
        }

        JwtClaimsSet claims = claimsBuilder.build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        if (authRedisRepository.isKeyBlacklisted(jwt)) {
            throw new ResourceNotFoundException("JWT Token has already been blacklisted");
        }

        authRedisRepository.saveJwtKey(authentication.getName(), jwt);
        response.setAccessToken(jwt);
        return response;
    }
    @Override
    public String generateTokenForUser(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.HOURS))
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("is_social", user.isSocial())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public void logout() {
        logger.debug("Logout initiated");
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        String jwt = authRedisRepository.getJwtKey(email);

        if (jwt != null) {
            authRedisRepository.blackListJwt(email, jwt);
            authRedisRepository.deleteJwtKey(email);
        }
    }

    public boolean authenticateClerk(SocialLoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user != null) {
            return true;
        }
        return false;
    }
}
