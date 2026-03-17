package com.example.parking.model;

import java.io.Serializable;

public class ParkingSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String slotNumber;
    private boolean isOccupied;
    private boolean isBookedByMember;
    private Long memberId;

    // Constructors
    public ParkingSlot() {}
    
    public ParkingSlot(Long id, String slotNumber) {
        this.id = id;
        this.slotNumber = slotNumber;
        this.isOccupied = false;
        this.isBookedByMember = false;
        this.memberId = null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }
    
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }

    public boolean isBookedByMember() { return isBookedByMember; }
    public void setBookedByMember(boolean bookedByMember) { isBookedByMember = bookedByMember; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
}
