package fr.utc.miage.transpitrack.model.jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    List<Activity> findByUserId(Long userId);

    List<Activity> findBySportId(Long sportId);

    List<Activity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Activity> findByEvaluation(String evaluation);

}
