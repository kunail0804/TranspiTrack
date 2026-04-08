package fr.utc.miage.transpitrack.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
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

    // ──────────────────────────────────────────────────────────────
    // GET /user/formCreate
    // ──────────────────────────────────────────────────────────────

    @Test
    void formCreate_shouldReturnDashboardWhenUserAlreadyLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.formCreate(null, model, session);

        assertEquals("dashboard", view);
    }

    @Test
    void formCreate_shouldReturnFormCreateWhenNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = userController.formCreate("Bienvenue", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Bienvenue");
    }

    // ──────────────────────────────────────────────────────────────
    // POST /user/createUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void createUser_shouldReturnFormCreateWhenEmailFormatInvalid() {
        String view = userController.createUser(
                "Alice", "Dupont", "email-invalide", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Email n'est pas au bon format");
    }

    @Test
    void createUser_shouldReturnFormCreateWhenAgeIsNegative() {
        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                -1, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Age ne peut pas être négatif");
    }

    @Test
    void createUser_shouldReturnFormCreateWhenHeightIsNegative() {
        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, -1.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Taille ne peut pas être négatif");
    }

    @Test
    void createUser_shouldReturnFormCreateWhenWeightIsNegative() {
        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", -1.0, "Paris", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "Poids ne peut pas être négatif");
    }

    @Test
    void createUser_shouldReturnFormCreateWhenEmailAlreadyExists() {
        when(userService.getUserByEmail("alice@example.com")).thenReturn(new User());

        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("formCreate", view);
        verify(model).addAttribute("message", "email dejas existant");
    }

    @Test
    void createUser_shouldReturnDashboardWhenUserCreatedSuccessfully() {
        User savedUser = new User("Alice", "Dupont", "alice@example.com", "secret", 25, 165.0, fr.utc.miage.transpitrack.Model.Enum.Gender.FEMALE, 60.0, "Paris");
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        String view = userController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", model, session);

        assertEquals("dashboard", view);
        verify(userService).createUser(any(User.class));
        verify(session).setAttribute("userId", savedUser.getId());
        verify(model).addAttribute("message", "Création compte réussie");
    }
}
