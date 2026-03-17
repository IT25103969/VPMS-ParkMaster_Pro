package com.example.parking.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Member implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String storeName;
    private String username;
    private String password;
    private LocalDate membershipStartDate = LocalDate.now();
    private boolean active = true;

    public Member() {
        this.membershipStartDate = LocalDate.now();
        this.active = true;
    }

    public Member(Long id, String name, String storeName, String username, String password) {
        this.id = id;
        this.name = name;
        this.storeName = storeName;
        this.username = username;
        this.password = password;
        this.membershipStartDate = LocalDate.now();
        this.active = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getMembershipStartDate() { return membershipStartDate; }
    public void setMembershipStartDate(LocalDate membershipStartDate) { this.membershipStartDate = membershipStartDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
