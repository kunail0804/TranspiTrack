package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.Model.Challenge;
import fr.utc.miage.transpitrack.Model.ChallengeScore;

@Service
public class ChallengeScoreService {

    @Autowired
    ChallengeScoreRepository challengeScoreRepository;

    public ChallengeScore addScore(ChallengeScore score) {
        return challengeScoreRepository.save(score);
    }

    public List<ChallengeScore> getScoresByChallenge(Challenge challenge) {
        return challengeScoreRepository.findByChallenge(challenge);
    }
}
