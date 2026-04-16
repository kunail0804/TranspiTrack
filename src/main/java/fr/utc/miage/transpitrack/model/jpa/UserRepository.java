package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.utc.miage.transpitrack.model.User;

/**
 * Spring Data JPA repository for {@link User} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus custom
 * JPQL queries for user search, email lookup, and challenge participation checks.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Returns all users whose first name exactly matches the given value.
     *
     * @param firstName the first name to search for
     * @return a list of matching users
     */
    @Query("SELECT u FROM User u WHERE u.firstName = ?1 ")
    List<User> findUserByFirstName(String firstName);

    /**
     * Returns all users whose last name exactly matches the given value.
     *
     * @param name the last name to search for
     * @return a list of matching users
     */
    @Query("SELECT u FROM User u WHERE u.name = ?1 ")
    List<User> findUserByName(String name);

    /**
     * Returns all users whose first name, last name, or full name contains the given
     * query string (case-insensitive).
     *
     * @param query the search term to match against
     * @return a list of users whose name partially matches the query
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(CONCAT(u.firstName, ' ', u.name)) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<User> searchByFullName(String query);

    /**
     * Returns the user with the given email address, or {@code null} if not found.
     *
     * @param email the email address to look up
     * @return the matching {@link User}, or {@code null}
     */
    @Query("SELECT u FROM User u WHERE u.email = ?1 ")
    User findByEmail(String email);

    /**
     * Returns the IDs of all challenges joined (not created) by the given user.
     *
     * @param userId the ID of the user
     * @return a set of joined challenge IDs
     */
    @Query("SELECT c.id FROM User u JOIN u.joinedChallenges c WHERE u.id = :userId")
    Set<Long> findJoinedChallengeIdsByUserId(@Param("userId") Long userId);

    /**
     * Returns the IDs of all challenges created by the given user.
     *
     * @param userId the ID of the user
     * @return a set of created challenge IDs
     */
    @Query("SELECT c.id FROM Challenge c WHERE c.creator.id = :userId")
    Set<Long> findCreatedChallengeIdsByUserId(@Param("userId") Long userId);

    /**
     * Returns whether the given user has joined the given challenge.
     *
     * @param userId      the ID of the user
     * @param challengeId the ID of the challenge
     * @return {@code true} if the user has joined that challenge
     */
    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.joinedChallenges c WHERE u.id = :userId AND c.id = :challengeId")
    boolean hasJoinedChallenge(@Param("userId") Long userId, @Param("challengeId") Long challengeId);
}
