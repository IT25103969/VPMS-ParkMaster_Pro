package com.example.parking.service;

import com.example.parking.model.Staff;
import com.example.parking.model.Setting;
import com.example.parking.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private FileRepository fileRepository;

    private static final String STAFF_ALLOWANCE_KEY = "STAFF_ALLOWANCE";
    private static final String DEFAULT_ALLOWANCE = "100.0";

    public List<Staff> getAllStaff() {
        return fileRepository.findAllStaff();
    }

    public Staff addStaff(Staff staff) {
        if (fileRepository.findStaffByUsername(staff.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        return fileRepository.saveStaff(staff);
    }

    public void updateStaff(Staff staff) {
        fileRepository.saveStaff(staff);
    }

    public void deleteStaff(Long id) {
        fileRepository.deleteStaff(id);
    }

    public Optional<Staff> login(String username, String password) {
        return fileRepository.findStaffByUsername(username)
                .filter(m -> m.getPassword().equals(password) && m.isActive());
    }

    public void changePassword(Long staffId, String newPassword) {
        fileRepository.findStaffById(staffId).ifPresent(m -> {
            m.setPassword(newPassword);
            fileRepository.saveStaff(m);
        });
    }

    public double calculateCurrentCycleAllowance(Long staffId) {
        Staff staff = fileRepository.findStaffById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        return Double.parseDouble(fileRepository.findSettingByKey(STAFF_ALLOWANCE_KEY)
                .map(Setting::getConfigValue).orElse(DEFAULT_ALLOWANCE));
    }

    public void updateStaffAllowance(double allowance) {
        Setting setting = fileRepository.findSettingByKey(STAFF_ALLOWANCE_KEY)
                .orElse(new Setting(null, STAFF_ALLOWANCE_KEY, String.valueOf(allowance)));
        setting.setConfigValue(String.valueOf(allowance));
        fileRepository.saveSetting(setting);
    }
}
