package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.Challenge;
import fr.utc.miage.transpitrack.model.ChallengeScore;
import fr.utc.miage.transpitrack.model.User;

@Repository
public interface ChallengeScoreRepository extends JpaRepository<ChallengeScore, Long> {
    List<ChallengeScore> findByChallenge(Challenge challenge);
    ChallengeScore findByUserAndChallenge(User user, Challenge challenge);
    List<ChallengeScore> findByChallengeIdOrderByScoreDesc(Long challengeId);
}
