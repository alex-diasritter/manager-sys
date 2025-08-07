package com.managersys.dto;

import com.managersys.model.SaleOrderItem;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class SaleOrderItemDTO {

    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name cannot exceed 200 characters")
    private String productName;

    @Size(max = 1000, message = "Product description cannot exceed 1000 characters")
    private String productDescription;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Unit price must have up to 10 digits before and 2 after decimal")
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @DecimalMin(value = "0.00", message = "Discount percentage must be greater than or equal to 0")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Discount percentage must have up to 3 digits before and 2 after decimal")
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.00", message = "Discount amount must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "Discount amount must have up to 10 digits before and 2 after decimal")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.00", message = "Total amount must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "Total amount must have up to 10 digits before and 2 after decimal")
    private BigDecimal totalAmount;

    public SaleOrderItemDTO() {
    }

    public SaleOrderItemDTO(Long id, Long productId, String productName, String productDescription, BigDecimal unitPrice, Integer quantity, BigDecimal discountPercentage, BigDecimal discountAmount, BigDecimal totalAmount) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
    }

    // Static factory method to convert from entity to DTO
    public static SaleOrderItemDTO fromEntity(SaleOrderItem item) {
        if (item == null) {
            return null;
        }

        SaleOrderItemDTO dto = new SaleOrderItemDTO(
            item.getId(),
            item.getProduct() != null ? item.getProduct().getId() : null,
            item.getProductName(),
            item.getProductDescription(),
            item.getUnitPrice(),
            item.getQuantity(),
            item.getDiscountPercentage(),
            item.getDiscountAmount(),
            item.getTotalAmount()
        );
        return dto;
    }

    // Convert DTO to entity
    public SaleOrderItem toEntity() {
        SaleOrderItem item = new SaleOrderItem();
        item.setId(this.id);
        item.setProductName(this.productName);
        item.setProductDescription(this.productDescription);
        item.setUnitPrice(this.unitPrice);
        item.setQuantity(this.quantity);
        item.setDiscountPercentage(this.discountPercentage);
        item.setDiscountAmount(this.discountAmount);
        item.setTotalAmount(this.totalAmount);
        return item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
