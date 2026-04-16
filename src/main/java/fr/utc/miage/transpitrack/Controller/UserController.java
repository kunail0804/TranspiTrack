package fr.utc.miage.transpitrack.controller;

import java.io.IOException;
import java.util.List;

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
import fr.utc.miage.transpitrack.model.Goal;
import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserSport;
import fr.utc.miage.transpitrack.model.enumer.Gender;
import fr.utc.miage.transpitrack.model.enumer.Level;
import fr.utc.miage.transpitrack.model.enumer.Temporality;
import fr.utc.miage.transpitrack.model.jpa.ActivityService;
import fr.utc.miage.transpitrack.model.jpa.BadgeService;
import fr.utc.miage.transpitrack.model.jpa.FriendshipService;
import fr.utc.miage.transpitrack.model.jpa.GoalService;
import fr.utc.miage.transpitrack.model.jpa.ImageStorageService;
import fr.utc.miage.transpitrack.model.jpa.SportService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import fr.utc.miage.transpitrack.model.jpa.UserSportService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final String REDIRECT_DASHBOARD   = "redirect:/users/dashboard";
    private static final String REDIRECT_LOGIN       = "redirect:/users/formLogin";
    private static final String REDIRECT_PREFERENCES = "redirect:/users/consultationPreferences";
    private static final String REDIRECT_GOALS       = "redirect:/users/consultationGoals";
    private static final String REDIRECT_FORM_UPDATE = "redirect:/users/formUpdate";
    private static final String REDIRECT_FORM_CREATE = "redirect:/users/formCreate";
    private static final String REDIRECT_SEARCH      = "redirect:/users/search";
    private static final String VIEW_FORM_CREATE     = "users/formCreate";
    private static final String VIEW_FORM_UPDATE     = "users/formUpdate";
    private static final String VIEW_FORM_LOGIN      = "users/formLogin";
    private static final String SESSION_USER_ID      = "userId";
    private static final String ERROR_MSG            = "errorMessage";
    private static final String SUCCESS_MSG          = "successMessage";
    private static final String MSG                  = "message";
    private static final String NEEDCONNEXION        = "Il faut être connecte !";

    @Autowired
    UserService userService;

    @Autowired
    ActivityService activityService;

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    UserSportService userSportService;

    @Autowired
    BadgeService badgeService;

    @Autowired
    SportService sportService;

    @Autowired
    GoalService goalService;

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
    // Modification du profil
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/formUpdate")
    public String formUpdate(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return VIEW_FORM_UPDATE;
    }

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

        // Vérification email si changement
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

    // ──────────────────────────────────────────────────────────────
    // Profil
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            model.addAttribute(MSG, NEEDCONNEXION);
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
    // Recherche
    // ──────────────────────────────────────────────────────────────

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

        java.util.Set<Long> relatedUserIds = new java.util.HashSet<>();
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

    // ──────────────────────────────────────────────────────────────
    // Objectifs
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/consultationGoals")
    public String consultationGoals(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            model.addAttribute(MSG, NEEDCONNEXION);
            return REDIRECT_LOGIN;
        }
        User user = userService.getUserById(userId);
        model.addAttribute("goals", user.getGoals());
        model.addAttribute("sports", sportService.getAllSports());
        model.addAttribute("temporalities", Temporality.values());

        return "goals/listGoals";
    }

    @PostMapping("/addGoal")
    public String addGoal(@RequestParam("goal") String textGoal,
                          @RequestParam("targetDistance") Double distance,
                          @RequestParam("sportId") Long sportId,
                          @RequestParam("temporality") Temporality temporality,
                          HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        if (textGoal == null || distance == null) return REDIRECT_GOALS;

        User user = userService.getUserById(userId);
        Sport sport = sportService.getSportById(sportId);
        Goal goal = new Goal(distance, textGoal, user, sport, temporality);
        goalService.createGoal(goal);
        user.addGoal(goal);
        userService.updateUser(user);
        return REDIRECT_GOALS;
    }

    @PostMapping("/updateGoal")
    public String updateGoal(@RequestParam("goalId") Long goalId,
                             @RequestParam("goal") String textGoal,
                             @RequestParam("targetDistance") Double distance,
                             @RequestParam("sportId") Long sportId,
                             @RequestParam("temporality") Temporality temporality,
                             HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        if (textGoal == null || distance == null) return REDIRECT_GOALS;

        Goal goal = goalService.getGoalById(goalId);
        Sport sport = sportService.getSportById(sportId);
        User user = userService.getUserById(userId);
        user.deleteGoal(goal);
        goal.setGoalText(textGoal);
        goal.setTargetDistance(distance);
        goal.setSport(sport);
        goal.setTemporality(temporality);
        goalService.updateGoal(goal);
        user.addGoal(goal);
        userService.updateUser(user);
        return REDIRECT_GOALS;
    }

    @PostMapping("/deleteGoal")
    public String deleteGoal(@RequestParam("goalId") Long goalId, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) return REDIRECT_LOGIN;
        if (goalId == null) return REDIRECT_GOALS;

        Goal goal = goalService.getGoalById(goalId);
        goalService.deleteGoal(goal);
        User user = userService.getUserById(userId);
        user.deleteGoal(goal);
        userService.updateUser(user);
        return REDIRECT_GOALS;
    }
}
