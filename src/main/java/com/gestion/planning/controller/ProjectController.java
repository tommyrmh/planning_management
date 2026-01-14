package com.gestion.planning.controller;

import com.gestion.planning.dto.CreateProjectRequest;
import com.gestion.planning.dto.ProjectResponse;
import com.gestion.planning.dto.UpdateProjectRequest;
import com.gestion.planning.model.ProjectStatus;
import com.gestion.planning.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        ProjectResponse response = projectService.createProject(request, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse response = projectService.getProjectById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(
            @RequestParam(required = false) ProjectStatus statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ProjectResponse> projects;

        if (statut != null && startDate != null && endDate != null) {
            projects = projectService.getProjectsByStatutAndDateRange(statut, startDate, endDate, pageable);
        } else if (statut != null) {
            projects = projectService.getProjectsByStatut(statut, pageable);
        } else if (startDate != null && endDate != null) {
            projects = projectService.getProjectsByDateRange(startDate, endDate, pageable);
        } else {
            projects = projectService.getAllProjects(pageable);
        }

        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request
    ) {
        ProjectResponse response = projectService.updateProject(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ProjectResponse> closeProject(@PathVariable Long id) {
        ProjectResponse response = projectService.closeProject(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> countByStatut(@RequestParam ProjectStatus statut) {
        long count = projectService.countByStatut(statut);
        return ResponseEntity.ok(count);
    }
}
