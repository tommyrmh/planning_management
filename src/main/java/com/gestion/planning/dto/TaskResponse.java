package com.gestion.planning.dto;

import com.gestion.planning.model.TaskPriority;
import com.gestion.planning.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private String titre;
    private String description;
    private Long projectId;
    private String projectNom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private TaskPriority priorite;
    private TaskStatus statut;
    private Long assignedToId;
    private String assignedToName;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
