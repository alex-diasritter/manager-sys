package com.managersys.dto;

import com.managersys.model.SaleOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SaleOrderDTO {

    private Long id;

    private String orderNumber;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long employeeId;

    @NotNull(message = "Status is required")
    private SaleOrder.Status status;

    private LocalDateTime orderDate;

    @DecimalMin(value = "0.00", message = "Total amount must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "Total amount must have up to 10 digits before and 2 after decimal")
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.00", message = "Discount amount must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "Discount amount must have up to 10 digits before and 2 after decimal")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.00", message = "Shipping amount must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "Shipping amount must have up to 10 digits before and 2 after decimal")
    private BigDecimal shippingAmount;

    @DecimalMin(value = "0.00", message = "Final amount must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "Final amount must have up to 10 digits before and 2 after decimal")
    private BigDecimal finalAmount;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    @Valid
    @NotEmpty(message = "At least one order item is required")
    private List<SaleOrderItemDTO> items;

    // Static factory method to convert from entity to DTO
    public static SaleOrderDTO fromEntity(SaleOrder order) {
        if (order == null) {
            return null;
        }

        SaleOrderDTO dto = new SaleOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        dto.setEmployeeId(order.getEmployee() != null ? order.getEmployee().getId() : null);
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setShippingAmount(order.getShippingAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setNotes(order.getNotes());
        
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(SaleOrderItemDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    // Convert DTO to entity
    public SaleOrder toEntity() {
        SaleOrder order = new SaleOrder();
        order.setId(this.id);
        order.setOrderNumber(this.orderNumber);
        order.setStatus(this.status);
        order.setOrderDate(this.orderDate != null ? this.orderDate : LocalDateTime.now());
        order.setTotalAmount(this.totalAmount != null ? this.totalAmount : BigDecimal.ZERO);
        order.setDiscountAmount(this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO);
        order.setShippingAmount(this.shippingAmount != null ? this.shippingAmount : BigDecimal.ZERO);
        order.setFinalAmount(this.finalAmount != null ? this.finalAmount : BigDecimal.ZERO);
        order.setNotes(this.notes);

        // Items will be added separately to maintain the relationship
        return order;
    }

    // Helper method to calculate order totals from items
    public void calculateTotals() {
        if (this.items == null || this.items.isEmpty()) {
            this.totalAmount = BigDecimal.ZERO;
            this.finalAmount = BigDecimal.ZERO;
            return;
        }

        // Calculate subtotal from items
        this.totalAmount = this.items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply discounts and shipping
        BigDecimal discount = this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO;
        BigDecimal shipping = this.shippingAmount != null ? this.shippingAmount : BigDecimal.ZERO;
        
        this.finalAmount = this.totalAmount
                .subtract(discount)
                .add(shipping);
                
        // Ensure final amount is not negative
        if (this.finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.finalAmount = BigDecimal.ZERO;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public SaleOrder.Status getStatus() {
        return status;
    }

    public void setStatus(SaleOrder.Status status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<SaleOrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<SaleOrderItemDTO> items) {
        this.items = items;
    }
}
