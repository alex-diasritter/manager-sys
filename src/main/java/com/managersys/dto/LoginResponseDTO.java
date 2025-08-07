package com.managersys.dto;

import com.managersys.model.Role;
import java.util.List;


public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long id;
    private String email;
    private String name;
    private Role role;
    private List<String> roles;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String accessToken, String refreshToken, String tokenType, Long id, String email, String name, Role role, List<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponseDTO that = (LoginResponseDTO) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(email, that.email) &&
               Objects.equals(accessToken, that.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, accessToken);
    }
    
    // ToString
    @Override
    public String toString() {
        return "LoginResponseDTO{" +
               "accessToken='[PROTECTED]'" +
               ", refreshToken='[PROTECTED]'" +
               ", tokenType='" + tokenType + '\'' +
               ", id=" + id +
               ", email='" + email + '\'' +
               ", name='" + name + '\'' +
               ", role=" + role +
               ", roles=" + roles +
               '}';
    }
}
