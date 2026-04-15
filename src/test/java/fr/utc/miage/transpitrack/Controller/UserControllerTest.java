package fr.utc.miage.transpitrack.Controller;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Enum.Gender;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.Enum.Level;
import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.Goal;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.Jpa.BadgeService;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;
import fr.utc.miage.transpitrack.Model.Jpa.GoalService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Jpa.UserSportService;
import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.UserSport;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ActivityService activityService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserController userController;

    @Mock
    private FriendshipService friendshipService;

    @Mock
    private SportService sportService;

    @Mock
    private UserSportService userSportService;

    @Mock
    private BadgeService badgeService;

    @Mock
    private GoalService goalService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void formCreateShouldReturnDashboardWhenUserAlreadyLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.formCreate(model, session);

        assertEquals("users/dashboard", view);
    }

    @Test
    void formCreateShouldReturnFormCreateWhenNotLoggedIn() {
        String view = userController.formCreate(model, session);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "Bienvenue");
    }

    @Test
    void createUserShouldReturnFormCreateWhenEmailFormatInvalid() {
        String view = userController.createUser(
                "Alice", "Dupont", "email-invalide", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "Email invalide");
    }

    @Test
    void createUserShouldReturnFormCreateWhenAgeIsNegative() {
        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                -1, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "Age ne peut pas être négatif");
    }

    @Test
    void createUserShouldReturnFormCreateWhenHeightIsNegative() {
        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, -1.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "Taille ne peut pas être négatif");
    }

    @Test
    void createUserShouldReturnFormCreateWhenWeightIsNegative() {
        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", -1.0, "Paris", model, session);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "Poids ne peut pas être négatif");
    }

    @Test
    void createUserShouldReturnFormCreateWhenEmailAlreadyExists() {
        when(userService.getUserByEmail("alice@example.com")).thenReturn(new User());

        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "email dejas existant");
    }

    @Test
    void createUserShouldReturnDashboardWhenUserCreatedSuccessfully() throws Exception {
        User savedUser = new User();
        savedUser.setFirstName("Alice");
        savedUser.setName("Dupont");
        savedUser.setEmail("alice@example.com");
        savedUser.setPassword("secret");
        savedUser.setAge(25);
        savedUser.setHeight(165.0);
        savedUser.setGender(Gender.FEMALE);
        savedUser.setWeight(60.0);
        savedUser.setCity("Paris");

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(savedUser, 1L);
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).createUser(any(User.class));
        verify(session).setAttribute("userId", savedUser.getId());
        verify(model).addAttribute("message", "Création compte réussie");
    }

    @Test
    void searchUserShouldRedirectToLoginWhenNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = userController.searchUser("Jean", model, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void searchUserShouldReturnResultsWhenQueryMatches() {
        when(session.getAttribute("userId")).thenReturn(1L);
        User user = new User();
        user.setFirstName("Jean");
        when(userService.searchUsers("Jean")).thenReturn(List.of(user));

        String view = userController.searchUser("Jean", model, session);

        assertEquals("search/searchUser", view);
        verify(userService).searchUsers("Jean");
        verify(model).addAttribute("users", List.of(user));
        verify(model).addAttribute("query", "Jean");
    }

    @Test
    void searchUserShouldReturnEmptyListWhenQueryIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.searchUser(null, model, session);

        assertEquals("search/searchUser", view);
        verify(model).addAttribute("users", List.of());
        verify(model).addAttribute("query", null);
    }

    @Test
    void searchUserShouldReturnEmptyListWhenQueryIsBlank() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.searchUser("   ", model, session);

        assertEquals("search/searchUser", view);
        verify(model).addAttribute("users", List.of());
        verify(model).addAttribute("query", "   ");
    }

    @Test
    void formUpdateShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.formUpdate(model, session);

        assertEquals("users/formLogin", view);
    }

    @Test
    void formUpdateShouldReturnFormUpdateWithUserWhenLoggedIn() {
        User user = new User();
        user.setFirstName("Alice");
        user.setName("Dupont");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
        user.setAge(25);
        user.setHeight(165.0);
        user.setGender(Gender.FEMALE);
        user.setWeight(60.0);
        user.setCity("Paris");

        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = userController.formUpdate(model, session);

        assertEquals("users/formUpdate", view);
        verify(model).addAttribute("message", "Modification du compte réussie");
        verify(model).addAttribute("user", user);
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenEmailFormatInvalid() {
        when(session.getAttribute("userId")).thenReturn(1L);

        User actualUser = mock(User.class);
        when(actualUser.getEmail()).thenReturn("old@email.com");

        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("email-invalide")).thenReturn(null);

        doThrow(new RuntimeException())
                .when(userService).updateUser(any(User.class));

        String view = userController.updateUser(
                "Alice", "Dupont", "email-invalide", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session
        );

        assertEquals("users/formUpdate", view);
        verify(model).addAttribute("message", "Email invalide");

    }

    @Test
    void updateUserShouldReturnFormUpdateWhenAgeIsNegative() {
        String view = userController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                -1, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("users/formUpdate", view);
        verify(model).addAttribute("message", "Age ne peut pas être négatif");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenHeightIsNegative() {
        String view = userController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, -1.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("users/formUpdate", view);
        verify(model).addAttribute("message", "Taille ne peut pas être négatif");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenWeightIsNegative() {
        String view = userController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", -1.0, "Paris", model, session);

        assertEquals("users/formUpdate", view);
        verify(model).addAttribute("message", "Poids ne peut pas être négatif");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenNewEmailAlreadyTaken() {
        User actualUser = new User();
        actualUser.setFirstName("Alice");
        actualUser.setName("Dupont");
        actualUser.setEmail("alice@example.com");
        actualUser.setPassword("secret");
        actualUser.setAge(25);
        actualUser.setHeight(165.0);
        actualUser.setGender(Gender.FEMALE);
        actualUser.setWeight(60.0);
        actualUser.setCity("Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(new User());

        String view = userController.updateUser(
                "Alice", "Dupont", "newemail@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("users/formUpdate", view);
        verify(model).addAttribute("message", "email déja existant");
    }

    @Test
    void updateUserShouldUpdateAndReturnDashboardWhenEmailChangedAndFree() {
        User actualUser = new User();
        actualUser.setFirstName("Alice");
        actualUser.setName("Dupont");
        actualUser.setEmail("alice@example.com");
        actualUser.setPassword("secret");
        actualUser.setAge(25);
        actualUser.setHeight(165.0);
        actualUser.setGender(Gender.FEMALE);
        actualUser.setWeight(60.0);
        actualUser.setCity("Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(null);

        String view = userController.updateUser(
                "Bob", "Martin", "newemail@example.com", "",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).updateUser(actualUser);
        verify(model).addAttribute("message", "Modification du compte réussie");
    }

    @Test
    void updateUserShouldEncodePasswordWhenEmailChangedAndPasswordNotBlank() {
        User actualUser = new User();
        actualUser.setFirstName("Alice");
        actualUser.setName("Dupont");
        actualUser.setEmail("alice@example.com");
        actualUser.setPassword("secret");
        actualUser.setAge(25);
        actualUser.setHeight(165.0);
        actualUser.setGender(Gender.FEMALE);
        actualUser.setWeight(60.0);
        actualUser.setCity("Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(null);

        String view = userController.updateUser(
                "Bob", "Martin", "newemail@example.com", "newpassword",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).updateUser(actualUser);
    }

    @Test
    void updateUserShouldUpdateAndReturnDashboardWhenEmailUnchanged() {
        User actualUser = new User();
        actualUser.setFirstName("Alice");
        actualUser.setName("Dupont");
        actualUser.setEmail("alice@example.com");
        actualUser.setPassword("secret");
        actualUser.setAge(25);
        actualUser.setHeight(165.0);
        actualUser.setGender(Gender.FEMALE);
        actualUser.setWeight(60.0);
        actualUser.setCity("Paris");

        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);

        String view = userController.updateUser(
                "Bob", "Martin", "alice@example.com", "",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).updateUser(actualUser);
        verify(model).addAttribute("message", "Modification du compte réussie");
    }
    @Test
    void updateUserShouldEncodePasswordWhenEmailUnchangedAndPasswordNotBlank() {
        User actualUser = new User();
        actualUser.setFirstName("Alice");
        actualUser.setName("Dupont");
        actualUser.setEmail("alice@example.com");
        actualUser.setPassword("secret");
        actualUser.setAge(25);
        actualUser.setHeight(165.0);
        actualUser.setGender(Gender.FEMALE);
        actualUser.setWeight(60.0);
        actualUser.setCity("Paris");

        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);

        String view = userController.updateUser(
                "Bob", "Martin", "alice@example.com", "newpassword",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).updateUser(actualUser);
    }

    @Test
    void formLoginShouldReturnDashboardWhenAlreadyLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.formLogin(model, session);

        assertEquals("users/dashboard", view);
    }

    @Test
    void formLoginShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.formLogin(model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void loginUserShouldReturnFormLoginWhenEmailNotFound() {
        String view = userController.loginUser("unknown@example.com", "secret", model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "email ou mots de passe incorrect");
    }

    @Test
    void loginUserShouldReturnFormLoginWhenPasswordInvalid() {
        User userLogin = new User();
        userLogin.setFirstName("Alice");
        userLogin.setName("Dupont");
        userLogin.setEmail("alice@example.com");
        userLogin.setPassword(encoder.encode("correct"));
        userLogin.setAge(25);
        userLogin.setHeight(165.0);
        userLogin.setGender(Gender.FEMALE);
        userLogin.setWeight(60.0);
        userLogin.setCity("Paris");

        when(userService.getUserByEmail("alice@example.com")).thenReturn(userLogin);

        String view = userController.loginUser("alice@example.com", "wrong", model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "email ou mots de passe incorrect");
    }

    @Test
    void loginUserShouldReturnDashboardWhenCredentialsValid() throws Exception {
        User userLogin = new User();
        userLogin.setFirstName("Alice");
        userLogin.setName("Dupont");
        userLogin.setEmail("alice@example.com");
        userLogin.setPassword(encoder.encode("secret"));
        userLogin.setAge(25);
        userLogin.setHeight(165.0);
        userLogin.setGender(Gender.FEMALE);
        userLogin.setWeight(60.0);
        userLogin.setCity("Paris");

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(userLogin, 1L);
        when(userService.getUserByEmail("alice@example.com")).thenReturn(userLogin);

        String view = userController.loginUser("alice@example.com", "secret", model, session);

        assertEquals("redirect:/users/dashboard", view);
        verify(session).setAttribute("userId", 1L);
        verify(model).addAttribute("message", "Connexion compte réussie");
    }

    @Test
    void logoutShouldInvalidateSessionAndReturnFormLogin() {
        String view = userController.logoutPage(session);

        assertEquals("users/formLogin", view);
        verify(session).invalidate();
    }

    @Test
    void profilePageShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.profilePage(session, model);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void profilePageShouldReturnFormLoginWhenUserNotFound() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(null);

        String view = userController.profilePage(session, model);

        assertEquals("users/formLogin", view);
    }

    @Test
    void profilePageShouldReturnProfileWithUserAndActivitiesSortedByDateDesc() {
        User user = new User();
        user.setFirstName("Alice");
        user.setName("Dupont");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
        user.setAge(25);
        user.setHeight(165.0);
        user.setGender(Gender.FEMALE);
        user.setWeight(60.0);
        user.setCity("Paris");


        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        Activity older = new Activity();
        older.setDate(java.time.LocalDate.of(2024, 1, 1));
        Activity newer = new Activity();
        newer.setDate(java.time.LocalDate.of(2024, 6, 1));
        when(activityService.getActivitiesByUserId(1L)).thenReturn(new java.util.ArrayList<>(List.of(older, newer)));

        String view = userController.profilePage(session, model);

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

        String view = userController.profilePage(session, model);

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

        String view = userController.profilePage(session, model);

        assertEquals("users/profile", view);
        verify(model).addAttribute("friends", List.of(friendUser));
    }

    @Test
    void viewProfileShouldRedirectToFormLoginWhenNotLoggedIn() {
        String view = userController.viewProfile(2L, null, model, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void viewProfileShouldRedirectToSearchWhenProfileUserNotFound() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(2L)).thenReturn(null);

        String view = userController.viewProfile(2L, null, model, session);

        assertEquals("redirect:/users/search", view);
    }

    @Test
    void viewProfileShouldReturnProfileViewWithIsOwnerTrueWhenViewingOwnProfile() {

        User user = new User();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(activityService.getActivitiesByUserId(1L)).thenReturn(List.of());
        when(friendshipService.requestOrFriendshipExists(1L, 1L)).thenReturn(false);

        String view = userController.viewProfile(1L, null, model, session);

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

        String view = userController.viewProfile(2L, null, model, session);

        assertEquals("users/profile", view);

        verify(model).addAttribute("user", profileUser);
        verify(model).addAttribute("isOwner", false);
        verify(model).addAttribute(eq("requestSent"), anyBoolean());
    }

    @Test
    void consultationPreferencesShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.consultationPreferences(model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void consultationPreferencesShouldReturnPreferencesViewWhenLoggedIn() {
        User user = new User();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(sportService.getAllSports()).thenReturn(List.of(new Sport()));

        String view = userController.consultationPreferences(model, session);

        assertEquals("users/listPreferences", view);
        verify(model).addAttribute("sportsPreference", user.getSportsPreference());
        verify(model).addAttribute(eq("sports"), any());
    }

    @Test
    void addPreferenceShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.addPreference(1L, Level.BEGINNER, model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void addPreferenceShouldRedirectWhenSportIdIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.addPreference(null, Level.BEGINNER, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
    }

    @Test
    void addPreferenceShouldRedirectWhenLevelIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.addPreference(1L, null, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
    }

    @Test
    void addPreferenceShouldRedirectWhenSportAlreadyInList() {
        User user = new User();
        Sport sport = new Sport();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(sportService.getSportById(1L)).thenReturn(sport);
        when(userSportService.getUserSportByUserAndSport(user, sport))
                .thenReturn(new UserSport(user, sport, Level.BEGINNER));

        String view = userController.addPreference(1L, Level.BEGINNER, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
        verify(model).addAttribute("message", "Ce sport est dejas dans votre liste !");
    }

    @Test
    void addPreferenceShouldSaveAndRedirectWhenValid() {
        User user = new User();
        Sport sport = new Sport();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(sportService.getSportById(1L)).thenReturn(sport);
        when(userSportService.getUserSportByUserAndSport(user, sport)).thenReturn(null);

        String view = userController.addPreference(1L, Level.INTERMEDIATE, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
        verify(userSportService).createUserSport(any(UserSport.class));
        verify(userService).updateUser(user);
    }

    @Test
    void updateLevelShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.updateLevel(1L, Level.ADVANCED, model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void updateLevelShouldRedirectWhenUserSportIdIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.updateLevel(null, Level.ADVANCED, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
    }

    @Test
    void updateLevelShouldRedirectWhenLevelIsNull() {

        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.updateLevel(1L, null, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
    }

    @Test
    void updateLevelShouldUpdateLevelAndRedirectWhenValid() {
        User user = new User();
        UserSport us = new UserSport(user, new Sport(), Level.BEGINNER);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(userSportService.getUserSportById(1L)).thenReturn(us);

        String view = userController.updateLevel(1L, Level.ADVANCED, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
        verify(userSportService).updateUserSport(us);
        verify(userService).updateUser(user);
        assertEquals(Level.ADVANCED, us.getLevel());
    }

    @Test
    void deletePreferenceShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.deletePreference(1L, model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void deletePreferenceShouldRedirectWhenUserSportIdIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.deletePreference(null, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
    }

    @Test
    void deletePreferenceShouldDeleteAndRedirectWhenValid() {
        User user = new User();
        UserSport us = new UserSport(user, new Sport(), Level.BEGINNER);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(userSportService.getUserSportById(1L)).thenReturn(us);

        String view = userController.deletePreference(1L, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
        verify(userSportService).deleteUserSport(us);
        verify(userService).updateUser(user);
    }

    @Test
    void consultationGoalsShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.consultationGoals(model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void consultationGoalsShouldReturnGoalsViewWhenLoggedIn() {
        User user = new User();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = userController.consultationGoals(model, session);

        assertEquals("goals/listGoals", view);
        verify(model).addAttribute("goals", user.getGoals());
    }

    @Test
    void addGoalShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.addGoal("Courir", 10.0, model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void addGoalShouldRedirectWhenTextIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.addGoal(null, 10.0, model, session);

        assertEquals("redirect:/users/consultationGoals", view);
    }

    @Test
    void addGoalShouldRedirectWhenDistanceIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.addGoal("Courir", null, model, session);

        assertEquals("redirect:/users/consultationGoals", view);
    }

    @Test
    void addGoalShouldSaveAndRedirectWhenValid() {
        User user = new User();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = userController.addGoal("Courir 10 km", 10.0, model, session);

        assertEquals("redirect:/users/consultationGoals", view);
        verify(goalService).createGoal(any(Goal.class));
        verify(userService).updateUser(user);
    }

    @Test
    void updateGoalShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.updateGoal(1L, "Courir", 10.0, model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void updateGoalShouldRedirectWhenTextIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.updateGoal(1L, null, 10.0, model, session);

        assertEquals("redirect:/users/consultationGoals", view);
    }

    @Test
    void updateGoalShouldRedirectWhenDistanceIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.updateGoal(1L, "Courir", null, model, session);

        assertEquals("redirect:/users/consultationGoals", view);
    }

    @Test
    void updateGoalShouldUpdateAndRedirectWhenValid() {
        User user = new User();
        Goal goal = new Goal(5.0, "Ancienne", user);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(goalService.getGoalById(1L)).thenReturn(goal);

        String view = userController.updateGoal(1L, "Courir 10 km", 10.0, model, session);

        assertEquals("redirect:/users/consultationGoals", view);
        verify(goalService).updateGoal(goal);
        verify(userService).updateUser(user);
        assertEquals("Courir 10 km", goal.getGoalText());
        assertEquals(10.0, goal.getTargetDistance(), 0.001);
    }

    @Test
    void deleteGoalShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.deleteGoal(1L, model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Il faut êtres connecter !");
    }

    @Test
    void deleteGoalShouldRedirectToPreferencesWhenGoalIdIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.deleteGoal(null, model, session);

        assertEquals("redirect:/users/consultationPreferences", view);
    }

    @Test
    void deleteGoalShouldDeleteAndRedirectWhenValid() {
        User user = new User();
        Goal goal = new Goal(10.0, "Courir", user);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(goalService.getGoalById(1L)).thenReturn(goal);

        String view = userController.deleteGoal(1L, model, session);

        assertEquals("redirect:/users/consultationGoals", view);
        verify(goalService).deleteGoal(goal);
        verify(userService).updateUser(user);
    }
}
