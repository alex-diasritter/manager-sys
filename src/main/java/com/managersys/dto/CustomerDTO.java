package com.managersys.dto;

import com.managersys.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

public class CustomerDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be between 10 to 15 digits")
    private String phone;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Valid
    private AddressDTO address;

    @NotBlank(message = "Tax ID is required")
    @Pattern(regexp = "([0-9]{11}|[0-9]{14})", message = "Tax ID must be 11 (CPF) or 14 (CNPJ) digits")
    private String taxId;

    @NotNull(message = "Customer type is required")
    private Customer.CustomerType customerType;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    // Constructors
    public CustomerDTO() {}

    public CustomerDTO(String name, String email, String taxId, Customer.CustomerType customerType) {
        this.name = name;
        this.email = email;
        this.taxId = taxId;
        this.customerType = customerType;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public Customer.CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(Customer.CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Static factory method to convert from entity to DTO
    public static CustomerDTO fromEntity(Customer customer) {
        if (customer == null) {
            return null;
        }

        AddressDTO addressDTO = customer.getAddress() != null ? 
            new AddressDTO(
                customer.getAddress().getStreet(),
                customer.getAddress().getNumber(),
                customer.getAddress().getComplement(),
                customer.getAddress().getNeighborhood(),
                customer.getAddress().getCity(),
                customer.getAddress().getState(),
                customer.getAddress().getCountry(),
                customer.getAddress().getPostalCode()
            ) : null;

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setBirthDate(customer.getBirthDate());
        dto.setAddress(addressDTO);
        dto.setTaxId(customer.getTaxId());
        dto.setCustomerType(customer.getCustomerType());
        dto.setNotes(customer.getNotes());
        
        return dto;
    }

    // Convert DTO to entity
    public Customer toEntity() {
        Customer.Address addressEntity = this.address != null ? 
            new Customer.Address(
                this.address.getStreet(),
                this.address.getNumber(),
                this.address.getCity(),
                this.address.getState(),
                this.address.getPostalCode()
            ) : null;

        if (addressEntity != null && this.address != null) {
            addressEntity.setComplement(this.address.getComplement());
            addressEntity.setNeighborhood(this.address.getNeighborhood());
            addressEntity.setCountry(this.address.getCountry());
        }

        Customer customer = new Customer();
        customer.setId(this.id);
        customer.setName(this.name);
        customer.setEmail(this.email);
        customer.setPhone(this.phone);
        customer.setBirthDate(this.birthDate);
        customer.setAddress(addressEntity);
        customer.setTaxId(this.taxId);
        customer.setCustomerType(this.customerType);
        customer.setNotes(this.notes);
        
        return customer;
    }

    public static class AddressDTO {
        @NotBlank(message = "Street is required")
        @Size(max = 200, message = "Street cannot exceed 200 characters")
        private String street;

        @NotBlank(message = "Number is required")
        @Size(max = 20, message = "Number cannot exceed 20 characters")
        private String number;

        @Size(max = 100, message = "Complement cannot exceed 100 characters")
        private String complement;

        @NotBlank(message = "Neighborhood is required")
        @Size(max = 100, message = "Neighborhood cannot exceed 100 characters")
        private String neighborhood;

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        private String city;

        @NotBlank(message = "State is required")
        @Size(max = 50, message = "State cannot exceed 50 characters")
        private String state;

        @NotBlank(message = "Country is required")
        @Size(max = 50, message = "Country cannot exceed 50 characters")
        private String country;

        @NotBlank(message = "Postal code is required")
        @Pattern(regexp = "^[0-9]{5}-?[0-9]{3}$", message = "Postal code must be in format XXXXX-XXX")
        private String postalCode;

        // Constructors
        public AddressDTO() {}

        public AddressDTO(String street, String number, String complement, String neighborhood, 
                         String city, String state, String country, String postalCode) {
            this.street = street;
            this.number = number;
            this.complement = complement;
            this.neighborhood = neighborhood;
            this.city = city;
            this.state = state;
            this.country = country;
            this.postalCode = postalCode;
        }

        // Getters and Setters
        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getComplement() {
            return complement;
        }

        public void setComplement(String complement) {
            this.complement = complement;
        }

        public String getNeighborhood() {
            return neighborhood;
        }

        public void setNeighborhood(String neighborhood) {
            this.neighborhood = neighborhood;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        @Override
        public String toString() {
            return "AddressDTO{" +
                    "street='" + street + '\'' +
                    ", number='" + number + '\'' +
                    ", city='" + city + '\'' +
                    ", state='" + state + '\'' +
                    ", postalCode='" + postalCode + '\'' +
                    '}';
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AddressDTO that = (AddressDTO) o;
            return Objects.equals(street, that.street) &&
                   Objects.equals(number, that.number) &&
                   Objects.equals(postalCode, that.postalCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(street, number, postalCode);
        }
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", customerType=" + customerType +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDTO that = (CustomerDTO) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(email, that.email) &&
               Objects.equals(taxId, that.taxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, taxId);
    }
}
