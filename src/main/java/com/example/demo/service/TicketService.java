package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.TicketDTO;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.entity.TicketType;

public interface TicketService {
    
    // CRUD
    TicketDTO createTicket(TicketDTO ticketDTO);
    TicketDTO getTicketById(Long id);
    TicketDTO getTicketByNumber(String ticketNumber);
    List<TicketDTO> getAllTickets();
    TicketDTO updateTicket(Long id, TicketDTO ticketDTO);
    void deleteTicket(Long id);
    
    // Satın Alma ve Ödeme
    TicketDTO purchaseTicket(Long eventId, Long participantId, TicketType ticketType);
    void markAsPaid(Long ticketId, String transactionId);
    void markAsFailed(Long ticketId);
    void refundTicket(Long ticketId);
    
    // Event bazlı sorgular
    List<TicketDTO> getTicketsByEvent(Long eventId);
    List<TicketDTO> getPaidTicketsByEvent(Long eventId);
    Page<TicketDTO> getTicketsByEventPaginated(Long eventId, Pageable pageable);
    Long countPaidTicketsByEvent(Long eventId);
    Double getEventRevenue(Long eventId);
    
    // Participant bazlı sorgular
    List<TicketDTO> getTicketsByParticipant(Long participantId);
    List<TicketDTO> getPaidTicketsByParticipant(Long participantId);
    Page<TicketDTO> getTicketsByParticipantPaginated(Long participantId, Pageable pageable);
    
    // Durum bazlı sorgular
    List<TicketDTO> getTicketsByStatus(PaymentStatus status);
    Page<TicketDTO> getTicketsByStatusPaginated(PaymentStatus status, Pageable pageable);
    
    // QR Code
    String generateQRCode(Long ticketId);
    boolean validateQRCode(String qrCode);
    
    // Kullanım
    void markAsUsed(Long ticketId);
    List<TicketDTO> getUnusedTickets(Long eventId);
    
    // Tarih bazlı
    List<TicketDTO> getTicketsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    // Kontrol
    boolean hasTicketForEvent(Long participantId, Long eventId);
}
