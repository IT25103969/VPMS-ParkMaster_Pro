package com.example.parking.model;

import java.io.Serializable;

public class ParkingSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String slotNumber;
    private boolean isOccupied;
    private boolean isBookedByStaff;
    private Long staffId;

    // Constructors
    public ParkingSlot() {}
    
    public ParkingSlot(Long id, String slotNumber) {
        this.id = id;
        this.slotNumber = slotNumber;
        this.isOccupied = false;
        this.isBookedByStaff = false;
        this.staffId = null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }
    
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }

    public boolean isBookedByStaff() { return isBookedByStaff; }
    public void setBookedByStaff(boolean bookedByStaff) { isBookedByStaff = bookedByStaff; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }
}
