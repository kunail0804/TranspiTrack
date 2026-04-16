package fr.utc.miage.transpitrack.model.jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.Activity;

/**
 * Spring Data JPA repository for {@link Activity} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus custom
 * query methods for filtering activities by user, sport, date range, and evaluation.
 * </p>
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * Returns all activities logged by the specified user.
     *
     * @param userId the ID of the user
     * @return a list of activities belonging to that user
     */
    List<Activity> findByUserId(Long userId);

    /**
     * Returns all activities associated with the specified sport.
     *
     * @param sportId the ID of the sport
     * @return a list of activities for that sport
     */
    List<Activity> findBySportId(Long sportId);

    /**
     * Returns all activities whose date falls within the given range (inclusive).
     *
     * @param startDate the start of the date range
     * @param endDate   the end of the date range
     * @return a list of activities within that range
     */
    List<Activity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Returns all activities with the specified evaluation text.
     *
     * @param evaluation the evaluation string to match
     * @return a list of matching activities
     */
    List<Activity> findByEvaluation(String evaluation);
}
