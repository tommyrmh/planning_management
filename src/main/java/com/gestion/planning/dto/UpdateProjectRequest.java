package com.gestion.planning.dto;

import com.gestion.planning.model.ProjectStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProjectRequest {

    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    private String description;

    @Size(max = 100, message = "Le nom du client ne peut pas dépasser 100 caractères")
    private String client;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private ProjectStatus statut;
}
