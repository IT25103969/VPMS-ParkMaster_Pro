package com.example.parking.service;

import com.example.parking.model.Member;
import com.example.parking.model.Setting;
import com.example.parking.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private FileRepository fileRepository;

    private static final String MEMBERSHIP_FEE_KEY = "MEMBERSHIP_FEE";
    private static final String DEFAULT_FEE = "50.0";

    public List<Member> getAllMembers() {
        return fileRepository.findAllMembers();
    }

    public Member addMember(Member member) {
        if (fileRepository.findMemberByUsername(member.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        return fileRepository.saveMember(member);
    }

    public void updateMember(Member member) {
        fileRepository.saveMember(member);
    }

    public void deleteMember(Long id) {
        fileRepository.deleteMember(id);
    }

    public Optional<Member> login(String username, String password) {
        return fileRepository.findMemberByUsername(username)
                .filter(m -> m.getPassword().equals(password) && m.isActive());
    }

    public void changePassword(Long memberId, String newPassword) {
        fileRepository.findMemberById(memberId).ifPresent(m -> {
            m.setPassword(newPassword);
            fileRepository.saveMember(m);
        });
    }

    public double calculateCurrentCycleFee(Long memberId) {
        Member member = fileRepository.findMemberById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        double monthlyFee = Double.parseDouble(fileRepository.findSettingByKey(MEMBERSHIP_FEE_KEY)
                .map(Setting::getConfigValue).orElse(DEFAULT_FEE));
        
        // Logic: Calculated on a cycle from 1 to 30th of a month
        // For simplicity, if they are active, they owe the monthly fee for the current cycle
        return monthlyFee;
    }

    public void updateMembershipFee(double fee) {
        Setting setting = fileRepository.findSettingByKey(MEMBERSHIP_FEE_KEY)
                .orElse(new Setting(null, MEMBERSHIP_FEE_KEY, String.valueOf(fee)));
        setting.setConfigValue(String.valueOf(fee));
        fileRepository.saveSetting(setting);
    }
}
