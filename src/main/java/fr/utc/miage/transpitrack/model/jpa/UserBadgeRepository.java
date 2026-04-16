package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.model.Badge;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserBadge;

/**
 * Spring Data JPA repository for {@link UserBadge} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus queries
 * for retrieving a user's earned badges and checking whether a specific badge
 * has already been awarded to a user.
 * </p>
 */
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    /**
     * Returns all badge award records for the given user.
     *
     * @param user the user whose badges to retrieve
     * @return a list of {@link UserBadge} records belonging to that user
     */
    List<UserBadge> findByUser(User user);

    /**
     * Returns whether the given badge has already been awarded to the given user.
     *
     * @param user  the user to check
     * @param badge the badge to check
     * @return {@code true} if the user already holds that badge
     */
    boolean existsByUserAndBadge(User user, Badge badge);
}
