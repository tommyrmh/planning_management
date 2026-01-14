package com.gestion.planning.dto;

import com.gestion.planning.model.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskRequest {

    private String titre;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private TaskPriority priorite;
}
