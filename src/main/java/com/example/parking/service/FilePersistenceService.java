package com.example.parking.service;

import com.example.parking.model.ParkingSlot;
import com.example.parking.model.Ticket;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilePersistenceService {

    private static final String FILE_NAME = "parking_data_log.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public synchronized void logStateChange(String operation, List<ParkingSlot> slots, List<Ticket> activeTickets) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             PrintWriter pw = new PrintWriter(fw)) {

            String timestamp = LocalDateTime.now().format(formatter);
            pw.println("[" + timestamp + "] OPERATION: " + operation);
            
            long occupiedCount = slots.stream().filter(ParkingSlot::isOccupied).count();
            pw.println("Summary: Total Slots: " + slots.size() + " | Occupied: " + occupiedCount + " | Free: " + (slots.size() - occupiedCount));
            
            pw.println("Current Slots State:");
            for (ParkingSlot slot : slots) {
                String status = slot.isOccupied() ? "OCCUPIED" : "FREE";
                String vehicleInfo = "";
                
                if (slot.isOccupied()) {
                    vehicleInfo = activeTickets.stream()
                        .filter(t -> t.getSlot() != null && t.getSlot().getId().equals(slot.getId()))
                        .map(t -> " (Vehicle: " + t.getVehicleNumber() + ")")
                        .findFirst()
                        .orElse("");
                }
                
                pw.println(" - " + slot.getSlotNumber() + ": " + status + vehicleInfo);
            }
            
            pw.println("--------------------------------------------------------------------------------");
            pw.flush();

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
