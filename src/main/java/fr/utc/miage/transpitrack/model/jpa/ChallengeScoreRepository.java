package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.Challenge;
import fr.utc.miage.transpitrack.model.ChallengeScore;
import fr.utc.miage.transpitrack.model.User;

/**
 * Spring Data JPA repository for {@link ChallengeScore} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus queries
 * for retrieving scores by challenge or by a specific user/challenge pair,
 * and a leaderboard query ordered by descending score.
 * </p>
 */
@Repository
public interface ChallengeScoreRepository extends JpaRepository<ChallengeScore, Long> {

    /**
     * Returns all score entries for the given challenge.
     *
     * @param challenge the challenge to filter on
     * @return a list of {@link ChallengeScore} entries for that challenge
     */
    List<ChallengeScore> findByChallenge(Challenge challenge);

    /**
     * Returns the score entry for the given user and challenge pair, or {@code null}.
     *
     * @param user      the user whose score to retrieve
     * @param challenge the challenge to look up
     * @return the matching {@link ChallengeScore}, or {@code null} if not found
     */
    ChallengeScore findByUserAndChallenge(User user, Challenge challenge);

    /**
     * Returns all score entries for the given challenge ordered by score descending,
     * suitable for displaying a leaderboard.
     *
     * @param challengeId the ID of the challenge
     * @return a list of {@link ChallengeScore} entries sorted from highest to lowest score
     */
    List<ChallengeScore> findByChallengeIdOrderByScoreDesc(Long challengeId);
}
