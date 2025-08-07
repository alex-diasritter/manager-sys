package com.managersys.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Table(name = "service_schedules")
public class ServiceSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_recurring", nullable = false)
    private boolean recurring = false;

    @Column(name = "recurrence_pattern")
    private String recurrencePattern; // e.g., "DAILY", "WEEKLY", "MONTHLY"

    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    @Column(name = "is_online_booking", nullable = false)
    private boolean onlineBooking = false;

    @Column(name = "confirmation_sent", nullable = false)
    private boolean confirmationSent = false;

    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "cancelled_by_id")
    private Long cancelledById;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "rating")
    private Integer rating; // 1-5 stars

    @Column(name = "is_paid", nullable = false)
    private boolean paid = false;

    @Column(name = "payment_amount", precision = 10, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // Status enum
    public enum Status {
        SCHEDULED,
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    // Business methods
    public boolean isAvailable() {
        return status == Status.SCHEDULED || status == Status.CONFIRMED;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    public boolean isNoShow() {
        return status == Status.NO_SHOW;
    }

    public void cancel(String reason, Long cancelledByUserId) {
        this.status = Status.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledById = cancelledByUserId;
        this.cancelledAt = LocalDateTime.now();
    }

    public void checkIn() {
        this.checkInTime = LocalDateTime.now();
        this.status = Status.IN_PROGRESS;
    }

    public void complete(String feedback, Integer rating) {
        this.status = Status.COMPLETED;
        this.checkOutTime = LocalDateTime.now();
        this.actualDurationMinutes = (int) Duration.between(this.checkInTime, this.checkOutTime).toMinutes();
        this.feedback = feedback;
        this.rating = rating;
    }

    public void markAsNoShow() {
        this.status = Status.NO_SHOW;
    }

    public void recordPayment(BigDecimal amount, String method, String reference) {
        this.paymentAmount = amount;
        this.paymentMethod = method;
        this.paymentReference = reference;
        this.paymentDate = LocalDateTime.now();
        this.paid = true;
    }

    // Validation methods
    public boolean isTimeSlotAvailable() {
        // In a real application, this would check for overlapping appointments
        // This is a simplified version
        return true;
    }

    public boolean isWithinBusinessHours() {
        // In a real application, this would check against business hours
        // This is a simplified version
        LocalTime start = startDateTime.toLocalTime();
        return !start.isBefore(LocalTime.of(9, 0)) && !start.isAfter(LocalTime.of(18, 0));
    }

    public boolean isCancellationAllowed() {
        // Allow cancellation up to 24 hours before the appointment
        return LocalDateTime.now().isBefore(startDateTime.minusHours(24));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public LocalDate getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setRecurrenceEndDate(LocalDate recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }

    public boolean isOnlineBooking() {
        return onlineBooking;
    }

    public void setOnlineBooking(boolean onlineBooking) {
        this.onlineBooking = onlineBooking;
    }

    public boolean isConfirmationSent() {
        return confirmationSent;
    }

    public void setConfirmationSent(boolean confirmationSent) {
        this.confirmationSent = confirmationSent;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Long getCancelledById() {
        return cancelledById;
    }

    public void setCancelledById(Long cancelledById) {
        this.cancelledById = cancelledById;
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
}
