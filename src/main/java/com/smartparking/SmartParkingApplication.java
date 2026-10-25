
package com.smartparking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class SmartParkingApplication {

    private final Map<Integer, String> slots = new LinkedHashMap<>();
    private int nextBookingId = 1001;

    public SmartParkingApplication() {
        for (int i = 1; i <= 12; i++) {
            slots.put(i, null);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(
            SmartParkingApplication.class, args
        );
    }

    @GetMapping("/")
    public String home() {
        return """
            <h1>Smart Parking System</h1>
            <p>Welcome to Smart Parking!</p>
            <p>Website backend is running.</p>
            <a href="/index.html">Open Parking Dashboard</a>
            """;
    }

    @GetMapping("/api/slots")
    public Map<Integer, String> getSlots() {
        return slots;
    }

    @PostMapping("/api/book")
    public Map<String, Object> book(
            @RequestParam String vehicle) {

        vehicle = vehicle.trim().toUpperCase();

        if (vehicle.isEmpty()) {
            return Map.of("message", "Enter vehicle number");
        }

        if (slots.containsValue(vehicle)) {
            return Map.of("message", "Vehicle already parked");
        }

        for (var entry : slots.entrySet()) {
            if (entry.getValue() == null) {
                entry.setValue(vehicle);

                return Map.of(
                    "bookingId", nextBookingId++,
                    "slot", entry.getKey(),
                    "vehicle", vehicle,
                    "message", "Booking successful"
                );
            }
        }

        return Map.of("message", "Parking full");
    }

    @PostMapping("/api/release/{slot}")
    public Map<String, Object> release(
            @PathVariable int slot) {

        if (!slots.containsKey(slot)
                || slots.get(slot) == null) {
            return Map.of("message", "Slot is empty or invalid");
        }

        String vehicle = slots.get(slot);
        slots.put(slot, null);

        return Map.of(
            "message", "Slot released",
            "slot", slot,
            "vehicle", vehicle
        );
    }
}
