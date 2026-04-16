package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Challenge;
import fr.utc.miage.transpitrack.model.ChallengeScore;
import fr.utc.miage.transpitrack.model.User;

/**
 * Service layer for {@link ChallengeScore} entities.
 * <p>
 * Provides operations for submitting and retrieving user scores within a challenge,
 * including a leaderboard query sorted by descending score.
 * </p>
 */
@Service
public class ChallengeScoreService {

    /** Repository used to persist and retrieve challenge scores. */
    @Autowired
    ChallengeScoreRepository challengeScoreRepository;

    /**
     * Persists a new or updated score entry.
     *
     * @param score the {@link ChallengeScore} to save
     * @return the saved score entry (with generated ID if new)
     */
    public ChallengeScore addScore(ChallengeScore score) {
        return challengeScoreRepository.save(score);
    }

    /**
     * Returns all score entries for the given challenge.
     *
     * @param challenge the challenge to filter on
     * @return a list of {@link ChallengeScore} entries
     */
    public List<ChallengeScore> getScoresByChallenge(Challenge challenge) {
        return challengeScoreRepository.findByChallenge(challenge);
    }

    /**
     * Returns the score entry for the given user and challenge, or {@code null} if none exists.
     *
     * @param user      the user whose score to retrieve
     * @param challenge the challenge to look up
     * @return the matching {@link ChallengeScore}, or {@code null}
     */
    public ChallengeScore getScoreByUserAndChallenge(User user, Challenge challenge) {
        return challengeScoreRepository.findByUserAndChallenge(user, challenge);
    }

    /**
     * Returns the leaderboard for the given challenge: all score entries sorted by
     * descending score.
     *
     * @param challengeId the ID of the challenge
     * @return an ordered list of {@link ChallengeScore} entries (highest score first)
     */
    public List<ChallengeScore> getClassementParChallenge(Long challengeId) {
        return challengeScoreRepository.findByChallengeIdOrderByScoreDesc(challengeId);
    }
}
