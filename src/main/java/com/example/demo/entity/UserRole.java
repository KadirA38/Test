package com.example.demo.entity;

/**
 * UserRole - Kullanıcı Rolleri
 */
public enum UserRole {
    USER("Kullanıcı"),
    ORGANIZER("Etkinlik Düzenleyen"),
    ADMIN("Yönetici"),
    SUPER_ADMIN("Süper Yönetici"),
    MODERATOR("Moderatör");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
