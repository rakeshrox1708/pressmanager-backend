package com.newspaper.System.controller;

import com.newspaper.System.dto.ChangePasswordDTO;
import com.newspaper.System.dto.UserUpdateDTO;
import com.newspaper.System.dto.response.UserResponseDTO;
import com.newspaper.System.model.User;
import com.newspaper.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping
    public UserResponseDTO getProfile(Authentication auth) {
        int userId = Integer.parseInt(auth.getName());

        User user = userRepo.findById(userId).orElseThrow();

        UserResponseDTO dto = new UserResponseDTO();
        dto.userId = user.getUserId();
        dto.name = user.getName();
        dto.phone = user.getPhone();
        dto.email = user.getEmail();
        dto.address = user.getAddress();

        return dto;
    }

    @PutMapping("/update")
    public String updateProfile(Authentication auth,
                                @RequestBody UserUpdateDTO dto) {

        int userId = Integer.parseInt(auth.getName());
        User user = userRepo.findById(userId).orElseThrow();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());

        userRepo.save(user);

        return "Profile updated";
    }

    @PutMapping("/change-password")
    public String changePassword(Authentication auth,
                                 @RequestBody ChangePasswordDTO dto) {

        int userId = Integer.parseInt(auth.getName());
        User user = userRepo.findById(userId).orElseThrow();

        if (!encoder.matches(dto.oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPassword(encoder.encode(dto.newPassword));
        userRepo.save(user);

        return "Password changed successfully";
    }
}