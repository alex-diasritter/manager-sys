package com.managersys.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "confirmation_tokens")
public class ConfirmationToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String token;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            nullable = false,
            name = "employee_id"
    )
    private Employee employee;

    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, Employee employee) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.employee = employee;
    }
}
