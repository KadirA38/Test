package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

import lombok.extern.slf4j.Slf4j;

/**
 * PaymentService - Stripe Ödeme İşlemleri
 */
@Service
@Slf4j
public class PaymentService {
    
    @Value("${stripe.api.key}")
    private String stripeApiKey;
    
    public PaymentService(@Value("${stripe.api.key}") String stripeApiKey) {
        Stripe.apiKey = stripeApiKey;
    }
    
    /**
     * Ödeme niyeti oluştur (Ödeme öncesi kontrol)
     */
    public PaymentIntent createPaymentIntent(Long amountInCents, String description, String customerId) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("try") // Türk Lirası
                .setDescription(description)
                .setCustomer(customerId)
                .build();
            
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            log.info("PaymentIntent oluşturuldu: {}", paymentIntent.getId());
            return paymentIntent;
        } catch (StripeException e) {
            log.error("PaymentIntent oluşturma başarısız: {}", e.getMessage());
            throw new RuntimeException("Ödeme işlemi başlatılamadı", e);
        }
    }
    
    /**
     * Müşteri oluştur
     */
    public Customer createCustomer(String email, String name) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("email", email);
            params.put("name", name);
            
            Customer customer = Customer.create(params);
            log.info("Müşteri oluşturuldu: {}", customer.getId());
            return customer;
        } catch (StripeException e) {
            log.error("Müşteri oluşturma başarısız: {}", e.getMessage());
            throw new RuntimeException("Müşteri oluşturulamadı", e);
        }
    }
    
    /**
     * Ödeme işlemi gerçekleştir
     */
    public Charge chargeCard(Long amountInCents, String source, String description) {
        try {
            ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("try") // Türk Lirası
                .setSource(source)
                .setDescription(description)
                .build();
            
            Charge charge = Charge.create(params);
            log.info("Ödeme işlemi tamamlandı: {}", charge.getId());
            return charge;
        } catch (StripeException e) {
            log.error("Ödeme işlemi başarısız: {}", e.getMessage());
            throw new RuntimeException("Ödeme işlemi başarısız", e);
        }
    }
    
    /**
     * İade işlemi gerçekleştir
     */
    public Refund refundCharge(String chargeId, Long amountInCents) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                .setCharge(chargeId)
                .setAmount(amountInCents)
                .build();
            
            Refund refund = Refund.create(params);
            log.info("İade işlemi tamamlandı: {}", refund.getId());
            return refund;
        } catch (StripeException e) {
            log.error("İade işlemi başarısız: {}", e.getMessage());
            throw new RuntimeException("İade işlemi başarısız", e);
        }
    }
    
    /**
     * PaymentIntent'i al
     */
    public PaymentIntent getPaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            log.error("PaymentIntent alınamadı: {}", e.getMessage());
            throw new RuntimeException("PaymentIntent alınamadı", e);
        }
    }
    
    /**
     * Ödeme durumunu kontrol et
     */
    public String getPaymentStatus(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return paymentIntent.getStatus();
        } catch (StripeException e) {
            log.error("Ödeme durumu kontrol edilemedi: {}", e.getMessage());
            throw new RuntimeException("Ödeme durumu kontrol edilemedi", e);
        }
    }
}
