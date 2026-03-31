package com.example.parking.service;

import com.example.parking.model.ParkingSlot;
import com.example.parking.model.Ticket;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FilePersistenceService {

    private static final String LOG_FILE = "parking_data_log.txt";

    public synchronized void logStateChange(String operation, List<ParkingSlot> slots, List<Ticket> activeTickets) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("-------------------------------------------------------------------------------");
            pw.println("Timestamp: " + LocalDateTime.now());
            pw.println("Operation: " + operation);
            pw.println("Active Sessions: " + activeTickets.size());
            pw.println("Parking Map Status:");

            for (ParkingSlot slot : slots) {
                pw.println(String.format("Slot %s: %s | Reserved: %s", 
                    slot.getSlotNumber(), 
                    slot.isOccupied() ? "Occupied" : "Free",
                    slot.isBookedByStaff() ? "Staff ID " + slot.getStaffId() : "No"));
            }

            pw.println("Active Tickets:");
            for (Ticket ticket : activeTickets) {
                pw.println(String.format(" - Vehicle: %s | Slot: %s | Entry: %s",
                    ticket.getVehicleNumber(),
                    ticket.getSlot().getSlotNumber(),
                    ticket.getEntryTime()));
            }
            pw.println("-------------------------------------------------------------------------------");
            pw.flush();

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
