package com.example.parking.controller;

import com.example.parking.model.ParkingSlot;
import com.example.parking.model.Ticket;
import com.example.parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    // --- Authentication ---
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        // Hardcoded admin for simplicity
        if ("admin".equals(username) && "admin123".equals(password)) {
            return ResponseEntity.ok(Map.of("token", "admin-sim-token", "role", "ADMIN", "name", "Administrator"));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    // --- Slots ---
    @GetMapping("/slots")
    public List<ParkingSlot> getAllSlots() {
        return parkingService.getAllSlots();
    }

    // --- Tickets ---
    @PostMapping("/tickets/entry")
    public ResponseEntity<?> parkVehicle(@RequestBody Map<String, Object> request) {
        String vehicleNumber = (String) request.get("vehicleNumber");
        Number preferredSlotIdNum = (Number) request.get("preferredSlotId");
        Long preferredSlotId = preferredSlotIdNum != null ? preferredSlotIdNum.longValue() : null;

        if (vehicleNumber == null || vehicleNumber.isEmpty()) {
            return ResponseEntity.badRequest().body("Vehicle number is required");
        }
        try {
            Ticket ticket = parkingService.parkVehicle(vehicleNumber, preferredSlotId);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/tickets/exit/{slotId}")
    public ResponseEntity<?> unparkVehicle(@PathVariable Long slotId) {
        try {
            Ticket ticket = parkingService.unparkVehicle(slotId);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/tickets/history")
    public List<Ticket> getHistory() {
        return parkingService.getHistory();
    }

    // --- Admin & Revenue ---
    @GetMapping("/admin/rate")
    public ResponseEntity<?> getRate() {
        return ResponseEntity.ok(Map.of("rate", parkingService.getHourlyRate()));
    }

    @PostMapping("/admin/rate")
    public ResponseEntity<?> updateRate(@RequestBody Map<String, Double> request) {
        Double newRate = request.get("rate");
        if (newRate == null || newRate <= 0) {
            return ResponseEntity.badRequest().body("Invalid rate");
        }
        parkingService.updateHourlyRate(newRate);
        return ResponseEntity.ok(Map.of("message", "Rate updated successfully"));
    }

    @GetMapping("/admin/revenue/stats")
    public ResponseEntity<?> getRevenueStats() {
        return ResponseEntity.ok(Map.of(
            "today", parkingService.getTodayRevenue(),
            "total", parkingService.getTotalRevenue(),
            "activeCount", parkingService.getAllSlots().stream().filter(ParkingSlot::isOccupied).count()
        ));
    }

    @GetMapping("/admin/report/daily")
    public ResponseEntity<Map<String, Object>> getDailyReportData() {
        return ResponseEntity.ok(parkingService.getDailyReportData());
    }

    @GetMapping("/admin/slots/count")
    public ResponseEntity<?> getSlotCount() {
        return ResponseEntity.ok(Map.of("count", parkingService.getSlotCount()));
    }

    @PostMapping("/admin/slots/count")
    public ResponseEntity<?> updateSlotCount(@RequestBody Map<String, Integer> request) {
        Integer newCount = request.get("count");
        if (newCount == null || newCount <= 0) {
            return ResponseEntity.badRequest().body("Invalid slot count");
        }
        try {
            parkingService.updateSlotCount(newCount);
            return ResponseEntity.ok(Map.of("message", "Slot count updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- Staff Slot Booking ---
    @PostMapping("/staff/book/{slotId}/{staffId}")
    public ResponseEntity<?> bookSlot(@PathVariable Long slotId, @PathVariable Long staffId) {
        try {
            parkingService.bookSlot(slotId, staffId);
            return ResponseEntity.ok(Map.of("message", "Slot booked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/staff/unbook/{slotId}/{staffId}")
    public ResponseEntity<?> unbookSlot(@PathVariable Long slotId, @PathVariable Long staffId) {
        try {
            parkingService.unbookSlot(slotId, staffId);
            return ResponseEntity.ok(Map.of("message", "Slot unbooked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
