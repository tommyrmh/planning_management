package com.gestion.planning.service;

import com.gestion.planning.dto.*;
import com.gestion.planning.model.*;
import com.gestion.planning.model.ProjectStatus;
import com.gestion.planning.repository.ProjectRepository;
import com.gestion.planning.repository.TaskRepository;
import com.gestion.planning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AvailabilityService availabilityService;

    public TaskResponse createTask(CreateTaskRequest request, User currentUser) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        // Règle: Projet TERMINE ou ANNULE ne peut plus recevoir de tâches
        if (project.getStatut() == ProjectStatus.TERMINE || project.getStatut() == ProjectStatus.ANNULE) {
            throw new IllegalArgumentException("Impossible de créer une tâche dans un projet terminé ou annulé");
        }

        // Règle: Date de fin après date de début
        if (request.getDateDebut().isAfter(request.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // Règle: Dates de la tâche dans l'intervalle du projet
        if (request.getDateDebut().isBefore(project.getDateDebut()) ||
            request.getDateFin().isAfter(project.getDateFin())) {
            throw new IllegalArgumentException(
                String.format("Les dates de la tâche doivent être dans l'intervalle du projet (%s - %s)",
                    project.getDateDebut(), project.getDateFin()));
        }

        Task task = Task.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .project(project)
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .priorite(request.getPriorite())
                .statut(request.getStatut())
                .createdBy(currentUser)
                .build();

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Long projectId, TaskStatus statut, Long assignedToId,
                                          LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Task> tasks;

        if (projectId != null && statut != null) {
            tasks = taskRepository.findByProjectIdAndStatut(projectId, statut, pageable);
        } else if (projectId != null) {
            tasks = taskRepository.findByProjectId(projectId, pageable);
        } else if (statut != null) {
            tasks = taskRepository.findByStatut(statut, pageable);
        } else if (assignedToId != null) {
            tasks = taskRepository.findByAssignedToId(assignedToId, pageable);
        } else if (startDate != null && endDate != null) {
            tasks = taskRepository.findByDateRange(startDate, endDate, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }

        return tasks.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        return mapToResponse(task);
    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        if (!hasManagerRole(currentUser) && !hasAdminRole(currentUser)) {
            throw new AccessDeniedException("Seuls les managers et admins peuvent modifier les tâches");
        }

        if (request.getTitre() != null) {
            task.setTitre(request.getTitre());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDateDebut() != null) {
            task.setDateDebut(request.getDateDebut());
        }
        if (request.getDateFin() != null) {
            task.setDateFin(request.getDateFin());
        }
        if (request.getPriorite() != null) {
            task.setPriorite(request.getPriorite());
        }

        // Règle: Date de fin après date de début
        if (task.getDateDebut() != null && task.getDateFin() != null &&
                task.getDateDebut().isAfter(task.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // Règle: Dates de la tâche dans l'intervalle du projet
        Project project = task.getProject();
        if (task.getDateDebut().isBefore(project.getDateDebut()) ||
            task.getDateFin().isAfter(project.getDateFin())) {
            throw new IllegalArgumentException(
                String.format("Les dates de la tâche doivent être dans l'intervalle du projet (%s - %s)",
                    project.getDateDebut(), project.getDateFin()));
        }

        // Règle anti-conflit: Si la tâche est assignée et les dates changent, vérifier les conflits
        if (task.getAssignedTo() != null && (request.getDateDebut() != null || request.getDateFin() != null)) {
            List<Task> conflicts = taskRepository.findConflictingTasks(
                    task.getAssignedTo().getId(), task.getDateDebut(), task.getDateFin(), task.getId());
            if (!conflicts.isEmpty()) {
                String conflictingTaskTitles = conflicts.stream()
                        .map(Task::getTitre)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
                throw new IllegalArgumentException(
                    String.format("Le collaborateur assigné a déjà une tâche sur ce créneau: %s", conflictingTaskTitles));
            }
        }

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    public TaskResponse changeTaskStatus(Long id, TaskStatus newStatus, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        task.setStatut(newStatus);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    public TaskResponse assignTask(Long taskId, Long userId, User currentUser) {
        if (!hasManagerRole(currentUser) && !hasAdminRole(currentUser)) {
            throw new AccessDeniedException("Seuls les managers peuvent assigner des tâches");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier la disponibilité
        boolean isAvailable = availabilityService.isUserAvailable(
                userId, task.getDateDebut(), task.getDateFin());
        if (!isAvailable) {
            throw new IllegalArgumentException(
                    "L'utilisateur n'est pas disponible pour cette période");
        }

        // Règle anti-conflit: Vérifier que le collaborateur n'a pas de tâche sur le même créneau
        List<Task> conflicts = taskRepository.findConflictingTasks(
                userId, task.getDateDebut(), task.getDateFin(), taskId);
        if (!conflicts.isEmpty()) {
            String conflictingTaskTitles = conflicts.stream()
                    .map(Task::getTitre)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            throw new IllegalArgumentException(
                String.format("Ce collaborateur a déjà une tâche sur ce créneau: %s", conflictingTaskTitles));
        }

        task.setAssignedTo(assignee);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    public TaskResponse reassignTask(Long taskId, Long newUserId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        boolean isManager = hasManagerRole(currentUser) || hasAdminRole(currentUser);
        boolean isCurrentlyAssigned = task.getAssignedTo() != null &&
                task.getAssignedTo().getId().equals(currentUser.getId());

        if (!isManager && !isCurrentlyAssigned) {
            throw new AccessDeniedException(
                    "Seuls les managers ou le collaborateur actuellement assigné peuvent réassigner cette tâche");
        }

        User newAssignee = userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier la disponibilité
        boolean isAvailable = availabilityService.isUserAvailable(
                newUserId, task.getDateDebut(), task.getDateFin());
        if (!isAvailable) {
            throw new IllegalArgumentException(
                    "L'utilisateur n'est pas disponible pour cette période");
        }

        // Règle anti-conflit: Vérifier que le nouveau collaborateur n'a pas de tâche sur le même créneau
        List<Task> conflicts = taskRepository.findConflictingTasks(
                newUserId, task.getDateDebut(), task.getDateFin(), taskId);
        if (!conflicts.isEmpty()) {
            String conflictingTaskTitles = conflicts.stream()
                    .map(Task::getTitre)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            throw new IllegalArgumentException(
                String.format("Ce collaborateur a déjà une tâche sur ce créneau: %s", conflictingTaskTitles));
        }

        task.setAssignedTo(newAssignee);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    public void deleteTask(Long id, User currentUser) {
        if (!hasAdminRole(currentUser)) {
            throw new AccessDeniedException("Seuls les admins peuvent supprimer des tâches");
        }

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectIdAndStatut(projectId, null);
    }

    @Transactional(readOnly = true)
    public long countTasksByStatus(TaskStatus status) {
        return taskRepository.countByStatut(status);
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .titre(task.getTitre())
                .description(task.getDescription())
                .projectId(task.getProject().getId())
                .projectNom(task.getProject().getNom())
                .dateDebut(task.getDateDebut())
                .dateFin(task.getDateFin())
                .priorite(task.getPriorite())
                .statut(task.getStatut())
                .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .assignedToName(task.getAssignedTo() != null ?
                        task.getAssignedTo().getFirstName() + " " + task.getAssignedTo().getLastName() : null)
                .createdById(task.getCreatedBy().getId())
                .createdByName(task.getCreatedBy().getFirstName() + " " + task.getCreatedBy().getLastName())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private boolean hasManagerRole(User user) {
        return user.getRole() == Role.MANAGER;
    }

    private boolean hasAdminRole(User user) {
        return user.getRole() == Role.ADMIN;
    }
}
