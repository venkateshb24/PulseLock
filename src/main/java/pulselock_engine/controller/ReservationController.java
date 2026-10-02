package pulselock_engine.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pulselock_engine.service.InventoryService;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class ReservationController {

    private final InventoryService inventoryService;

    @PostMapping("/seed")
    public ResponseEntity<String> seedStock(@RequestParam String itemId, @RequestParam int stock) {
        inventoryService.setStock(itemId, stock);
        return ResponseEntity.ok("Stock Initialized for " + itemId + " with " + stock + " items.");
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@RequestParam String itemId, @RequestParam int qty) {
        String result = inventoryService.attemptReservation(itemId, qty);
        if(result.startsWith("SOLD_OUT")) {
            return ResponseEntity.status(409).body(result);
        }
        if(result.startsWith("ERROR")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}
