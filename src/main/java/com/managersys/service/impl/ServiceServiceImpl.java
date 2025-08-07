package com.managersys.service.impl;

import com.managersys.dto.ServiceDTO;
import com.managersys.exception.ResourceNotFoundException;
import com.managersys.model.Service;
import com.managersys.model.ServiceCategory;
import com.managersys.repository.CategoryRepository;
import com.managersys.repository.ServiceRepository;
import com.managersys.service.ServiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository, CategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ServiceDTO createService(ServiceDTO serviceDTO) {
        Service service = serviceDTO.toEntity();
        
        // Set category if provided
        if (serviceDTO.getCategoryId() != null) {
            ServiceCategory category = categoryRepository.findById(serviceDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", serviceDTO.getCategoryId()));
            service.setCategory(category);
        }
        
        // Set default values if not provided
        if (service.getActive() == null) {
            service.setActive(true);
        }
        
        Service savedService = serviceRepository.save(service);
        return ServiceDTO.fromEntity(savedService);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceDTO getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        return ServiceDTO.fromEntity(service);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> getAllServices(Pageable pageable) {
        return serviceRepository.findAll(pageable)
                .map(ServiceDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> searchServices(String query, Pageable pageable) {
        return serviceRepository.search(query, pageable)
                .map(ServiceDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> getServicesByCategory(Long categoryId, Pageable pageable) {
        return serviceRepository.findByCategoryId(categoryId, pageable)
                .map(ServiceDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> getAvailableForOnlineBooking(Pageable pageable) {
        return serviceRepository.findAvailableForOnlineBooking(pageable)
                .map(ServiceDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> getAvailableForOnlineBookingByCategory(Long categoryId, Pageable pageable) {
        return serviceRepository.findAvailableForOnlineBookingByCategory(categoryId, pageable)
                .map(ServiceDTO::fromEntity);
    }

    @Override
    @Transactional
    public ServiceDTO updateService(Long id, ServiceDTO serviceDTO) {
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        
        // Update fields
        existingService.setName(serviceDTO.getName());
        existingService.setDescription(serviceDTO.getDescription());
        existingService.setPrice(serviceDTO.getPrice());
        existingService.setDurationMinutes(serviceDTO.getDurationMinutes());
        existingService.setActive(serviceDTO.isActive());
        existingService.setRequiresAppointment(serviceDTO.isRequiresAppointment());
        existingService.setMaxParticipants(serviceDTO.getMaxParticipants());
        existingService.setPreparationInstructions(serviceDTO.getPreparationInstructions());
        existingService.setCancellationPolicy(serviceDTO.getCancellationPolicy());
        existingService.setRecurring(serviceDTO.isRecurring());
        existingService.setRecurrencePattern(serviceDTO.getRecurrencePattern());
        existingService.setBufferTimeMinutes(serviceDTO.getBufferTimeMinutes());
        existingService.setOnlineBookingAvailable(serviceDTO.isOnlineBookingAvailable());
        existingService.setDepositRequired(serviceDTO.isDepositRequired());
        existingService.setDepositAmount(serviceDTO.getDepositAmount());
        existingService.setTaxRate(serviceDTO.getTaxRate());
        existingService.setTaxInclusive(serviceDTO.isTaxInclusive());
        
        // Update category if changed
        if (serviceDTO.getCategoryId() != null && 
            (existingService.getCategory() == null || 
             !existingService.getCategory().getId().equals(serviceDTO.getCategoryId()))) {
            
            ServiceCategory category = categoryRepository.findById(serviceDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", serviceDTO.getCategoryId()));
            existingService.setCategory(category);
        } else if (serviceDTO.getCategoryId() == null && existingService.getCategory() != null) {
            existingService.setCategory(null);
        }
        
        Service updatedService = serviceRepository.save(existingService);
        return ServiceDTO.fromEntity(updatedService);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        
        // Check if service has any schedules
        if (!service.getSchedules().isEmpty()) {
            throw new IllegalStateException("Cannot delete service with existing schedules");
        }
        
        serviceRepository.delete(service);
    }

    @Override
    @Transactional
    public void toggleServiceStatus(Long id, boolean active) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        
        service.setActive(active);
        serviceRepository.save(service);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> getServicesByEmployee(Long employeeId, Pageable pageable) {
        return serviceRepository.findByEmployeeId(employeeId, pageable)
                .map(ServiceDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> getServicesByCustomer(Long customerId, Pageable pageable) {
        return serviceRepository.findByCustomerId(customerId, pageable)
                .map(ServiceDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> getServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice == null) {
            minPrice = BigDecimal.ZERO;
        }
        
        if (maxPrice == null) {
            maxPrice = new BigDecimal("999999.99");
        }
        
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        
        return serviceRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(ServiceDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> getServicesWithUpcomingAppointments(Pageable pageable) {
        return serviceRepository.findWithUpcomingAppointments(pageable)
                .map(ServiceDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getServiceUtilization(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both start date and end date are required");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        
        return serviceRepository.findMostPopularServices(startDate, endDate, Pageable.unpaged())
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getServiceRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation would depend on your revenue calculation logic
        // This is a placeholder for the actual implementation
        return List.of();
    }
}
