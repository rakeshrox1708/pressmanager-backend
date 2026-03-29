package com.newspaper.System.controller;

import com.newspaper.System.dto.*;
import com.newspaper.System.dto.request.RefreshRequestDTO;
import com.newspaper.System.dto.response.LoginResponseDTO;
import com.newspaper.System.dto.response.UserResponseDTO;
import com.newspaper.System.response.ApiResponse;
import com.newspaper.System.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ✅ REGISTER
    @PostMapping("/register")
    public ApiResponse<UserResponseDTO> register(
            @RequestBody RegisterDTO dto) {

        UserResponseDTO user =
                authService.register(dto);

        return new ApiResponse<>(
                true,
                "User registered successfully",
                user
        );
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(
            @RequestBody LoginDTO dto) {

        LoginResponseDTO response =
                authService.login(dto);

        return new ApiResponse<>(
                true,
                "Login successful",
                response
        );
    }

    // ✅ REFRESH
    @PostMapping("/refresh")
    public ApiResponse<LoginResponseDTO> refresh(
            @RequestBody RefreshRequestDTO dto) {

        LoginResponseDTO response =
                authService.refresh(dto.refreshToken);

        return new ApiResponse<>(
                true,
                "Token refreshed",
                response
        );
    }

    // ✅ LOGOUT
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestHeader("Authorization") String header) {

        authService.logout(header);

        return new ApiResponse<>(
                true,
                "Logged out successfully",
                null
        );
    }
}