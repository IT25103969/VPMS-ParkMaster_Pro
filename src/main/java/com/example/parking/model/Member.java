package com.example.parking.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Member extends User {
    private LocalDate joinDate = LocalDate.now();
    private boolean active = true;

    public Member() {
        super();
        this.joinDate = LocalDate.now();
        this.active = true;
    }

    public Member(Long id, String name, String email) {
        super(id, name, email);
        this.joinDate = LocalDate.now();
        this.active = true;
    }

    // Getters and Setters
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
