package com.busora.dto;

import com.busora.model.Role;

public class AuthResponse {

    private boolean success;
    private String message;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private Role role;

    public AuthResponse() {
    }

    public static AuthResponse success(Long userId, String fullName, String email, String phone, Role role) {
        AuthResponse resp = new AuthResponse();
        resp.success = true;
        resp.message = "Authentication successful";
        resp.userId = userId;
        resp.fullName = fullName;
        resp.email = email;
        resp.phone = phone;
        resp.role = role;
        return resp;
    }

    public static AuthResponse failure(String message) {
        AuthResponse resp = new AuthResponse();
        resp.success = false;
        resp.message = message;
        return resp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
