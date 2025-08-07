package com.managersys.service;

import com.managersys.dto.ServiceScheduleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    
    ServiceScheduleDTO createSchedule(ServiceScheduleDTO scheduleDTO);
    
    ServiceScheduleDTO getScheduleById(Long id);
    
    Page<ServiceScheduleDTO> getAllSchedules(Pageable pageable);
    
    Page<ServiceScheduleDTO> getSchedulesByService(Long serviceId, Pageable pageable);
    
    Page<ServiceScheduleDTO> getSchedulesByEmployee(Long employeeId, Pageable pageable);
    
    Page<ServiceScheduleDTO> getSchedulesByCustomer(Long customerId, Pageable pageable);
    
    Page<ServiceScheduleDTO> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<ServiceScheduleDTO> getAvailableTimeSlots(Long serviceId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    ServiceScheduleDTO updateSchedule(Long id, ServiceScheduleDTO scheduleDTO);
    
    void deleteSchedule(Long id);
    
    ServiceScheduleDTO updateScheduleStatus(Long id, String status, String reason);
    
    ServiceScheduleDTO checkIn(Long scheduleId);
    
    ServiceScheduleDTO checkOut(Long scheduleId, String feedback, Integer rating);
    
    ServiceScheduleDTO recordPayment(Long scheduleId, BigDecimal amount, String method, String reference);
    
    List<ServiceScheduleDTO> createRecurringSchedule(ServiceScheduleDTO scheduleDTO, String frequency, int occurrences, LocalDateTime endDate);
    
    void cancelRecurringSchedule(Long recurringScheduleId, String reason);
    
    Page<ServiceScheduleDTO> getUpcomingSchedules(Pageable pageable);
    
    Page<ServiceScheduleDTO> getPastSchedules(Pageable pageable);
    
    List<ServiceScheduleDTO> getConflictingSchedules(Long serviceId, Long employeeId, LocalDateTime start, LocalDateTime end);
    
    boolean isTimeSlotAvailable(Long serviceId, Long employeeId, LocalDateTime start, LocalDateTime end, Long excludeScheduleId);
    
    Page<ServiceScheduleDTO> getSchedulesByStatus(String status, Pageable pageable);
    
    Page<ServiceScheduleDTO> searchSchedules(String query, Pageable pageable);
}
