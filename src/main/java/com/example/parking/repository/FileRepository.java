package com.example.parking.repository;

import com.example.parking.model.ParkingSlot;
import com.example.parking.model.Setting;
import com.example.parking.model.Ticket;
import com.example.parking.model.Staff;
import com.example.parking.model.Member;
import com.example.parking.model.ProblemReport;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
// ... (omitting some imports for brevity in explanation, but providing exact code below)
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FileRepository {

    private final String DATA_DIR = "data/";
    private final String SLOTS_FILE = DATA_DIR + "slots.txt";
    private final String SETTINGS_FILE = DATA_DIR + "settings.txt";
    private final String TICKETS_FILE = DATA_DIR + "tickets.txt";
    private final String STAFF_FILE = DATA_DIR + "staff.txt";
    private final String MEMBERS_FILE = DATA_DIR + "members.txt";
    private final String LOGINS_FILE = DATA_DIR + "logins.txt";
    private final String REPORTS_FILE = DATA_DIR + "reports.txt";

    private List<ParkingSlot> slots = new ArrayList<>();
    private List<Setting> settings = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private List<Staff> staffMembers = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    private List<ProblemReport> problemReports = new ArrayList<>();

    private AtomicLong slotIdGen = new AtomicLong(1);
    private AtomicLong settingIdGen = new AtomicLong(1);
    private AtomicLong ticketIdGen = new AtomicLong(1);
    private AtomicLong staffIdGen = new AtomicLong(1);
    private AtomicLong memberIdGen = new AtomicLong(1);
    private AtomicLong reportIdGen = new AtomicLong(1);

    private final ObjectMapper mapper;

    public FileRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @PostConstruct
    public void init() {
        ensureDataDirectoryExists();
        migrateExistingFiles();
        loadAll();
    }

    private void ensureDataDirectoryExists() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private void migrateExistingFiles() {
        String[] files = {"slots.txt", "settings.txt", "tickets.txt", "staff.txt", "members.txt", "logins.txt", "parking_data_log.txt", "reports.txt"};
        for (String fileName : files) {
            File oldFile = new File(fileName);
            if (oldFile.exists() && oldFile.isFile()) {
                File newFile = new File(DATA_DIR + fileName);
                if (!newFile.exists()) {
                    boolean success = oldFile.renameTo(newFile);
                    if (success) {
                        System.out.println("Migrated " + fileName + " to data/ directory.");
                    }
                }
            }
        }
    }

    private synchronized void loadAll() {
        slots = loadFromFile(SLOTS_FILE, new TypeReference<List<ParkingSlot>>() {});
        settings = loadFromFile(SETTINGS_FILE, new TypeReference<List<Setting>>() {});
        tickets = loadFromFile(TICKETS_FILE, new TypeReference<List<Ticket>>() {});
        staffMembers = loadFromFile(STAFF_FILE, new TypeReference<List<Staff>>() {});
        members = loadFromFile(MEMBERS_FILE, new TypeReference<List<Member>>() {});
        problemReports = loadFromFile(REPORTS_FILE, new TypeReference<List<ProblemReport>>() {});

        // Update ID generators based on loaded data
        slotIdGen.set(slots.stream().mapToLong(s -> s.getId() != null ? s.getId() : 0).max().orElse(0) + 1);
        settingIdGen.set(settings.stream().mapToLong(s -> s.getId() != null ? s.getId() : 0).max().orElse(0) + 1);
        ticketIdGen.set(tickets.stream().mapToLong(t -> t.getId() != null ? t.getId() : 0).max().orElse(0) + 1);
        staffIdGen.set(staffMembers.stream().mapToLong(s -> s.getId() != null ? s.getId() : 0).max().orElse(0) + 1);
        memberIdGen.set(members.stream().mapToLong(m -> m.getId() != null ? m.getId() : 0).max().orElse(0) + 1);
        reportIdGen.set(problemReports.stream().mapToLong(r -> r.getId() != null ? r.getId() : 0).max().orElse(0) + 1);
    }

    private <T> List<T> loadFromFile(String fileName, TypeReference<List<T>> typeReference) {
        File file = new File(fileName);
        if (!file.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.err.println("Error loading from " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public synchronized void logLogin(String username) {
        String logEntry = LocalDateTime.now().toString() + "," + username + "\n";
        try {
            Files.write(Paths.get(LOGINS_FILE), 
                logEntry.getBytes(), 
                StandardOpenOption.CREATE, 
                StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String[]> getLoginsForDate(LocalDate date) {
        List<String[]> logins = new ArrayList<>();
        try {
            Path path = Paths.get(LOGINS_FILE);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        try {
                            LocalDateTime dt = LocalDateTime.parse(parts[0]);
                            if (dt.toLocalDate().equals(date)) {
                                logins.add(parts);
                            }
                        } catch (Exception e) {}
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logins;
    }

    private <T> void saveToFile(String fileName, List<T> data) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), data);
        } catch (IOException e) {
            System.err.println("Error saving to " + fileName + ": " + e.getMessage());
        }
    }

    // ProblemReport Operations
    public synchronized List<ProblemReport> findAllReports() { return new ArrayList<>(problemReports); }
    public synchronized ProblemReport saveReport(ProblemReport report) {
        if (report.getId() == null) {
            report.setId(reportIdGen.getAndIncrement());
            problemReports.add(report);
        } else {
            problemReports.stream().filter(r -> r.getId().equals(report.getId())).findFirst().ifPresent(r -> {
                int index = problemReports.indexOf(r);
                problemReports.set(index, report);
            });
        }
        saveToFile(REPORTS_FILE, problemReports);
        return report;
    }

    // ParkingSlot Operations
    public synchronized List<ParkingSlot> findAllSlots() { return new ArrayList<>(slots); }
    public synchronized Optional<ParkingSlot> findSlotById(Long id) {
        return slots.stream().filter(s -> s.getId().equals(id)).findFirst();
    }
    public synchronized ParkingSlot saveSlot(ParkingSlot slot) {
        if (slot.getId() == null) {
            slot.setId(slotIdGen.getAndIncrement());
            slots.add(slot);
        } else {
            slots.stream().filter(s -> s.getId().equals(slot.getId())).findFirst().ifPresent(s -> {
                int index = slots.indexOf(s);
                slots.set(index, slot);
            });
        }
        saveToFile(SLOTS_FILE, slots);
        return slot;
    }
    public synchronized void deleteSlot(ParkingSlot slot) {
        slots.removeIf(s -> s.getId().equals(slot.getId()));
        saveToFile(SLOTS_FILE, slots);
    }
    public synchronized long countSlots() { return (long) slots.size(); }
    public synchronized List<ParkingSlot> findSlotsByOccupied(boolean isOccupied) {
        return slots.stream().filter(s -> s.isOccupied() == isOccupied).collect(Collectors.toList());
    }
    public synchronized ParkingSlot findSlotBySlotNumber(String slotNumber) {
        return slots.stream().filter(s -> s.getSlotNumber().equals(slotNumber)).findFirst().orElse(null);
    }

    // Setting Operations
    public synchronized Optional<Setting> findSettingByKey(String key) {
        return settings.stream().filter(s -> s.getConfigKey().equals(key)).findFirst();
    }
    public synchronized Setting saveSetting(Setting setting) {
        if (setting.getId() == null) {
            setting.setId(settingIdGen.getAndIncrement());
            settings.add(setting);
        } else {
            settings.stream().filter(s -> s.getId().equals(setting.getId())).findFirst().ifPresent(s -> {
                int index = settings.indexOf(s);
                settings.set(index, setting);
            });
        }
        saveToFile(SETTINGS_FILE, settings);
        return setting;
    }

    // Ticket Operations
    public synchronized List<Ticket> findAllTickets() { return new ArrayList<>(tickets); }
    public synchronized Optional<Ticket> findActiveTicketBySlotId(Long slotId) {
        return tickets.stream()
                .filter(t -> t.getSlot() != null && t.getSlot().getId().equals(slotId) && t.getStatus() == Ticket.TicketStatus.ACTIVE)
                .findFirst();
    }
    public synchronized List<Ticket> findTicketsByStatus(Ticket.TicketStatus status) {
        return tickets.stream().filter(t -> t.getStatus() == status).collect(Collectors.toList());
    }
    public synchronized Ticket saveTicket(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(ticketIdGen.getAndIncrement());
            tickets.add(ticket);
        } else {
            tickets.stream().filter(t -> t.getId().equals(ticket.getId())).findFirst().ifPresent(t -> {
                int index = tickets.indexOf(t);
                tickets.set(index, ticket);
            });
        }
        saveToFile(TICKETS_FILE, tickets);
        return ticket;
    }

    // Staff Operations
    public synchronized List<Staff> findAllStaff() { return new ArrayList<>(staffMembers); }
    public synchronized Optional<Staff> findStaffByUsername(String username) {
        if (username == null) return Optional.empty();
        return staffMembers.stream()
                .filter(m -> username.equals(m.getUsername()))
                .findFirst();
    }
    public synchronized Optional<Staff> findStaffById(Long id) {
        return staffMembers.stream().filter(m -> m.getId().equals(id)).findFirst();
    }
    public synchronized Staff saveStaff(Staff staff) {
        if (staff.getId() == null) {
            staff.setId(staffIdGen.getAndIncrement());
            staffMembers.add(staff);
        } else {
            staffMembers.stream().filter(m -> m.getId().equals(staff.getId())).findFirst().ifPresent(m -> {
                int index = staffMembers.indexOf(m);
                staffMembers.set(index, staff);
            });
        }
        saveToFile(STAFF_FILE, staffMembers);
        return staff;
    }
    public synchronized void deleteStaff(Long id) {
        staffMembers.removeIf(m -> m.getId().equals(id));
        saveToFile(STAFF_FILE, staffMembers);
    }

    // Member Operations
    public synchronized List<Member> findAllMembers() { return new ArrayList<>(members); }
    public synchronized Optional<Member> findMemberById(Long id) {
        return members.stream().filter(m -> m.getId().equals(id)).findFirst();
    }
    public synchronized Member saveMember(Member member) {
        if (member.getId() == null) {
            member.setId(memberIdGen.getAndIncrement());
            members.add(member);
        } else {
            members.stream().filter(m -> m.getId().equals(member.getId())).findFirst().ifPresent(m -> {
                int index = members.indexOf(m);
                members.set(index, member);
            });
        }
        saveToFile(MEMBERS_FILE, members);
        return member;
    }
    public synchronized void deleteMember(Long id) {
        members.removeIf(m -> m.getId().equals(id));
        saveToFile(MEMBERS_FILE, members);
    }
}
