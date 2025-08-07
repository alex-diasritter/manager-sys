package com.managersys.repository;

import com.managersys.model.SaleOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {

    Page<SaleOrder> findByCustomerId(Long customerId, Pageable pageable);
    
    @Query("SELECT so FROM SaleOrder so WHERE DATE(so.orderDate) BETWEEN :startDate AND :endDate")
    Page<SaleOrder> findByOrderDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    Page<SaleOrder> findByStatus(SaleOrder.Status status, Pageable pageable);
    
    @Query("SELECT so FROM SaleOrder so WHERE so.status IN :statuses")
    Page<SaleOrder> findByStatusIn(@Param("statuses") List<SaleOrder.Status> statuses, Pageable pageable);
    
    @Query("SELECT so FROM SaleOrder so JOIN so.customer c WHERE " +
           "LOWER(so.orderNumber) LIKE LOWER(concat('%', :query, '%')) OR " +
           "LOWER(c.name) LIKE LOWER(concat('%', :query, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(concat('%', :query, '%'))")
    Page<SaleOrder> searchSales(@Param("query") String query, Pageable pageable);
    
    @Query(value = "SELECT " +
            "DATE_TRUNC(:period, so.order_date) AS period, " +
            "COUNT(so.id) AS total_orders, " +
            "SUM(so.final_amount) AS total_sales, " +
            "AVG(so.final_amount) AS average_order_value " +
            "FROM sale_orders so " +
            "WHERE so.order_date BETWEEN :startDate AND :endDate " +
            "GROUP BY period " +
            "ORDER BY period", nativeQuery = true)
    List<Object[]> getSalesSummaryByPeriod(
            @Param("period") String period, // 'day', 'week', 'month', 'year'
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p.id, p.name, SUM(soi.quantity) AS totalQuantity, SUM(soi.total_amount) AS totalAmount " +
           "FROM SaleOrderItem soi " +
           "JOIN soi.product p " +
           "JOIN soi.order so " +
           "WHERE so.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.id, p.name " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    @Query("SELECT c.id, c.name, COUNT(so.id) AS orderCount, SUM(so.final_amount) AS totalSpent " +
           "FROM SaleOrder so " +
           "JOIN so.customer c " +
           "WHERE so.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY c.id, c.name " +
           "ORDER BY totalSpent DESC")
    List<Object[]> findSalesByCustomer(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c.id, c.name, COUNT(DISTINCT so.id) AS orderCount, SUM(soi.quantity) AS totalItems, SUM(so.final_amount) AS totalAmount " +
           "FROM SaleOrder so " +
           "JOIN so.items soi " +
           "JOIN soi.product p " +
           "JOIN p.category c " +
           "WHERE so.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY c.id, c.name " +
           "ORDER BY totalAmount DESC")
    List<Object[]> findSalesByCategory(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
