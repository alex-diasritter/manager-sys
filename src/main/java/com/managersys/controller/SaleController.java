package com.managersys.controller;

import com.managersys.dto.SaleOrderDTO;
import com.managersys.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales", description = "Sales management APIs")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SALES') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new sale order")
    public ResponseEntity<SaleOrderDTO> createSaleOrder(
            @Valid @RequestBody SaleOrderDTO saleOrderDTO,
            Authentication authentication) {
        
        Long employeeId = getEmployeeIdFromAuthentication(authentication);
        SaleOrderDTO createdOrder = saleService.createSaleOrder(saleOrderDTO, employeeId);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();
                
        return ResponseEntity.created(location).body(createdOrder);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a sale order by ID")
    public ResponseEntity<SaleOrderDTO> getSaleOrder(
            @Parameter(description = "ID of the sale order to be retrieved", required = true)
            @PathVariable Long id) {
        
        return ResponseEntity.ok(saleService.getSaleOrderById(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all sale orders with pagination and optional filters")
    public ResponseEntity<Page<SaleOrderDTO>> getAllSaleOrders(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        if (customerId != null) {
            return ResponseEntity.ok(saleService.getSaleOrdersByCustomer(customerId, pageable));
        }
        
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(saleService.getSaleOrdersByDateRange(startDate, endDate, pageable));
        }
        
        if (status != null && !status.trim().isEmpty()) {
            return ResponseEntity.ok(saleService.getSaleOrdersByStatus(status, pageable));
        }
        
        return ResponseEntity.ok(saleService.getAllSaleOrders(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SALES') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Update a sale order")
    public ResponseEntity<SaleOrderDTO> updateSaleOrder(
            @Parameter(description = "ID of the sale order to be updated", required = true)
            @PathVariable Long id,
            
            @Valid @RequestBody SaleOrderDTO saleOrderDTO) {
        
        return ResponseEntity.ok(saleService.updateSaleOrder(id, saleOrderDTO));
    }

    @PatchMapping("/{id}/status/{status}")
    @PreAuthorize("hasRole('SALES') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Update the status of a sale order")
    public ResponseEntity<SaleOrderDTO> updateSaleOrderStatus(
            @Parameter(description = "ID of the sale order", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "New status for the order", required = true)
            @PathVariable String status) {
        
        return ResponseEntity.ok(saleService.updateSaleOrderStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('SALES') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Cancel a sale order")
    public ResponseEntity<Void> cancelSaleOrder(
            @Parameter(description = "ID of the sale order to be cancelled", required = true)
            @PathVariable Long id) {
        
        saleService.cancelSaleOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/process-payment")
    @PreAuthorize("hasRole('SALES') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Process payment for a sale order")
    public ResponseEntity<Void> processPayment(
            @Parameter(description = "ID of the sale order", required = true)
            @PathVariable Long id,
            
            @RequestParam(required = false) String paymentDetails) {
        
        saleService.processPayment(id, paymentDetails);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get sales summary by period")
    public ResponseEntity<List<Object[]>> getSalesSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return ResponseEntity.ok(saleService.getSalesSummaryByPeriod(startDate, endDate));
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get top selling products")
    public ResponseEntity<List<Object[]>> getTopSellingProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        return ResponseEntity.ok(saleService.getTopSellingProducts(startDate, endDate, limit));
    }

    @GetMapping("/by-customer")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get sales by customer")
    public ResponseEntity<List<Object[]>> getSalesByCustomer(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return ResponseEntity.ok(saleService.getSalesByCustomer(startDate, endDate));
    }

    @GetMapping("/by-category")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get sales by category")
    public ResponseEntity<List<Object[]>> getSalesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return ResponseEntity.ok(saleService.getSalesByCategory(startDate, endDate));
    }

    // Helper method to get employee ID from authentication
    private Long getEmployeeIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authenticated user found");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            // In a real application, you would fetch the employee ID from the database
            // For now, we'll return a default value or throw an exception
            throw new UnsupportedOperationException("Employee ID resolution not implemented");
        }
        
        throw new SecurityException("Could not determine employee ID from authentication");
    }
}
