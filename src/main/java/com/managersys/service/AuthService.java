package com.managersys.service;

import com.managersys.dto.LoginRequestDTO;
import com.managersys.dto.LoginResponseDTO;
import com.managersys.dto.RegistrationRequestDTO;
import com.managersys.model.ConfirmationToken;
import com.managersys.model.Employee;
import com.managersys.model.Role;
import com.managersys.repository.ConfirmationTokenRepository;
import com.managersys.repository.EmployeeRepository;
import com.managersys.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public String register(RegistrationRequestDTO request) {
        // Check if email is already in use
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already in use");
        }

        // Create new employee
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setRole(Role.valueOf(request.getRole().toUpperCase()));
        employee.setEnabled(false); // Will be enabled after email confirmation

        // Save employee
        Employee savedEmployee = employeeRepository.save(employee);

        // Generate confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                savedEmployee
        );
        confirmationTokenRepository.save(confirmationToken);

        // Send confirmation email via RabbitMQ
        sendConfirmationEmail(savedEmployee.getEmail(), token);

        return "Registration successful. Please check your email to confirm your account.";
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get user from database
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Check if account is enabled
        if (!employee.isEnabled()) {
            throw new IllegalStateException("Please confirm your email first");
        }

        // Generate tokens
        String accessToken = jwtService.generateToken(employee);
        String refreshToken = jwtService.generateRefreshToken(employee);

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(employee.getId())
                .email(employee.getEmail())
                .name(employee.getName())
                .role(employee.getRole())
                .build();
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        // Enable user
        Employee employee = confirmationToken.getEmployee();
        employee.setEnabled(true);
        employeeRepository.save(employee);

        // Update confirmation token
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);

        return "Email confirmed successfully";
    }

    private void sendConfirmationEmail(String email, String token) {
        String confirmationLink = "http://localhost:8080/api/auth/confirm?token=" + token;
        String message = String.format(
                "Click the link to confirm your email: %s",
                confirmationLink
        );

        // In a real application, you would send an email here
        // For now, we'll just log it
        System.out.println("Sending confirmation email to: " + email);
        System.out.println("Confirmation link: " + confirmationLink);

        // In a real application, you would use RabbitMQ to send the email asynchronously
        // rabbitTemplate.convertAndSend("email-exchange", "email.confirmation", message);
    }
}
