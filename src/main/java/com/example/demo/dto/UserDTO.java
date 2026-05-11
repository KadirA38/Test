package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserDTO - Kullanıcı Veri Transfer Nesnesi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String password; // Sadece giriş/kayıt için
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePictureUrl;
    private String bio;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private UserRole userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return firstName != null ? firstName : lastName;
    }
}
