package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.TicketDTO;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.entity.TicketType;
import com.example.demo.service.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * TicketController - Bilet Yönetimi Endpoint'leri
 */
@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Bilet Yönetimi", description = "Bilet satın alma ve yönetimi")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Bilet Getir", description = "ID ile bilet bilgilerini al")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        TicketDTO ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }
    
    @GetMapping("/number/{ticketNumber}")
    @Operation(summary = "Bilet Numarasıyla Getir", description = "Bilet numarası ile bilet al")
    public ResponseEntity<TicketDTO> getTicketByNumber(@PathVariable String ticketNumber) {
        TicketDTO ticket = ticketService.getTicketByNumber(ticketNumber);
        return ResponseEntity.ok(ticket);
    }
    
    @PostMapping("/purchase")
    @Operation(summary = "Bilet Satın Al", description = "Etkinlik için bilet satın al")
    public ResponseEntity<TicketDTO> purchaseTicket(
            @RequestParam Long eventId,
            @RequestParam Long participantId,
            @RequestParam(required = false, defaultValue = "STANDARD") String ticketType) {
        TicketType type = TicketType.valueOf(ticketType.toUpperCase());
        TicketDTO ticket = ticketService.purchaseTicket(eventId, participantId, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
    
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Etkinlik Biletleri", description = "Etkinliğe ait tüm biletleri al")
    public ResponseEntity<Page<TicketDTO>> getTicketsByEvent(
            @PathVariable Long eventId,
            Pageable pageable) {
        Page<TicketDTO> tickets = ticketService.getTicketsByEventPaginated(eventId, pageable);
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/event/{eventId}/paid")
    @Operation(summary = "Etkinlik Ödenen Biletleri", description = "Etkinliğe ait ödenen biletleri al")
    public ResponseEntity<Page<TicketDTO>> getPaidTicketsByEvent(
            @PathVariable Long eventId,
            Pageable pageable) {
        Page<TicketDTO> tickets = ticketService.getTicketsByStatusPaginated(PaymentStatus.PAID, pageable);
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/event/{eventId}/revenue")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    @Operation(summary = "Etkinlik Geliri", description = "Etkinlikten elde edilen toplam geliri al")
    public ResponseEntity<Double> getEventRevenue(@PathVariable Long eventId) {
        Double revenue = ticketService.getEventRevenue(eventId);
        return ResponseEntity.ok(revenue);
    }
    
    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Katılımcı Biletleri", description = "Katılımcının tüm biletlerini al")
    public ResponseEntity<Page<TicketDTO>> getTicketsByParticipant(
            @PathVariable Long participantId,
            Pageable pageable) {
        Page<TicketDTO> tickets = ticketService.getTicketsByParticipantPaginated(participantId, pageable);
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Durum Bazlı Biletler", description = "Durum bazlı biletleri al (Admin)")
    public ResponseEntity<Page<TicketDTO>> getTicketsByStatus(
            @PathVariable String status,
            Pageable pageable) {
        PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        Page<TicketDTO> tickets = ticketService.getTicketsByStatusPaginated(paymentStatus, pageable);
        return ResponseEntity.ok(tickets);
    }
    
    @PostMapping("/{id}/mark-paid")
    @Operation(summary = "Ödendi Olarak İşaretle", description = "Bileti ödendi olarak işaretle")
    public ResponseEntity<String> markAsPaid(
            @PathVariable Long id,
            @RequestParam String transactionId) {
        ticketService.markAsPaid(id, transactionId);
        return ResponseEntity.ok("Bilet ödendi olarak işaretlendi");
    }
    
    @PostMapping("/{id}/mark-used")
    @Operation(summary = "Kullanıldı Olarak İşaretle", description = "Bileti kullanıldı olarak işaretle")
    public ResponseEntity<String> markAsUsed(@PathVariable Long id) {
        ticketService.markAsUsed(id);
        return ResponseEntity.ok("Bilet kullanıldı olarak işaretlendi");
    }
    
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bilet İadesi", description = "Bileti iade et (Admin)")
    public ResponseEntity<String> refundTicket(@PathVariable Long id) {
        ticketService.refundTicket(id);
        return ResponseEntity.ok("Bilet iadesi yapıldı");
    }
    
    @GetMapping("/{id}/qr-code")
    @Operation(summary = "QR Kod Oluştur", description = "Bilet için QR kod oluştur")
    public ResponseEntity<String> generateQRCode(@PathVariable Long id) {
        String qrCode = ticketService.generateQRCode(id);
        return ResponseEntity.ok(qrCode);
    }
}
