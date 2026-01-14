package com.gestion.planning.service;

import com.gestion.planning.dto.CreateProjectRequest;
import com.gestion.planning.dto.ProjectResponse;
import com.gestion.planning.dto.UpdateProjectRequest;
import com.gestion.planning.exception.ResourceNotFoundException;
import com.gestion.planning.model.Project;
import com.gestion.planning.model.ProjectStatus;
import com.gestion.planning.model.User;
import com.gestion.planning.repository.ProjectRepository;
import com.gestion.planning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(CreateProjectRequest request, String username) {
        // Validation des dates
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être avant la date de début");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Project project = Project.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .client(request.getClient())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .statut(request.getStatut())
                .createdBy(user)
                .build();

        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet non trouvé avec l'ID : " + id));
        return mapToResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjectsByStatut(ProjectStatus statut, Pageable pageable) {
        return projectRepository.findByStatut(statut, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjectsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return projectRepository.findByDateRange(startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjectsByStatutAndDateRange(ProjectStatus statut, LocalDate startDate,
                                                                  LocalDate endDate, Pageable pageable) {
        return projectRepository.findByStatutAndDateRange(statut, startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet non trouvé avec l'ID : " + id));

        if (request.getNom() != null) {
            project.setNom(request.getNom());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getClient() != null) {
            project.setClient(request.getClient());
        }
        if (request.getDateDebut() != null) {
            project.setDateDebut(request.getDateDebut());
        }
        if (request.getDateFin() != null) {
            project.setDateFin(request.getDateFin());
        }
        if (request.getStatut() != null) {
            project.setStatut(request.getStatut());
        }

        // Validation des dates après mise à jour
        if (project.getDateFin().isBefore(project.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être avant la date de début");
        }

        Project updatedProject = projectRepository.save(project);
        return mapToResponse(updatedProject);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet non trouvé avec l'ID : " + id));

        // Vérification : le projet ne doit pas avoir de tâches
        // TODO: Ajouter la vérification des tâches associées quand la gestion des tâches sera implémentée

        projectRepository.delete(project);
    }

    public ProjectResponse closeProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet non trouvé avec l'ID : " + id));

        project.setStatut(ProjectStatus.TERMINE);
        project.setClosedAt(LocalDateTime.now());

        Project closedProject = projectRepository.save(project);
        return mapToResponse(closedProject);
    }

    public long countByStatut(ProjectStatus statut) {
        return projectRepository.countByStatut(statut);
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .nom(project.getNom())
                .description(project.getDescription())
                .client(project.getClient())
                .dateDebut(project.getDateDebut())
                .dateFin(project.getDateFin())
                .statut(project.getStatut())
                .createdByUsername(project.getCreatedBy() != null ? project.getCreatedBy().getUsername() : null)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .closedAt(project.getClosedAt())
                .build();
    }
}
