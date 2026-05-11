package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PaymentStatus;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketType;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    
    List<Ticket> findByEventId(Long eventId);
    
    List<Ticket> findByParticipantId(Long participantId);
    
    List<Ticket> findByPaymentStatus(PaymentStatus paymentStatus);
    
    List<Ticket> findByTicketType(TicketType ticketType);
    
    List<Ticket> findByEventIdAndPaymentStatus(Long eventId, PaymentStatus paymentStatus);
    
    List<Ticket> findByParticipantIdAndPaymentStatus(Long participantId, PaymentStatus paymentStatus);
    
    @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId AND t.paymentStatus = 'PAID'")
    List<Ticket> findPaidTicketsByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT t FROM Ticket t WHERE t.participant.id = :participantId AND t.paymentStatus = 'PAID'")
    List<Ticket> findPaidTicketsByParticipant(@Param("participantId") Long participantId);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event.id = :eventId AND t.paymentStatus = 'PAID'")
    Long countPaidTicketsByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT COALESCE(SUM(t.finalPrice), 0) FROM Ticket t WHERE t.event.id = :eventId AND t.paymentStatus = 'PAID'")
    Double sumPaidTicketsRevenueByEvent(@Param("eventId") Long eventId);
    
    Page<Ticket> findByEventId(Long eventId, Pageable pageable);
    
    Page<Ticket> findByParticipantId(Long participantId, Pageable pageable);
    
    Page<Ticket> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
    
    @Query("SELECT t FROM Ticket t WHERE t.expiresAt < CURRENT_TIMESTAMP AND t.paymentStatus = 'PENDING'")
    List<Ticket> findExpiredPendingTickets();
    
    @Query("SELECT t FROM Ticket t WHERE t.purchaseDate BETWEEN :startDate AND :endDate")
    List<Ticket> findTicketsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    boolean existsByEventIdAndParticipantId(Long eventId, Long participantId);
}
