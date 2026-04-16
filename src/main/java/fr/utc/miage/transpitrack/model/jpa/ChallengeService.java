package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Challenge;

/**
 * Service layer for {@link Challenge} entities.
 * <p>
 * Provides CRUD operations and filtering queries for challenges,
 * delegating to {@link ChallengeRepository}.
 * </p>
 */
@Service
public class ChallengeService {

    /** Repository used to persist and retrieve challenges. */
    @Autowired
    ChallengeRepository challengeRepository;

    /**
     * Persists a new challenge.
     *
     * @param challenge the {@link Challenge} to create
     * @return the saved challenge (with generated ID)
     */
    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    /**
     * Returns all challenges in the database.
     *
     * @return a list of all {@link Challenge} entities
     */
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    /**
     * Returns the challenge with the given ID.
     *
     * @param id the challenge ID
     * @return the matching {@link Challenge}
     * @throws IllegalArgumentException if no challenge with that ID exists
     */
    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Challenge introuvable avec l'ID : " + id));
    }

    /**
     * Returns all challenges with the specified visibility setting.
     *
     * @param visibility the visibility to filter on (e.g., {@code "PUBLIC"})
     * @return a list of matching challenges
     */
    public List<Challenge> getChallengesByVisibility(String visibility){
        return challengeRepository.findChallengesByVisibility(visibility);
    }

    /**
     * Returns all challenges created by the specified user.
     *
     * @param creatorId the ID of the creator user
     * @return a list of challenges created by that user
     */
    public List<Challenge> getChallengesByCreatorId(Long creatorId) {
        return challengeRepository.findByCreatorId(creatorId);
    }
}
