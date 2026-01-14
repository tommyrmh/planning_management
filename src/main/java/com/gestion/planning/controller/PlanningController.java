package com.gestion.planning.controller;

import com.gestion.planning.dto.CreatePlanningRequest;
import com.gestion.planning.dto.PlanningResponse;
import com.gestion.planning.dto.UpdatePlanningRequest;
import com.gestion.planning.model.PlanningType;
import com.gestion.planning.model.User;
import com.gestion.planning.service.PlanningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/plannings")
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningService planningService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<PlanningResponse> createPlanning(
            @Valid @RequestBody CreatePlanningRequest request,
            @AuthenticationPrincipal User currentUser) {
        PlanningResponse response = planningService.createPlanning(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PlanningResponse>> getAllPlannings() {
        List<PlanningResponse> plannings = planningService.getAllPlannings();
        return ResponseEntity.ok(plannings);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlanningResponse>> getPlanningsByUser(@PathVariable Long userId) {
        List<PlanningResponse> plannings = planningService.getPlanningsByUser(userId);
        return ResponseEntity.ok(plannings);
    }

    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<PlanningResponse>> getPlanningsByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<PlanningResponse> plannings =
                planningService.getPlanningsByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(plannings);
    }

    @GetMapping("/range")
    public ResponseEntity<List<PlanningResponse>> getPlanningsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<PlanningResponse> plannings =
                planningService.getPlanningsByDateRange(startDate, endDate);
        return ResponseEntity.ok(plannings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanningResponse> getPlanningById(@PathVariable Long id) {
        PlanningResponse response = planningService.getPlanningById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<PlanningResponse> updatePlanning(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlanningRequest request) {
        PlanningResponse response = planningService.updatePlanning(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Void> deletePlanning(@PathVariable Long id) {
        planningService.deletePlanning(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<PlanningResponse>> getPlanningsByType(@PathVariable PlanningType type) {
        List<PlanningResponse> plannings = planningService.getPlanningsByType(type);
        return ResponseEntity.ok(plannings);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<PlanningResponse>> getPlanningsByProject(@PathVariable Long projectId) {
        List<PlanningResponse> plannings = planningService.getPlanningsByProject(projectId);
        return ResponseEntity.ok(plannings);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<PlanningResponse>> getPlanningsByTask(@PathVariable Long taskId) {
        List<PlanningResponse> plannings = planningService.getPlanningsByTask(taskId);
        return ResponseEntity.ok(plannings);
    }
}
