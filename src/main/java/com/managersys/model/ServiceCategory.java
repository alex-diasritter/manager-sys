package com.managersys.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_categories")
public class ServiceCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(length = 7)
    private String color; // Hex color code

    @Column(length = 50)
    private String icon; // Icon name or class

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private ServiceCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceCategory> subcategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Service> services = new ArrayList<>();

    // Constructors
    public ServiceCategory() {}

    public ServiceCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
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

    public ServiceCategory getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(ServiceCategory parentCategory) {
        this.parentCategory = parentCategory;
    }

    public List<ServiceCategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<ServiceCategory> subcategories) {
        this.subcategories = subcategories;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    // Business methods
    public boolean isParentCategory() {
        return parentCategory == null;
    }

    public boolean hasSubcategories() {
        return !subcategories.isEmpty();
    }

    public boolean hasServices() {
        return !services.isEmpty();
    }

    public void addSubcategory(ServiceCategory subcategory) {
        subcategories.add(subcategory);
        subcategory.setParentCategory(this);
    }

    public void removeSubcategory(ServiceCategory subcategory) {
        subcategories.remove(subcategory);
        subcategory.setParentCategory(null);
    }

    public void addService(Service service) {
        services.add(service);
        service.setCategory(this);
    }

    public void removeService(Service service) {
        services.remove(service);
        service.setCategory(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceCategory)) return false;
        ServiceCategory that = (ServiceCategory) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ServiceCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
