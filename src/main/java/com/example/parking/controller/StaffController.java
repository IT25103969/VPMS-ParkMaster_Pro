package com.example.parking.controller;

import com.example.parking.model.Staff;
import com.example.parking.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    @Autowired
    private StaffService staffService;

    // Login for Staff
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        Optional<Staff> staff = staffService.login(username, password);
        if (staff.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "token", "staff-sim-token-" + staff.get().getId(),
                "role", "STAFF",
                "id", staff.get().getId(),
                "name", staff.get().getName()
            ));
        }
        return ResponseEntity.status(401).body("Invalid staff credentials");
    }

    // Change Password
    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("password");
        if (newPassword == null || newPassword.length() < 4) {
            return ResponseEntity.badRequest().body("Password too weak");
        }
        staffService.changePassword(id, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    // --- Admin Endpoints for Staff ---
    
    @GetMapping
    public List<Staff> getAllStaff() {
        return staffService.getAllStaff();
    }

    @PostMapping
    public ResponseEntity<?> addStaff(@RequestBody Staff staff) {
        try {
            return ResponseEntity.ok(staffService.addStaff(staff));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/allowance")
    public ResponseEntity<?> getCycleAllowance(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("allowance", staffService.calculateCurrentCycleAllowance(id)));
    }
}
