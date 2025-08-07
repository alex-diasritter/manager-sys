package com.managersys.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_order_items")
public class SaleOrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private SaleOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 200)
    private String productName;

    @Column(length = 1000)
    private String productDescription;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Constructors
    public SaleOrderItem() {}

    public SaleOrderItem(SaleOrder order, Product product, Integer quantity, BigDecimal unitPrice) {
        this.order = order;
        this.product = product;
        this.productName = product.getName();
        this.productDescription = product.getDescription();
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateTotalAmount();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SaleOrder getOrder() {
        return order;
    }

    public void setOrder(SaleOrder order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    // Business methods
    public void calculateTotalAmount() {
        if (unitPrice == null || quantity == null) {
            this.totalAmount = BigDecimal.ZERO;
            return;
        }

        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discount = BigDecimal.ZERO;

        if (discountAmount != null) {
            discount = discountAmount;
        } else if (discountPercentage != null) {
            discount = subtotal.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        }

        this.totalAmount = subtotal.subtract(discount);
        
        // Ensure total amount is not negative
        if (this.totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.totalAmount = BigDecimal.ZERO;
        }
    }

    @PrePersist
    @PreUpdate
    private void calculateTotals() {
        // Calculate total before discount
        BigDecimal subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        
        // Calculate discount amount if percentage is provided
        if (this.discountPercentage != null && this.discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            this.discountAmount = subtotal.multiply(this.discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        } else if (this.discountAmount == null) {
            this.discountAmount = BigDecimal.ZERO;
        }
        
        // Ensure discount doesn't exceed subtotal
        if (this.discountAmount.compareTo(subtotal) > 0) {
            this.discountAmount = subtotal;
        }
        
        // Calculate final total
        this.totalAmount = subtotal.subtract(this.discountAmount);
    }

    // Business method to update product stock
    public void updateProductStock() {
        if (this.order == null || this.product == null) {
            return;
        }
        
        // Only update stock for certain order statuses
        if (this.order.getStatus() == SaleOrder.Status.DRAFT || 
            this.order.getStatus() == SaleOrder.Status.PENDING) {
            // When order is in draft or pending, reserve the stock
            this.product.decreaseStock(this.quantity);
        } else if (this.order.getStatus() == SaleOrder.Status.CANCELLED) {
            // When order is cancelled, return stock
            this.product.increaseStock(this.quantity);
        }
        // For other statuses, stock was already updated when order was placed
    }

    // Factory method to create an item from a product
    public static SaleOrderItem fromProduct(Product product, int quantity) {
        return new SaleOrderItem(null, product, quantity, product.getPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaleOrderItem)) return false;
        SaleOrderItem that = (SaleOrderItem) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SaleOrderItem{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
