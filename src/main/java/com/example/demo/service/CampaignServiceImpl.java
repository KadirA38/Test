package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CampaignDTO;
import com.example.demo.entity.Campaign;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CampaignRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CampaignServiceImpl implements CampaignService {
    
    @Autowired
    private CampaignRepository campaignRepository;
    
    @Override
    public CampaignDTO createCampaign(CampaignDTO campaignDTO) {
        Campaign campaign = new Campaign();
        campaign.setName(campaignDTO.getName());
        campaign.setDescription(campaignDTO.getDescription());
        campaign.setDiscountPercentage(campaignDTO.getDiscountPercentage());
        campaign.setStartDate(campaignDTO.getStartDate());
        campaign.setEndDate(campaignDTO.getEndDate());
        campaign.setIsActive(true);
        
        Campaign savedCampaign = campaignRepository.save(campaign);
        log.info("Kampanya oluşturuldu: {}", savedCampaign.getId());
        
        return convertToDTO(savedCampaign);
    }
    
    @Override
    public CampaignDTO getCampaignById(Long id) {
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kampanya bulunamadı: " + id));
        return convertToDTO(campaign);
    }
    
    @Override
    public List<CampaignDTO> getAllCampaigns() {
        return campaignRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CampaignDTO> getActiveCampaigns() {
        return campaignRepository.findByIsActiveTrue().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public CampaignDTO updateCampaign(Long id, CampaignDTO campaignDTO) {
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kampanya bulunamadı: " + id));
        
        campaign.setName(campaignDTO.getName());
        campaign.setDescription(campaignDTO.getDescription());
        campaign.setDiscountPercentage(campaignDTO.getDiscountPercentage());
        campaign.setStartDate(campaignDTO.getStartDate());
        campaign.setEndDate(campaignDTO.getEndDate());
        
        Campaign updatedCampaign = campaignRepository.save(campaign);
        log.info("Kampanya güncellendi: {}", id);
        
        return convertToDTO(updatedCampaign);
    }
    
    @Override
    public void deleteCampaign(Long id) {
        if (!campaignRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kampanya bulunamadı: " + id);
        }
        campaignRepository.deleteById(id);
        log.info("Kampanya silindi: {}", id);
    }
    
    @Override
    public void activateCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kampanya bulunamadı: " + id));
        campaign.setIsActive(true);
        campaignRepository.save(campaign);
        log.info("Kampanya aktif edildi: {}", id);
    }
    
    @Override
    public void deactivateCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kampanya bulunamadı: " + id));
        campaign.setIsActive(false);
        campaignRepository.save(campaign);
        log.info("Kampanya deaktif edildi: {}", id);
    }
    
    private CampaignDTO convertToDTO(Campaign campaign) {
        CampaignDTO dto = new CampaignDTO();
        dto.setId(campaign.getId());
        dto.setName(campaign.getName());
        dto.setDescription(campaign.getDescription());
        dto.setDiscountPercentage(campaign.getDiscountPercentage());
        dto.setStartDate(campaign.getStartDate());
        dto.setEndDate(campaign.getEndDate());
        dto.setIsActive(campaign.getIsActive());
        dto.setCreatedAt(campaign.getCreatedAt());
        dto.setUpdatedAt(campaign.getUpdatedAt());
        return dto;
    }
}
