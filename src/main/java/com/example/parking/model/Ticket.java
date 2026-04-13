package com.example.parking.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum TicketStatus {
        ACTIVE, COMPLETED
    }

    private Long id;
    private String vehicleNumber;
    private String vehicleType;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double amount;
    private TicketStatus status;
    private ParkingSlot slot;

    // Constructors
    public Ticket() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public ParkingSlot getSlot() { return slot; }
    public void setSlot(ParkingSlot slot) { this.slot = slot; }
}
