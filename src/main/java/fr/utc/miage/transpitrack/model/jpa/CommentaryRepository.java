package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.model.Commentary;

/**
 * Spring Data JPA repository for {@link Commentary} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus queries
 * for retrieving commentaries by author/activity pair and by activity (newest first).
 * </p>
 */
public interface CommentaryRepository extends JpaRepository<Commentary, Long> {

    /**
     * Returns all commentaries written by the given author on the given activity.
     * Due to the unique constraint on the table, this list contains at most one entry.
     *
     * @param authorId   the ID of the author user
     * @param activityId the ID of the activity
     * @return a list of matching commentaries (0 or 1 element)
     */
    List<Commentary> findByAuthorIdAndActivityId(Long authorId, Long activityId);

    /**
     * Returns all commentaries for the given activity, ordered by ID descending
     * (most recent first).
     *
     * @param activityId the ID of the activity
     * @return an ordered list of commentaries for that activity
     */
    List<Commentary> findByActivityIdOrderByIdDesc(Long activityId);
}
