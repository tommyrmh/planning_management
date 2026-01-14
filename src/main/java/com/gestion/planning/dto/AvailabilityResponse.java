package com.gestion.planning.dto;

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
public class AvailabilityResponse {

    private Long id;
    private Long userId;
    private String userName;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean disponible;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
