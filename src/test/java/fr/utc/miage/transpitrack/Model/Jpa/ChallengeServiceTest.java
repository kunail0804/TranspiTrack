package fr.utc.miage.transpitrack.Model.Jpa;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.Model.Challenge;
import fr.utc.miage.transpitrack.Model.User;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    @Test
    void createChallengeShouldReturnSavedChallenge() {
        User creator = new User();
        Challenge challenge = new Challenge("Run 5km", "PUBLIC", Duration.ofDays(7), creator);
        when(challengeRepository.save(challenge)).thenReturn(challenge);

        Challenge result = challengeService.createChallenge(challenge);

        assertEquals(challenge, result);
        verify(challengeRepository).save(challenge);
    }
}
