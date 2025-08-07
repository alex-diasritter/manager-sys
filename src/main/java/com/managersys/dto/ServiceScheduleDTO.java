package com.managersys.dto;

import com.managersys.model.ServiceSchedule;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ServiceScheduleDTO {

    private Long id;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private String serviceName;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String employeeName;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private String customerName;

    @NotNull(message = "Start date and time is required")
    @FutureOrPresent(message = "Start date and time must be in the present or future")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time is required")
    @FutureOrPresent(message = "End date and time must be in the present or future")
    private LocalDateTime endDateTime;

    private String status;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    private boolean recurring = false;
    private String recurrencePattern;
    private LocalDateTime recurrenceEndDate;
    private boolean onlineBooking = false;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer actualDurationMinutes;
    private String feedback;
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;
    
    private boolean paid = false;
    private BigDecimal paymentAmount;
    private String paymentMethod;
    private String paymentReference;
    private LocalDateTime paymentDate;
    
    // Construtores
    public ServiceScheduleDTO() {
    }
    
    public ServiceScheduleDTO(Long id, Long serviceId, String serviceName, Long employeeId, String employeeName,
                            Long customerId, String customerName, LocalDateTime startDateTime, LocalDateTime endDateTime,
                            String status, String notes, boolean recurring, String recurrencePattern,
                            LocalDateTime recurrenceEndDate, boolean onlineBooking, String cancellationReason,
                            LocalDateTime cancelledAt, LocalDateTime checkInTime, LocalDateTime checkOutTime,
                            Integer actualDurationMinutes, String feedback, Integer rating, boolean paid,
                            BigDecimal paymentAmount, String paymentMethod, String paymentReference,
                            LocalDateTime paymentDate) {
        this.id = id;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.notes = notes;
        this.recurring = recurring;
        this.recurrencePattern = recurrencePattern;
        this.recurrenceEndDate = recurrenceEndDate;
        this.onlineBooking = onlineBooking;
        this.cancellationReason = cancellationReason;
        this.cancelledAt = cancelledAt;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.actualDurationMinutes = actualDurationMinutes;
        this.feedback = feedback;
        this.rating = rating;
        this.paid = paid;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentReference = paymentReference;
        this.paymentDate = paymentDate;
    }

    // Static factory method to convert from entity to DTO
    public static ServiceScheduleDTO fromEntity(ServiceSchedule schedule) {
        if (schedule == null) {
            return null;
        }

        String employeeFullName = null;
        if (schedule.getEmployee() != null) {
            employeeFullName = schedule.getEmployee().getFirstName() + " " + 
                             (schedule.getEmployee().getLastName() != null ? 
                              schedule.getEmployee().getLastName() : "");
        }
        
        return new ServiceScheduleDTO(
            schedule.getId(),
            schedule.getService() != null ? schedule.getService().getId() : null,
            schedule.getService() != null ? schedule.getService().getName() : null,
            schedule.getEmployee() != null ? schedule.getEmployee().getId() : null,
            employeeFullName,
            schedule.getCustomer() != null ? schedule.getCustomer().getId() : null,
            schedule.getCustomer() != null ? schedule.getCustomer().getName() : null,
            schedule.getStartDateTime(),
            schedule.getEndDateTime(),
            schedule.getStatus() != null ? schedule.getStatus().name() : null,
            schedule.getNotes(),
            schedule.isRecurring(),
            schedule.getRecurrencePattern(),
            schedule.getRecurrenceEndDate() != null ? schedule.getRecurrenceEndDate().atStartOfDay() : null,
            schedule.isOnlineBooking(),
            schedule.getCancellationReason(),
            schedule.getCancelledAt(),
            schedule.getCheckInTime(),
            schedule.getCheckOutTime(),
            schedule.getActualDurationMinutes(),
            schedule.getFeedback(),
            schedule.getRating(),
            schedule.isPaid(),
            schedule.getPaymentAmount(),
            schedule.getPaymentMethod(),
            schedule.getPaymentReference(),
            schedule.getPaymentDate()
        );
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public LocalDateTime getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setRecurrenceEndDate(LocalDateTime recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }

    public boolean isOnlineBooking() {
        return onlineBooking;
    }

    public void setOnlineBooking(boolean onlineBooking) {
        this.onlineBooking = onlineBooking;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Integer getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public void setActualDurationMinutes(Integer actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceScheduleDTO that = (ServiceScheduleDTO) o;
        return recurring == that.recurring &&
               onlineBooking == that.onlineBooking &&
               paid == that.paid &&
               Objects.equals(id, that.id) &&
               Objects.equals(serviceId, that.serviceId) &&
               Objects.equals(serviceName, that.serviceName) &&
               Objects.equals(employeeId, that.employeeId) &&
               Objects.equals(employeeName, that.employeeName) &&
               Objects.equals(customerId, that.customerId) &&
               Objects.equals(customerName, that.customerName) &&
               Objects.equals(startDateTime, that.startDateTime) &&
               Objects.equals(endDateTime, that.endDateTime) &&
               Objects.equals(status, that.status) &&
               Objects.equals(notes, that.notes) &&
               Objects.equals(recurrencePattern, that.recurrencePattern) &&
               Objects.equals(recurrenceEndDate, that.recurrenceEndDate) &&
               Objects.equals(cancellationReason, that.cancellationReason) &&
               Objects.equals(cancelledAt, that.cancelledAt) &&
               Objects.equals(checkInTime, that.checkInTime) &&
               Objects.equals(checkOutTime, that.checkOutTime) &&
               Objects.equals(actualDurationMinutes, that.actualDurationMinutes) &&
               Objects.equals(feedback, that.feedback) &&
               Objects.equals(rating, that.rating) &&
               Objects.equals(paymentAmount, that.paymentAmount) &&
               Objects.equals(paymentMethod, that.paymentMethod) &&
               Objects.equals(paymentReference, that.paymentReference) &&
               Objects.equals(paymentDate, that.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serviceId, serviceName, employeeId, employeeName, customerId, customerName,
                startDateTime, endDateTime, status, notes, recurring, recurrencePattern, recurrenceEndDate,
                onlineBooking, cancellationReason, cancelledAt, checkInTime, checkOutTime, actualDurationMinutes,
                feedback, rating, paid, paymentAmount, paymentMethod, paymentReference, paymentDate);
    }

    @Override
    public String toString() {
        return "ServiceScheduleDTO{" +
               "id=" + id +
               ", serviceId=" + serviceId +
               ", serviceName='" + serviceName + '\'' +
               ", employeeId=" + employeeId +
               ", employeeName='" + employeeName + '\'' +
               ", customerId=" + customerId +
               ", customerName='" + customerName + '\'' +
               ", startDateTime=" + startDateTime +
               ", endDateTime=" + endDateTime +
               ", status='" + status + '\'' +
               ", notes='" + notes + '\'' +
               ", recurring=" + recurring +
               ", recurrencePattern='" + recurrencePattern + '\'' +
               ", recurrenceEndDate=" + recurrenceEndDate +
               ", onlineBooking=" + onlineBooking +
               ", cancellationReason='" + cancellationReason + '\'' +
               ", cancelledAt=" + cancelledAt +
               ", checkInTime=" + checkInTime +
               ", checkOutTime=" + checkOutTime +
               ", actualDurationMinutes=" + actualDurationMinutes +
               ", feedback='" + feedback + '\'' +
               ", rating=" + rating +
               ", paid=" + paid +
               ", paymentAmount=" + paymentAmount +
               ", paymentMethod='" + paymentMethod + '\'' +
               ", paymentReference='" + paymentReference + '\'' +
               ", paymentDate=" + paymentDate +
               '}';
    }
    
    // Convert DTO to entity
    public ServiceSchedule toEntity() {
        ServiceSchedule schedule = new ServiceSchedule();
        schedule.setId(this.id);
        schedule.setStartDateTime(this.startDateTime);
        schedule.setEndDateTime(this.endDateTime);
        schedule.setStatus(this.status != null ? ServiceSchedule.Status.valueOf(this.status) : null);
        schedule.setNotes(this.notes);
        schedule.setRecurring(this.recurring);
        schedule.setRecurrencePattern(this.recurrencePattern);
        schedule.setRecurrenceEndDate(this.recurrenceEndDate != null ? this.recurrenceEndDate.toLocalDate() : null);
        schedule.setOnlineBooking(this.onlineBooking);
        schedule.setCancellationReason(this.cancellationReason);
        schedule.setCancelledAt(this.cancelledAt);
        schedule.setCheckInTime(this.checkInTime);
        schedule.setCheckOutTime(this.checkOutTime);
        schedule.setActualDurationMinutes(this.actualDurationMinutes);
        schedule.setFeedback(this.feedback);
        schedule.setRating(this.rating);
        schedule.setPaid(this.paid);
        schedule.setPaymentAmount(this.paymentAmount);
        schedule.setPaymentMethod(this.paymentMethod);
        schedule.setPaymentReference(this.paymentReference);
        schedule.setPaymentDate(this.paymentDate);
        
        // As associações serão configuradas pelo serviço
        return schedule;
    }
}
