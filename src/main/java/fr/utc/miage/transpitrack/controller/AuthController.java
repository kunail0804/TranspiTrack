package fr.utc.miage.transpitrack.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.enumer.Gender;
import fr.utc.miage.transpitrack.model.jpa.ImageStorageService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import jakarta.servlet.http.HttpSession;

/**
 * Spring MVC controller handling user registration, login, and logout under
 * {@code /users}.
 * <p>
 * Passwords are hashed with BCrypt before persistence and verified on login.
 * The authenticated user's ID is stored in the HTTP session under the key
 * {@code "userId"}.
 * </p>
 */
@Controller
@RequestMapping("/users")
public class AuthController {

    /** Redirect to the user dashboard. */
    private static final String REDIRECT_DASHBOARD   = "redirect:/users/dashboard";

    /** Redirect to the login form. */
    private static final String REDIRECT_LOGIN       = "redirect:/users/formLogin";

    /** Redirect to the registration form. */
    private static final String REDIRECT_FORM_CREATE = "redirect:/users/formCreate";

    /** View name for the registration form. */
    private static final String VIEW_FORM_CREATE     = "users/formCreate";

    /** View name for the login form. */
    private static final String VIEW_FORM_LOGIN      = "users/formLogin";

    /** Session attribute key that stores the authenticated user's ID. */
    private static final String SESSION_USER_ID      = "userId";

    /** Model attribute key for flash success messages. */
    private static final String SUCCESS_MSG          = "successMessage";

    /** Model attribute key for general messages. */
    private static final String MSG                  = "message";

    /** Service for user CRUD operations and email lookups. */
    @Autowired
    UserService userService;

    /** Service for storing and deleting profile images. */
    @Autowired
    ImageStorageService imageStorageService;

    /** BCrypt encoder used to hash passwords before storage and verify them on login. */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ──────────────────────────────────────────────────────────────
    // Registration
    // ──────────────────────────────────────────────────────────────

    /**
     * Displays the account registration form.
     * Redirects already-authenticated users to the dashboard.
     *
     * @param model   the Spring MVC model
     * @param session the current HTTP session
     * @return the {@code users/formCreate} view, or a redirect to the dashboard
     */
    @GetMapping("/formCreate")
    public String formCreate(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId != null) return REDIRECT_DASHBOARD;
        model.addAttribute(MSG, "");
        return VIEW_FORM_CREATE;
    }

    /**
     * Processes the registration form, validates input, creates the user account,
     * and logs the user in by storing their ID in the session.
     *
     * @param firstName        the user's first name
     * @param name             the user's last name
     * @param email            the user's email address
     * @param password         the plain-text password (will be BCrypt-hashed)
     * @param age              the user's age in years
     * @param height           the user's height in centimetres
     * @param gender           the user's gender string (must match a {@link Gender} constant)
     * @param weight           the user's weight in kilograms
     * @param city             the user's city
     * @param profileImageFile the optional profile image file upload
     * @param model            the Spring MVC model
     * @param session          the current HTTP session
     * @param redirectAttrs    used to pass flash attributes after redirect
     * @return a redirect to the dashboard on success, or back to the registration form on error
     */
    @PostMapping("/createUser")
    public String createUser(@RequestParam("firstName") String firstName,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("age") int age,
            @RequestParam("height") double height,
            @RequestParam("gender") String gender,
            @RequestParam("weight") double weight,
            @RequestParam("city") String city,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImageFile,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        if (age < 0) {
            model.addAttribute(MSG, "Age ne peut pas être négatif");
            return REDIRECT_FORM_CREATE;
        }
        if (height < 0) {
            model.addAttribute(MSG, "Taille ne peut pas être négatif");
            return REDIRECT_FORM_CREATE;
        }
        if (weight < 0) {
            model.addAttribute(MSG, "Poids ne peut pas être négatif");
            return REDIRECT_FORM_CREATE;
        }

        User userExist = userService.getUserByEmail(email);
        if (userExist != null) {
            model.addAttribute(MSG, "email dejas existant");
            return REDIRECT_FORM_CREATE;
        }

        try {
            User newUser = new User();
            newUser.setName(name);
            newUser.setFirstName(firstName);
            newUser.setEmail(email);
            newUser.setPassword(encoder.encode(password));
            newUser.setAge(age);
            newUser.setHeight(height);
            newUser.setGender(Gender.valueOf(gender));
            newUser.setWeight(weight);
            newUser.setCity(city);
            String filename = imageStorageService.store(profileImageFile);
            newUser.setProfileImage(filename);
            User savedUser = userService.createUser(newUser);
            session.setAttribute(SESSION_USER_ID, savedUser.getId());
        } catch (IOException _) {
            model.addAttribute(MSG, "Erreur lors de l'upload de l'image");
            return VIEW_FORM_CREATE;
        } catch (Exception _) {
            model.addAttribute(MSG, "Email invalide");
            return REDIRECT_FORM_CREATE;
        }

        redirectAttrs.addFlashAttribute(SUCCESS_MSG, "Compte créé avec succès ! Bienvenue sur TranspiTrack.");
        return REDIRECT_DASHBOARD;
    }

    // ──────────────────────────────────────────────────────────────
    // Login / Logout
    // ──────────────────────────────────────────────────────────────

    /**
     * Displays the login form.
     * Redirects already-authenticated users to the dashboard.
     *
     * @param model   the Spring MVC model
     * @param session the current HTTP session
     * @return the {@code users/formLogin} view, or a redirect to the dashboard
     */
    @GetMapping("/formLogin")
    public String formLogin(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId != null) return REDIRECT_DASHBOARD;
        model.addAttribute(MSG, "");
        return VIEW_FORM_LOGIN;
    }

    /**
     * Processes the login form, verifies the credentials, and stores the user's ID
     * in the session on success.
     *
     * @param email         the submitted email address
     * @param password      the submitted plain-text password
     * @param model         the Spring MVC model
     * @param session       the current HTTP session
     * @param redirectAttrs used to pass flash attributes after redirect
     * @return a redirect to the dashboard on success, or back to the login form on failure
     */
    @PostMapping("/loginUser")
    public String loginUser(@RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        User userLogin = userService.getUserByEmail(email);
        if (userLogin == null) {
            model.addAttribute(MSG, "email ou mots de passe incorrect");
            return REDIRECT_LOGIN;
        }

        boolean isValid = encoder.matches(password, userLogin.getPassword());
        if (!isValid) {
            model.addAttribute(MSG, "email ou mots de passe incorrect");
            return REDIRECT_LOGIN;
        }

        session.setAttribute(SESSION_USER_ID, userLogin.getId());
        redirectAttrs.addFlashAttribute(SUCCESS_MSG, "Bienvenue, " + userLogin.getFirstName() + " !");
        return REDIRECT_DASHBOARD;
    }

    /**
     * Logs the user out by invalidating the session and redirects to the login form.
     *
     * @param session the current HTTP session to invalidate
     * @return a redirect to the login form
     */
    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        session.invalidate();
        return REDIRECT_LOGIN;
    }
}
