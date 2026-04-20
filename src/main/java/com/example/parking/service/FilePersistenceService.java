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

    private static final String LOG_FILE = "data/parking_data_log.txt";

    public synchronized void logStateChange(String operation, List<ParkingSlot> slots, List<Ticket> activeTickets) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            long freeSlots = slots.stream().filter(s -> !s.isOccupied() && !s.isBookedByStaff()).count();
            long reservedSlots = slots.stream().filter(s -> s.isBookedByStaff()).count();

            pw.println("-------------------------------------------------------------------------------");
            pw.println("Timestamp   : " + LocalDateTime.now());
            pw.println("Operation   : " + operation);
            pw.println("Status      : " + activeTickets.size() + " Active | " + freeSlots + " Free | " + reservedSlots + " Reserved");
            
            if (!activeTickets.isEmpty()) {
                pw.println("Active Vehicles:");
                for (Ticket ticket : activeTickets) {
                    pw.println(String.format(" > %s (Slot %s) - Entry: %s",
                        ticket.getVehicleNumber(),
                        ticket.getSlot().getSlotNumber(),
                        ticket.getEntryTime()));
                }
            }
            pw.println("-------------------------------------------------------------------------------");
            pw.flush();

        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
