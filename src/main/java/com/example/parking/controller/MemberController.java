package com.example.parking.controller;

import com.example.parking.model.Member;
import com.example.parking.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // Login for Members
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        Optional<Member> member = memberService.login(username, password);
        if (member.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "token", "member-sim-token-" + member.get().getId(),
                "role", "MEMBER",
                "id", member.get().getId(),
                "name", member.get().getName()
            ));
        }
        return ResponseEntity.status(401).body("Invalid member credentials");
    }

    // Change Password (without username, just ID and new password)
    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("password");
        if (newPassword == null || newPassword.length() < 4) {
            return ResponseEntity.badRequest().body("Password too weak");
        }
        memberService.changePassword(id, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    // --- Admin Endpoints for Members ---
    
    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @PostMapping
    public ResponseEntity<?> addMember(@RequestBody Member member) {
        try {
            return ResponseEntity.ok(memberService.addMember(member));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/fee")
    public ResponseEntity<?> getCycleFee(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("fee", memberService.calculateCurrentCycleFee(id)));
    }
}
