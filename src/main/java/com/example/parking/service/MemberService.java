package com.example.parking.service;

import com.example.parking.model.Member;
import com.example.parking.model.Setting;
import com.example.parking.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return fileRepository.saveMember(member);
    }

    public void deleteMember(Long id) {
        fileRepository.deleteMember(id);
    }

    public double getMembershipFee() {
        return Double.parseDouble(fileRepository.findSettingByKey(MEMBERSHIP_FEE_KEY)
                .map(Setting::getConfigValue).orElse(DEFAULT_FEE));
    }

    public void updateMembershipFee(double fee) {
        Setting setting = fileRepository.findSettingByKey(MEMBERSHIP_FEE_KEY)
                .orElse(new Setting(null, MEMBERSHIP_FEE_KEY, String.valueOf(fee)));
        setting.setConfigValue(String.valueOf(fee));
        fileRepository.saveSetting(setting);
    }
}
