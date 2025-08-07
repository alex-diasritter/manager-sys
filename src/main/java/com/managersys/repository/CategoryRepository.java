package com.managersys.repository;

import com.managersys.model.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<ServiceCategory, Long> {

    @Query("SELECT c FROM ServiceCategory c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ServiceCategory> search(@Param("query") String query, Pageable pageable);
    
    List<ServiceCategory> findByParentCategoryIsNull();
    
    List<ServiceCategory> findByParentCategoryId(Long parentId);
    
    Page<ServiceCategory> findByParentCategoryIsNull(Pageable pageable);
    
    Page<ServiceCategory> findByParentCategoryId(Long parentId, Pageable pageable);
    
    Page<ServiceCategory> findByActiveTrue(Pageable pageable);
    
    @Query("SELECT c FROM ServiceCategory c WHERE " +
           "c.active = true AND c.parentCategory IS NULL")
    Page<ServiceCategory> findActiveParentCategories(Pageable pageable);
    
    @Query("SELECT c FROM ServiceCategory c WHERE " +
           "c.active = true AND c.parentCategory.id = :parentId")
    Page<ServiceCategory> findActiveSubcategories(
            @Param("parentId") Long parentId, Pageable pageable);
    
    @Query("SELECT c FROM ServiceCategory c WHERE " +
           "c.id IN (SELECT DISTINCT s.category.id FROM Service s WHERE s.active = true)")
    Page<ServiceCategory> findCategoriesWithActiveServices(Pageable pageable);
    
    @Query("SELECT c FROM ServiceCategory c WHERE " +
           "c.id IN (SELECT DISTINCT s.category.id FROM Service s WHERE " +
           "s.onlineBookingAvailable = true AND s.active = true)")
    Page<ServiceCategory> findCategoriesWithOnlineBookableServices(Pageable pageable);
    
    @Query("SELECT c FROM ServiceCategory c WHERE " +
           "c.id IN (SELECT s.category.id FROM Service s GROUP BY s.category.id " +
           "HAVING COUNT(s) > 0) ORDER BY c.name")
    Page<ServiceCategory> findCategoriesWithServices(Pageable pageable);
    
    @Query("SELECT c FROM ServiceCategory c WHERE " +
           "c.id NOT IN (SELECT s.category.id FROM Service s WHERE s.category IS NOT NULL)")
    Page<ServiceCategory> findEmptyCategories(Pageable pageable);
    
    @Query("SELECT c FROM ServiceCategory c WHERE " +
           "c.parentCategory IS NULL AND " +
           "c.id IN (SELECT s.category.id FROM Service s WHERE s.active = true)")
    List<ServiceCategory> findParentCategoriesWithActiveServices();
    
    @Query("SELECT c FROM ServiceCategory c WHERE " +
           "c.parentCategory IS NOT NULL AND " +
           "c.id IN (SELECT s.category.id FROM Service s WHERE s.active = true)")
    List<ServiceCategory> findSubcategoriesWithActiveServices();
    
    @Query("SELECT DISTINCT c FROM ServiceCategory c WHERE " +
           "c.id IN (SELECT s.category.id FROM Service s WHERE " +
           "s.id IN (SELECT ss.service.id FROM ServiceSchedule ss WHERE " +
           "ss.startDateTime > CURRENT_TIMESTAMP))")
    List<ServiceCategory> findCategoriesWithUpcomingSchedules();
}
