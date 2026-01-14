package com.gestion.planning.dto;

import com.gestion.planning.model.PlanningType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanningResponse {

    private Long id;
    private String titre;
    private String description;
    private Long userId;
    private String userName;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private PlanningType type;
    private Long projectId;
    private String projectNom;
    private Long taskId;
    private String taskTitre;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
