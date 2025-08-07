package com.managersys.repository;

import com.managersys.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    Optional<Supplier> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.name) LIKE LOWER(concat('%', :query, '%')) " +
           "OR LOWER(s.contactPerson) LIKE LOWER(concat('%', :query, '%')) " +
           "OR LOWER(s.email) LIKE LOWER(concat('%', :query, '%'))")
    List<Supplier> searchSuppliers(@Param("query") String query);
}
