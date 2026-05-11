package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.TicketDTO;
import com.example.demo.entity.Event;
import com.example.demo.entity.Participant;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketType;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ParticipantRepository;
import com.example.demo.repository.TicketRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Override
    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Event event = eventRepository.findById(ticketDTO.getEventId())
            .orElseThrow(() -> new ResourceNotFoundException("Etkinlik bulunamadı"));
        
        Participant participant = participantRepository.findById(ticketDTO.getParticipantId())
            .orElseThrow(() -> new ResourceNotFoundException("Katılımcı bulunamadı"));
        
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setParticipant(participant);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setTicketType(ticketDTO.getTicketType());
        ticket.setPrice(ticketDTO.getPrice());
        ticket.setDiscountAmount(ticketDTO.getDiscountAmount());
        ticket.setFinalPrice(ticketDTO.getFinalPrice());
        ticket.setPaymentStatus(PaymentStatus.PENDING);
        ticket.setPaymentMethod(PaymentMethod.NONE);
        
        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Bilet oluşturuldu: {}", savedTicket.getTicketNumber());
        
        return convertToDTO(savedTicket);
    }
    
    @Override
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bilet bulunamadı: " + id));
        return convertToDTO(ticket);
    }
    
    @Override
    public TicketDTO getTicketByNumber(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Bilet bulunamadı: " + ticketNumber));
        return convertToDTO(ticket);
    }
    
    @Override
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public TicketDTO updateTicket(Long id, TicketDTO ticketDTO) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bilet bulunamadı: " + id));
        
        if (ticketDTO.getNotes() != null) {
            ticket.setNotes(ticketDTO.getNotes());
        }
        if (ticketDTO.getDiscountAmount() != null) {
            ticket.setDiscountAmount(ticketDTO.getDiscountAmount());
            ticket.setFinalPrice(ticket.getPrice() - ticketDTO.getDiscountAmount());
        }
        
        Ticket updatedTicket = ticketRepository.save(ticket);
        log.info("Bilet güncellendi: {}", id);
        
        return convertToDTO(updatedTicket);
    }
    
    @Override
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bilet bulunamadı: " + id);
        }
        ticketRepository.deleteById(id);
        log.info("Bilet silindi: {}", id);
    }
    
    @Override
    public TicketDTO purchaseTicket(Long eventId, Long participantId, TicketType ticketType) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Etkinlik bulunamadı"));
        
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new ResourceNotFoundException("Katılımcı bulunamadı"));
        
        if (ticketRepository.existsByEventIdAndParticipantId(eventId, participantId)) {
            throw new IllegalArgumentException("Bu katılımcı zaten bu etkinlik için bilet satın almıştır");
        }
        
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setParticipant(participant);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setTicketType(ticketType);
        ticket.setPrice(event.getPrice());
        ticket.setDiscountAmount(0.0);
        ticket.setFinalPrice(event.getPrice());
        ticket.setPaymentStatus(PaymentStatus.PENDING);
        ticket.setPaymentMethod(PaymentMethod.NONE);
        ticket.setExpiresAt(LocalDateTime.now().plusHours(1)); // 1 saat süre
        
        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Bilet satın alındı: {} - {}", savedTicket.getTicketNumber(), participantId);
        
        return convertToDTO(savedTicket);
    }
    
    @Override
    public void markAsPaid(Long ticketId, String transactionId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Bilet bulunamadı: " + ticketId));
        
        ticket.setPaymentStatus(PaymentStatus.PAID);
        ticket.setTransactionId(transactionId);
        ticketRepository.save(ticket);
        log.info("Bilet ödendi: {} - Transaction: {}", ticketId, transactionId);
    }
    
    @Override
    public void markAsFailed(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Bilet bulunamadı: " + ticketId));
        
        ticket.setPaymentStatus(PaymentStatus.FAILED);
        ticketRepository.save(ticket);
        log.info("Bilet ödeme başarısız: {}", ticketId);
    }
    
    @Override
    public void refundTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Bilet bulunamadı: " + ticketId));
        
        ticket.setPaymentStatus(PaymentStatus.REFUNDED);
        ticketRepository.save(ticket);
        log.info("Bilet iadesi yapıldı: {}", ticketId);
    }
    
    @Override
    public List<TicketDTO> getTicketsByEvent(Long eventId) {
        return ticketRepository.findByEventId(eventId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<TicketDTO> getPaidTicketsByEvent(Long eventId) {
        return ticketRepository.findPaidTicketsByEvent(eventId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<TicketDTO> getTicketsByEventPaginated(Long eventId, Pageable pageable) {
        return ticketRepository.findByEventId(eventId, pageable)
            .map(this::convertToDTO);
    }
    
    @Override
    public Long countPaidTicketsByEvent(Long eventId) {
        return ticketRepository.countPaidTicketsByEvent(eventId);
    }
    
    @Override
    public Double getEventRevenue(Long eventId) {
        return ticketRepository.sumPaidTicketsRevenueByEvent(eventId);
    }
    
    @Override
    public List<TicketDTO> getTicketsByParticipant(Long participantId) {
        return ticketRepository.findByParticipantId(participantId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<TicketDTO> getPaidTicketsByParticipant(Long participantId) {
        return ticketRepository.findPaidTicketsByParticipant(participantId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<TicketDTO> getTicketsByParticipantPaginated(Long participantId, Pageable pageable) {
        return ticketRepository.findByParticipantId(participantId, pageable)
            .map(this::convertToDTO);
    }
    
    @Override
    public List<TicketDTO> getTicketsByStatus(PaymentStatus status) {
        return ticketRepository.findByPaymentStatus(status).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<TicketDTO> getTicketsByStatusPaginated(PaymentStatus status, Pageable pageable) {
        return ticketRepository.findByPaymentStatus(status, pageable)
            .map(this::convertToDTO);
    }
    
    @Override
    public String generateQRCode(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Bilet bulunamadı: " + ticketId));
        
        // QR Code URL simülasyonu
        String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + ticket.getTicketNumber();
        ticket.setQrCodeUrl(qrCodeUrl);
        ticketRepository.save(ticket);
        
        return qrCodeUrl;
    }
    
    @Override
    public boolean validateQRCode(String qrCode) {
        return ticketRepository.findByTicketNumber(qrCode).isPresent();
    }
    
    @Override
    public void markAsUsed(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Bilet bulunamadı: " + ticketId));
        
        ticket.setIsUsed(true);
        ticket.setUsedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        log.info("Bilet kullanıldı: {}", ticketId);
    }
    
    @Override
    public List<TicketDTO> getUnusedTickets(Long eventId) {
        return ticketRepository.findByEventId(eventId).stream()
            .filter(t -> !t.getIsUsed())
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<TicketDTO> getTicketsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return ticketRepository.findTicketsBetweenDates(startDate, endDate).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean hasTicketForEvent(Long participantId, Long eventId) {
        return ticketRepository.existsByEventIdAndParticipantId(eventId, participantId);
    }
    
    private String generateTicketNumber() {
        return "TKT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setEventId(ticket.getEvent().getId());
        dto.setParticipantId(ticket.getParticipant().getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setTicketType(ticket.getTicketType());
        dto.setPrice(ticket.getPrice());
        dto.setDiscountAmount(ticket.getDiscountAmount());
        dto.setFinalPrice(ticket.getFinalPrice());
        dto.setPurchaseDate(ticket.getPurchaseDate());
        dto.setPaymentStatus(ticket.getPaymentStatus());
        dto.setPaymentMethod(ticket.getPaymentMethod());
        dto.setTransactionId(ticket.getTransactionId());
        dto.setQrCodeUrl(ticket.getQrCodeUrl());
        dto.setIsUsed(ticket.getIsUsed());
        dto.setUsedAt(ticket.getUsedAt());
        dto.setExpiresAt(ticket.getExpiresAt());
        dto.setNotes(ticket.getNotes());
        return dto;
    }
}
