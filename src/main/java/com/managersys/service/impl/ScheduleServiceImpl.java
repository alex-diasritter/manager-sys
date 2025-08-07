package com.managersys.service.impl;

import com.managersys.dto.ServiceScheduleDTO;
import com.managersys.exception.InvalidScheduleException;
import com.managersys.exception.ResourceNotFoundException;
import com.managersys.model.*;
import com.managersys.repository.CustomerRepository;
import com.managersys.repository.EmployeeRepository;
import com.managersys.repository.ServiceRepository;
import com.managersys.repository.ScheduleRepository;
import com.managersys.service.ScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, ServiceRepository serviceRepository,
                               EmployeeRepository employeeRepository, CustomerRepository customerRepository) {
        this.scheduleRepository = scheduleRepository;
        this.serviceRepository = serviceRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public ServiceScheduleDTO createSchedule(ServiceScheduleDTO scheduleDTO) {
        // Add null checks
        if (scheduleDTO == null) {
            throw new IllegalArgumentException("Schedule DTO cannot be null");
        }
        if (scheduleDTO.getServiceId() == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }
        if (scheduleDTO.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (scheduleDTO.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (scheduleDTO.getStartDateTime() == null) {
            throw new IllegalArgumentException("Start date time cannot be null");
        }
        if (scheduleDTO.getEndDateTime() == null) {
            throw new IllegalArgumentException("End date time cannot be null");
        }
        if (scheduleDTO.getStartDateTime().isAfter(scheduleDTO.getEndDateTime())) {
            throw new IllegalArgumentException("Start date time must be before end date time");
        }
        
        // Validate and fetch related entities
        Service service = serviceRepository.findById(scheduleDTO.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", scheduleDTO.getServiceId()));
        
        Employee employee = employeeRepository.findById(scheduleDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", scheduleDTO.getEmployeeId()));
        
        Customer customer = customerRepository.findById(scheduleDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", scheduleDTO.getCustomerId()));
        
        // Validate time slot
        if (!isTimeSlotAvailable(
                scheduleDTO.getServiceId(), 
                scheduleDTO.getEmployeeId(), 
                scheduleDTO.getStartDateTime(), 
                scheduleDTO.getEndDateTime(), 
                null)) {
            throw new InvalidScheduleException("The selected time slot is not available");
        }
        
        // Create and save the schedule
        ServiceSchedule schedule = scheduleDTO.toEntity();
        schedule.setService(service);
        schedule.setEmployee(employee);
        schedule.setCustomer(customer);
        schedule.setStatus(ServiceSchedule.Status.SCHEDULED);
        
        // Set default values
        if (schedule.getNotes() == null) {
            schedule.setNotes("");
        }
        
        ServiceSchedule savedSchedule = scheduleRepository.save(schedule);
        return ServiceScheduleDTO.fromEntity(savedSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceScheduleDTO getScheduleById(Long id) {
        // Add null checks
        if (id == null) {
            throw new IllegalArgumentException("Schedule ID cannot be null");
        }
        
        ServiceSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        return ServiceScheduleDTO.fromEntity(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> getAllSchedules(Pageable pageable) {
        // Add null checks
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        return scheduleRepository.findAll(pageable)
                .map(ServiceScheduleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> getSchedulesByService(Long serviceId, Pageable pageable) {
        // Add null checks
        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        return scheduleRepository.findByServiceId(serviceId, pageable)
                .map(ServiceScheduleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> getSchedulesByEmployee(Long employeeId, Pageable pageable) {
        // Add null checks
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        return scheduleRepository.findByEmployeeId(employeeId, pageable)
                .map(ServiceScheduleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> getSchedulesByCustomer(Long customerId, Pageable pageable) {
        // Add null checks
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        return scheduleRepository.findByCustomerId(customerId, pageable)
                .map(ServiceScheduleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        // Add null checks
        if (start == null) {
            throw new IllegalArgumentException("Start date time cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("End date time cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        
        return scheduleRepository.findByStartDateTimeBetween(start, end, pageable)
                .map(ServiceScheduleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> getAvailableTimeSlots(Long serviceId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        // Add null checks
        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }
        if (start == null) {
            throw new IllegalArgumentException("Start date time cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("End date time cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        // This is a simplified implementation
        // In a real application, you would calculate available slots based on business hours,
        // existing appointments, service duration, etc.
        return scheduleRepository.findAvailableTimeSlots(serviceId, start, end, pageable)
                .map(ServiceScheduleDTO::fromEntity);
    }

    @Override
    @Transactional
    public ServiceScheduleDTO updateSchedule(Long id, ServiceScheduleDTO scheduleDTO) {
        // Add null checks
        if (id == null) {
            throw new IllegalArgumentException("Schedule ID cannot be null");
        }
        if (scheduleDTO == null) {
            throw new IllegalArgumentException("Schedule DTO cannot be null");
        }
        if (scheduleDTO.getStartDateTime() == null) {
            throw new IllegalArgumentException("Start date time cannot be null");
        }
        if (scheduleDTO.getEndDateTime() == null) {
            throw new IllegalArgumentException("End date time cannot be null");
        }
        if (scheduleDTO.getStartDateTime().isAfter(scheduleDTO.getEndDateTime())) {
            throw new IllegalArgumentException("Start date time must be before end date time");
        }
        
        ServiceSchedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        
        // Check if the schedule can be modified
        if (!canModifySchedule(existingSchedule)) {
            throw new InvalidScheduleException("This schedule cannot be modified");
        }
        
        // Update fields
        existingSchedule.setStartDateTime(scheduleDTO.getStartDateTime());
        existingSchedule.setEndDateTime(scheduleDTO.getEndDateTime());
        existingSchedule.setNotes(scheduleDTO.getNotes());
        
        // Update service if changed
        if (scheduleDTO.getServiceId() != null && !existingSchedule.getService().getId().equals(scheduleDTO.getServiceId())) {
            Service service = serviceRepository.findById(scheduleDTO.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service", "id", scheduleDTO.getServiceId()));
            existingSchedule.setService(service);
        }
        
        // Update employee if changed
        if (scheduleDTO.getEmployeeId() != null && !existingSchedule.getEmployee().getId().equals(scheduleDTO.getEmployeeId())) {
            Employee employee = employeeRepository.findById(scheduleDTO.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", scheduleDTO.getEmployeeId()));
            existingSchedule.setEmployee(employee);
        }
        
        // Validate the updated time slot
        if (!isTimeSlotAvailable(
                existingSchedule.getService().getId(),
                existingSchedule.getEmployee().getId(),
                existingSchedule.getStartDateTime(),
                existingSchedule.getEndDateTime(),
                existingSchedule.getId())) {
            throw new InvalidScheduleException("The selected time slot is not available");
        }
        
        ServiceSchedule updatedSchedule = scheduleRepository.save(existingSchedule);
        return ServiceScheduleDTO.fromEntity(updatedSchedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        // Add null checks
        if (id == null) {
            throw new IllegalArgumentException("Schedule ID cannot be null");
        }
        
        ServiceSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        
        if (!canModifySchedule(schedule)) {
            throw new InvalidScheduleException("This schedule cannot be deleted");
        }
        
        scheduleRepository.delete(schedule);
    }

    @Override
    @Transactional
    public ServiceScheduleDTO updateScheduleStatus(Long id, String status, String reason) {
        // Add null checks
        if (id == null) {
            throw new IllegalArgumentException("Schedule ID cannot be null");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        
        ServiceSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        
        try {
            ServiceSchedule.Status newStatus = ServiceSchedule.Status.valueOf(status.toUpperCase());
            
            // Validate status transition
            if (!isValidStatusTransition(schedule.getStatus(), newStatus)) {
                throw new InvalidScheduleException("Invalid status transition");
            }
            
            // Handle status-specific logic
            switch (newStatus) {
                case CANCELLED:
                    if (reason == null || reason.trim().isEmpty()) {
                        throw new IllegalArgumentException("Cancellation reason is required");
                    }
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    Long currentUserId;
                    try {
                        currentUserId = Long.parseLong(authentication.getName());
                    } catch (NumberFormatException e) {
                        // Fallback to a default user ID or handle differently based on your authentication system
                        currentUserId = 1L; // Or throw a more specific exception
                    }
                    schedule.cancel(reason, currentUserId);
                    break;
                case CONFIRMED:
                    // Send confirmation notification
                    break;
                case IN_PROGRESS:
                    schedule.checkIn();
                    break;
                case COMPLETED:
                    // This should be handled by the checkOut method
                    throw new InvalidScheduleException("Use checkOut method to complete a schedule");
                case NO_SHOW:
                    schedule.markAsNoShow();
                    break;
            }
            
            schedule.setStatus(newStatus);
            ServiceSchedule updatedSchedule = scheduleRepository.save(schedule);
            return ServiceScheduleDTO.fromEntity(updatedSchedule);
            
        } catch (IllegalArgumentException e) {
            throw new InvalidScheduleException("Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public ServiceScheduleDTO checkIn(Long scheduleId) {
        // Add null checks
        if (scheduleId == null) {
            throw new IllegalArgumentException("Schedule ID cannot be null");
        }
        
        ServiceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));
        
        if (schedule.getStatus() != ServiceSchedule.Status.CONFIRMED && 
            schedule.getStatus() != ServiceSchedule.Status.SCHEDULED) {
            throw new InvalidScheduleException("Only CONFIRMED or SCHEDULED schedules can be checked in");
        }
        
        schedule.checkIn();
        ServiceSchedule updatedSchedule = scheduleRepository.save(schedule);
        return ServiceScheduleDTO.fromEntity(updatedSchedule);
    }

    @Override
    @Transactional
    public ServiceScheduleDTO checkOut(Long scheduleId, String feedback, Integer rating) {
        // Add null checks
        if (scheduleId == null) {
            throw new IllegalArgumentException("Schedule ID cannot be null");
        }
        
        ServiceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));
        
        if (schedule.getStatus() != ServiceSchedule.Status.IN_PROGRESS) {
            throw new InvalidScheduleException("Only schedules IN_PROGRESS can be checked out");
        }
        
        schedule.complete(feedback, rating);
        ServiceSchedule updatedSchedule = scheduleRepository.save(schedule);
        return ServiceScheduleDTO.fromEntity(updatedSchedule);
    }

    @Override
    @Transactional
    public ServiceScheduleDTO recordPayment(Long scheduleId, BigDecimal amount, String method, String reference) {
        // Add null checks
        if (scheduleId == null) {
            throw new IllegalArgumentException("Schedule ID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment reference cannot be null or empty");
        }
        
        ServiceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));
        
        if (schedule.isPaid()) {
            throw new InvalidScheduleException("This schedule is already paid");
        }
        
        schedule.recordPayment(amount, method, reference);
        ServiceSchedule updatedSchedule = scheduleRepository.save(schedule);
        return ServiceScheduleDTO.fromEntity(updatedSchedule);
    }

    @Override
    @Transactional
    public List<ServiceScheduleDTO> createRecurringSchedule(ServiceScheduleDTO scheduleDTO, String frequency, int occurrences, LocalDateTime endDate) {
        // Add null checks
        if (scheduleDTO == null) {
            throw new IllegalArgumentException("Schedule DTO cannot be null");
        }
        if (frequency == null || frequency.trim().isEmpty()) {
            throw new IllegalArgumentException("Frequency cannot be null or empty");
        }
        if (occurrences <= 0 && endDate == null) {
            throw new IllegalArgumentException("Either occurrences must be positive or endDate must be provided");
        }
        
        List<ServiceScheduleDTO> createdSchedules = new ArrayList<>();
        LocalDateTime currentStart = scheduleDTO.getStartDateTime();
        LocalDateTime currentEnd = scheduleDTO.getEndDateTime();
        
        // Calculate duration between start and end for each occurrence
        Duration duration = Duration.between(currentStart, currentEnd);
        
        // Create the first schedule
        ServiceScheduleDTO firstSchedule = createSchedule(scheduleDTO);
        createdSchedules.add(firstSchedule);
        
        // Create recurring schedules
        int count = 1;
        while ((occurrences > 0 && count < occurrences) || 
               (endDate != null && currentStart.isBefore(endDate))) {
            
            // Calculate next occurrence based on frequency
            switch (frequency.toUpperCase()) {
                case "DAILY":
                    currentStart = currentStart.plusDays(1);
                    currentEnd = currentStart.plus(duration);
                    break;
                case "WEEKLY":
                    currentStart = currentStart.plusWeeks(1);
                    currentEnd = currentStart.plus(duration);
                    break;
                case "MONTHLY":
                    currentStart = currentStart.plusMonths(1);
                    currentEnd = currentStart.plus(duration);
                    break;
                default:
                    throw new InvalidScheduleException("Invalid frequency: " + frequency);
            }
            
            // Stop if we've reached the end date
            if (endDate != null && currentStart.isAfter(endDate)) {
                break;
            }
            
            // Create the next schedule
            scheduleDTO.setStartDateTime(currentStart);
            scheduleDTO.setEndDateTime(currentEnd);
            scheduleDTO.setRecurring(true);
            
            ServiceScheduleDTO created = createSchedule(scheduleDTO);
            createdSchedules.add(created);
            
            count++;
            
            // Stop if we've reached the maximum number of occurrences
            if (occurrences > 0 && count >= occurrences) {
                break;
            }
        }
        
        return createdSchedules;
    }

    @Override
    @Transactional
    public void cancelRecurringSchedule(Long recurringScheduleId, String reason) {
        // Add null checks
        if (recurringScheduleId == null) {
            throw new IllegalArgumentException("Recurring schedule ID cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancellation reason cannot be null or empty");
        }
        
        // Find the original schedule
        ServiceSchedule originalSchedule = scheduleRepository.findById(recurringScheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", recurringScheduleId));
        
        if (!originalSchedule.isRecurring()) {
            throw new InvalidScheduleException("Schedule is not part of a recurring series");
        }
        
        // Find all related recurring schedules
        // This is a simplified implementation - in a real application, you would need
        // a proper way to track recurring schedule relationships
        List<ServiceSchedule> recurringSchedules = scheduleRepository.findRecurringSchedules(
                originalSchedule.getService().getId(),
                originalSchedule.getEmployee().getId(),
                originalSchedule.getCustomer().getId(),
                originalSchedule.getStartDateTime().toLocalDate()
        );
        
        // Cancel all future schedules in the series
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId;
        try {
            currentUserId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            // Fallback to a default user ID or handle differently based on your authentication system
            currentUserId = 1L; // Or throw a more specific exception
        }
        
        for (ServiceSchedule schedule : recurringSchedules) {
            if (schedule.getStartDateTime().isAfter(LocalDateTime.now()) && 
                canModifySchedule(schedule)) {
                schedule.cancel(reason, currentUserId);
                scheduleRepository.save(schedule);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> getUpcomingSchedules(Pageable pageable) {
        // Add null checks
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        return scheduleRepository.findByStartDateTimeAfterAndStatusIn(
                LocalDateTime.now(),
                List.of(ServiceSchedule.Status.SCHEDULED, ServiceSchedule.Status.CONFIRMED),
                pageable)
                .map(ServiceScheduleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> getPastSchedules(Pageable pageable) {
        // Add null checks
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        return scheduleRepository.findByEndDateTimeBefore(
                LocalDateTime.now(),
                pageable)
                .map(ServiceScheduleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceScheduleDTO> getConflictingSchedules(Long serviceId, Long employeeId, LocalDateTime start, LocalDateTime end) {
        // Add null checks
        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (start == null) {
            throw new IllegalArgumentException("Start date time cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("End date time cannot be null");
        }
        
        return scheduleRepository.findConflictingSchedules(serviceId, employeeId, start, end).stream()
                .map(ServiceScheduleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(Long serviceId, Long employeeId, LocalDateTime start, LocalDateTime end, Long excludeScheduleId) {
        // Add null checks
        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (start == null) {
            throw new IllegalArgumentException("Start date time cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("End date time cannot be null");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date time must be before end date time");
        }
        
        List<ServiceSchedule> conflicts = scheduleRepository.findConflictingSchedules(
                serviceId, employeeId, start, end);
        
        if (excludeScheduleId != null) {
            conflicts = conflicts.stream()
                    .filter(s -> !s.getId().equals(excludeScheduleId))
                    .collect(Collectors.toList());
        }
        
        return conflicts.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> getSchedulesByStatus(String status, Pageable pageable) {
        // Add null checks
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        try {
            ServiceSchedule.Status statusEnum = ServiceSchedule.Status.valueOf(status.toUpperCase());
            return scheduleRepository.findByStatus(statusEnum, pageable)
                    .map(ServiceScheduleDTO::fromEntity);
        } catch (IllegalArgumentException e) {
            throw new InvalidScheduleException("Invalid status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceScheduleDTO> searchSchedules(String query, Pageable pageable) {
        // Add null checks
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        return scheduleRepository.search(query, pageable)
                .map(ServiceScheduleDTO::fromEntity);
    }
    
    // Helper methods
    private boolean canModifySchedule(ServiceSchedule schedule) {
        // Only allow modification if the schedule is in a modifiable state
        return schedule.getStatus() == ServiceSchedule.Status.SCHEDULED || 
               schedule.getStatus() == ServiceSchedule.Status.CONFIRMED;
    }
    
    private boolean isValidStatusTransition(ServiceSchedule.Status current, ServiceSchedule.Status next) {
        // Define valid status transitions
        switch (current) {
            case SCHEDULED:
                return next == ServiceSchedule.Status.CONFIRMED || 
                       next == ServiceSchedule.Status.CANCELLED;
            case CONFIRMED:
                return next == ServiceSchedule.Status.IN_PROGRESS || 
                       next == ServiceSchedule.Status.CANCELLED ||
                       next == ServiceSchedule.Status.NO_SHOW;
            case IN_PROGRESS:
                return next == ServiceSchedule.Status.COMPLETED;
            case COMPLETED:
            case CANCELLED:
            case NO_SHOW:
                return false;
            default:
                return false;
        }
    }
}
