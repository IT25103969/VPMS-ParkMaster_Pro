package com.example.parking.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String department;
    private String username;
    private String password;
    private LocalDate joinDate = LocalDate.now();
    private boolean active = true;

    public Staff() {
        this.joinDate = LocalDate.now();
        this.active = true;
    }

    public Staff(Long id, String name, String department, String username, String password) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.username = username;
        this.password = password;
        this.joinDate = LocalDate.now();
        this.active = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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
}
