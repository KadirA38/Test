package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.EventOrganizerDTO;
import com.example.demo.entity.EventOrganizer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventOrganizerRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventOrganizerServiceImpl implements EventOrganizerService {
    
    @Autowired
    private EventOrganizerRepository eventOrganizerRepository;
    
    @Override
    public EventOrganizerDTO createEventOrganizer(EventOrganizerDTO organizerDTO) {
        EventOrganizer organizer = new EventOrganizer();
        organizer.setName(organizerDTO.getName());
        organizer.setEmail(organizerDTO.getEmail());
        organizer.setPhone(organizerDTO.getPhone());
        organizer.setBio(organizerDTO.getBio());
        organizer.setFoundedYear(organizerDTO.getFoundedYear());
        organizer.setWebsite(organizerDTO.getWebsite());
        organizer.setIsVerified(false);
        
        EventOrganizer savedOrganizer = eventOrganizerRepository.save(organizer);
        log.info("Etkinlik düzenleyen oluşturuldu: {}", savedOrganizer.getId());
        
        return convertToDTO(savedOrganizer);
    }
    
    @Override
    public EventOrganizerDTO getEventOrganizerById(Long id) {
        EventOrganizer organizer = eventOrganizerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Etkinlik düzenleyen bulunamadı: " + id));
        return convertToDTO(organizer);
    }
    
    @Override
    public EventOrganizerDTO getEventOrganizerByEmail(String email) {
        EventOrganizer organizer = eventOrganizerRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Email bulunamadı: " + email));
        return convertToDTO(organizer);
    }
    
    @Override
    public EventOrganizerDTO getEventOrganizerByName(String name) {
        EventOrganizer organizer = eventOrganizerRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("İsim bulunamadı: " + name));
        return convertToDTO(organizer);
    }
    
    @Override
    public List<EventOrganizerDTO> getAllEventOrganizers() {
        return eventOrganizerRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventOrganizerDTO> getAllVerifiedOrganizers() {
        return eventOrganizerRepository.findAllVerified().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventOrganizerDTO> getTopRatedOrganizers() {
        return eventOrganizerRepository.findTopRatedOrganizers().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventOrganizerDTO> getMostActiveOrganizers() {
        return eventOrganizerRepository.findMostActiveOrganizers().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventOrganizerDTO> getOrganizersByMinRating(Double minRating) {
        return eventOrganizerRepository.findByMinRating(minRating).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public EventOrganizerDTO updateEventOrganizer(Long id, EventOrganizerDTO organizerDTO) {
        EventOrganizer organizer = eventOrganizerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Etkinlik düzenleyen bulunamadı: " + id));
        
        organizer.setName(organizerDTO.getName());
        organizer.setEmail(organizerDTO.getEmail());
        organizer.setPhone(organizerDTO.getPhone());
        organizer.setBio(organizerDTO.getBio());
        organizer.setFoundedYear(organizerDTO.getFoundedYear());
        organizer.setWebsite(organizerDTO.getWebsite());
        
        EventOrganizer updatedOrganizer = eventOrganizerRepository.save(organizer);
        log.info("Etkinlik düzenleyen güncellendi: {}", id);
        
        return convertToDTO(updatedOrganizer);
    }
    
    @Override
    public void deleteEventOrganizer(Long id) {
        if (!eventOrganizerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Etkinlik düzenleyen bulunamadı: " + id);
        }
        eventOrganizerRepository.deleteById(id);
        log.info("Etkinlik düzenleyen silindi: {}", id);
    }
    
    @Override
    public void verifyOrganizer(Long id) {
        EventOrganizer organizer = eventOrganizerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Etkinlik düzenleyen bulunamadı: " + id));
        organizer.setIsVerified(true);
        eventOrganizerRepository.save(organizer);
        log.info("Etkinlik düzenleyen doğrulandı: {}", id);
    }
    
    @Override
    public void updateRating(Long id, Double newRating, Integer reviewCount) {
        EventOrganizer organizer = eventOrganizerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Etkinlik düzenleyen bulunamadı: " + id));
        organizer.setAverageRating(newRating);
        organizer.setReviewCount(reviewCount);
        eventOrganizerRepository.save(organizer);
        log.info("Etkinlik düzenleyen puanı güncellendi: {} - Puan: {}", id, newRating);
    }
    
    @Override
    public Page<EventOrganizerDTO> getAllOrganizersPaginated(Pageable pageable) {
        return eventOrganizerRepository.findAll(pageable)
            .map(this::convertToDTO);
    }
    
    private EventOrganizerDTO convertToDTO(EventOrganizer organizer) {
        EventOrganizerDTO dto = new EventOrganizerDTO();
        dto.setId(organizer.getId());
        dto.setName(organizer.getName());
        dto.setEmail(organizer.getEmail());
        dto.setPhone(organizer.getPhone());
        dto.setBio(organizer.getBio());
        dto.setFoundedYear(organizer.getFoundedYear());
        dto.setTotalEvents(organizer.getTotalEvents());
        dto.setTotalParticipants(organizer.getTotalParticipants());
        dto.setWebsite(organizer.getWebsite());
        dto.setAverageRating(organizer.getAverageRating());
        dto.setReviewCount(organizer.getReviewCount());
        dto.setIsVerified(organizer.getIsVerified());
        dto.setCreatedAt(organizer.getCreatedAt());
        dto.setUpdatedAt(organizer.getUpdatedAt());
        return dto;
    }
}
