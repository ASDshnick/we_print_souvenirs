package com.weprintsouvenirs.we_print_souvenirs.user.model;

import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_blocked")
    private boolean isBlocked = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UserEntity() {
    }

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isBlocked() { return isBlocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
