package fr.utc.miage.transpitrack.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.utc.miage.transpitrack.model.Activity;
import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.enumer.Gender;
import fr.utc.miage.transpitrack.model.jpa.ActivityService;
import fr.utc.miage.transpitrack.model.jpa.BadgeService;
import fr.utc.miage.transpitrack.model.jpa.FriendshipService;
import fr.utc.miage.transpitrack.model.jpa.ImageStorageService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import jakarta.servlet.http.HttpSession;

/**
 * Spring MVC controller handling user profile operations under {@code /users}.
 * <p>
 * Covers profile display (own and others'), profile editing (personal details,
 * password, city, and profile picture), profile picture deletion, and user
 * search. BCrypt is used to hash updated passwords before persistence.
 * </p>
 */
@Controller
@RequestMapping("/users")
public class ProfileController {

    /** Redirect to the dashboard after a successful profile update. */
    private static final String REDIRECT_DASHBOARD   = "redirect:/users/dashboard";

    /** Redirect to the login form when the user is not authenticated. */
    private static final String REDIRECT_LOGIN       = "redirect:/users/formLogin";

    /** Redirect to the profile update form after a validation error. */
    private static final String REDIRECT_FORM_UPDATE = "redirect:/users/formUpdate";

    /** Redirect to the user search page when a target profile is not found. */
    private static final String REDIRECT_SEARCH      = "redirect:/users/search";

    /** Logical view name for the profile update form. */
    private static final String VIEW_FORM_UPDATE     = "users/formUpdate";

    /** Session attribute key that stores the authenticated user's ID. */
    private static final String SESSION_USER_ID      = "userId";

    /** Model attribute key for error messages. */
    private static final String ERROR_MSG            = "errorMessage";

    /** Model attribute key for success messages. */
    private static final String SUCCESS_MSG          = "successMessage";

    /** Model attribute key for general messages. */
    private static final String MSG                  = "message";

    /** Message shown when an unauthenticated user attempts to access a protected resource. */
    private static final String LOGIN_REQUIRED        = "Il faut être connecte !";

    /** Service for user retrieval and updates. */
    @Autowired
    UserService userService;

    /** Service for retrieving the user's activities. */
    @Autowired
    ActivityService activityService;

    /** Service for retrieving friendship data. */
    @Autowired
    FriendshipService friendshipService;

    /** Service for retrieving earned badges. */
    @Autowired
    BadgeService badgeService;

    /** Service for storing and deleting profile images. */
    @Autowired
    ImageStorageService imageStorageService;

    /** BCrypt encoder used to hash passwords before storing them. */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ──────────────────────────────────────────────────────────────
    // Profile Update
    // ──────────────────────────────────────────────────────────────

    /**
     * Displays the profile update form pre-populated with the authenticated user's data.
     *
     * @param model   the Spring MVC model
     * @param session the current HTTP session
     * @return the {@code users/formUpdate} view, or a redirect to login if not authenticated
     */
    @GetMapping("/formUpdate")
    public String formUpdate(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return VIEW_FORM_UPDATE;
    }

    /**
     * Processes the profile update form submission.
     * <p>
     * Validates that age, height, and weight are non-negative, and that the new
     * email address is not already taken by another account. If a profile image
     * is provided, the old image is deleted and replaced. If the password field
     * is blank, the existing password is kept unchanged.
     * </p>
     *
     * @param firstName        the user's first name
     * @param name             the user's family name
     * @param email            the user's email address
     * @param password         the new password (left blank to keep the current one)
     * @param age              the user's age in years (must be &ge; 0)
     * @param height           the user's height in centimetres (must be &ge; 0)
     * @param gender           the user's gender as a {@link Gender} constant name
     * @param weight           the user's weight in kilograms (must be &ge; 0)
     * @param city             the user's city used for weather lookups
     * @param profileImageFile an optional new profile picture
     * @param model            the Spring MVC model
     * @param session          the current HTTP session
     * @param redirectAttrs    used to pass a success flash message after redirect
     * @return a redirect to the dashboard on success, or back to the form on validation error
     */
    @PostMapping("/updateUser")
    public String updateUser(@RequestParam("firstName") String firstName,
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

        Long actualUserId = (Long) session.getAttribute(SESSION_USER_ID);
        User actualUser = userService.getUserById(actualUserId);

        if (age < 0) {
            model.addAttribute(MSG, "Age ne peut pas être négatif");
            return REDIRECT_FORM_UPDATE;
        }
        if (height < 0) {
            model.addAttribute(MSG, "Taille ne peut pas être négatif");
            return REDIRECT_FORM_UPDATE;
        }
        if (weight < 0) {
            model.addAttribute(MSG, "Poids ne peut pas être négatif");
            return REDIRECT_FORM_UPDATE;
        }

        if (!email.equals(actualUser.getEmail())) {
            User userExist = userService.getUserByEmail(email);
            if (userExist != null) {
                model.addAttribute(MSG, "email déja existant");
                return REDIRECT_FORM_UPDATE;
            }
        }

        actualUser.setName(name);
        actualUser.setFirstName(firstName);
        actualUser.setAge(age);
        actualUser.setGender(Gender.valueOf(gender));
        actualUser.setEmail(email);
        actualUser.setHeight(height);
        actualUser.setWeight(weight);
        actualUser.setCity(city);
        if (!password.isBlank()) {
            actualUser.setPassword(encoder.encode(password));
        }

        try {
            String newFilename = imageStorageService.store(profileImageFile);
            if (newFilename != null) {
                imageStorageService.delete(actualUser.getProfileImage());
                actualUser.setProfileImage(newFilename);
            }
            userService.updateUser(actualUser);
        } catch (IOException _) {
            model.addAttribute(ERROR_MSG, "Erreur lors de l'upload de la photo de profil.");
            return VIEW_FORM_UPDATE;
        } catch (Exception _) {
            model.addAttribute(MSG, "Email invalide");
            return REDIRECT_FORM_UPDATE;
        }

        redirectAttrs.addFlashAttribute(SUCCESS_MSG, "Profil mis à jour avec succès !");
        return REDIRECT_DASHBOARD;
    }

