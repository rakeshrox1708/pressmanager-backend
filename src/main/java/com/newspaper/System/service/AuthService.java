package com.newspaper.System.service;

import com.newspaper.System.dto.*;
import com.newspaper.System.dto.response.LoginResponseDTO;
import com.newspaper.System.dto.response.UserResponseDTO;
import com.newspaper.System.model.*;
import com.newspaper.System.repository.*;
import com.newspaper.System.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private VendorRepository vendorRepo;

    @Autowired
    private AreaRepository areaRepo;

    @Autowired
    private RefreshTokenRepository refreshRepo;

    @Autowired
    private TokenBlacklistRepository blacklistRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder encoder;

    // ✅ REGISTER
    @Transactional
    public UserResponseDTO register(RegisterDTO dto) {

        Area area = areaRepo.findById(dto.areaId)
                .orElseThrow(() -> new RuntimeException("Area not found"));

        User user = new User();
        user.setName(dto.name);
        user.setPhone(dto.phone);
        user.setEmail(dto.email);
        user.setPassword(encoder.encode(dto.password));
        user.setAddress(dto.address);
        user.setRole("ROLE_USER");
        user.setArea(area);

        user = userRepo.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.userId = user.getUserId();
        response.name = user.getName();
        response.phone = user.getPhone();
        response.email = user.getEmail();
        response.address = user.getAddress();
        response.role = user.getRole();
        response.areaName = area.getName();

        return response;
    }

    // ✅ LOGIN
    @Transactional
    public LoginResponseDTO login(LoginDTO dto) {

        // 1️⃣ USER / ADMIN
        User user = userRepo.findByPhone(dto.getPhone());

        if (user != null &&
                encoder.matches(dto.getPassword(), user.getPassword())) {

            refreshRepo.deleteByUserId(user.getUserId());

            String access =
                    jwtUtil.generateAccessToken(
                            user.getUserId(),
                            user.getRole());

            String refresh =
                    jwtUtil.generateRefreshToken(user.getUserId());

            RefreshToken rt = new RefreshToken();
            rt.setUserId(user.getUserId());
            rt.setToken(refresh);
            rt.setExpiry(Instant.now()
                    .plus(7, ChronoUnit.DAYS));

            refreshRepo.save(rt);

            LoginResponseDTO response = new LoginResponseDTO();
            response.accessToken = access;
            response.refreshToken = refresh;
            response.role = user.getRole();

            return response;
        }

        // 2️⃣ VENDOR
        Vendor vendor = vendorRepo.findByPhone(dto.getPhone());

        if (vendor != null &&
                encoder.matches(dto.getPassword(), vendor.getPassword())) {

            String access =
                    jwtUtil.generateAccessToken(
                            vendor.getVendorId(),
                            "ROLE_VENDOR");

            String refresh =
                    jwtUtil.generateRefreshToken(
                            vendor.getVendorId());

            LoginResponseDTO response = new LoginResponseDTO();
            response.accessToken = access;
            response.refreshToken = refresh;
            response.role = "ROLE_VENDOR";

            return response;
        }

        throw new RuntimeException("Invalid credentials");
    }

    // ✅ REFRESH
    public LoginResponseDTO refresh(String refreshToken) {

        RefreshToken rt =
                refreshRepo.findByToken(refreshToken)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid refresh token"));

        if (rt.getExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        String newAccess =
                jwtUtil.generateAccessToken(
                        rt.getUserId(),
                        "ROLE_USER");

        LoginResponseDTO response = new LoginResponseDTO();
        response.accessToken = newAccess;
        response.refreshToken = refreshToken;
        response.role = "ROLE_USER";

        return response;
    }

    // ✅ LOGOUT
    public void logout(String header) {

        String token = header.substring(7);

        TokenBlacklist bl = new TokenBlacklist();
        bl.setToken(token);
        bl.setExpiry(
                jwtUtil.extractExpiry(token).toInstant()
        );

        blacklistRepo.save(bl);
    }
}