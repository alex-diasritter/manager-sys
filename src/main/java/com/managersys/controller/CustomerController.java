package com.managersys.controller;

import com.managersys.dto.CustomerDTO;
import com.managersys.model.Customer;
import com.managersys.service.CustomerService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SALES')")
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerDTO> createCustomer(
            @Valid @RequestBody CustomerDTO customerDTO) {
        
        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCustomer.getId())
                .toUri();
                
        return ResponseEntity.created(location).body(createdCustomer);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a customer by ID")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "ID of the customer to be obtained", required = true)
            @PathVariable Long id) {
        
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all customers with pagination and optional filters")
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Customer.CustomerType type,
            @PageableDefault(size = 20) Pageable pageable) {
        
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomers(search, pageable));
        }
        
        if (type != null) {
            return ResponseEntity.ok(customerService.findByCustomerType(type, pageable));
        }
        
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    @GetMapping("/birthdays")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get customers with birthdays in a date range")
    public ResponseEntity<Page<CustomerDTO>> getCustomersWithBirthdayBetween(
            @Parameter(description = "Start date (format: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date (format: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        
        return ResponseEntity.ok(customerService.findCustomersWithBirthdayBetween(
                startDate, endDate, search != null ? search : "", pageable));
    }

    @GetMapping("/locations")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get unique city and state combinations")
    public ResponseEntity<List<Map<String, String>>> getCustomerLocations() {
        return ResponseEntity.ok(customerService.findCitiesWithStates());
    }

    @GetMapping("/by-location")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get customers by city and state")
    public ResponseEntity<List<CustomerDTO>> getCustomersByLocation(
            @RequestParam String city,
            @RequestParam String state) {
        
        return ResponseEntity.ok(customerService.findByCityAndState(city, state));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SALES')")
    @Operation(summary = "Update a customer")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "ID of the customer to be updated", required = true)
            @PathVariable Long id,
            
            @Valid @RequestBody CustomerDTO customerDTO) {
        
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Delete a customer")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID of the customer to be deleted", required = true)
            @PathVariable Long id) {
        
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/email")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if a customer with the given email exists")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(
            @RequestParam String email) {
        
        return ResponseEntity.ok(Map.of("exists", customerService.existsByEmail(email)));
    }

    @GetMapping("/exists/tax-id")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if a customer with the given tax ID exists")
    public ResponseEntity<Map<String, Boolean>> checkTaxIdExists(
            @RequestParam String taxId) {
        
        return ResponseEntity.ok(Map.of("exists", customerService.existsByTaxId(taxId)));
    }
}
