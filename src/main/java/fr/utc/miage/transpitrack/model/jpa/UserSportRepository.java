package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserSport;

/**
 * Spring Data JPA repository for {@link UserSport} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus queries
 * for retrieving sport preferences by user and by user/sport pair.
 * </p>
 */
public interface UserSportRepository extends JpaRepository<UserSport, Long> {

    /**
     * Returns all sport preferences declared by the given user.
     *
     * @param user the user whose preferences to retrieve
     * @return a list of {@link UserSport} entries for that user
     */
    List<UserSport> findByUser(User user);

    /**
     * Returns the preference record for the given user and sport, or {@code null}
     * if the user has not declared a preference for that sport.
     *
     * @param user  the user to look up
     * @param sport the sport to look up
     * @return the matching {@link UserSport}, or {@code null}
     */
    @Query("SELECT u FROM UserSport u WHERE u.user = :user AND u.sport = :sport")
    UserSport findByUserAndSport(User user, Sport sport);
}
