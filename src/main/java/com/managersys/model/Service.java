package com.managersys.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;

@Entity
@Table(name = "services")
public class Service extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

    @Column(name = "requires_appointment", nullable = false)
    private boolean requiresAppointment = true;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "preparation_instructions", columnDefinition = "TEXT")
    private String preparationInstructions;

    @Column(name = "cancellation_policy", columnDefinition = "TEXT")
    private String cancellationPolicy;

    @Column(name = "is_recurring")
    private boolean recurring;

    @Column(name = "recurrence_pattern")
    private String recurrencePattern; // e.g., "DAILY", "WEEKLY", "MONTHLY"

    @Column(name = "buffer_time_minutes")
    private Integer bufferTimeMinutes;

    @Column(name = "is_online_booking_available", nullable = false)
    private boolean onlineBookingAvailable = true;

    @Column(name = "deposit_required", nullable = false)
    private boolean depositRequired = false;

    @Column(name = "deposit_amount", precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "is_tax_inclusive", nullable = false)
    private boolean taxInclusive = false;

    // Constructors
    public Service() {}

    public Service(String name, String description, BigDecimal price, Integer durationMinutes) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
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

    public ServiceCategory getCategory() {
        return category;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
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

    // Business methods
    public BigDecimal calculateTotalPrice() {
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) <= 0) {
            return price;
        }
        
        if (taxInclusive) {
            return price;
        } else {
            return price.add(price.multiply(taxRate.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)));
        }
    }

    public BigDecimal calculateDepositAmount() {
        if (!depositRequired || depositAmount == null) {
            return BigDecimal.ZERO;
        }
        return depositAmount;
    }

    public Duration getDuration() {
        return durationMinutes != null ? Duration.ofMinutes(durationMinutes) : null;
    }

    public boolean isAvailableForOnlineBooking() {
        return active && onlineBookingAvailable;
    }

    public boolean canAccommodateParticipants(int participantCount) {
        return maxParticipants == null || participantCount <= maxParticipants;
    }
}
