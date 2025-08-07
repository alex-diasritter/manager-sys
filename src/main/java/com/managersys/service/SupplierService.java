package com.managersys.service;

import com.managersys.dto.SupplierDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierService {
    
    SupplierDTO createSupplier(SupplierDTO supplierDTO);
    
    SupplierDTO getSupplierById(Long id);
    
    Page<SupplierDTO> getAllSuppliers(Pageable pageable);
    
    List<SupplierDTO> searchSuppliers(String query);
    
    SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO);
    
    void deleteSupplier(Long id);
    
    List<SupplierDTO> getSuppliersWithProducts();
}
