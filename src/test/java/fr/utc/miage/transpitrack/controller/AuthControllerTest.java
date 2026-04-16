package fr.utc.miage.transpitrack.controller;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.enumer.Gender;
import fr.utc.miage.transpitrack.model.jpa.ImageStorageService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private ImageStorageService imageStorageService;
    @Mock
    private Model model;
    @Mock
    private HttpSession session;
    @Mock
    private RedirectAttributes redirectAttrs;

    @InjectMocks
    private AuthController authController;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private User buildLoginUser(String plainPassword) {
        User user = new User();
        user.setFirstName("Alice");
        user.setName("Dupont");
        user.setEmail("alice@example.com");
        user.setPassword(encoder.encode(plainPassword));
        user.setAge(25);
        user.setHeight(165.0);
        user.setGender(Gender.FEMALE);
        user.setWeight(60.0);
        user.setCity("Paris");
        return user;
    }

    // ──────────────────────────────────────────────────────────────
    // GET /users/formCreate
    // ──────────────────────────────────────────────────────────────

    @Test
    void formCreateShouldRedirectToDashboardWhenUserAlreadyLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = authController.formCreate(model, session);

        assertEquals("redirect:/users/dashboard", view);
    }

    @Test
    void formCreateShouldReturnFormCreateWhenNotLoggedIn() {
        String view = authController.formCreate(model, session);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "");
    }

    // ──────────────────────────────────────────────────────────────
    // POST /users/createUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void createUserShouldReturnFormCreateWhenAgeIsNegative() {
        String view = authController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                -1, 165.0, "FEMALE", 60.0, "Paris", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/formCreate", view);
        verify(model).addAttribute("message", "Age ne peut pas être négatif");
    }

    @Test
    void createUserShouldReturnFormCreateWhenHeightIsNegative() {
        String view = authController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, -1.0, "FEMALE", 60.0, "Paris", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/formCreate", view);
        verify(model).addAttribute("message", "Taille ne peut pas être négatif");
    }

    @Test
    void createUserShouldReturnFormCreateWhenWeightIsNegative() {
        String view = authController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", -1.0, "Paris", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/formCreate", view);
        verify(model).addAttribute("message", "Poids ne peut pas être négatif");
    }

    @Test
    void createUserShouldReturnFormCreateWhenEmailAlreadyExists() {
        when(userService.getUserByEmail("alice@example.com")).thenReturn(new User());

        String view = authController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/formCreate", view);
        verify(model).addAttribute("message", "email dejas existant");
    }

    @Test
    void createUserShouldReturnFormCreateWhenEmailFormatInvalid() {
        String view = authController.createUser(
                "Alice", "Dupont", "email-invalide", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/formCreate", view);
        verify(model).addAttribute("message", "Email invalide");
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

        String view = authController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", null, model, session, redirectAttrs);

        assertEquals("redirect:/users/dashboard", view);
        verify(userService).createUser(any(User.class));
        verify(session).setAttribute("userId", savedUser.getId());
        verify(redirectAttrs).addFlashAttribute(eq("successMessage"), any());
    }

    @Test
    void createUserShouldReturnFormCreateWhenImageUploadFails() throws IOException {
        when(userService.getUserByEmail("alice@example.com")).thenReturn(null);
        when(imageStorageService.store(any(MultipartFile.class))).thenThrow(new IOException("disk full"));

        String view = authController.createUser(
                "Alice", "Dupont", "alice@example.com", "secret",
                25, 165.0, "FEMALE", 60.0, "Paris", mock(MultipartFile.class), model, session, redirectAttrs);

        assertEquals("users/formCreate", view);
        verify(model).addAttribute("message", "Erreur lors de l'upload de l'image");
    }

    // ──────────────────────────────────────────────────────────────
    // GET /users/formLogin
    // ──────────────────────────────────────────────────────────────

    @Test
    void formLoginShouldRedirectToDashboardWhenAlreadyLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = authController.formLogin(model, session);

        assertEquals("redirect:/users/dashboard", view);
    }

    @Test
    void formLoginShouldReturnFormLoginWhenNotLoggedIn() {
        String view = authController.formLogin(model, session);

        assertEquals("users/formLogin", view);
    }

    // ──────────────────────────────────────────────────────────────
    // POST /users/loginUser
    // ──────────────────────────────────────────────────────────────

    @Test
    void loginUserShouldReturnFormLoginWhenEmailNotFound() {
        String view = authController.loginUser("unknown@example.com", "secret", model, session, redirectAttrs);

        assertEquals("redirect:/users/formLogin", view);
        verify(model).addAttribute("message", "email ou mots de passe incorrect");
    }

    @Test
    void loginUserShouldReturnFormLoginWhenPasswordInvalid() {
        when(userService.getUserByEmail("alice@example.com")).thenReturn(buildLoginUser("correct"));

        String view = authController.loginUser("alice@example.com", "wrong", model, session, redirectAttrs);

        assertEquals("redirect:/users/formLogin", view);
        verify(model).addAttribute("message", "email ou mots de passe incorrect");
    }

    @Test
    void loginUserShouldReturnDashboardWhenCredentialsValid() throws Exception {
        User userLogin = buildLoginUser("secret");
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(userLogin, 1L);
        when(userService.getUserByEmail("alice@example.com")).thenReturn(userLogin);

        String view = authController.loginUser("alice@example.com", "secret", model, session, redirectAttrs);

        assertEquals("redirect:/users/dashboard", view);
        verify(session).setAttribute("userId", 1L);
        verify(redirectAttrs).addFlashAttribute(eq("successMessage"), any());
    }

    // ──────────────────────────────────────────────────────────────
    // GET /users/logout
    // ──────────────────────────────────────────────────────────────

    @Test
    void logoutShouldInvalidateSessionAndReturnFormLogin() {
        String view = authController.logoutPage(session);

        assertEquals("redirect:/users/formLogin", view);
        verify(session).invalidate();
    }
}
