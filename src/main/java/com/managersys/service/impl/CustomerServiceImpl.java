package com.managersys.service.impl;

import com.managersys.dto.CustomerDTO;
import com.managersys.exception.ResourceNotFoundException;
import com.managersys.model.Customer;
import com.managersys.repository.CustomerRepository;
import com.managersys.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        // Check if email already exists
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Customer", "email", customerDTO.getEmail());
        }

        // Check if tax ID already exists
        if (customerRepository.existsByTaxId(customerDTO.getTaxId())) {
            throw new ResourceAlreadyExistsException("Customer", "taxId", customerDTO.getTaxId());
        }

        Customer customer = customerDTO.toEntity();
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerDTO.fromEntity(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return CustomerDTO.fromEntity(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(CustomerDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> searchCustomers(String query, Pageable pageable) {
        return customerRepository.searchCustomers(query, pageable)
                .map(CustomerDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findCustomersWithBirthdayBetween(
            LocalDate startDate, LocalDate endDate, String query, Pageable pageable) {
        return customerRepository.findCustomersWithBirthdayBetween(
                        startDate, endDate, query, pageable)
                .map(CustomerDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findByCustomerType(Customer.CustomerType customerType, Pageable pageable) {
        return customerRepository.findByCustomerType(customerType, pageable)
                .map(CustomerDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> findCitiesWithStates() {
        return customerRepository.findDistinctCityAndState().stream()
                .map(tuple -> Map.of(
                        "city", (String) tuple[0],
                        "state", (String) tuple[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findByCityAndState(String city, String state) {
        return customerRepository.findByCityAndState(city, state).stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        // Check if email is being changed and if the new email already exists
        if (!Objects.equals(existingCustomer.getEmail(), customerDTO.getEmail()) && 
            customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Customer", "email", customerDTO.getEmail());
        }

        // Check if tax ID is being changed and if the new tax ID already exists
        if (!Objects.equals(existingCustomer.getTaxId(), customerDTO.getTaxId()) && 
            customerRepository.existsByTaxId(customerDTO.getTaxId())) {
            throw new ResourceAlreadyExistsException("Customer", "taxId", customerDTO.getTaxId());
        }

        // Update fields
        existingCustomer.setName(customerDTO.getName());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setPhone(customerDTO.getPhone());
        existingCustomer.setBirthDate(customerDTO.getBirthDate());
        existingCustomer.setTaxId(customerDTO.getTaxId());
        existingCustomer.setCustomerType(customerDTO.getCustomerType());
        existingCustomer.setNotes(customerDTO.getNotes());

        // Update address if provided
        if (customerDTO.getAddress() != null) {
            Customer.Address address = existingCustomer.getAddress() != null ? 
                    existingCustomer.getAddress() : new Customer.Address();
            
            address.setStreet(customerDTO.getAddress().getStreet());
            address.setNumber(customerDTO.getAddress().getNumber());
            address.setComplement(customerDTO.getAddress().getComplement());
            address.setNeighborhood(customerDTO.getAddress().getNeighborhood());
            address.setCity(customerDTO.getAddress().getCity());
            address.setState(customerDTO.getAddress().getState());
            address.setCountry(customerDTO.getAddress().getCountry());
            address.setPostalCode(customerDTO.getAddress().getPostalCode());
            
            existingCustomer.setAddress(address);
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return CustomerDTO.fromEntity(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        customerRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTaxId(String taxId) {
        return customerRepository.existsByTaxId(taxId);
    }
}
