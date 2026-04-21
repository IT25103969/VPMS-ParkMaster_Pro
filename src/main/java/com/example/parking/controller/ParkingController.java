package com.example.parking.controller;

import com.example.parking.model.ParkingSlot;
import com.example.parking.model.Ticket;
import com.example.parking.model.Staff;
import com.example.parking.model.ProblemReport;
import com.example.parking.service.ParkingService;
import com.example.parking.service.StaffService;
import com.example.parking.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private FileRepository fileRepository;

    // --- Reports ---
    @GetMapping("/reports")
    public List<ProblemReport> getReports() {
        return fileRepository.findAllReports();
    }

    @PostMapping("/reports")
    public ProblemReport addReport(@RequestBody ProblemReport report) {
        report.setReportTime(LocalDateTime.now());
        return fileRepository.saveReport(report);
    }

    // --- Authentication ---
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        // 1. Check for Admin
        if ("admin".equals(username) && parkingService.getAdminPassword().equals(password)) {
            return ResponseEntity.ok(Map.of(
                "token", "admin-sim-token",
                "role", "ADMIN",
                "id", 0,
                "name", "Administrator"
            ));
        }

        // 2. Check for Staff
        Optional<Staff> staff = staffService.login(username, password);
        if (staff.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("token", "staff-sim-token-" + staff.get().getId());
            response.put("role", "STAFF");
            response.put("id", staff.get().getId());
            response.put("name", staff.get().getName());
            response.put("accessibleTabs", staff.get().getAccessibleTabs());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/admin/password")
    public ResponseEntity<?> updateAdminPassword(@RequestBody Map<String, String> request) {
        String newPassword = request.get("password");
        if (newPassword == null || newPassword.length() < 4) {
            return ResponseEntity.badRequest().body("Password too weak (min 4 characters)");
        }
        parkingService.updateAdminPassword(newPassword);
        return ResponseEntity.ok(Map.of("message", "Admin password updated successfully"));
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
        String vehicleType = (String) request.get("vehicleType");
        Number preferredSlotIdNum = (Number) request.get("preferredSlotId");
        Long preferredSlotId = preferredSlotIdNum != null ? preferredSlotIdNum.longValue() : null;

        if (vehicleNumber == null || vehicleNumber.isEmpty()) {
            return ResponseEntity.badRequest().body("Vehicle number is required");
        }
        try {
            Ticket ticket = parkingService.parkVehicle(vehicleNumber, vehicleType, preferredSlotId);
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

    // --- Staff/Member Slot Booking ---
    @PostMapping("/book/{slotId}/{id}/{type}")
    public ResponseEntity<?> bookSlot(@PathVariable Long slotId, @PathVariable Long id, @PathVariable String type) {
        try {
            boolean isStaff = "STAFF".equalsIgnoreCase(type);
            parkingService.bookSlot(slotId, id, isStaff);
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

    @PostMapping("/member/unbook/{slotId}/{memberId}")
    public ResponseEntity<?> unbookMemberSlot(@PathVariable Long slotId, @PathVariable Long memberId) {
        try {
            parkingService.unbookMemberSlot(slotId, memberId);
            return ResponseEntity.ok(Map.of("message", "Slot unbooked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
