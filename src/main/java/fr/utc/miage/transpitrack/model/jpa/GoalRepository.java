package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.model.Goal;
import fr.utc.miage.transpitrack.model.User;

/**
 * Spring Data JPA repository for {@link Goal} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus a query
 * for retrieving goals belonging to a specific user.
 * </p>
 */
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * Returns all goals owned by the given user.
     *
     * @param user the user whose goals to retrieve
     * @return a list of {@link Goal} entries belonging to that user
     */
    List<Goal> findByUser(User user);
}
