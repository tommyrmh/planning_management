package com.gestion.planning.repository;

import com.gestion.planning.model.Task;
import com.gestion.planning.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByStatut(TaskStatus statut, Pageable pageable);

    Page<Task> findByAssignedToId(Long assignedToId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.dateDebut >= :startDate AND t.dateFin <= :endDate")
    Page<Task> findByDateRange(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.statut = :statut")
    Page<Task> findByProjectIdAndStatut(@Param("projectId") Long projectId,
                                         @Param("statut") TaskStatus statut,
                                         Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.statut = :statut")
    Page<Task> findByAssignedToIdAndStatut(@Param("userId") Long userId,
                                            @Param("statut") TaskStatus statut,
                                            Pageable pageable);

    List<Task> findByProjectIdAndStatut(Long projectId, TaskStatus statut);

    long countByStatut(TaskStatus statut);

    long countByProjectId(Long projectId);

    // Vérifier les conflits : tâches assignées au même utilisateur sur le même créneau
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId " +
           "AND t.id != :excludeTaskId " +
           "AND t.statut != 'DONE' " +
           "AND ((t.dateDebut <= :dateFin AND t.dateFin >= :dateDebut))")
    List<Task> findConflictingTasks(@Param("userId") Long userId,
                                     @Param("dateDebut") LocalDate dateDebut,
                                     @Param("dateFin") LocalDate dateFin,
                                     @Param("excludeTaskId") Long excludeTaskId);

    // Variante pour nouvelle assignation (sans exclure de tâche)
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId " +
           "AND t.statut != 'DONE' " +
           "AND ((t.dateDebut <= :dateFin AND t.dateFin >= :dateDebut))")
    List<Task> findConflictingTasksForUser(@Param("userId") Long userId,
                                            @Param("dateDebut") LocalDate dateDebut,
                                            @Param("dateFin") LocalDate dateFin);
}
