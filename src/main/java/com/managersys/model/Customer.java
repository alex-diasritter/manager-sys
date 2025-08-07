package com.managersys.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String phone;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Embedded
    private Address address;
    
    @Column(name = "tax_id", unique = true)
    private String taxId; // CPF/CNPJ
    
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType;
    
    @Column(columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public Customer() {}

    public Customer(String name, String email, String phone, CustomerType customerType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFormattedTaxId() {
        if (taxId == null) return "";
        
        return customerType == CustomerType.INDIVIDUAL && taxId.length() == 11
            ? String.format("%s.%s.%s-%s", 
                taxId.substring(0, 3), 
                taxId.substring(3, 6), 
                taxId.substring(6, 9), 
                taxId.substring(9))
            : customerType == CustomerType.COMPANY && taxId.length() == 14
                ? String.format("%s.%s.%s/%s-%s",
                    taxId.substring(0, 2),
                    taxId.substring(2, 5),
                    taxId.substring(5, 8),
                    taxId.substring(8, 12),
                    taxId.substring(12))
                : taxId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return id != null && id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", customerType=" + customerType +
                '}';
    }
    
    public enum CustomerType {
        INDIVIDUAL, COMPANY
    }
    
    @Embeddable
    public static class Address {
        private String street;
        private String number;
        private String complement;
        private String neighborhood;
        private String city;
        private String state;
        private String country;
        @Column(name = "postal_code")
        private String postalCode;

        // Constructors
        public Address() {}

        public Address(String street, String number, String city, String state, String postalCode) {
            this.street = street;
            this.number = number;
            this.city = city;
            this.state = state;
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
            return String.format(
                "%s, %s%s, %s, %s - %s, %s, %s",
                street,
                number,
                complement != null ? ", " + complement : "",
                neighborhood,
                city,
                state,
                country,
                postalCode
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Address)) return false;
            Address address = (Address) o;
            return Objects.equals(street, address.street) &&
                    Objects.equals(number, address.number) &&
                    Objects.equals(city, address.city) &&
                    Objects.equals(state, address.state) &&
                    Objects.equals(postalCode, address.postalCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(street, number, city, state, postalCode);
        }
    }
}
