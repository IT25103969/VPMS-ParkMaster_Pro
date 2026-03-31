package com.example.parking.controller;

import com.example.parking.model.Member;
import com.example.parking.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @PostMapping
    public ResponseEntity<?> addMember(@RequestBody Member member) {
        return ResponseEntity.ok(memberService.addMember(member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fee")
    public ResponseEntity<?> getFee() {
        return ResponseEntity.ok(Map.of("fee", memberService.getMembershipFee()));
    }

    @PostMapping("/fee")
    public ResponseEntity<?> updateFee(@RequestBody Map<String, Double> request) {
        Double newFee = request.get("fee");
        if (newFee == null || newFee < 0) {
            return ResponseEntity.badRequest().body("Invalid fee");
        }
        memberService.updateMembershipFee(newFee);
        return ResponseEntity.ok(Map.of("message", "Membership fee updated successfully"));
    }
}
