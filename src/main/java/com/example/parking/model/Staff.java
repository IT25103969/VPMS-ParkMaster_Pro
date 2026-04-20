package com.example.parking.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Staff extends User {
    private String department;
    private String username;
    private String password;
    private LocalDate joinDate = LocalDate.now();
    private boolean active = true;
    private List<String> accessibleTabs = new ArrayList<>();

    public Staff() {
        super();
        this.joinDate = LocalDate.now();
        this.active = true;
        this.accessibleTabs = new ArrayList<>();
    }

    public Staff(Long id, String name, String email, String department, String username, String password) {
        super(id, name, email);
        this.department = department;
        this.username = username;
        this.password = password;
        this.joinDate = LocalDate.now();
        this.active = true;
        this.accessibleTabs = new ArrayList<>();
    }

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<String> getAccessibleTabs() { return accessibleTabs; }
    public void setAccessibleTabs(List<String> accessibleTabs) { this.accessibleTabs = accessibleTabs; }
}
