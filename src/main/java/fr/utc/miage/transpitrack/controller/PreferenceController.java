package fr.utc.miage.transpitrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserSport;
import fr.utc.miage.transpitrack.model.enumer.Level;
import fr.utc.miage.transpitrack.model.jpa.SportService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import fr.utc.miage.transpitrack.model.jpa.UserSportService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class PreferenceController {

    private static final String REDIRECT_LOGIN       = "redirect:/users/formLogin";
    private static final String REDIRECT_PREFERENCES = "redirect:/users/consultationPreferences";
    private static final String SESSION_USER_ID      = "userId";
    private static final String ERROR_MSG            = "errorMessage";
    private static final String MSG                  = "message";
    private static final String NEEDCONNEXION        = "Il faut être connecte !";

    @Autowired
    UserService userService;

    @Autowired
    UserSportService userSportService;

    @Autowired
    SportService sportService;

    // ──────────────────────────────────────────────────────────────
    // Préférences sportives
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/consultationPreferences")
    public String consultationPreferences(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            model.addAttribute(MSG, NEEDCONNEXION);
            return REDIRECT_LOGIN;
        }
        User user = userService.getUserById(userId);
        model.addAttribute("sportsPreference", user.getSportsPreference());
        model.addAttribute("sports", sportService.getAllSports());
        return "users/listPreferences";
    }

    @PostMapping("/addPreference")
    public String addPreference(@RequestParam("sport") Long sportId,
                                @RequestParam("level") Level level,
                                HttpSession session,
                                RedirectAttributes redirectAttrs) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        if (sportId == null || level == null) return REDIRECT_PREFERENCES;

        Sport sport = sportService.getSportById(sportId);
        User user = userService.getUserById(userId);
        UserSport userSportExist = userSportService.getUserSportByUserAndSport(user, sport);
        if (userSportExist != null) {
            redirectAttrs.addFlashAttribute(ERROR_MSG, "Ce sport est déjà dans votre liste !");
            return REDIRECT_PREFERENCES;
        }

        UserSport userSport = new UserSport(user, sport, level);
        userSportService.createUserSport(userSport);
        user.addPreference(userSport);
        userService.updateUser(user);
        return REDIRECT_PREFERENCES;
    }

    @PostMapping("/updateLevel")
    public String updateLevel(@RequestParam("userSport") Long userSportId,
                              @RequestParam("level") Level level,
                              HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        if (userSportId == null || level == null) return REDIRECT_PREFERENCES;

        UserSport userSport = userSportService.getUserSportById(userSportId);
        User user = userService.getUserById(userId);
        user.deletePreference(userSport);
        userSport.setLevel(level);
        userSportService.updateUserSport(userSport);
        user.addPreference(userSport);
        userService.updateUser(user);
        return REDIRECT_PREFERENCES;
    }

    @PostMapping("/deletePreference")
    public String deletePreference(@RequestParam("userSport") Long userSportId,
                                   HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        if (userSportId == null) return REDIRECT_PREFERENCES;

        UserSport userSport = userSportService.getUserSportById(userSportId);
        userSportService.deleteUserSport(userSport);
        User user = userService.getUserById(userId);
        user.deletePreference(userSport);
        userService.updateUser(user);
        return REDIRECT_PREFERENCES;
    }
}
