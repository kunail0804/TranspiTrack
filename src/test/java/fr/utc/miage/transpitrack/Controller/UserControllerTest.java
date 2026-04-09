package fr.utc.miage.transpitrack.Controller;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Model.Enum.Gender;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.User;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserController userController;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ──────────────────────────────────────────────────────────────
    // GET /users/formCreate
    // ──────────────────────────────────────────────────────────────

    @Test
    void formCreateShouldReturnDashboardWhenUserAlreadyLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.formCreate(null, model, session);

        assertEquals("users/dashboard", view);
    }

    @Test
    void formCreateShouldReturnFormCreateWhenNotLoggedIn() {
        String view = userController.formCreate("Bienvenue", model, session);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "Bienvenue");
    }

    // ──────────────────────────────────────────────────────────────
    // POST /users/createUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void createUserShouldReturnFormCreateWhenEmailFormatInvalid() {
        String view = userController.createUser(
                "Alice", "Dupont", "email-invalide", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "Email n'est pas au bon format");
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
        User savedUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, Gender.FEMALE, 60.0, "Paris");
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(savedUser, 1L);
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("users/dashboard", view);
        verify(userService).createUser(any(User.class));
        verify(session).setAttribute("userId", savedUser.getId());
        verify(model).addAttribute("message", "Création compte réussie");
    }

    // ──────────────────────────────────────────────────────────────
    // GET /users/formLogin
    // ──────────────────────────────────────────────────────────────

    @Test
    void formLoginShouldReturnDashboardWhenUserAlreadyLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.formLogin(null, model, session);

        assertEquals("users/dashboard", view);
    }

    @Test
    void formLoginShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.formLogin("Bienvenue", model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "Bienvenue");
    }

    // ──────────────────────────────────────────────────────────────
    // POST /users/loginUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void loginUserShouldReturnFormLoginWhenEmailDoesNotExist() {
        String view = userController.loginUser("unknown@example.com", "secret", model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "email ou mots de passe incorrect");
    }

    @Test
    void loginUserShouldReturnFormLoginWhenPasswordIsInvalid() {
        User user = new User();
        user.setPassword(encoder.encode("secret"));
        when(userService.getUserByEmail("alice@example.com")).thenReturn(user);

        String view = userController.loginUser("alice@example.com", "wrong", model, session);

        assertEquals("users/formLogin", view);
        verify(model).addAttribute("message", "email ou mots de passe incorrect");
    }

    @Test
    void loginUserShouldReturnDashboardWhenCredentialsAreValid() throws Exception {
        User user = new User();
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);
        user.setPassword(encoder.encode("secret"));
        when(userService.getUserByEmail("alice@example.com")).thenReturn(user);

        String view = userController.loginUser("alice@example.com", "secret", model, session);

        assertEquals("users/dashboard", view);
        verify(session).setAttribute("userId", 1L);
        verify(model).addAttribute("message", "Connexion compte réussie");
    }

    // ──────────────────────────────────────────────────────────────
    // GET /users/search
    // ──────────────────────────────────────────────────────────────

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

    // ──────────────────────────────────────────────────────────────
    // GET /users/logout
    // ──────────────────────────────────────────────────────────────

    @Test
    void logoutPageShouldInvalidateSessionAndReturnFormLogin() {
        String view = userController.logoutPage(session);

        verify(session).invalidate();
        assertEquals("users/formLogin", view);
    }

    // ──────────────────────────────────────────────────────────────
    // GET /users/profile
    // ──────────────────────────────────────────────────────────────
    @Test
    void profilePageShouldReturnLoginWhenUserNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = userController.profilePage(session, model);

        assertEquals("users/formLogin", view);
    }

    @Test
    void profilePageShouldReturnProfileWhenUserExists() {
        User user = new User();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = userController.profilePage(session, model);

        assertEquals("users/profile", view);
        verify(model).addAttribute("user", user);
    }

    @Test
    void profilePageShouldRedirectWhenUserNotFound() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(null);

        String view = userController.profilePage(session, model);

        assertEquals("users/formLogin", view);
    }
}