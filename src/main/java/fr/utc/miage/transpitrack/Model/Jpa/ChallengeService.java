package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.Model.Challenge;

@Service
public class ChallengeService {

    @Autowired
    ChallengeRepository challengeRepository;

    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    public Challenge getChallengeById(Long id) {
        return challengeRepository.findChallengeById(id);
    }

    public List<Challenge> getChallengesByVisibility(String visibility){
        return challengeRepository.findChallengesByVisibility(visibility);
    }
}