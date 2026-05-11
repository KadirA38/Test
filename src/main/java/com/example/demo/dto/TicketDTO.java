package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.entity.TicketType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TicketDTO - Bilet Veri Transfer Nesnesi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long id;
    private Long eventId;
    private Long participantId;
    private String ticketNumber;
    private TicketType ticketType;
    private Double price;
    private Double discountAmount;
    private Double finalPrice;
    private LocalDateTime purchaseDate;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String qrCodeUrl;
    private Boolean isUsed;
    private LocalDateTime usedAt;
    private LocalDateTime expiresAt;
    private String notes;
}
