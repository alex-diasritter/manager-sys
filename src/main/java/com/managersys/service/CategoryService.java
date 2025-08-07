package com.managersys.service;

import com.managersys.dto.ServiceCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    
    ServiceCategoryDTO createCategory(ServiceCategoryDTO categoryDTO);
    
    ServiceCategoryDTO getCategoryById(Long id);
    
    Page<ServiceCategoryDTO> getAllCategories(Pageable pageable);
    
    Page<ServiceCategoryDTO> searchCategories(String query, Pageable pageable);
    
    List<ServiceCategoryDTO> getParentCategories();
    
    Page<ServiceCategoryDTO> getSubcategories(Long parentId, Pageable pageable);
    
    ServiceCategoryDTO updateCategory(Long id, ServiceCategoryDTO categoryDTO);
    
    void deleteCategory(Long id);
    
    void toggleCategoryStatus(Long id, boolean active);
    
    Page<ServiceCategoryDTO> getCategoriesWithActiveServices(Pageable pageable);
    
    Page<ServiceCategoryDTO> getCategoriesWithOnlineBookableServices(Pageable pageable);
    
    List<ServiceCategoryDTO> getCategoryTree();
    
    void moveCategory(Long categoryId, Long newParentId);
    
    List<ServiceCategoryDTO> getBreadcrumbs(Long categoryId);
}
