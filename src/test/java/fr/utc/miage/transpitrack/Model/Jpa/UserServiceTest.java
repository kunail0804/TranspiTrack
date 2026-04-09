package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.Model.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByFirstName() {
        User user = new User();
        when(userRepository.findUserByFirstName("Alice")).thenReturn(List.of(user));

        List<User> result = userService.getUserByFirstName("Alice");

        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
        verify(userRepository).findUserByFirstName("Alice");
    }

    @Test
    void getUserByName() {
        User user = new User();
        when(userRepository.findUserByName("Dupont")).thenReturn(List.of(user));

        List<User> result = userService.getUserByName("Dupont");

        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
        verify(userRepository).findUserByName("Dupont");
    }

    @Test
    void getUserByEmail() {
        User user = new User();
        when(userRepository.findByEmail("alice@example.com")).thenReturn(user);

        User result = userService.getUserByEmail("alice@example.com");

        assertEquals(user, result);
        verify(userRepository).findByEmail("alice@example.com");
    }

    @Test
    void createUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void getAllUsers() {
        User user1 = new User();
        User user2 = new User();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void updateUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void getUserById() {
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
}
