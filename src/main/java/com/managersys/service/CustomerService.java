package com.managersys.service;

import com.managersys.dto.CustomerDTO;
import com.managersys.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CustomerService {
    
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    
    CustomerDTO getCustomerById(Long id);
    
    Page<CustomerDTO> getAllCustomers(Pageable pageable);
    
    Page<CustomerDTO> searchCustomers(String query, Pageable pageable);
    
    Page<CustomerDTO> findCustomersWithBirthdayBetween(
            LocalDate startDate, 
            LocalDate endDate, 
            String query, 
            Pageable pageable);
    
    Page<CustomerDTO> findByCustomerType(Customer.CustomerType customerType, Pageable pageable);
    
    List<Map<String, String>> findCitiesWithStates();
    
    List<CustomerDTO> findByCityAndState(String city, String state);
    
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);
    
    void deleteCustomer(Long id);
    
    boolean existsByEmail(String email);
    
    boolean existsByTaxId(String taxId);
}
