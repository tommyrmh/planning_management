package com.gestion.planning.dto;

import com.gestion.planning.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateProjectRequest {

    @NotBlank(message = "Le nom du projet est requis")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    private String description;

    @NotBlank(message = "Le nom du client est requis")
    @Size(max = 100, message = "Le nom du client ne peut pas dépasser 100 caractères")
    private String client;

    @NotNull(message = "La date de début est requise")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est requise")
    private LocalDate dateFin;

    @NotNull(message = "Le statut est requis")
    private ProjectStatus statut;
}
