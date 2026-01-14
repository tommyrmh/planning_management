package com.gestion.planning.service;

import com.gestion.planning.dto.CreatePlanningRequest;
import com.gestion.planning.dto.PlanningResponse;
import com.gestion.planning.dto.UpdatePlanningRequest;
import com.gestion.planning.model.*;
import com.gestion.planning.repository.PlanningRepository;
import com.gestion.planning.repository.ProjectRepository;
import com.gestion.planning.repository.TaskRepository;
import com.gestion.planning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public PlanningResponse createPlanning(CreatePlanningRequest request, User currentUser) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (request.getDateDebut().isAfter(request.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        Planning.PlanningBuilder builder = Planning.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .user(user)
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .type(request.getType())
                .createdBy(currentUser);

        // Lien optionnel avec projet
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
            builder.project(project);
        }

        // Lien optionnel avec tâche
        if (request.getTaskId() != null) {
            Task task = taskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
            builder.task(task);
        }

        Planning planning = builder.build();
        planning = planningRepository.save(planning);
        return mapToResponse(planning);
    }

    @Transactional(readOnly = true)
    public List<PlanningResponse> getAllPlannings() {
        return planningRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanningResponse> getPlanningsByUser(Long userId) {
        return planningRepository.findByUserIdOrderByDateDebutDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanningResponse> getPlanningsByUserAndDateRange(
            Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return planningRepository.findByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanningResponse> getPlanningsByDateRange(
            LocalDateTime startDate, LocalDateTime endDate) {
        return planningRepository.findByDateRange(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlanningResponse getPlanningById(Long id) {
        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planning non trouvé"));
        return mapToResponse(planning);
    }

    public PlanningResponse updatePlanning(Long id, UpdatePlanningRequest request) {
        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planning non trouvé"));

        if (request.getTitre() != null) {
            planning.setTitre(request.getTitre());
        }
        if (request.getDescription() != null) {
            planning.setDescription(request.getDescription());
        }
        if (request.getDateDebut() != null) {
            planning.setDateDebut(request.getDateDebut());
        }
        if (request.getDateFin() != null) {
            planning.setDateFin(request.getDateFin());
        }
        if (request.getType() != null) {
            planning.setType(request.getType());
        }

        if (planning.getDateDebut().isAfter(planning.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // Mise à jour projet
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
            planning.setProject(project);
        }

        // Mise à jour tâche
        if (request.getTaskId() != null) {
            Task task = taskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
            planning.setTask(task);
        }

        planning = planningRepository.save(planning);
        return mapToResponse(planning);
    }

    public void deletePlanning(Long id) {
        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planning non trouvé"));
        planningRepository.delete(planning);
    }

    @Transactional(readOnly = true)
    public List<PlanningResponse> getPlanningsByType(PlanningType type) {
        return planningRepository.findByType(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanningResponse> getPlanningsByProject(Long projectId) {
        return planningRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanningResponse> getPlanningsByTask(Long taskId) {
        return planningRepository.findByTaskId(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PlanningResponse mapToResponse(Planning planning) {
        return PlanningResponse.builder()
                .id(planning.getId())
                .titre(planning.getTitre())
                .description(planning.getDescription())
                .userId(planning.getUser().getId())
                .userName(planning.getUser().getFirstName() + " " + planning.getUser().getLastName())
                .dateDebut(planning.getDateDebut())
                .dateFin(planning.getDateFin())
                .type(planning.getType())
                .projectId(planning.getProject() != null ? planning.getProject().getId() : null)
                .projectNom(planning.getProject() != null ? planning.getProject().getNom() : null)
                .taskId(planning.getTask() != null ? planning.getTask().getId() : null)
                .taskTitre(planning.getTask() != null ? planning.getTask().getTitre() : null)
                .createdById(planning.getCreatedBy().getId())
                .createdByName(planning.getCreatedBy().getFirstName() + " " + planning.getCreatedBy().getLastName())
                .createdAt(planning.getCreatedAt())
                .updatedAt(planning.getUpdatedAt())
                .build();
    }
}
