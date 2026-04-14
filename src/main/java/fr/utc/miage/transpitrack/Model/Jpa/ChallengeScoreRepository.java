package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.Model.Challenge;
import fr.utc.miage.transpitrack.Model.ChallengeScore;

@Repository
public interface ChallengeScoreRepository extends JpaRepository<ChallengeScore, Long> {
    List<ChallengeScore> findByChallenge(Challenge challenge);
}
