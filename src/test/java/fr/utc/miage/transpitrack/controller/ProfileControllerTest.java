package fr.utc.miage.transpitrack.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.utc.miage.transpitrack.model.Activity;
import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.jpa.ActivityService;
import fr.utc.miage.transpitrack.model.jpa.BadgeService;
import fr.utc.miage.transpitrack.model.jpa.FriendshipService;
import fr.utc.miage.transpitrack.model.jpa.ImageStorageService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private ActivityService activityService;
    @Mock
    private FriendshipService friendshipService;
    @Mock
    private BadgeService badgeService;
    @Mock
    private ImageStorageService imageStorageService;
    @Mock
    private Model model;
    @Mock
    private HttpSession session;
    @Mock
    private RedirectAttributes redirectAttrs;

    @InjectMocks
    private ProfileController profileController;

    // ──────────────────────────────────────────────────────────────
    // GET /users/formUpdate
    // ──────────────────────────────────────────────────────────────

    @Test
    void formUpdateShouldReturnFormLoginWhenNotLoggedIn() {
        String view = profileController.formUpdate(model, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void formUpdateShouldReturnFormUpdateWithUserWhenLoggedIn() {
        User user = new User();
        user.setFirstName("Alice");
        user.setEmail("alice@example.com");

        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = profileController.formUpdate(model, session);

        assertEquals("users/formUpdate", view);
        verify(model).addAttribute("user", user);
    }

    // ──────────────────────────────────────────────────────────────
    // POST /users/updateUser
    // ──────────────────────────────────────────────────────────────

    @ParameterizedTest
    @MethodSource("provideNegativeFieldParams")
    void updateUserShouldReturnFormUpdateWhenFieldIsNegative(
            int age, double height, double weight, String expectedMessage) {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(new User());

        String view = profileController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                age, height, "FEMALE", weight, "Paris", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/formUpdate", view);
        verify(model).addAttribute("message", expectedMessage);
    }

    static Stream<Arguments> provideNegativeFieldParams() {
        return Stream.of(
            Arguments.of(-1, 165.0, 60.0, "Age ne peut pas être négatif"),
            Arguments.of(25, -1.0, 60.0, "Taille ne peut pas être négatif"),
            Arguments.of(25, 165.0, -1.0, "Poids ne peut pas être négatif")
        );
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenNewEmailAlreadyTaken() {
        User actualUser = new User();
        actualUser.setEmail("alice@example.com");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(new User());

        String view = profileController.updateUser(
                "Alice", "Dupont", "newemail@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/formUpdate", view);
        verify(model).addAttribute("message", "email déja existant");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenEmailFormatInvalid() {
        when(session.getAttribute("userId")).thenReturn(1L);

        User actualUser = mock(User.class);
        when(actualUser.getEmail()).thenReturn("old@email.com");
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("email-invalide")).thenReturn(null);

        org.mockito.Mockito.doThrow(new RuntimeException())
                .when(userService).updateUser(any(User.class));

        String view = profileController.updateUser(
                "Alice", "Dupont", "email-invalide", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/formUpdate", view);
        verify(model).addAttribute("message", "Email invalide");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenImageUploadFails() throws IOException {
        User actualUser = new User();
        actualUser.setEmail("alice@example.com");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(imageStorageService.store(any(MultipartFile.class))).thenThrow(new IOException("disk full"));

        String view = profileController.updateUser(
                "Alice", "Dupont", "alice@example.com", "",
                25, 165.0, "FEMALE", 60.0, "Paris", mock(MultipartFile.class), model, session, redirectAttrs);

        assertEquals("users/formUpdate", view);
        verify(model).addAttribute("errorMessage", "Erreur lors de l'upload de la photo de profil.");
    }

    @Test
    void updateUserShouldRedirectToDashboardWhenEmailChangedAndFree() {
        User actualUser = new User();
        actualUser.setEmail("alice@example.com");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(null);

        String view = profileController.updateUser(
                "Bob", "Martin", "newemail@example.com", "",
                30, 180.0, "MALE", 80.0, "Lyon", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).updateUser(actualUser);
        verify(redirectAttrs).addFlashAttribute(eq("successMessage"), any());
    }

    @Test
    void updateUserShouldEncodePasswordWhenEmailChangedAndPasswordNotBlank() {
        User actualUser = new User();
        actualUser.setEmail("alice@example.com");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(null);

        String view = profileController.updateUser(
                "Bob", "Martin", "newemail@example.com", "newpassword",
                30, 180.0, "MALE", 80.0, "Lyon", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).updateUser(actualUser);
    }

    @Test
    void updateUserShouldRedirectToDashboardWhenEmailUnchanged() {
        User actualUser = new User();
        actualUser.setEmail("alice@example.com");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);

        String view = profileController.updateUser(
                "Bob", "Martin", "alice@example.com", "",
                30, 180.0, "MALE", 80.0, "Lyon", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).updateUser(actualUser);
    }

    @Test
    void updateUserShouldEncodePasswordWhenEmailUnchangedAndPasswordNotBlank() {
        User actualUser = new User();
        actualUser.setEmail("alice@example.com");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);

        String view = profileController.updateUser(
                "Bob", "Martin", "alice@example.com", "newpassword",
                30, 180.0, "MALE", 80.0, "Lyon", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).updateUser(actualUser);
    }

    @Test
    void updateUserShouldReplaceOldImageWhenNewImageProvided() throws IOException {
        User actualUser = new User();
        actualUser.setEmail("alice@example.com");
        actualUser.setProfileImage("old.jpg");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(imageStorageService.store(any(MultipartFile.class))).thenReturn("new.jpg");

        String view = profileController.updateUser(
                "Alice", "Dupont", "alice@example.com", "",
                25, 165.0, "FEMALE", 60.0, "Paris", mock(MultipartFile.class), model, session, redirectAttrs);

        assertEquals("redirect:/users/dashboard", view);
        verify(imageStorageService).delete("old.jpg");
        verify(userService).updateUser(actualUser);
    }

    // ──────────────────────────────────────────────────────────────
    // POST /users/deleteProfileImage
    // ──────────────────────────────────────────────────────────────

    @Test
    void deleteProfileImageShouldRedirectToLoginWhenNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = profileController.deleteProfileImage(session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void deleteProfileImageShouldDeleteImageAndRedirectToFormUpdate() {
        User user = new User();
        user.setProfileImage("myimage.jpg");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = profileController.deleteProfileImage(session);

        assertEquals("redirect:/users/formUpdate", view);
        verify(imageStorageService).delete("myimage.jpg");
        verify(userService).updateUser(user);
        assertNull(user.getProfileImage());
    }

    // ──────────────────────────────────────────────────────────────
    // GET /users/profile
    // ──────────────────────────────────────────────────────────────

    @Test
    void profilePageShouldRedirectToLoginWhenNotLoggedIn() {
        String view = profileController.profilePage(session, model);

        assertEquals("redirect:/users/formLogin", view);
        verify(model).addAttribute("message", "Il faut être connecte !");
    }

    @Test
    void profilePageShouldRedirectToLoginWhenUserNotFound() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(null);

        String view = profileController.profilePage(session, model);

        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void profilePageShouldReturnProfileWithUserAndActivitiesSortedByDateDesc() {
        User user = new User();
        user.setFirstName("Alice");

        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        Activity older = new Activity();
        older.setDate(java.time.LocalDate.of(2024, 1, 1));
        Activity newer = new Activity();
        newer.setDate(java.time.LocalDate.of(2024, 6, 1));
        when(activityService.getActivitiesByUserId(1L)).thenReturn(new java.util.ArrayList<>(List.of(older, newer)));

        String view = profileController.profilePage(session, model);

        assertEquals("users/profile", view);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("activities", List.of(newer, older));
    }

    @Test
    void profilePageShouldResolveFriendAsReceiverWhenCurrentUserIsRequester() throws Exception {
        User currentUser = new User();
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(currentUser, 1L);

        User friendUser = new User();
        idField.set(friendUser, 2L);

        Friendship friendship = new Friendship(currentUser, friendUser);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(currentUser);
        when(activityService.getActivitiesByUserId(1L)).thenReturn(new java.util.ArrayList<>());
        when(friendshipService.getMyFriendships(1L)).thenReturn(List.of(friendship));
        when(friendshipService.getMyPendingFriendships(1L)).thenReturn(List.of());

        String view = profileController.profilePage(session, model);

        assertEquals("users/profile", view);
        verify(model).addAttribute("friends", List.of(friendUser));
    }

    @Test
    void profilePageShouldResolveFriendAsRequesterWhenCurrentUserIsReceiver() throws Exception {
        User currentUser = new User();
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(currentUser, 1L);

        User friendUser = new User();
        idField.set(friendUser, 2L);

        Friendship friendship = new Friendship(friendUser, currentUser);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(currentUser);
        when(activityService.getActivitiesByUserId(1L)).thenReturn(new java.util.ArrayList<>());
        when(friendshipService.getMyFriendships(1L)).thenReturn(List.of(friendship));
        when(friendshipService.getMyPendingFriendships(1L)).thenReturn(List.of());

        String view = profileController.profilePage(session, model);

        assertEquals("users/profile", view);
        verify(model).addAttribute("friends", List.of(friendUser));
    }

    // ──────────────────────────────────────────────────────────────
    // GET /users/profile/{id}
    // ──────────────────────────────────────────────────────────────

    @Test
    void viewProfileShouldRedirectToFormLoginWhenNotLoggedIn() {
        String view = profileController.viewProfile(2L, model, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void viewProfileShouldRedirectToSearchWhenProfileUserNotFound() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(2L)).thenReturn(null);

        String view = profileController.viewProfile(2L, model, session);

        assertEquals("redirect:/users/search", view);
    }

    @Test
    void viewProfileShouldReturnProfileViewWithIsOwnerTrueWhenViewingOwnProfile() {
        User user = new User();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(activityService.getActivitiesByUserId(1L)).thenReturn(List.of());
        when(friendshipService.requestOrFriendshipExists(1L, 1L)).thenReturn(false);

        String view = profileController.viewProfile(1L, model, session);

        assertEquals("users/profile", view);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("isOwner", true);
        verify(model).addAttribute(eq("requestSent"), anyBoolean());
    }

    @Test
    void viewProfileShouldReturnProfileViewWithIsOwnerFalseWhenViewingOtherUserProfile() {
        User profileUser = new User();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(2L)).thenReturn(profileUser);
        when(activityService.getActivitiesByUserId(2L)).thenReturn(List.of());
        when(friendshipService.requestOrFriendshipExists(1L, 2L)).thenReturn(true);

        String view = profileController.viewProfile(2L, model, session);

        assertEquals("users/profile", view);
        verify(model).addAttribute("user", profileUser);
        verify(model).addAttribute("isOwner", false);
        verify(model).addAttribute(eq("requestSent"), anyBoolean());
    }

    // ──────────────────────────────────────────────────────────────
    // GET /users/search
    // ──────────────────────────────────────────────────────────────

    @Test
    void searchUserShouldRedirectToLoginWhenNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = profileController.searchUser("Jean", model, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void searchUserShouldReturnResultsWhenQueryMatches() {
        when(session.getAttribute("userId")).thenReturn(1L);
        User user = new User();
        user.setFirstName("Jean");
        when(userService.searchUsers("Jean")).thenReturn(List.of(user));

        String view = profileController.searchUser("Jean", model, session);

        assertEquals("search/searchUser", view);
        verify(userService).searchUsers("Jean");
        verify(model).addAttribute("users", List.of(user));
        verify(model).addAttribute("query", "Jean");
    }

    @Test
    void searchUserShouldReturnEmptyListWhenQueryIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = profileController.searchUser(null, model, session);

        assertEquals("search/searchUser", view);
        verify(model).addAttribute("users", List.of());
        verify(model).addAttribute("query", null);
    }

    @Test
    void searchUserShouldReturnEmptyListWhenQueryIsBlank() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = profileController.searchUser("   ", model, session);

        assertEquals("search/searchUser", view);
        verify(model).addAttribute("users", List.of());
        verify(model).addAttribute("query", "   ");
    }

    @Test
    void searchUserShouldHandleFriendships() {
        when(session.getAttribute("userId")).thenReturn(1L);

        User requester = mock(User.class);
        User receiver = mock(User.class);

        when(requester.getId()).thenReturn(1L);
        when(receiver.getId()).thenReturn(2L);

        Friendship friendship = mock(Friendship.class);
        when(friendship.getRequester()).thenReturn(requester);
        when(friendship.getReceiver()).thenReturn(receiver);

        Friendship friendship2 = mock(Friendship.class);
        when(friendship2.getRequester()).thenReturn(requester);
        when(friendship2.getReceiver()).thenReturn(receiver);

        when(friendshipService.getMyFriendships(1L)).thenReturn(List.of(friendship));
        when(friendshipService.getMySentPendingFriendships(1L)).thenReturn(List.of(friendship2));
        when(friendshipService.getMyPendingFriendships(1L)).thenReturn(List.of(friendship2));

        String view = profileController.searchUser("Jean", model, session);

        assertEquals("search/searchUser", view);
        verify(model).addAttribute(eq("relatedUserIds"), anySet());
    }

    @Test
    void searchUserShouldAddRequesterIdWhenCurrentUserIsReceiver() {
        when(session.getAttribute("userId")).thenReturn(1L);

        // Current user (id=1) is the RECEIVER; the other user (id=3) is the requester.
        // The ternary takes the false branch → f.getRequester().getId() (=3) is added to relatedUserIds.
        User requester = mock(User.class);
        when(requester.getId()).thenReturn(3L);

        Friendship friendship = mock(Friendship.class);
        when(friendship.getRequester()).thenReturn(requester);

        when(friendshipService.getMyFriendships(1L)).thenReturn(List.of(friendship));
        when(friendshipService.getMySentPendingFriendships(1L)).thenReturn(List.of());
        when(friendshipService.getMyPendingFriendships(1L)).thenReturn(List.of());

        String view = profileController.searchUser(null, model, session);

        assertEquals("search/searchUser", view);
        verify(model).addAttribute(eq("relatedUserIds"), anySet());
    }
}
