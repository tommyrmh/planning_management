package com.gestion.planning.repository;

import com.gestion.planning.model.Planning;
import com.gestion.planning.model.PlanningType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlanningRepository extends JpaRepository<Planning, Long> {

    List<Planning> findByUserId(Long userId);

    @Query("SELECT p FROM Planning p WHERE p.user.id = :userId " +
           "AND p.dateDebut >= :startDate AND p.dateFin <= :endDate " +
           "ORDER BY p.dateDebut ASC")
    List<Planning> findByUserIdAndDateRange(@Param("userId") Long userId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Planning p WHERE " +
           "p.dateDebut >= :startDate AND p.dateFin <= :endDate " +
           "ORDER BY p.dateDebut ASC")
    List<Planning> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    List<Planning> findByType(PlanningType type);

    @Query("SELECT p FROM Planning p WHERE p.user.id = :userId " +
           "AND p.type = :type " +
           "ORDER BY p.dateDebut DESC")
    List<Planning> findByUserIdAndType(@Param("userId") Long userId,
                                        @Param("type") PlanningType type);

    @Query("SELECT p FROM Planning p WHERE p.user.id = :userId " +
           "ORDER BY p.dateDebut DESC")
    List<Planning> findByUserIdOrderByDateDebutDesc(@Param("userId") Long userId);

    @Query("SELECT p FROM Planning p WHERE p.project.id = :projectId " +
           "ORDER BY p.dateDebut ASC")
    List<Planning> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT p FROM Planning p WHERE p.task.id = :taskId " +
           "ORDER BY p.dateDebut ASC")
    List<Planning> findByTaskId(@Param("taskId") Long taskId);
}
