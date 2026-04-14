package fr.utc.miage.transpitrack.Model.Jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.Model.Enum.Level;
import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.UserSport;

@ExtendWith(MockitoExtension.class)
class UserSportServiceTest {

    @Mock
    private UserSportRepository userSportRepository;

    @InjectMocks
    private UserSportService userSportService;

    // ── getAllUserSport ─────────────────────────────────────────────

    @Test
    void getAllUserSportShouldReturnAllEntries() {
        UserSport us1 = new UserSport();
        UserSport us2 = new UserSport();
        when(userSportRepository.findAll()).thenReturn(List.of(us1, us2));

        List<UserSport> result = userSportService.getAllUserSport();

        assertEquals(2, result.size());
        verify(userSportRepository).findAll();
    }

    // ── getUserSportByUser ──────────────────────────────────────────

    @Test
    void getUserSportByUserShouldReturnEntriesForUser() {
        User user = new User();
        UserSport us = new UserSport(user, new Sport(), Level.BEGINNER);
        when(userSportRepository.findByUser(user)).thenReturn(List.of(us));

        List<UserSport> result = userSportService.getUserSportByUser(user);

        assertEquals(1, result.size());
        verify(userSportRepository).findByUser(user);
    }

    // ── getUserSportByUserAndSport ──────────────────────────────────

    @Test
    void getUserSportByUserAndSportShouldReturnMatchingEntry() {
        User user = new User();
        Sport sport = new Sport();
        UserSport us = new UserSport(user, sport, Level.AMATEUR);
        when(userSportRepository.findByUserAndSport(user, sport)).thenReturn(us);

        UserSport result = userSportService.getUserSportByUserAndSport(user, sport);

        assertEquals(us, result);
        verify(userSportRepository).findByUserAndSport(user, sport);
    }

    @Test
    void getUserSportByUserAndSportShouldReturnNullWhenNotFound() {
        User user = new User();
        Sport sport = new Sport();
        when(userSportRepository.findByUserAndSport(user, sport)).thenReturn(null);

        assertNull(userSportService.getUserSportByUserAndSport(user, sport));
    }

    // ── createUserSport ────────────────────────────────────────────

    @Test
    void createUserSportShouldSaveAndReturnUserSport() {
        UserSport us = new UserSport(new User(), new Sport(), Level.INTERMEDIATE);
        when(userSportRepository.save(us)).thenReturn(us);

        UserSport result = userSportService.createUserSport(us);

        assertEquals(us, result);
        verify(userSportRepository).save(us);
    }

    // ── updateUserSport ────────────────────────────────────────────

    @Test
    void updateUserSportShouldCallSave() {
        UserSport us = new UserSport(new User(), new Sport(), Level.ADVANCED);

        userSportService.updateUserSport(us);

        verify(userSportRepository).save(us);
    }

    // ── deleteUserSport ────────────────────────────────────────────

    @Test
    void deleteUserSportShouldCallDelete() {
        UserSport us = new UserSport();

        userSportService.deleteUserSport(us);

        verify(userSportRepository).delete(us);
    }

    // ── getUserSportById ───────────────────────────────────────────

    @Test
    void getUserSportByIdShouldReturnUserSportWhenFound() {
        UserSport us = new UserSport();
        when(userSportRepository.findById(1L)).thenReturn(Optional.of(us));

        assertEquals(us, userSportService.getUserSportById(1L));
    }

    @Test
    void getUserSportByIdShouldReturnNullWhenNotFound() {
        when(userSportRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(userSportService.getUserSportById(99L));
    }
}
