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

/**
 * Spring MVC controller handling sport preference management under {@code /users}.
 * <p>
 * Allows authenticated users to declare which sports they practise and at what
 * proficiency level ({@link Level}), and to update or remove those preferences.
 * </p>
 */
@Controller
@RequestMapping("/users")
public class PreferenceController {

    /** Redirect to the login form when the user is not authenticated. */
    private static final String REDIRECT_LOGIN       = "redirect:/users/formLogin";

    /** Redirect to the preference list after a successful operation. */
    private static final String REDIRECT_PREFERENCES = "redirect:/users/consultationPreferences";

    /** Session attribute key that stores the authenticated user's ID. */
    private static final String SESSION_USER_ID      = "userId";

    /** Model attribute key for error messages. */
    private static final String ERROR_MSG            = "errorMessage";

    /** Model attribute key for general messages. */
    private static final String MSG                  = "message";

    /** Message shown when an unauthenticated user attempts to access a protected resource. */
    private static final String LOGIN_REQUIRED       = "Il faut être connecte !";

    /** Service for user retrieval and updates. */
    @Autowired
    UserService userService;

    /** Service for user sport preference CRUD operations. */
    @Autowired
    UserSportService userSportService;

    /** Service for sport retrieval. */
    @Autowired
    SportService sportService;

    // ──────────────────────────────────────────────────────────────
    // Sports Preferences
    // ──────────────────────────────────────────────────────────────

    /**
     * Displays the list of sport preferences for the currently authenticated user.
     *
     * @param model   the Spring MVC model
     * @param session the current HTTP session
     * @return the {@code users/listPreferences} view, or a redirect to login if not authenticated
     */
    @GetMapping("/consultationPreferences")
    public String consultationPreferences(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            model.addAttribute(MSG, LOGIN_REQUIRED);
            return REDIRECT_LOGIN;
        }
        User user = userService.getUserById(userId);
        model.addAttribute("sportsPreference", user.getSportsPreference());
        model.addAttribute("sports", sportService.getAllSports());
        return "users/listPreferences";
    }

    /**
     * Adds a new sport preference for the currently authenticated user.
     * If the user already has a preference for the selected sport, an error
     * message is flashed and no record is created.
     *
     * @param sportId       the ID of the sport to add
     * @param level         the user's proficiency level for that sport
     * @param session       the current HTTP session
     * @param redirectAttrs used to pass flash error attributes after redirect
     * @return a redirect to the preference list, or to login if not authenticated
     */
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

    /**
     * Updates the proficiency level of an existing sport preference.
     *
     * @param userSportId the ID of the {@link UserSport} record to update
     * @param level       the new proficiency level
     * @param session     the current HTTP session
     * @return a redirect to the preference list, or to login if not authenticated
     */
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

    /**
     * Removes a sport preference from the currently authenticated user's list.
     *
     * @param userSportId the ID of the {@link UserSport} record to delete
     * @param session     the current HTTP session
     * @return a redirect to the preference list, or to login if not authenticated
     */
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
