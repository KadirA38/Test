package com.example.demo.entity;

/**
 * PaymentMethod - Ödeme Yöntemleri
 */
public enum PaymentMethod {
    NONE("Seçilmedi"),
    CREDIT_CARD("Kredi Kartı"),
    DEBIT_CARD("Banka Kartı"),
    BANK_TRANSFER("Havale/EFT"),
    PAYPAL("PayPal"),
    STRIPE("Stripe"),
    APPLE_PAY("Apple Pay"),
    GOOGLE_PAY("Google Pay"),
    CRYPTOCURRENCY("Kripto Para");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
