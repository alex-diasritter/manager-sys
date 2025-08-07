package com.managersys.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sale_orders")
public class SaleOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "shipping_amount", precision = 10, scale = 2)
    private BigDecimal shippingAmount;

    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleOrderItem> items = new ArrayList<>();

    // Constructors
    public SaleOrder() {}

    public SaleOrder(String orderNumber, Customer customer, Employee employee) {
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.employee = employee;
        this.status = Status.DRAFT;
        this.orderDate = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    // Getters and Setters
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
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

    public List<SaleOrderItem> getItems() {
        return items;
    }

    public void setItems(List<SaleOrderItem> items) {
        this.items = items;
    }

    public enum Status {
        DRAFT,          // Rascunho
        PENDING,        // Aguardando pagamento
        PAID,           // Pago
        PROCESSING,     // Em processamento
        SHIPPED,        // Enviado
        DELIVERED,      // Entregue
        CANCELLED,      // Cancelado
        REFUNDED        // Reembolsado
    }

    // Helper method to add item
    public void addItem(SaleOrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotals();
    }

    // Helper method to remove item
    public void removeItem(SaleOrderItem item) {
        items.remove(item);
        item.setOrder(null);
        calculateTotals();
    }

    // Calculate order totals
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

    // Business method to process payment
    public void processPayment() {
        if (this.status != Status.DRAFT && this.status != Status.PENDING) {
            throw new IllegalStateException("Only DRAFT or PENDING orders can be processed");
        }
        
        // In a real application, this would integrate with a payment gateway
        this.status = Status.PAID;
    }

    // Business method to cancel order
    public void cancel() {
        if (this.status == Status.DELIVERED || this.status == Status.REFUNDED) {
            throw new IllegalStateException("Cannot cancel an order that is already " + this.status);
        }
        
        // Return items to stock if order was already processed
        if (this.status == Status.PAID || this.status == Status.PROCESSING || this.status == Status.SHIPPED) {
            this.items.forEach(item -> {
                Product product = item.getProduct();
                product.increaseStock(item.getQuantity());
            });
        }
        
        this.status = Status.CANCELLED;
    }
}
