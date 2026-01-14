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
public class UpdatePlanningRequest {

    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private PlanningType type;
    private Long projectId;
    private Long taskId;
}
