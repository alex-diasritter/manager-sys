package com.managersys.service.impl;

import com.managersys.dto.SaleOrderDTO;
import com.managersys.dto.SaleOrderItemDTO;

import com.managersys.exception.ResourceNotFoundException;
import com.managersys.model.*;
import com.managersys.repository.CustomerRepository;
import com.managersys.repository.EmployeeRepository;
import com.managersys.repository.ProductRepository;
import com.managersys.repository.SaleOrderItemRepository;
import com.managersys.repository.SaleOrderRepository;
import com.managersys.service.SaleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleOrderRepository saleOrderRepository;
    private final SaleOrderItemRepository saleOrderItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    public SaleServiceImpl(SaleOrderRepository saleOrderRepository, SaleOrderItemRepository saleOrderItemRepository, ProductRepository productRepository, CustomerRepository customerRepository, EmployeeRepository employeeRepository) {
        this.saleOrderRepository = saleOrderRepository;
        this.saleOrderItemRepository = saleOrderItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public SaleOrderDTO createSaleOrder(SaleOrderDTO saleOrderDTO, Long employeeId) {
        // Validate and fetch customer
        Customer customer = customerRepository.findById(saleOrderDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", saleOrderDTO.getCustomerId()));
        
        // Fetch employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        // Create and save order
        SaleOrder order = saleOrderDTO.toEntity();
        order.setCustomer(customer);
        order.setEmployee(employee);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(SaleOrder.Status.DRAFT);
        order.setOrderDate(LocalDate.now());
        
        // Save order first to get ID
        SaleOrder savedOrder = saleOrderRepository.save(order);
        
        // Process order items
        processOrderItems(saleOrderDTO, savedOrder);
        
        // Calculate and save totals
        savedOrder.calculateTotals();
        
        return SaleOrderDTO.fromEntity(saleOrderRepository.save(savedOrder));
    }

    @Override
    @Transactional(readOnly = true)
    public SaleOrderDTO getSaleOrderById(Long id) {
        SaleOrder order = saleOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SaleOrder", "id", id));
        
        // Fetch items with product details
        List<SaleOrderItem> items = saleOrderItemRepository.findItemsWithProductByOrderId(id);
        order.setItems(items);
        
        return SaleOrderDTO.fromEntity(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleOrderDTO> getAllSaleOrders(Pageable pageable) {
        return saleOrderRepository.findAll(pageable)
                .map(SaleOrderDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleOrderDTO> getSaleOrdersByCustomer(Long customerId, Pageable pageable) {
        return saleOrderRepository.findByCustomerId(customerId, pageable)
                .map(SaleOrderDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleOrderDTO> getSaleOrdersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        
        return saleOrderRepository.findByOrderDateBetween(startDate, endDate, pageable)
                .map(SaleOrderDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleOrderDTO> getSaleOrdersByStatus(String status, Pageable pageable) {
        try {
            SaleOrder.Status statusEnum = SaleOrder.Status.valueOf(status.toUpperCase());
            return saleOrderRepository.findByStatus(statusEnum, pageable)
                    .map(SaleOrderDTO::fromEntity);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public SaleOrderDTO updateSaleOrderStatus(Long orderId, String status) {
        SaleOrder order = saleOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("SaleOrder", "id", orderId));
        
        try {
            SaleOrder.Status newStatus = SaleOrder.Status.valueOf(status.toUpperCase());

            // Handle status-specific logic
            switch (newStatus) {
                case CANCELLED:
                    handleOrderCancellation(order);
                    break;
                case PAID:
                    processPayment(order);
                    break;
                // Add other status transitions as needed
            }
            
            order.setStatus(newStatus);
            SaleOrder updatedOrder = saleOrderRepository.save(order);
            return SaleOrderDTO.fromEntity(updatedOrder);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public SaleOrderDTO updateSaleOrder(Long orderId, SaleOrderDTO saleOrderDTO) {
        SaleOrder existingOrder = saleOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("SaleOrder", "id", orderId));
        
        // Only allow updates for DRAFT or PENDING orders
        if (existingOrder.getStatus() != SaleOrder.Status.DRAFT && 
            existingOrder.getStatus() != SaleOrder.Status.PENDING) {
            throw new IllegalStateException("Only DRAFT or PENDING orders can be updated");
        }
        
        // Update order fields
        existingOrder.setDiscountAmount(saleOrderDTO.getDiscountAmount());
        existingOrder.setShippingAmount(saleOrderDTO.getShippingAmount());
        existingOrder.setNotes(saleOrderDTO.getNotes());
        
        // Update items
        updateOrderItems(saleOrderDTO, existingOrder);
        
        // Recalculate totals
        existingOrder.calculateTotals();
        
        SaleOrder updatedOrder = saleOrderRepository.save(existingOrder);
        return SaleOrderDTO.fromEntity(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelSaleOrder(Long orderId) {
        SaleOrder order = saleOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("SaleOrder", "id", orderId));
        
        if (order.getStatus() == SaleOrder.Status.CANCELLED) {
            return; // Already cancelled
        }
        
        handleOrderCancellation(order);
        order.setStatus(SaleOrder.Status.CANCELLED);
        saleOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void processPayment(Long orderId, String paymentDetails) {
        SaleOrder order = saleOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("SaleOrder", "id", orderId));
        
        if (order.getStatus() != SaleOrder.Status.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be processed");
        }
        
        // In a real application, this would integrate with a payment gateway
        // For now, we'll just update the status
        processPayment(order);
        
        order.setStatus(SaleOrder.Status.PAID);
        saleOrderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getSalesSummaryByPeriod(LocalDate startDate, LocalDate endDate) {
        return saleOrderRepository.getSalesSummaryByPeriod("month", startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        return saleOrderRepository.findTopSellingProducts(
                startDate, 
                endDate, 
                Pageable.ofSize(limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getSalesByCustomer(LocalDate startDate, LocalDate endDate) {
        return saleOrderRepository.findSalesByCustomer(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getSalesByCategory(LocalDate startDate, LocalDate endDate) {
        return saleOrderRepository.findSalesByCategory(startDate, endDate);
    }

    // Helper Methods
    
    private void processOrderItems(SaleOrderDTO saleOrderDTO, SaleOrder order) {
        // Clear existing items
        order.getItems().clear();
        
        // Process each item
        for (SaleOrderItemDTO itemDTO : saleOrderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDTO.getProductId()));
            
            // Create and add item
            SaleOrderItem item = itemDTO.toEntity();
            item.setOrder(order);
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setProductDescription(product.getDescription());
            
            // Reserve stock
            product.decreaseStock(item.getQuantity());
            
            order.addItem(item);
        }
    }
    
    private void updateOrderItems(SaleOrderDTO saleOrderDTO, SaleOrder order) {
        // This is a simplified implementation
        // In a real application, you'd want to handle updates more carefully
        
        // First, return all items to stock
        for (SaleOrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                item.getProduct().increaseStock(item.getQuantity());
            }
        }
        
        // Clear existing items
        order.getItems().clear();
        
        // Add updated items
        processOrderItems(saleOrderDTO, order);
    }
    
    private void handleOrderCancellation(SaleOrder order) {
        // Return items to stock
        for (SaleOrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                item.getProduct().increaseStock(item.getQuantity());
            }
        }
    }
    
    private void processPayment(SaleOrder order) {
        // In a real application, this would integrate with a payment gateway
        // For now, we'll just log the payment
        System.out.println("Processing payment for order: " + order.getId());
    }
    
    private String generateOrderNumber() {
        // Simple order number generation - can be enhanced as needed
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
