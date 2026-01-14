package com.gestion.planning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAvailabilityRequest {

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean disponible;
    private String commentaire;
}
