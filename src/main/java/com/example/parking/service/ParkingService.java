package com.example.parking.service;

import com.example.parking.model.ParkingSlot;
import com.example.parking.model.Ticket;
import com.example.parking.model.Setting;
import com.example.parking.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ParkingService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FilePersistenceService filePersistenceService;

    private static final String RATE_KEY = "HOURLY_RATE";
    private static final String SLOT_COUNT_KEY = "SLOT_COUNT";
    private static final String ADMIN_PASSWORD_KEY = "ADMIN_PASSWORD";
    private static final String DEFAULT_RATE = "10.0";
    private static final String DEFAULT_SLOT_COUNT = "20";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    @PostConstruct
    public void init() {
        if (fileRepository.findSettingByKey(RATE_KEY).isEmpty()) {
            fileRepository.saveSetting(new Setting(null, RATE_KEY, DEFAULT_RATE));
        }
        if (fileRepository.findSettingByKey(SLOT_COUNT_KEY).isEmpty()) {
            fileRepository.saveSetting(new Setting(null, SLOT_COUNT_KEY, DEFAULT_SLOT_COUNT));
        }
        if (fileRepository.findSettingByKey(ADMIN_PASSWORD_KEY).isEmpty()) {
            fileRepository.saveSetting(new Setting(null, ADMIN_PASSWORD_KEY, DEFAULT_ADMIN_PASSWORD));
        }
        
        int count = getSlotCount();
        if (fileRepository.countSlots() == 0) {
            for (int i = 1; i <= count; i++) {
                fileRepository.saveSlot(new ParkingSlot(null, "A" + i));
            }
        }
        syncToFile("SYSTEM_INITIALIZATION");
    }

    private void syncToFile(String operation) {
        List<ParkingSlot> slots = fileRepository.findAllSlots();
        List<Ticket> activeTickets = fileRepository.findTicketsByStatus(Ticket.TicketStatus.ACTIVE);
        filePersistenceService.logStateChange(operation, slots, activeTickets);
    }

    public List<ParkingSlot> getAllSlots() {
        return fileRepository.findAllSlots();
    }

    public double getHourlyRate() {
        return fileRepository.findSettingByKey(RATE_KEY)
                .map(s -> Double.parseDouble(s.getConfigValue()))
                .orElse(Double.parseDouble(DEFAULT_RATE));
    }

    public void updateHourlyRate(double newRate) {
        Setting setting = fileRepository.findSettingByKey(RATE_KEY)
                .orElse(new Setting(null, RATE_KEY, String.valueOf(newRate)));
        setting.setConfigValue(String.valueOf(newRate));
        fileRepository.saveSetting(setting);
        syncToFile("UPDATE_RATE (" + newRate + ")");
    }

    public int getSlotCount() {
        return fileRepository.findSettingByKey(SLOT_COUNT_KEY)
                .map(s -> Integer.parseInt(s.getConfigValue()))
                .orElse(Integer.parseInt(DEFAULT_SLOT_COUNT));
    }

    public void updateSlotCount(int newCount) {
        int currentCount = (int) fileRepository.countSlots();
        if (newCount == currentCount) return;

        if (newCount > currentCount) {
            // Increase
            for (int i = currentCount + 1; i <= newCount; i++) {
                fileRepository.saveSlot(new ParkingSlot(null, "A" + i));
            }
        } else {
            // Decrease (careful with occupied ones)
            List<ParkingSlot> allSlots = fileRepository.findAllSlots();
            int toRemove = currentCount - newCount;
            int removed = 0;
            
            // Remove from the end, only if free
            for (int i = allSlots.size() - 1; i >= 0 && removed < toRemove; i--) {
                ParkingSlot slot = allSlots.get(i);
                if (!slot.isOccupied()) {
                    fileRepository.deleteSlot(slot);
                    removed++;
                } else {
                    throw new RuntimeException("Cannot reduce slot count below " + (i + 1) + " because slot " + slot.getSlotNumber() + " is occupied.");
                }
            }
        }

        Setting setting = fileRepository.findSettingByKey(SLOT_COUNT_KEY)
                .orElse(new Setting(null, SLOT_COUNT_KEY, String.valueOf(newCount)));
        setting.setConfigValue(String.valueOf(newCount));
        fileRepository.saveSetting(setting);
        syncToFile("ADJUST_CAPACITY (Total: " + newCount + ")");
    }

    public List<ParkingSlot> getAvailableSlotsForGeneral() {
        return fileRepository.findAllSlots().stream()
                .filter(s -> !s.isOccupied() && !s.isBookedByStaff())
                .collect(java.util.stream.Collectors.toList());
    }

    public void bookSlot(Long slotId, Long staffId) {
        ParkingSlot slot = fileRepository.findSlotById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.isOccupied() || slot.isBookedByStaff()) {
            throw new RuntimeException("Slot is already taken");
        }

        // Check if staff already has a booking
        boolean hasBooking = fileRepository.findAllSlots().stream()
                .anyMatch(s -> staffId.equals(s.getStaffId()));
        if (hasBooking) {
            throw new RuntimeException("Staff already has a booked slot");
        }

        slot.setBookedByStaff(true);
        slot.setStaffId(staffId);
        fileRepository.saveSlot(slot);
        syncToFile("STAFF_BOOKING (Staff: " + staffId + " | Slot: " + slot.getSlotNumber() + ")");
    }

    public void unbookSlot(Long slotId, Long staffId) {
        ParkingSlot slot = fileRepository.findSlotById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // If staffId is 0, it's an admin override
        if (staffId != 0 && !staffId.equals(slot.getStaffId())) {
            throw new RuntimeException("This slot is not booked by you");
        }

        slot.setBookedByStaff(false);
        slot.setStaffId(null);
        fileRepository.saveSlot(slot);
        syncToFile("STAFF_UNBOOKING (" + (staffId == 0 ? "ADMIN_OVERRIDE" : "Staff: " + staffId) + " | Slot: " + slot.getSlotNumber() + ")");
    }

    public Ticket parkVehicle(String vehicleNumber, String vehicleType, Long preferredSlotId) {
        if (vehicleNumber == null || !vehicleNumber.matches("^[A-Z]{2,3}[0-9]{4}$")) {
            throw new RuntimeException("Invalid vehicle number format. Must be 3 letters (or 2) followed by 4 numbers (e.g. ABC1234 or AB1234)");
        }
        if (vehicleType == null || vehicleType.isEmpty()) {
            throw new RuntimeException("Vehicle type is required");
        }

        ParkingSlot slot = null;
        if (preferredSlotId != null) {
            slot = fileRepository.findSlotById(preferredSlotId)
                    .filter(s -> !s.isOccupied())
                    .orElse(null);
        }

        if (slot == null) {
            List<ParkingSlot> freeSlots = getAvailableSlotsForGeneral();
            if (freeSlots.isEmpty()) {
                throw new RuntimeException("No parking slots available");
            }
            slot = freeSlots.get(0);
        }

        slot.setOccupied(true);
        // Clear booking if it was booked
        slot.setBookedByStaff(false);
        slot.setStaffId(null);
        fileRepository.saveSlot(slot);

        Ticket ticket = new Ticket();
        ticket.setVehicleNumber(vehicleNumber);
        ticket.setVehicleType(vehicleType);
        ticket.setEntryTime(LocalDateTime.now());
        ticket.setStatus(Ticket.TicketStatus.ACTIVE);
        ticket.setSlot(slot);
        
        Ticket saved = fileRepository.saveTicket(ticket);
        syncToFile("VEHICLE_ENTRY (Vehicle: " + vehicleNumber + " [" + vehicleType + "] | Slot: " + slot.getSlotNumber() + ")");
        return saved;
    }

    public Ticket unparkVehicle(Long slotId) {
        Ticket ticket = fileRepository.findActiveTicketBySlotId(slotId)
                .orElseThrow(() -> new RuntimeException("No active ticket found for this slot"));

        LocalDateTime exitTime = LocalDateTime.now();
        ticket.setExitTime(exitTime);
        
        long hours = Duration.between(ticket.getEntryTime(), exitTime).toHours();
        if (hours == 0) hours = 1; 
        
        ticket.setAmount(hours * getHourlyRate());
        ticket.setStatus(Ticket.TicketStatus.COMPLETED);
        
        ParkingSlot slot = ticket.getSlot();
        slot.setOccupied(false);
        fileRepository.saveSlot(slot);

        Ticket saved = fileRepository.saveTicket(ticket);
        syncToFile("VEHICLE_EXIT (Vehicle: " + ticket.getVehicleNumber() + " | Slot: " + slot.getSlotNumber() + ")");
        return saved;
    }

    public Map<String, Object> getDailyReportData() {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime startOfDay = today.atStartOfDay();
        
        List<Ticket> tickets = fileRepository.findAllTickets();
        double revenue = tickets.stream()
                .filter(t -> t.getExitTime() != null && t.getExitTime().isAfter(startOfDay))
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();
        long entries = tickets.stream().filter(t -> t.getEntryTime().isAfter(startOfDay)).count();
        long exits = tickets.stream().filter(t -> t.getExitTime() != null && t.getExitTime().isAfter(startOfDay)).count();
        List<String[]> logins = fileRepository.getLoginsForDate(today);

        Map<String, Object> data = new HashMap<>();
        data.put("date", today.toString());
        data.put("revenue", revenue);
        data.put("entries", entries);
        data.put("exits", exits);
        data.put("logins", logins);
        return data;
    }

    public List<Ticket> getHistory() {
        return fileRepository.findAllTickets();
    }

    public double getTodayRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        return fileRepository.findAllTickets().stream()
                .filter(t -> t.getExitTime() != null && t.getExitTime().isAfter(startOfDay))
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();
    }

    public double getTotalRevenue() {
        return fileRepository.findAllTickets().stream()
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();
    }

    public String getAdminPassword() {
        return fileRepository.findSettingByKey(ADMIN_PASSWORD_KEY)
                .map(Setting::getConfigValue)
                .orElse(DEFAULT_ADMIN_PASSWORD);
    }

    public void updateAdminPassword(String newPassword) {
        Setting setting = fileRepository.findSettingByKey(ADMIN_PASSWORD_KEY)
                .orElse(new Setting(null, ADMIN_PASSWORD_KEY, newPassword));
        setting.setConfigValue(newPassword);
        fileRepository.saveSetting(setting);
        syncToFile("UPDATE_ADMIN_PASSWORD");
    }
}
