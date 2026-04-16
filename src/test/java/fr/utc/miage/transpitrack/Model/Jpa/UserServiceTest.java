package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.model.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByFirstNameShouldReturnMatchingUsers() {
        User user = new User();
        when(userRepository.findUserByFirstName("Alice")).thenReturn(List.of(user));

        List<User> result = userService.getUserByFirstName("Alice");

        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
        verify(userRepository).findUserByFirstName("Alice");
    }

    @Test
    void getUserByFirstNameShouldReturnEmptyListWhenNoMatch() {
        when(userRepository.findUserByFirstName("Inconnu")).thenReturn(List.of());

        List<User> result = userService.getUserByFirstName("Inconnu");

        assertEquals(0, result.size());
        verify(userRepository).findUserByFirstName("Inconnu");
    }

    @Test
    void getUserByNameShouldReturnMatchingUsers() {
        User user = new User();
        when(userRepository.findUserByName("Dupont")).thenReturn(List.of(user));

        List<User> result = userService.getUserByName("Dupont");

        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
        verify(userRepository).findUserByName("Dupont");
    }

    @Test
    void getUserByNameShouldReturnEmptyListWhenNoMatch() {
        when(userRepository.findUserByName("Inconnu")).thenReturn(List.of());

        List<User> result = userService.getUserByName("Inconnu");

        assertEquals(0, result.size());
        verify(userRepository).findUserByName("Inconnu");
    }

    @Test
    void getUserByEmailShouldReturnUser() {
        User user = new User();
        when(userRepository.findByEmail("alice@example.com")).thenReturn(user);

        User result = userService.getUserByEmail("alice@example.com");

        assertEquals(user, result);
        verify(userRepository).findByEmail("alice@example.com");
    }

    @Test
    void getUserByEmailShouldReturnNullWhenNotFound() {
        User result = userService.getUserByEmail("unknown@example.com");

        assertNull(result);
        verify(userRepository).findByEmail("unknown@example.com");
    }

    @Test
    void createUserShouldReturnSavedUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void getAllUsersShouldReturnAllUsers() {
        User user1 = new User();
        User user2 = new User();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void deleteUserByIdShouldCallRepository() {
        userService.deleteUserById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void updateUserShouldReturnUpdatedUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void getUserByIdShouldReturnUserWhenFound() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByIdShouldReturnNullWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.getUserById(99L);

        assertNull(result);
        verify(userRepository).findById(99L);
    }

    // ──────────────────────────────────────────────────────────────
    // searchUsers
    // ──────────────────────────────────────────────────────────────
    @Test
    void searchUsersShouldReturnMatchingUsers() {
        User user = new User();
        when(userRepository.searchByFullName("Jean")).thenReturn(List.of(user));

        List<User> result = userService.searchUsers("Jean");

        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
        verify(userRepository).searchByFullName("Jean");
    }

    @Test
    void searchUsersShouldReturnEmptyListWhenNoMatch() {
        when(userRepository.searchByFullName("Inconnu")).thenReturn(List.of());

        List<User> result = userService.searchUsers("Inconnu");

        assertEquals(0, result.size());
        verify(userRepository).searchByFullName("Inconnu");
    }

    @Test
    void shouldReturnCreatedAndJoinedChallengeIds() {

        when(userRepository.findCreatedChallengeIdsByUserId(1L))
                .thenReturn(Set.of(1L, 2L));

        when(userRepository.findJoinedChallengeIdsByUserId(1L))
                .thenReturn(Set.of(2L, 3L));

        Set<Long> result = userService.getParticipatingChallengeIds(1L);

        assertEquals(Set.of(1L, 2L, 3L), result);

        verify(userRepository).findCreatedChallengeIdsByUserId(1L);
        verify(userRepository).findJoinedChallengeIdsByUserId(1L);
    }

    @Test
    void shouldReturnOnlyCreatedChallenges() {

        when(userRepository.findCreatedChallengeIdsByUserId(1L))
                .thenReturn(Set.of(1L));

        when(userRepository.findJoinedChallengeIdsByUserId(1L))
                .thenReturn(Set.of());

        Set<Long> result = userService.getParticipatingChallengeIds(1L);

        assertEquals(Set.of(1L), result);
    }

    @Test
    void shouldReturnOnlyJoinedChallenges() {

        when(userRepository.findCreatedChallengeIdsByUserId(1L))
                .thenReturn(Set.of());

        when(userRepository.findJoinedChallengeIdsByUserId(1L))
                .thenReturn(Set.of(5L, 6L));

        Set<Long> result = userService.getParticipatingChallengeIds(1L);

        assertEquals(Set.of(5L, 6L), result);
    }

    @Test
    void shouldReturnTrueWhenUserHasJoinedChallenge() {

        when(userRepository.hasJoinedChallenge(1L, 10L))
                .thenReturn(true);

        boolean result = userService.hasJoinedChallenge(1L, 10L);

        assertTrue(result);

        verify(userRepository).hasJoinedChallenge(1L, 10L);
    }

    @Test
    void shouldReturnFalseWhenUserHasNotJoinedChallenge() {

        when(userRepository.hasJoinedChallenge(1L, 10L))
                .thenReturn(false);

        boolean result = userService.hasJoinedChallenge(1L, 10L);

        assertFalse(result);

        verify(userRepository).hasJoinedChallenge(1L, 10L);
    }
}
