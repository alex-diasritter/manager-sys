package com.managersys.repository;

import com.managersys.model.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    @Query("SELECT s FROM Service s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Service> search(@Param("query") String query, Pageable pageable);
    
    Page<Service> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Service> findByActiveTrue(Pageable pageable);
    
    List<Service> findByCategoryIdAndActiveTrue(Long categoryId);
    
    @Query("SELECT s FROM Service s WHERE " +
           "s.onlineBookingAvailable = true AND s.active = true AND " +
           "(s.category IS NULL OR s.category.active = true)")
    Page<Service> findAvailableForOnlineBooking(Pageable pageable);
    
    @Query("SELECT s FROM Service s WHERE " +
           "s.onlineBookingAvailable = true AND s.active = true AND " +
           "s.category.id = :categoryId AND (s.category IS NULL OR s.category.active = true)")
    Page<Service> findAvailableForOnlineBookingByCategory(
            @Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT s FROM Service s WHERE " +
           "s.id IN (SELECT DISTINCT ss.service.id FROM ServiceSchedule ss WHERE ss.employee.id = :employeeId)")
    Page<Service> findByEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);
    
    @Query("SELECT s FROM Service s WHERE " +
           "s.id IN (SELECT DISTINCT ss.service.id FROM ServiceSchedule ss WHERE ss.customer.id = :customerId)")
    Page<Service> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT s FROM Service s WHERE " +
           "s.price BETWEEN :minPrice AND :maxPrice AND " +
           "s.active = true")
    Page<Service> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    @Query("SELECT s FROM Service s WHERE " +
           "s.id IN (SELECT ss.service.id FROM ServiceSchedule ss WHERE " +
           "ss.startDateTime >= CURRENT_DATE AND ss.status = 'SCHEDULED')")
    Page<Service> findWithUpcomingAppointments(Pageable pageable);
    
    @Query("SELECT s, COUNT(ss) as appointmentCount FROM Service s " +
           "LEFT JOIN s.schedules ss " +
           "WHERE ss.startDateTime BETWEEN :startDate AND :endDate " +
           "GROUP BY s " +
           "ORDER BY appointmentCount DESC")
    Page<Object[]> findMostPopularServices(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
}
