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

@Controller
@RequestMapping("/users")
public class AuthController {

    private static final String REDIRECT_DASHBOARD   = "redirect:/users/dashboard";
    private static final String REDIRECT_LOGIN       = "redirect:/users/formLogin";
    private static final String REDIRECT_FORM_CREATE = "redirect:/users/formCreate";
    private static final String VIEW_FORM_CREATE     = "users/formCreate";
    private static final String VIEW_FORM_LOGIN      = "users/formLogin";
    private static final String SESSION_USER_ID      = "userId";
    private static final String SUCCESS_MSG          = "successMessage";
    private static final String MSG                  = "message";

    @Autowired
    UserService userService;

    @Autowired
    ImageStorageService imageStorageService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ──────────────────────────────────────────────────────────────
    // Inscription
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/formCreate")
    public String formCreate(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId != null) return REDIRECT_DASHBOARD;
        model.addAttribute(MSG, "");
        return VIEW_FORM_CREATE;
    }

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
    // Connexion / Déconnexion
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/formLogin")
    public String formLogin(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId != null) return REDIRECT_DASHBOARD;
        model.addAttribute(MSG, "");
        return VIEW_FORM_LOGIN;
    }

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

    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        session.invalidate();
        return REDIRECT_LOGIN;
    }
}
