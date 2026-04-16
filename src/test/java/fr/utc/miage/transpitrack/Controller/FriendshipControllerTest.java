package fr.utc.miage.transpitrack.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.controller.FriendshipController;
import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.jpa.FriendshipService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
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

        assertEquals("redirect:/users/login?msg=Vous devez etre connecte pour ajouter un ami", result);
    }

    @Test
    void shouldRedirectWhenFriendIdIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);

        String result = controller.addFriend(null, session, model);

        assertEquals("redirect:/users/profile/null?msg=ID de l'ami est requis", result);
    }

    @Test
    void shouldRejectWhenUserTriesToAddHimself() {
        when(session.getAttribute("userId")).thenReturn(1L);

        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);

        String result = controller.addFriend(1L, session, model);

        assertEquals("redirect:/users/profile/1?msg=Vous ne pouvez pas vous ajouter en tant qu'ami", result);
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

        assertEquals("redirect:/users/profile/2?msg=Erreur lors de la creation de la demande d'amitie", result);
    }

    @Test
    void addFriendShouldRedirectToLoginWhenUserIdIsNull() {

        when(session.getAttribute("userId")).thenReturn(null);

        String result = controller.addFriend(2L, session, model);

        assertEquals(
                "redirect:/users/login?msg=Vous devez etre connecte pour ajouter un ami",
                result
        );

        verifyNoInteractions(userService);
        verifyNoInteractions(friendshipService);
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

        assertEquals("redirect:/users/profile/2?msg=Demande d'amitie envoyee avec succes", result);
    }

    @Test
    void showInvitesShouldRedirectWhenNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String result = controller.showInvites(null, session, model);

        assertEquals("redirect:/users/login?msg=Vous devez etre connecte pour voir vos invitations", result);
    }

    @Test
    void showInvitesShouldReturnViewWithModel() {

        when(session.getAttribute("userId")).thenReturn(1L);

        when(friendshipService.getMyPendingFriendships(1L))
                .thenReturn(java.util.List.of());

        String result = controller.showInvites("msg", session, model);

        assertEquals("users/friendInvites", result);

        verify(model).addAttribute(eq("friendInvites"), any());
        verify(model).addAttribute("msg", "msg");
    }

    @Test
    void acceptInviteShouldRedirectWhenUserNotLoggedIn() {

        when(session.getAttribute("userId")).thenReturn(null);

        String result = controller.acceptInvite(1L, session);

        assertEquals("redirect:/users/login?msg=Vous devez etre connecte pour accepter une invitation", result);
    }

    @Test
    void acceptInviteShouldFailWhenFriendshipNotFound() {

        when(session.getAttribute("userId")).thenReturn(1L);

        when(friendshipService.getFriendshipById(1L)).thenReturn(null);

        String result = controller.acceptInvite(1L, session);

        assertEquals("redirect:/users/friends/invites?msg=Invitation non trouvee", result);
    }

    @Test
    void acceptInviteShouldFailWhenUserIsNotReceiver() {

        when(session.getAttribute("userId")).thenReturn(1L);

        User requester = new User();
        requester.setName("Bob");

        User receiver = mock(User.class);
        when(receiver.getId()).thenReturn(2L);

        Friendship friendship = mock(Friendship.class);
        when(friendship.getReceiver()).thenReturn(receiver);

        when(friendshipService.getFriendshipById(1L)).thenReturn(friendship);

        String result = controller.acceptInvite(1L, session);

        assertEquals("redirect:/users/friends/invites?msg=Invitation non trouvee", result);
    }

    @Test
    void acceptInviteShouldSucceed() {

        when(session.getAttribute("userId")).thenReturn(1L);

        User requester = mock(User.class);
        when(requester.getName()).thenReturn("Bob");

        User receiver = mock(User.class);
        when(receiver.getId()).thenReturn(1L);

        Friendship friendship = mock(Friendship.class);
        when(friendship.getReceiver()).thenReturn(receiver);
        when(friendship.getRequester()).thenReturn(requester);

        when(friendshipService.getFriendshipById(1L)).thenReturn(friendship);

        String result = controller.acceptInvite(1L, session);

        assertEquals("redirect:/users/friends/invites?msg=Vous etes maintenant amis avec Bob", result);

        verify(friendshipService).acceptFriendRequest(friendship);
    }

    @Test
    void rejectInviteShouldRedirectWhenUserNotLoggedIn() {

        when(session.getAttribute("userId")).thenReturn(null);

        String result = controller.refuseInvite(1L, session);

        assertEquals("redirect:/users/login?msg=Vous devez etre connecte pour refuser une invitation", result);
    }

    @Test
    void rejectInviteShouldFailWhenFriendshipNotFound() {

        when(session.getAttribute("userId")).thenReturn(1L);

        when(friendshipService.getFriendshipById(1L)).thenReturn(null);

        String result = controller.refuseInvite(1L, session);

        assertEquals("redirect:/users/friends/invites?msg=Invitation non trouvee", result);
    }

    @Test
    void rejectInviteShouldSucceed() {

        when(session.getAttribute("userId")).thenReturn(1L);

        Friendship friendship = mock(Friendship.class);

        when(friendshipService.getFriendshipById(1L)).thenReturn(friendship);

        String result = controller.refuseInvite(1L, session);

        assertEquals("redirect:/users/friends/invites?msg=Invitation refusee", result);

        verify(friendshipService).rejectFriendRequest(friendship);
    }
}
