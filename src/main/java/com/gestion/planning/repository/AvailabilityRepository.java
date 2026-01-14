package com.gestion.planning.repository;

import com.gestion.planning.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByUserId(Long userId);

    @Query("SELECT a FROM Availability a WHERE a.user.id = :userId " +
           "AND ((a.dateDebut <= :endDate AND a.dateFin >= :startDate))")
    List<Availability> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Availability a WHERE " +
           "((a.dateDebut <= :endDate AND a.dateFin >= :startDate))")
    List<Availability> findByDateRange(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Availability a WHERE a.user.id = :userId " +
           "AND a.disponible = true " +
           "AND a.dateDebut <= :date AND a.dateFin >= :date")
    List<Availability> findAvailableByUserIdAndDate(@Param("userId") Long userId,
                                                     @Param("date") LocalDate date);

    @Query("SELECT a FROM Availability a WHERE a.user.id = :userId " +
           "AND ((a.dateDebut <= :endDate AND a.dateFin >= :startDate))")
    List<Availability> checkOverlap(@Param("userId") Long userId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
}
