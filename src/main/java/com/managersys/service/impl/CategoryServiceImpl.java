package com.managersys.service.impl;

import com.managersys.dto.ServiceCategoryDTO;
import com.managersys.exception.ResourceExistsException;
import com.managersys.model.ServiceCategory;
import com.managersys.repository.CategoryRepository;
import com.managersys.repository.ServiceRepository;
import com.managersys.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ServiceRepository serviceRepository) {
        this.categoryRepository = categoryRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    @Transactional
    public ServiceCategoryDTO createCategory(ServiceCategoryDTO categoryDTO) {
        // Check for duplicate category name
        if (categoryRepository.existsByNameIgnoreCase(categoryDTO.getName())) {
            throw new ResourceExistsException("Category with name " + categoryDTO.getName() + " already exists");
        }

        ServiceCategory category = new ServiceCategory();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        ServiceCategory savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceCategoryDTO getCategoryById(Long id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceCategoryDTO> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceCategoryDTO updateCategory(Long id, ServiceCategoryDTO categoryDTO) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if new name already exists (excluding current category)
        if (!Objects.equals(category.getName(), categoryDTO.getName()) &&
                categoryRepository.existsByNameIgnoreCase(categoryDTO.getName())) {
            throw new ResourceExistsException("Category with name " + categoryDTO.getName() + " already exists");
        }

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        ServiceCategory updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if category has associated services
        if (serviceRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Cannot delete category with associated services");
        }

        categoryRepository.delete(category);
    }

    private ServiceCategoryDTO convertToDTO(ServiceCategory category) {
        ServiceCategoryDTO dto = new ServiceCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