    /**
     * Deletes the profile picture of the currently authenticated user and resets
     * the profile image field to {@code null}.
     *
     * @param session the current HTTP session
     * @return a redirect to the profile update form
     */
    @PostMapping("/deleteProfileImage")
    public String deleteProfileImage(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        User user = userService.getUserById(userId);
        imageStorageService.delete(user.getProfileImage());
        user.setProfileImage(null);
        userService.updateUser(user);
        return REDIRECT_FORM_UPDATE;
    }

    // ──────────────────────────────────────────────────────────────
    // Profile
    // ──────────────────────────────────────────────────────────────

    /**
     * Displays the profile page of the currently authenticated user.
     * <p>
     * Loads the user's activities (sorted newest first), accepted friends,
     * pending incoming friend-request count, and earned badges.
     * </p>
     *
     * @param session the current HTTP session
     * @param model   the Spring MVC model
     * @return the {@code users/profile} view, or a redirect to login if not authenticated
     */
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            model.addAttribute(MSG, LOGIN_REQUIRED);
            return REDIRECT_LOGIN;
        }
        User user = userService.getUserById(userId);
        if (user == null) return REDIRECT_LOGIN;

        List<Activity> activities = activityService.getActivitiesByUserId(userId);
        List<Friendship> friendships = friendshipService.getMyFriendships(userId);
        List<User> friends = friendships.stream()
                .map(f -> f.getRequester().getId().equals(userId) ? f.getReceiver() : f.getRequester())
                .toList();
        int pendingFriendships = friendshipService.getMyPendingFriendships(userId).size();

        activities.sort((a1, a2) -> a2.getDate().compareTo(a1.getDate()));
        model.addAttribute("user", user);
        model.addAttribute("activities", activities);
        model.addAttribute("friends", friends);
        model.addAttribute("pendingFriendships", pendingFriendships);
        model.addAttribute("isOwner", true);
        model.addAttribute("userBadges", badgeService.getUserBadges(user));

        return "users/profile";
    }

    /**
     * Displays the public profile page of the user identified by {@code profileId}.
     * <p>
     * Resolves whether the viewer is the profile owner and whether a friendship or
     * pending request already exists between the viewer and the profile owner.
     * </p>
     *
     * @param profileId the ID of the user whose profile to display
     * @param model     the Spring MVC model
     * @param session   the current HTTP session
     * @return the {@code users/profile} view, or a redirect to login / search
     */
    @GetMapping("/profile/{id}")
    public String viewProfile(@PathVariable("id") Long profileId, Model model, HttpSession session) {
        Long currentUserId = (Long) session.getAttribute(SESSION_USER_ID);
        if (currentUserId == null) return REDIRECT_LOGIN;

        User profileUser = userService.getUserById(profileId);
        if (profileUser == null) return REDIRECT_SEARCH;

        List<Activity> activities = activityService.getActivitiesByUserId(profileId);
        boolean isOwner = currentUserId.equals(profileId);
        boolean requestSent = friendshipService.requestOrFriendshipExists(currentUserId, profileId);

        model.addAttribute("user", profileUser);
        model.addAttribute("activities", activities);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("requestSent", requestSent);
        return "users/profile";
    }

    // ──────────────────────────────────────────────────────────────
    // Search
    // ──────────────────────────────────────────────────────────────

    /**
     * Searches for users by name or email and displays the results.
     * <p>
     * Also computes a set of user IDs that already have a relationship with the
     * authenticated user (friends, sent requests, received requests) so that the
     * template can suppress the "Add friend" button for those users.
     * </p>
     *
     * @param query   the search string; if blank or {@code null}, an empty result list is returned
     * @param model   the Spring MVC model
     * @param session the current HTTP session
     * @return the {@code search/searchUser} view, or a redirect to login if not authenticated
     */
    @GetMapping("/search")
    public String searchUser(@RequestParam(required = false) String query,
                             Model model,
                             HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;

        if (query != null && !query.isBlank()) {
            model.addAttribute("users", userService.searchUsers(query));
        } else {
            model.addAttribute("users", List.of());
        }

        Set<Long> relatedUserIds = new HashSet<>();
        friendshipService.getMyFriendships(userId).forEach(f ->
            relatedUserIds.add(f.getRequester().getId().equals(userId) ? f.getReceiver().getId() : f.getRequester().getId())
        );
        friendshipService.getMySentPendingFriendships(userId).forEach(f -> relatedUserIds.add(f.getReceiver().getId()));
        friendshipService.getMyPendingFriendships(userId).forEach(f -> relatedUserIds.add(f.getRequester().getId()));

        model.addAttribute("currentUserId", userId);
        model.addAttribute("relatedUserIds", relatedUserIds);
        model.addAttribute("query", query);
        return "search/searchUser";
    }
}
