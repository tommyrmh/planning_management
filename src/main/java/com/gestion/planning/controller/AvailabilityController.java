package com.gestion.planning.controller;

import com.gestion.planning.dto.AvailabilityResponse;
import com.gestion.planning.dto.CreateAvailabilityRequest;
import com.gestion.planning.dto.UpdateAvailabilityRequest;
import com.gestion.planning.model.User;
import com.gestion.planning.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<AvailabilityResponse> createAvailability(
            @Valid @RequestBody CreateAvailabilityRequest request,
            @AuthenticationPrincipal User currentUser) {
        AvailabilityResponse response = availabilityService.createAvailability(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> getAllAvailabilities() {
        List<AvailabilityResponse> availabilities = availabilityService.getAllAvailabilities();
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilitiesByUser(
            @PathVariable Long userId) {
        List<AvailabilityResponse> availabilities = availabilityService.getAvailabilitiesByUser(userId);
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilitiesByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AvailabilityResponse> availabilities =
                availabilityService.getAvailabilitiesByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/range")
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AvailabilityResponse> availabilities =
                availabilityService.getAvailabilitiesByDateRange(startDate, endDate);
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> getAvailabilityById(@PathVariable Long id) {
        AvailabilityResponse response = availabilityService.getAvailabilityById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        AvailabilityResponse response = availabilityService.updateAvailability(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        boolean isAvailable = availabilityService.isUserAvailable(userId, startDate, endDate);
        return ResponseEntity.ok(isAvailable);
    }
}
