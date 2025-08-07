package com.managersys.service;

import com.managersys.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    
    ProductDTO createProduct(ProductDTO productDTO);
    
    ProductDTO getProductById(Long id);
    
    Page<ProductDTO> getAllProducts(Pageable pageable);
    
    List<ProductDTO> getLowStockProducts(int threshold);
    
    List<ProductDTO> searchProducts(String query);
    
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    
    void deleteProduct(Long id);
    
    void updateStock(Long productId, int quantity);
}
