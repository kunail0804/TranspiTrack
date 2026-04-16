package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.Challenge;

/**
 * Spring Data JPA repository for {@link Challenge} entities.
 * <p>
 * Extends {@link JpaRepository} with custom queries for retrieving challenges
 * by visibility setting or by the creator's ID.
 * </p>
 */
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    /**
     * Returns the challenge with the given ID, or {@code null} if not found.
     *
     * @param id the challenge ID
     * @return the matching {@link Challenge}, or {@code null}
     */
    Challenge findChallengeById(Long id);

    /**
     * Returns all challenges with the specified visibility setting.
     *
     * @param visibility the visibility value to filter on (e.g., {@code "PUBLIC"})
     * @return a list of matching challenges
     */
    @Query("SELECT c FROM Challenge c WHERE c.visibility = :visibility")
    List<Challenge> findChallengesByVisibility(String visibility);

    /**
     * Returns all challenges created by the specified user.
     *
     * @param creatorId the ID of the creator user
     * @return a list of challenges created by that user
     */
    List<Challenge> findByCreatorId(Long creatorId);
}
