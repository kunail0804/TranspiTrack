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
    // GET /user/formCreate
    // ──────────────────────────────────────────────────────────────

    @Test
    void formCreateShouldReturnDashboardWhenUserAlreadyLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.formCreate(null, model, session);

        assertEquals("dashboard", view);
    }

    @Test
    void formCreateShouldReturnFormCreateWhenNotLoggedIn() {
        String view = userController.formCreate("Bienvenue", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Bienvenue");
    }

    // ──────────────────────────────────────────────────────────────
    // POST /user/createUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void createUserShouldReturnFormCreateWhenEmailFormatInvalid() {
        String view = userController.createUser(
                "Alice", "Dupont", "email-invalide", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Email n'est pas au bon format");
    }

    @Test
    void createUserShouldReturnFormCreateWhenAgeIsNegative() {
        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                -1, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Age ne peut pas être négatif");
    }

    @Test
    void createUserShouldReturnFormCreateWhenHeightIsNegative() {
        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, -1.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Taille ne peut pas être négatif");
    }

    @Test
    void createUserShouldReturnFormCreateWhenWeightIsNegative() {
        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", -1.0, "Paris", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Poids ne peut pas être négatif");
    }

    @Test
    void createUserShouldReturnFormCreateWhenEmailAlreadyExists() {
        when(userService.getUserByEmail("alice@example.com")).thenReturn(new User());

        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formCreate", view);
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

        assertEquals("dashboard", view);
        verify(userService).createUser(any(User.class));
        verify(session).setAttribute("userId", savedUser.getId());
        verify(model).addAttribute("message", "Création compte réussie");
    }

    // ──────────────────────────────────────────────────────────────
    // GET /user/formUpdate
    // ──────────────────────────────────────────────────────────────

    @Test
    void formUpdateShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.formUpdate(null, model, session);

        assertEquals("formLogin", view);
    }

    @Test
    void formUpdateShouldReturnFormUpdateWithUserWhenLoggedIn() {
        User user = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = userController.formUpdate("hello", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "hello");
        verify(model).addAttribute("user", user);
    }

    // ──────────────────────────────────────────────────────────────
    // POST /user/updateUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void updateUserShouldReturnFormUpdateWhenEmailFormatInvalid() {
        String view = userController.updateUser(
                "Alice", "Dupont", "email-invalide", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "Email n'est pas au bon format");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenAgeIsNegative() {
        String view = userController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                -1, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "Age ne peut pas être négatif");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenHeightIsNegative() {
        String view = userController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, -1.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "Taille ne peut pas être négatif");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenWeightIsNegative() {
        String view = userController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", -1.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "Poids ne peut pas être négatif");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenNewEmailAlreadyTaken() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(new User());

        String view = userController.updateUser(
                "Alice", "Dupont", "newemail@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "email déja existant");
    }

    @Test
    void updateUserShouldUpdateAndReturnDashboardWhenEmailChangedAndFree() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(null);

        String view = userController.updateUser(
                "Bob", "Martin", "newemail@example.com", "",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("dashboard", view);
        verify(userService).updateUser(actualUser);
        verify(model).addAttribute("message", "Modification du compte réussie");
    }

    @Test
    void updateUserShouldEncodePasswordWhenEmailChangedAndPasswordNotBlank() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(null);

        String view = userController.updateUser(
                "Bob", "Martin", "newemail@example.com", "newpassword",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("dashboard", view);
        verify(userService).updateUser(actualUser);
    }

    @Test
    void updateUserShouldUpdateAndReturnDashboardWhenEmailUnchanged() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);

        String view = userController.updateUser(
                "Bob", "Martin", "alice@example.com", "",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("dashboard", view);
        verify(userService).updateUser(actualUser);
        verify(model).addAttribute("message", "Modification du compte réussie");
    }
    // ──────────────────────────────────────────────────────────────
    // GET /user/search
    // ──────────────────────────────────────────────────────────────

    @Test
    void searchUserShouldRedirectToLoginWhenNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = userController.searchUser("Jean", model, session);

        assertEquals("redirect:/user/formLogin", view);
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
    // GET /user/logout
    // ──────────────────────────────────────────────────────────────

    @Test
    void updateUserShouldEncodePasswordWhenEmailUnchangedAndPasswordNotBlank() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);

        String view = userController.updateUser(
                "Bob", "Martin", "alice@example.com", "newpassword",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("dashboard", view);
        verify(userService).updateUser(actualUser);
    }

    // ──────────────────────────────────────────────────────────────
    // GET /user/formUpdate
    // ──────────────────────────────────────────────────────────────

    @Test
    void formUpdateShouldReturnFormLoginWhenNotLoggedIn() {
        String view = userController.formUpdate(null, model, session);

        assertEquals("formLogin", view);
    }

    @Test
    void formUpdateShouldReturnFormUpdateWithUserWhenLoggedIn() {
        User user = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = userController.formUpdate("hello", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "hello");
        verify(model).addAttribute("user", user);
    }

    // ──────────────────────────────────────────────────────────────
    // POST /user/updateUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void updateUserShouldReturnFormUpdateWhenEmailFormatInvalid() {
        String view = userController.updateUser(
                "Alice", "Dupont", "email-invalide", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "Email n'est pas au bon format");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenAgeIsNegative() {
        String view = userController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                -1, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "Age ne peut pas être négatif");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenHeightIsNegative() {
        String view = userController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, -1.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "Taille ne peut pas être négatif");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenWeightIsNegative() {
        String view = userController.updateUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", -1.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "Poids ne peut pas être négatif");
    }

    @Test
    void updateUserShouldReturnFormUpdateWhenNewEmailAlreadyTaken() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(new User());

        String view = userController.updateUser(
                "Alice", "Dupont", "newemail@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formUpdate", view);
        verify(model).addAttribute("message", "email déja existant");
    }

    @Test
    void updateUserShouldUpdateAndReturnDashboardWhenEmailChangedAndFree() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(null);

        String view = userController.updateUser(
                "Bob", "Martin", "newemail@example.com", "",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("dashboard", view);
        verify(userService).updateUser(actualUser);
        verify(model).addAttribute("message", "Modification du compte réussie");
    }

    @Test
    void updateUserShouldEncodePasswordWhenEmailChangedAndPasswordNotBlank() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);
        when(userService.getUserByEmail("newemail@example.com")).thenReturn(null);

        String view = userController.updateUser(
                "Bob", "Martin", "newemail@example.com", "newpassword",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("dashboard", view);
        verify(userService).updateUser(actualUser);
    }

    @Test
    void updateUserShouldUpdateAndReturnDashboardWhenEmailUnchanged() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);

        String view = userController.updateUser(
                "Bob", "Martin", "alice@example.com", "",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("dashboard", view);
        verify(userService).updateUser(actualUser);
        verify(model).addAttribute("message", "Modification du compte réussie");
    }

    @Test
    void updateUserShouldEncodePasswordWhenEmailUnchangedAndPasswordNotBlank() {
        User actualUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(actualUser);

        String view = userController.updateUser(
                "Bob", "Martin", "alice@example.com", "newpassword",
                30, 180.0, "MALE", 80.0, "Lyon", model, session);

        assertEquals("dashboard", view);
        verify(userService).updateUser(actualUser);
    }
}
