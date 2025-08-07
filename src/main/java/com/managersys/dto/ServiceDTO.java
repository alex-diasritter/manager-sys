package com.managersys.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ServiceDTO {

    private Long id;

    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name cannot exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits before and 2 after decimal")
    private BigDecimal price;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private boolean active = true;

    private Long categoryId;

    private String categoryName;

    private boolean requiresAppointment = true;

    @Min(value = 1, message = "Maximum participants must be at least 1")
    private Integer maxParticipants;

    @Size(max = 2000, message = "Preparation instructions cannot exceed 2000 characters")
    private String preparationInstructions;

    @Size(max = 1000, message = "Cancellation policy cannot exceed 1000 characters")
    private String cancellationPolicy;

    private boolean recurring;
    private String recurrencePattern;
    private Integer bufferTimeMinutes;
    private boolean onlineBookingAvailable = true;
    private boolean depositRequired = false;
    private BigDecimal depositAmount;

    @DecimalMin(value = "0.00", message = "Tax rate cannot be negative")
    @DecimalMax(value = "100.00", message = "Tax rate cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Tax rate must have up to 3 digits before and 2 after decimal")
    private BigDecimal taxRate;

    private boolean taxInclusive = false;

    // Constructors
    public ServiceDTO() {}

    public ServiceDTO(Long id, String name, String description, BigDecimal price, Integer durationMinutes, boolean active, Long categoryId, String categoryName, boolean requiresAppointment, Integer maxParticipants, String preparationInstructions, String cancellationPolicy, boolean recurring, String recurrencePattern, Integer bufferTimeMinutes, boolean onlineBookingAvailable, boolean depositRequired, BigDecimal depositAmount, BigDecimal taxRate, boolean taxInclusive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.active = active;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.requiresAppointment = requiresAppointment;
        this.maxParticipants = maxParticipants;
        this.preparationInstructions = preparationInstructions;
        this.cancellationPolicy = cancellationPolicy;
        this.recurring = recurring;
        this.recurrencePattern = recurrencePattern;
        this.bufferTimeMinutes = bufferTimeMinutes;
        this.onlineBookingAvailable = onlineBookingAvailable;
        this.depositRequired = depositRequired;
        this.depositAmount = depositAmount;
        this.taxRate = taxRate;
        this.taxInclusive = taxInclusive;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isRequiresAppointment() {
        return requiresAppointment;
    }

    public void setRequiresAppointment(boolean requiresAppointment) {
        this.requiresAppointment = requiresAppointment;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getPreparationInstructions() {
        return preparationInstructions;
    }

    public void setPreparationInstructions(String preparationInstructions) {
        this.preparationInstructions = preparationInstructions;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
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

    public Integer getBufferTimeMinutes() {
        return bufferTimeMinutes;
    }

    public void setBufferTimeMinutes(Integer bufferTimeMinutes) {
        this.bufferTimeMinutes = bufferTimeMinutes;
    }

    public boolean isOnlineBookingAvailable() {
        return onlineBookingAvailable;
    }

    public void setOnlineBookingAvailable(boolean onlineBookingAvailable) {
        this.onlineBookingAvailable = onlineBookingAvailable;
    }

    public boolean isDepositRequired() {
        return depositRequired;
    }

    public void setDepositRequired(boolean depositRequired) {
        this.depositRequired = depositRequired;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public boolean isTaxInclusive() {
        return taxInclusive;
    }

    public void setTaxInclusive(boolean taxInclusive) {
        this.taxInclusive = taxInclusive;
    }

    // Static factory method to convert from entity to DTO
    public static ServiceDTO fromEntity(Service service) {
        if (service == null) {
            return null;
        }

        return new ServiceDTO(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.isActive(),
                service.getCategory() != null ? service.getCategory().getId() : null,
                service.getCategory() != null ? service.getCategory().getName() : null,
                service.isRequiresAppointment(),
                service.getMaxParticipants(),
                service.getPreparationInstructions(),
                service.getCancellationPolicy(),
                service.isRecurring(),
                service.getRecurrencePattern(),
                service.getBufferTimeMinutes(),
                service.isOnlineBookingAvailable(),
                service.isDepositRequired(),
                service.getDepositAmount(),
                service.getTaxRate(),
                service.isTaxInclusive()
        );
    }

    // Convert DTO to entity
    public Service toEntity() {
        Service service = new Service();
        service.setId(this.id);
        service.setName(this.name);
        service.setDescription(this.description);
        service.setPrice(this.price);
        service.setDurationMinutes(this.durationMinutes);
        service.setActive(this.active);
        service.setRequiresAppointment(this.requiresAppointment);
        service.setMaxParticipants(this.maxParticipants);
        service.setPreparationInstructions(this.preparationInstructions);
        service.setCancellationPolicy(this.cancellationPolicy);
        service.setRecurring(this.recurring);
        service.setRecurrencePattern(this.recurrencePattern);
        service.setBufferTimeMinutes(this.bufferTimeMinutes);
        service.setOnlineBookingAvailable(this.onlineBookingAvailable);
        service.setDepositRequired(this.depositRequired);
        service.setDepositAmount(this.depositAmount);
        service.setTaxRate(this.taxRate);
        service.setTaxInclusive(this.taxInclusive);
        
        return service;
    }
}
