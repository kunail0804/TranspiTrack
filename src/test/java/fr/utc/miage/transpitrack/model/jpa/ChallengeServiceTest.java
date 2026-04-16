package fr.utc.miage.transpitrack.model.jpa;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.model.Challenge;
import fr.utc.miage.transpitrack.model.User;

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

    @Test
    void getChallengeByIdShouldReturnChallengeWhenFound() {
        Challenge challenge = new Challenge("Run 5km", "PUBLIC", Duration.ofDays(7), new User(), null);
        when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

        Challenge result = challengeService.getChallengeById(1L);

        assertEquals(challenge, result);
        verify(challengeRepository).findById(1L);
    }

    @Test
    void getChallengeByIdShouldThrowWhenNotFound() {
        when(challengeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> challengeService.getChallengeById(99L));
        verify(challengeRepository).findById(99L);
    }

    @Test
    void getChallengesByVisibilityShouldReturnMatchingChallenges() {
        Challenge c1 = new Challenge();
        Challenge c2 = new Challenge();
        when(challengeRepository.findChallengesByVisibility("PUBLIC")).thenReturn(List.of(c1, c2));

        List<Challenge> result = challengeService.getChallengesByVisibility("PUBLIC");

        assertEquals(2, result.size());
        verify(challengeRepository).findChallengesByVisibility("PUBLIC");
    }

    @Test
    void getChallengesByVisibilityShouldReturnEmptyListWhenNoMatch() {
        when(challengeRepository.findChallengesByVisibility("PRIVATE")).thenReturn(List.of());

        List<Challenge> result = challengeService.getChallengesByVisibility("PRIVATE");

        assertEquals(0, result.size());
        verify(challengeRepository).findChallengesByVisibility("PRIVATE");
    }

    @Test
    void shouldReturnChallengesByCreatorId() {

        Challenge challenge = mock(Challenge.class);
        List<Challenge> expected = List.of(challenge);

        when(challengeRepository.findByCreatorId(1L))
                .thenReturn(expected);

        List<Challenge> result = challengeService.getChallengesByCreatorId(1L);

        assertEquals(expected, result);

        verify(challengeRepository).findByCreatorId(1L);
    }
}
