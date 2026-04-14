package fr.utc.miage.transpitrack.Model.Jpa;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import fr.utc.miage.transpitrack.Model.Challenge;
import fr.utc.miage.transpitrack.Model.User;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    @Test
    void getAllChallengesShouldReturnAllChallenges() {
        Challenge c1 = new Challenge();
        Challenge c2 = new Challenge();
        when(challengeRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Challenge> result = challengeService.getAllChallenges();

        assertEquals(2, result.size());
        verify(challengeRepository).findAll();
    }

    @Test
    void createChallengeShouldReturnSavedChallenge() {
        User creator = new User();
        Challenge challenge = new Challenge("Run 5km", "PUBLIC", Duration.ofDays(7), creator, null);
        when(challengeRepository.save(challenge)).thenReturn(challenge);

        Challenge result = challengeService.createChallenge(challenge);

        assertEquals(challenge, result);
        verify(challengeRepository).save(challenge);
    }
}
