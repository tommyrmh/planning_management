package com.gestion.planning.service;

import com.gestion.planning.dto.AvailabilityResponse;
import com.gestion.planning.dto.CreateAvailabilityRequest;
import com.gestion.planning.dto.UpdateAvailabilityRequest;
import com.gestion.planning.model.Availability;
import com.gestion.planning.model.User;
import com.gestion.planning.repository.AvailabilityRepository;
import com.gestion.planning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public AvailabilityResponse createAvailability(CreateAvailabilityRequest request, User currentUser) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (request.getDateDebut().isAfter(request.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // Vérifier les chevauchements
        List<Availability> overlapping = availabilityRepository.checkOverlap(
                user.getId(),
                request.getDateDebut(),
                request.getDateFin()
        );

        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Cette période chevauche une disponibilité existante");
        }

        Availability availability = Availability.builder()
                .user(user)
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .disponible(request.getDisponible())
                .commentaire(request.getCommentaire())
                .build();

        availability = availabilityRepository.save(availability);
        return mapToResponse(availability);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAvailabilitiesByUser(Long userId) {
        return availabilityRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAvailabilitiesByUserAndDateRange(
            Long userId, LocalDate startDate, LocalDate endDate) {
        return availabilityRepository.findByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAvailabilitiesByDateRange(
            LocalDate startDate, LocalDate endDate) {
        return availabilityRepository.findByDateRange(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailabilityById(Long id) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));
        return mapToResponse(availability);
    }

    public AvailabilityResponse updateAvailability(Long id, UpdateAvailabilityRequest request) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));

        if (request.getDateDebut() != null) {
            availability.setDateDebut(request.getDateDebut());
        }
        if (request.getDateFin() != null) {
            availability.setDateFin(request.getDateFin());
        }
        if (request.getDisponible() != null) {
            availability.setDisponible(request.getDisponible());
        }
        if (request.getCommentaire() != null) {
            availability.setCommentaire(request.getCommentaire());
        }

        if (availability.getDateDebut().isAfter(availability.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // Vérifier les chevauchements (en excluant l'availability actuelle)
        List<Availability> overlapping = availabilityRepository.checkOverlap(
                availability.getUser().getId(),
                availability.getDateDebut(),
                availability.getDateFin()
        ).stream()
                .filter(a -> !a.getId().equals(id))
                .collect(Collectors.toList());

        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Cette période chevauche une disponibilité existante");
        }

        availability = availabilityRepository.save(availability);
        return mapToResponse(availability);
    }

    public void deleteAvailability(Long id) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));
        availabilityRepository.delete(availability);
    }

    @Transactional(readOnly = true)
    public boolean isUserAvailable(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Availability> availabilities = availabilityRepository.findByUserIdAndDateRange(
                userId, startDate, endDate);

        // Vérifier qu'il n'y a pas de périodes occupées dans la plage
        boolean hasUnavailable = availabilities.stream()
                .anyMatch(a -> !a.getDisponible());

        if (hasUnavailable) {
            return false;
        }

        // Vérifier qu'il existe au moins une période disponible couvrant la plage
        return availabilities.stream()
                .anyMatch(a -> a.getDisponible() &&
                        !a.getDateDebut().isAfter(startDate) &&
                        !a.getDateFin().isBefore(endDate));
    }

    private AvailabilityResponse mapToResponse(Availability availability) {
        return AvailabilityResponse.builder()
                .id(availability.getId())
                .userId(availability.getUser().getId())
                .userName(availability.getUser().getFirstName() + " " + availability.getUser().getLastName())
                .dateDebut(availability.getDateDebut())
                .dateFin(availability.getDateFin())
                .disponible(availability.getDisponible())
                .commentaire(availability.getCommentaire())
                .createdAt(availability.getCreatedAt())
                .updatedAt(availability.getUpdatedAt())
                .build();
    }
}
