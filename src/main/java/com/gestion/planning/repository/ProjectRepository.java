package com.gestion.planning.repository;

import com.gestion.planning.model.Project;
import com.gestion.planning.model.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Recherche par statut
    Page<Project> findByStatut(ProjectStatus statut, Pageable pageable);

    // Recherche par statut avec liste
    List<Project> findByStatut(ProjectStatus statut);

    // Recherche par client
    Page<Project> findByClientContainingIgnoreCase(String client, Pageable pageable);

    // Recherche par période
    @Query("SELECT p FROM Project p WHERE p.dateDebut >= :startDate AND p.dateFin <= :endDate")
    Page<Project> findByDateRange(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   Pageable pageable);

    // Recherche combinée : statut + période
    @Query("SELECT p FROM Project p WHERE p.statut = :statut AND p.dateDebut >= :startDate AND p.dateFin <= :endDate")
    Page<Project> findByStatutAndDateRange(@Param("statut") ProjectStatus statut,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            Pageable pageable);

    // Compter les projets par statut
    long countByStatut(ProjectStatus statut);

    // Projets actifs (en cours)
    List<Project> findByStatutOrderByDateDebutDesc(ProjectStatus statut);
}
