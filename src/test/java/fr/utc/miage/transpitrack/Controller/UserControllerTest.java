package fr.utc.miage.transpitrack.Controller;

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
    void createUserShouldReturnDashboardWhenUserCreatedSuccessfully() {
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

    // ──────────────────────────────────────────────────────────────
    // GET /user/formLogin
    // ──────────────────────────────────────────────────────────────

    @Test
    void formLogin_shouldReturnDashboardWhenUserAlreadyLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = userController.formLogin(null, model, session);

        assertEquals("dashboard", view);
    }

    @Test
    void formLogin_shouldReturnFormLoginWhenNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String view = userController.formLogin("Bienvenue", model, session);

        assertEquals("formLogin", view);
        verify(model).addAttribute("message", "Bienvenue");
    }

    // ──────────────────────────────────────────────────────────────
    // POST /user/LoginUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void loginUser_shouldReturnFormLoginWhenEmailDoesNotExist() {

        when(userService.getUserByEmail("email-invalide")).thenReturn(null);

        String view = userController.loginUser(
                "email-invalide", "secret",
                model, session);

        assertEquals("formLogin", view);
        verify(model).addAttribute("message", "email ou mots de passe incorrect");
    }

    @Test
    void loginUser_shouldReturnFormLoginWhenPasswordIsInvalid() {

        User user = new User();
        user.setPassword(encoder.encode("secret"));

        when(userService.getUserByEmail("alice@example.com")).thenReturn(user);

        String view = userController.loginUser(
                "alice@example.com", "wrong",
                model, session);

        assertEquals("formLogin", view);
        verify(model).addAttribute("message", "email ou mots de passe incorrect");
    }


    @Test
    void loginUser_shouldReturnDashboardWhenEmailAndPasswordValid() {
        User user = new User();
        user.setId(1L);
        user.setEmail("alice@example.com");
        user.setPassword(encoder.encode("secret"));
        user.setId(1L);

        when(userService.getUserByEmail("alice@example.com")).thenReturn(user);

        String view = userController.loginUser(
                "alice@example.com", "secret",
                model, session);

        assertEquals("dashboard", view);
        verify(session).setAttribute("userId", 1L);
        verify(model).addAttribute("message", "Connexion compte réussie");
    }

}
