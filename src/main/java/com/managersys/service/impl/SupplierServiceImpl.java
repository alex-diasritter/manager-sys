package com.managersys.service.impl;

import com.managersys.dto.SupplierDTO;
import com.managersys.exception.ResourceNotFoundException;
import com.managersys.model.Product;
import com.managersys.model.Supplier;
import com.managersys.repository.ProductRepository;
import com.managersys.repository.SupplierRepository;
import com.managersys.service.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        // Check if email already exists
        if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new IllegalStateException("Email already in use: " + supplierDTO.getEmail());
        }

        Supplier supplier = supplierDTO.toEntity();
        
        // Save the supplier first to get the ID
        Supplier savedSupplier = supplierRepository.save(supplier);
        
        // Handle products if any
        if (supplierDTO.getProductIds() != null && !supplierDTO.getProductIds().isEmpty()) {
            List<Product> products = productRepository.findAllById(supplierDTO.getProductIds());
            
            // Check if all products were found
            if (products.size() != supplierDTO.getProductIds().size()) {
                throw new ResourceNotFoundException("One or more products not found");
            }
            
            // Add products to supplier
            products.forEach(savedSupplier::addProduct);
            supplierRepository.save(savedSupplier);
        }
        
        return SupplierDTO.fromEntity(savedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return SupplierDTO.fromEntity(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(SupplierDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> searchSuppliers(String query) {
        return supplierRepository.searchSuppliers(query).stream()
                .map(SupplierDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        // Check if email is being changed and if the new email already exists
        if (!Objects.equals(existingSupplier.getEmail(), supplierDTO.getEmail()) && 
            supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new IllegalStateException("Email already in use: " + supplierDTO.getEmail());
        }

        // Update basic fields
        existingSupplier.setName(supplierDTO.getName());
        existingSupplier.setContactPerson(supplierDTO.getContactPerson());
        existingSupplier.setEmail(supplierDTO.getEmail());
        existingSupplier.setPhone(supplierDTO.getPhone());
        existingSupplier.setAddress(supplierDTO.getAddress());

        // Handle products if changed
        if (supplierDTO.getProductIds() != null) {
            // Remove all current products
            existingSupplier.getProducts().forEach(product -> 
                product.setSupplier(null));
            existingSupplier.getProducts().clear();
            
            // Add new products
            if (!supplierDTO.getProductIds().isEmpty()) {
                List<Product> products = productRepository.findAllById(supplierDTO.getProductIds());
                
                // Check if all products were found
                if (products.size() != supplierDTO.getProductIds().size()) {
                    throw new ResourceNotFoundException("One or more products not found");
                }
                
                products.forEach(existingSupplier::addProduct);
            }
        }

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return SupplierDTO.fromEntity(updatedSupplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        // Remove supplier reference from all products
        supplier.getProducts().forEach(product -> product.setSupplier(null));
        supplier.getProducts().clear();
        
        supplierRepository.delete(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> getSuppliersWithProducts() {
        return supplierRepository.findAll().stream()
                .filter(supplier -> !supplier.getProducts().isEmpty())
                .map(SupplierDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
