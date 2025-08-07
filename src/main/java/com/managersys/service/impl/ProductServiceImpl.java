package com.managersys.service.impl;

import com.managersys.dto.ProductDTO;
import com.managersys.exception.ResourceNotFoundException;
import com.managersys.model.Product;
import com.managersys.model.Supplier;
import com.managersys.repository.ProductRepository;
import com.managersys.repository.SupplierRepository;
import com.managersys.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductServiceImpl(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Check if SKU already exists
        if (productRepository.existsBySku(productDTO.getSku())) {
            throw new IllegalStateException("SKU already exists: " + productDTO.getSku());
        }

        Product product = productDTO.toEntity();
        
        // Set supplier if provided
        if (productDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", productDTO.getSupplierId()));
            product.setSupplier(supplier);
        }
        
        Product savedProduct = productRepository.save(product);
        return ProductDTO.fromEntity(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductDTO.fromEntity(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(int threshold) {
        return productRepository.findByStockQuantityLessThanEqual(threshold).stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String query) {
        return productRepository.searchProducts(query).stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Check if SKU is being changed and if the new SKU already exists
        if (!Objects.equals(existingProduct.getSku(), productDTO.getSku()) && 
            productRepository.existsBySku(productDTO.getSku())) {
            throw new IllegalStateException("SKU already exists: " + productDTO.getSku());
        }

        // Update fields
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setSku(productDTO.getSku());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStockQuantity(productDTO.getStockQuantity());

        // Update supplier if changed
        if (!Objects.equals(
                existingProduct.getSupplier() != null ? existingProduct.getSupplier().getId() : null, 
                productDTO.getSupplierId())) {
            
            if (productDTO.getSupplierId() == null) {
                existingProduct.setSupplier(null);
            } else {
                Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                        .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", productDTO.getSupplierId()));
                existingProduct.setSupplier(supplier);
            }
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductDTO.fromEntity(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Remove from supplier's product list if exists
        if (product.getSupplier() != null) {
            product.getSupplier().getProducts().remove(product);
        }
        
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void updateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (quantity < 0 && product.getStockQuantity() < Math.abs(quantity)) {
            throw new IllegalStateException("Insufficient stock");
        }
        
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }
}
