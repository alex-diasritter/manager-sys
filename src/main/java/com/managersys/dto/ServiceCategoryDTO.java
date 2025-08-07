package com.managersys.dto;

import com.managersys.model.ServiceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServiceCategoryDTO {

    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private boolean active = true;
    private Integer displayOrder;
    private String color;
    private String icon;
    private Long parentCategoryId;
    private String parentCategoryName;
    private List<ServiceCategoryDTO> subcategories;
    private List<ServiceDTO> services;
    
    // Construtores
    public ServiceCategoryDTO() {
    }
    
    public ServiceCategoryDTO(Long id, String name, String description, boolean active, 
                            Integer displayOrder, String color, String icon, 
                            Long parentCategoryId, String parentCategoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.displayOrder = displayOrder;
        this.color = color;
        this.icon = icon;
        this.parentCategoryId = parentCategoryId;
        this.parentCategoryName = parentCategoryName;
    }

    // Static factory method to convert from entity to DTO
    public static ServiceCategoryDTO fromEntity(ServiceCategory category) {
        if (category == null) {
            return null;
        }

        ServiceCategoryDTO dto = new ServiceCategoryDTO(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.isActive(),
            category.getDisplayOrder(),
            category.getColor(),
            category.getIcon(),
            category.getParentCategory() != null ? category.getParentCategory().getId() : null,
            category.getParentCategory() != null ? category.getParentCategory().getName() : null
        );
        
        return dto;
    }

    // Convert DTO to entity
    public ServiceCategory toEntity() {
        ServiceCategory category = new ServiceCategory();
        category.setId(this.id);
        category.setName(this.name);
        category.setDescription(this.description);
        category.setActive(this.active);
        category.setDisplayOrder(this.displayOrder);
        category.setColor(this.color);
        category.setIcon(this.icon);
        
        return category;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public List<ServiceCategoryDTO> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<ServiceCategoryDTO> subcategories) {
        this.subcategories = subcategories;
    }

    public List<ServiceDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceDTO> services) {
        this.services = services;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceCategoryDTO that = (ServiceCategoryDTO) o;
        return active == that.active &&
               Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(description, that.description) &&
               Objects.equals(displayOrder, that.displayOrder) &&
               Objects.equals(color, that.color) &&
               Objects.equals(icon, that.icon) &&
               Objects.equals(parentCategoryId, that.parentCategoryId) &&
               Objects.equals(parentCategoryName, that.parentCategoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, active, displayOrder, 
                          color, icon, parentCategoryId, parentCategoryName);
    }

    @Override
    public String toString() {
        return "ServiceCategoryDTO{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", active=" + active +
               ", displayOrder=" + displayOrder +
               ", color='" + color + '\'' +
               ", icon='" + icon + '\'' +
               ", parentCategoryId=" + parentCategoryId +
               ", parentCategoryName='" + parentCategoryName + '\'' +
               '}';
    }

    // Convert entity with nested relationships to DTO
    public static ServiceCategoryDTO fromEntityWithRelationships(ServiceCategory category, boolean includeSubcategories, boolean includeServices) {
        ServiceCategoryDTO dto = fromEntity(category);
        
        if (dto == null) {
            return null;
        }
        
        if (includeSubcategories && category.getSubcategories() != null) {
            dto.setSubcategories(category.getSubcategories().stream()
                    .map(sub -> fromEntityWithRelationships(sub, true, includeServices))
                    .collect(Collectors.toList()));
        }
        
        if (includeServices && category.getServices() != null) {
            dto.setServices(category.getServices().stream()
                    .map(ServiceDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}
