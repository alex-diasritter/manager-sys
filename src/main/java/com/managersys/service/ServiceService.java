package com.managersys.service;

import com.managersys.dto.ServiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ServiceService {
    
    ServiceDTO createService(ServiceDTO serviceDTO);
    
    ServiceDTO getServiceById(Long id);
    
    Page<ServiceDTO> getAllServices(Pageable pageable);
    
    Page<ServiceDTO> searchServices(String query, Pageable pageable);
    
    Page<ServiceDTO> getServicesByCategory(Long categoryId, Pageable pageable);
    
    Page<ServiceDTO> getAvailableForOnlineBooking(Pageable pageable);
    
    Page<ServiceDTO> getAvailableForOnlineBookingByCategory(Long categoryId, Pageable pageable);
    
    ServiceDTO updateService(Long id, ServiceDTO serviceDTO);
    
    void deleteService(Long id);
    
    void toggleServiceStatus(Long id, boolean active);
    
    Page<ServiceDTO> getServicesByEmployee(Long employeeId, Pageable pageable);
    
    Page<ServiceDTO> getServicesByCustomer(Long customerId, Pageable pageable);
    
    Page<ServiceDTO> getServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    Page<ServiceDTO> getServicesWithUpcomingAppointments(Pageable pageable);
    
    List<Object[]> getServiceUtilization(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Object[]> getServiceRevenue(LocalDateTime startDate, LocalDateTime endDate);
}
