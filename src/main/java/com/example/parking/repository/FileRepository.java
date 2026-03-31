package com.example.parking.repository;

import com.example.parking.model.ParkingSlot;
import com.example.parking.model.Setting;
import com.example.parking.model.Ticket;
import com.example.parking.model.Staff;
import com.example.parking.model.Member;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FileRepository {

    private final String SLOTS_FILE = "slots.txt";
    private final String SETTINGS_FILE = "settings.txt";
    private final String TICKETS_FILE = "tickets.txt";
    private final String STAFF_FILE = "staff.txt";
    private final String MEMBERS_FILE = "members.txt";

    private List<ParkingSlot> slots = new ArrayList<>();
    private List<Setting> settings = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private List<Staff> staffMembers = new ArrayList<>();
    private List<Member> members = new ArrayList<>();

    private AtomicLong slotIdGen = new AtomicLong(1);
    private AtomicLong settingIdGen = new AtomicLong(1);
    private AtomicLong ticketIdGen = new AtomicLong(1);
    private AtomicLong staffIdGen = new AtomicLong(1);
    private AtomicLong memberIdGen = new AtomicLong(1);

    private final ObjectMapper mapper;

    public FileRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void init() {
        loadAll();
    }

    private synchronized void loadAll() {
        slots = loadFromFile(SLOTS_FILE, new TypeReference<List<ParkingSlot>>() {});
        settings = loadFromFile(SETTINGS_FILE, new TypeReference<List<Setting>>() {});
        tickets = loadFromFile(TICKETS_FILE, new TypeReference<List<Ticket>>() {});
        staffMembers = loadFromFile(STAFF_FILE, new TypeReference<List<Staff>>() {});
        members = loadFromFile(MEMBERS_FILE, new TypeReference<List<Member>>() {});

        // Update ID generators based on loaded data
        slotIdGen.set(getMaxId(slots) + 1);
        settingIdGen.set(getMaxId(settings) + 1);
        ticketIdGen.set(getMaxId(tickets) + 1);
        staffIdGen.set(getMaxId(staffMembers) + 1);
        memberIdGen.set(getMaxId(members) + 1);
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

    private <T> void saveToFile(String fileName, List<T> data) {
        try {
            mapper.writeValue(new File(fileName), data);
        } catch (IOException e) {
            System.err.println("Error saving to " + fileName + ": " + e.getMessage());
        }
    }

    private long getMaxId(List<?> list) {
        return list.stream().mapToLong(obj -> {
            try {
                return (Long) obj.getClass().getMethod("getId").invoke(obj);
            } catch (Exception e) {
                return 0L;
            }
        }).max().orElse(0L);
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
    public synchronized long countSlots() { return slots.size(); }
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
        return staffMembers.stream().filter(m -> m.getUsername().equals(username)).findFirst();
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
