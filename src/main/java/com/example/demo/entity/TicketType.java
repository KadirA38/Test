package com.example.demo.entity;

/**
 * TicketType - Bilet Türleri
 */
public enum TicketType {
    STANDARD("Standart"),
    VIP("VIP"),
    EARLY_BIRD("Erken Kayıt"),
    GROUP("Grup"),
    STUDENT("Öğrenci"),
    PREMIUM("Premium");
    
    private final String displayName;
    
    TicketType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
