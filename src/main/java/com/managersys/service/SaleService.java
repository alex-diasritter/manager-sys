package com.managersys.service;

import com.managersys.dto.SaleOrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SaleService {
    
    SaleOrderDTO createSaleOrder(SaleOrderDTO saleOrderDTO, Long employeeId);
    
    SaleOrderDTO getSaleOrderById(Long id);
    
    Page<SaleOrderDTO> getAllSaleOrders(Pageable pageable);
    
    Page<SaleOrderDTO> getSaleOrdersByCustomer(Long customerId, Pageable pageable);
    
    Page<SaleOrderDTO> getSaleOrdersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    Page<SaleOrderDTO> getSaleOrdersByStatus(String status, Pageable pageable);
    
    SaleOrderDTO updateSaleOrderStatus(Long orderId, String status);
    
    SaleOrderDTO updateSaleOrder(Long orderId, SaleOrderDTO saleOrderDTO);
    
    void cancelSaleOrder(Long orderId);
    
    void processPayment(Long orderId, String paymentDetails);
    
    List<Object[]> getSalesSummaryByPeriod(LocalDate startDate, LocalDate endDate);
    
    List<Object[]> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit);
    
    List<Object[]> getSalesByCustomer(LocalDate startDate, LocalDate endDate);
    
    List<Object[]> getSalesByCategory(LocalDate startDate, LocalDate endDate);
}
