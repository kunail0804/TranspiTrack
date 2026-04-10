package fr.utc.miage.transpitrack.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class FriendshipControllerTest {

    @Mock
    private FriendshipService friendshipService;

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private FriendshipController controller;

    @Test
    void shouldRedirectToLoginWhenUserIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(null);

        String result = controller.addFriend(2L, session, model);

        assertEquals("redirect:/users/login?error=Vous devez être connecté pour ajouter un ami", result);
    }

    @Test
    void shouldRedirectWhenFriendIdIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);

        String result = controller.addFriend(null, session, model);

        assertEquals("redirect:/users/profile/null?error=ID de l'ami est requis", result);
    }

    @Test
    void shouldRejectWhenUserTriesToAddHimself() {
        when(session.getAttribute("userId")).thenReturn(1L);

        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);

        String result = controller.addFriend(1L, session, model);

        assertEquals("redirect:/users/profile/1?error=Vous ne pouvez pas vous ajouter en tant qu'ami", result);
    }

    @Test
    void shouldFailWhenFriendshipCreationReturnsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        User user = new User();
        User friend = new User();

        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.getUserById(2L)).thenReturn(friend);

        when(friendshipService.sendFriendRequest(user, friend)).thenReturn(null);

        String result = controller.addFriend(2L, session, model);

        assertEquals("redirect:/users/profile/2?error=Erreur lors de la création de la demande d'amitié", result);
    }

    @Test
    void shouldSucceedWhenFriendRequestIsCreated() {
        when(session.getAttribute("userId")).thenReturn(1L);

        User user = new User();
        User friend = new User();

        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.getUserById(2L)).thenReturn(friend);

        when(friendshipService.sendFriendRequest(user, friend))
                .thenReturn(new Friendship(user, friend));

        String result = controller.addFriend(2L, session, model);

        assertEquals("redirect:/users/profile/2?success=Demande d'amitié envoyée avec succès", result);
    }
}