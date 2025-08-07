package com.managersys.repository;

import com.managersys.model.SaleOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleOrderItemRepository extends JpaRepository<SaleOrderItem, Long> {

    List<SaleOrderItem> findByOrderId(Long orderId);
    
    @Query("SELECT soi FROM SaleOrderItem soi WHERE soi.order.id = :orderId AND soi.product.id = :productId")
    List<SaleOrderItem> findByOrderIdAndProductId(
            @Param("orderId") Long orderId,
            @Param("productId") Long productId);
    
    @Query("SELECT SUM(soi.quantity) FROM SaleOrderItem soi WHERE soi.product.id = :productId")
    Long getTotalSoldQuantityByProductId(@Param("productId") Long productId);
    
    @Query("SELECT soi FROM SaleOrderItem soi " +
           "JOIN soi.order so " +
           "WHERE so.status = 'DELIVERED' AND " +
           "so.orderDate BETWEEN :startDate AND :endDate")
    List<SaleOrderItem> findDeliveredItemsInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p.id, p.name, SUM(soi.quantity) AS totalQuantity, SUM(soi.totalAmount) AS totalAmount " +
           "FROM SaleOrderItem soi " +
           "JOIN soi.product p " +
           "JOIN soi.order so " +
           "WHERE so.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.id, p.name " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> getProductSalesSummary(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT soi FROM SaleOrderItem soi " +
           "JOIN FETCH soi.product " +
           "JOIN FETCH soi.order so " +
           "WHERE so.id = :orderId")
    List<SaleOrderItem> findItemsWithProductByOrderId(@Param("orderId") Long orderId);
}
