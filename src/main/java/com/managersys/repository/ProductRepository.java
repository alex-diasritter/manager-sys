package com.managersys.repository;

import com.managersys.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    List<Product> findByStockQuantityLessThanEqual(int quantity);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(concat('%', :query, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(concat('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);
    
    @Query("SELECT p FROM Product p WHERE p.supplier.id = :supplierId")
    List<Product> findBySupplierId(@Param("supplierId") Long supplierId);
}
