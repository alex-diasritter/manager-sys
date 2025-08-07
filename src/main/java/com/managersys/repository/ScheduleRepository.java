package com.managersys.repository;

import com.managersys.model.ServiceSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ServiceSchedule, Long> {

    @Query("SELECT ss FROM ServiceSchedule ss WHERE " +
           "ss.service.id = :serviceId AND " +
           "ss.status = 'SCHEDULED' AND " +
           "((ss.startDateTime BETWEEN :start AND :end) OR " +
           "(ss.endDateTime BETWEEN :start AND :end) OR " +
           "(ss.startDateTime <= :start AND ss.endDateTime >= :end))")
    List<ServiceSchedule> findConflictingSchedules(
            @Param("serviceId") Long serviceId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE " +
           "ss.service.id = :serviceId AND ss.employee.id = :employeeId AND " +
           "ss.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS') AND " +
           "((ss.startDateTime BETWEEN :start AND :end) OR " +
           "(ss.endDateTime BETWEEN :start AND :end) OR " +
           "(ss.startDateTime <= :start AND ss.endDateTime >= :end))")
    List<ServiceSchedule> findConflictingSchedules(
            @Param("serviceId") Long serviceId,
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE " +
           "ss.service.id = :serviceId AND " +
           "ss.employee.id = :employeeId AND " +
           "ss.customer.id = :customerId AND " +
           "ss.recurring = true AND " +
           "DATE(ss.startDateTime) >= :startDate")
    List<ServiceSchedule> findRecurringSchedules(
            @Param("serviceId") Long serviceId,
            @Param("employeeId") Long employeeId,
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDate startDate);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE " +
           "ss.service.id = :serviceId AND " +
           "ss.startDateTime BETWEEN :start AND :end AND " +
           "ss.status NOT IN ('CANCELLED', 'NO_SHOW')")
    Page<ServiceSchedule> findAvailableTimeSlots(
            @Param("serviceId") Long serviceId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE ss.employee.id = :employeeId")
    Page<ServiceSchedule> findByEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE ss.customer.id = :customerId")
    Page<ServiceSchedule> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE ss.service.id = :serviceId")
    Page<ServiceSchedule> findByServiceId(@Param("serviceId") Long serviceId, Pageable pageable);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE ss.startDateTime BETWEEN :start AND :end")
    Page<ServiceSchedule> findByStartDateTimeBetween(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end,
                                                     Pageable pageable);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE ss.startDateTime > :now AND ss.status IN :statuses")
    Page<ServiceSchedule> findByStartDateTimeAfterAndStatusIn(@Param("now") LocalDateTime now,
                                                             @Param("statuses") List<ServiceSchedule.Status> statuses,
                                                             Pageable pageable);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE ss.endDateTime < :now")
    Page<ServiceSchedule> findByEndDateTimeBefore(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE ss.status = :status")
    Page<ServiceSchedule> findByStatus(@Param("status") ServiceSchedule.Status status, Pageable pageable);

    @Query("SELECT ss FROM ServiceSchedule ss WHERE " +
           "LOWER(ss.notes) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(ss.service.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(ss.customer.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ServiceSchedule> search(@Param("query") String query, Pageable pageable);
}
