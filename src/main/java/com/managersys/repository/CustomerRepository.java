package com.managersys.repository;

import com.managersys.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    Optional<Customer> findByTaxId(String taxId);
    
    boolean existsByEmail(String email);
    
    boolean existsByTaxId(String taxId);
    
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.name) LIKE LOWER(concat('%', :query, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(concat('%', :query, '%')) OR " +
           "c.phone LIKE %:query% OR " +
           "c.taxId LIKE %:query%")
    Page<Customer> searchCustomers(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE " +
           "(c.birthDate BETWEEN :startDate AND :endDate) AND " +
           "(LOWER(c.name) LIKE LOWER(concat('%', :query, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(concat('%', :query, '%')))")
    Page<Customer> findCustomersWithBirthdayBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("query") String query,
            Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.customerType = :customerType")
    Page<Customer> findByCustomerType(
            @Param("customerType") Customer.CustomerType customerType,
            Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.address.city) = LOWER(:city) AND " +
           "LOWER(c.address.state) = LOWER(:state)")
    List<Customer> findByCityAndState(
            @Param("city") String city,
            @Param("state") String state);
    
    @Query("SELECT DISTINCT c.address.city, c.address.state FROM Customer c WHERE c.address.city IS NOT NULL AND c.address.state IS NOT NULL")
    List<Object[]> findDistinctCityAndState();
}
