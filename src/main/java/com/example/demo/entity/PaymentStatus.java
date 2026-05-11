package com.example.demo.entity;

/**
 * PaymentStatus - Ödeme Durumları
 */
public enum PaymentStatus {
    PENDING("Beklemede"),
    PAID("Ödendi"),
    FAILED("Başarısız"),
    REFUNDED("İade Yapıldı"),
    CANCELLED("İptal Edildi"),
    PROCESSING("İşleniyor");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
