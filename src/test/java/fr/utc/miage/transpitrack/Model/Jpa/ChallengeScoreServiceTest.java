package fr.utc.miage.transpitrack.Model.Jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.Model.Challenge;
import fr.utc.miage.transpitrack.Model.ChallengeScore;
import fr.utc.miage.transpitrack.Model.User;

@ExtendWith(MockitoExtension.class)
class ChallengeScoreServiceTest {

    @Mock
    private ChallengeScoreRepository challengeScoreRepository;

    @InjectMocks
    private ChallengeScoreService challengeScoreService;

    // ── addScore ───────────────────────────────────────────────────

    @Test
    void addScoreShouldSaveAndReturnScore() {
        ChallengeScore score = new ChallengeScore(new User(), new Challenge(), 75.0);
        when(challengeScoreRepository.save(score)).thenReturn(score);

        ChallengeScore result = challengeScoreService.addScore(score);

        assertEquals(score, result);
        verify(challengeScoreRepository).save(score);
    }

    // ── getScoresByChallenge ───────────────────────────────────────

    @Test
    void getScoresByChallengeShouldReturnAllScoresForChallenge() {
        Challenge challenge = new Challenge();
        ChallengeScore s1 = new ChallengeScore(new User(), challenge, 10.0);
        ChallengeScore s2 = new ChallengeScore(new User(), challenge, 20.0);
        when(challengeScoreRepository.findByChallenge(challenge)).thenReturn(List.of(s1, s2));

        List<ChallengeScore> result = challengeScoreService.getScoresByChallenge(challenge);

        assertEquals(2, result.size());
        verify(challengeScoreRepository).findByChallenge(challenge);
    }

    @Test
    void getScoresByChallengeShouldReturnEmptyListWhenNoScores() {
        Challenge challenge = new Challenge();
        when(challengeScoreRepository.findByChallenge(challenge)).thenReturn(List.of());

        List<ChallengeScore> result = challengeScoreService.getScoresByChallenge(challenge);

        assertEquals(0, result.size());
        verify(challengeScoreRepository).findByChallenge(challenge);
    }

    // ── getScoreByUserAndChallenge ─────────────────────────────────

    @Test
    void getScoreByUserAndChallengeShouldReturnScoreWhenFound() {
        User user = new User();
        Challenge challenge = new Challenge();
        ChallengeScore score = new ChallengeScore(user, challenge, 42.0);
        when(challengeScoreRepository.findByUserAndChallenge(user, challenge)).thenReturn(score);

        ChallengeScore result = challengeScoreService.getScoreByUserAndChallenge(user, challenge);

        assertEquals(score, result);
        verify(challengeScoreRepository).findByUserAndChallenge(user, challenge);
    }

    @Test
    void getScoreByUserAndChallengeShouldReturnNullWhenNotFound() {
        User user = new User();
        Challenge challenge = new Challenge();
        when(challengeScoreRepository.findByUserAndChallenge(user, challenge)).thenReturn(null);

        assertNull(challengeScoreService.getScoreByUserAndChallenge(user, challenge));
        verify(challengeScoreRepository).findByUserAndChallenge(user, challenge);
    }
}
