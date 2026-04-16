package fr.utc.miage.transpitrack.Controller;

import fr.utc.miage.transpitrack.Model.Jpa.UserSportService;
import fr.utc.miage.transpitrack.Service.ImageStorageService;

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

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.Enum.Gender;
import fr.utc.miage.transpitrack.Model.Enum.Level;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.Goal;

import fr.utc.miage.transpitrack.Model.Jpa.BadgeService;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;

import fr.utc.miage.transpitrack.Model.Jpa.SportService;

import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.Jpa.GoalService;
import fr.utc.miage.transpitrack.Model.UserSport;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final String REDIRECT_DASHBOARD   = "redirect:/users/dashboard";
    private static final String REDIRECT_LOGIN       = "redirect:/users/formLogin";
    private static final String REDIRECT_PREFERENCES = "redirect:/users/consultationPreferences";
    private static final String REDIRECT_GOALS       = "redirect:/users/consultationGoals";
    private static final String VIEW_FORM_CREATE     = "users/formCreate";
    private static final String VIEW_FORM_UPDATE     = "users/formUpdate";
    private static final String VIEW_FORM_LOGIN      = "users/formLogin";
    private static final String SESSION_USER_ID      = "userId";
    private static final String ERROR_MSG            = "errorMessage";
    private static final String SUCCESS_MSG          = "successMessage";

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

    @GetMapping("/formCreate")
    public String formCreate(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId != null) {
            return REDIRECT_DASHBOARD;
        }
        return "users/formCreate";
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
            model.addAttribute(ERROR_MSG, "L'âge ne peut pas être négatif.");
            return "users/formCreate";
        }
        if (height < 0) {
            model.addAttribute(ERROR_MSG, "La taille ne peut pas être négative.");
            return "users/formCreate";
        }
        if (weight < 0) {
            model.addAttribute(ERROR_MSG, "Le poids ne peut pas être négatif.");
            return "users/formCreate";
        }
        if (userService.getUserByEmail(email) != null) {
            model.addAttribute(ERROR_MSG, "Cette adresse email est déjà utilisée.");
            return "users/formCreate";
        }

        try {
            User newUser = new User(firstName, name, email, encoder.encode(password), age, height, Gender.valueOf(gender), weight, city);
            String filename = imageStorageService.store(profileImageFile);
            newUser.setProfileImage(filename);
            User savedUser = userService.createUser(newUser);
            session.setAttribute(SESSION_USER_ID, savedUser.getId());
        } catch (IOException e) {
            model.addAttribute(ERROR_MSG, "Erreur lors de l'upload de la photo de profil.");
            return "users/formCreate";
        } catch (Exception e) {
            model.addAttribute(ERROR_MSG, "Email invalide.");
            return "users/formCreate";
        }

        redirectAttrs.addFlashAttribute(SUCCESS_MSG, "Compte créé avec succès ! Bienvenue sur TranspiTrack.");
        return REDIRECT_DASHBOARD;
    }


    @GetMapping("/formUpdate")
    public String formUpdate(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        model.addAttribute("user", userService.getUserById(userId));
        return "users/formUpdate";
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
            model.addAttribute(ERROR_MSG, "L'âge ne peut pas être négatif.");
            model.addAttribute("user", actualUser);
            return "users/formUpdate";
        }
        if (height < 0) {
            model.addAttribute(ERROR_MSG, "La taille ne peut pas être négative.");
            model.addAttribute("user", actualUser);
            return "users/formUpdate";
        }
        if (weight < 0) {
            model.addAttribute(ERROR_MSG, "Le poids ne peut pas être négatif.");
            model.addAttribute("user", actualUser);
            return "users/formUpdate";
        }

        if (!actualUser.getEmail().equals(email)) {
            if (userService.getUserByEmail(email) != null) {
                model.addAttribute(ERROR_MSG, "Cette adresse email est déjà utilisée.");
                model.addAttribute("user", actualUser);
                return "users/formUpdate";
            }
            actualUser.setEmail(email);
        }

        actualUser.setName(name);
        actualUser.setFirstName(firstName);
        actualUser.setAge(age);
        actualUser.setGender(Gender.valueOf(gender));
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
        } catch (IOException e) {
            model.addAttribute(ERROR_MSG, "Erreur lors de l'upload de la photo de profil.");
            model.addAttribute("user", actualUser);
            return "users/formUpdate";
        } catch (Exception e) {
            model.addAttribute(ERROR_MSG, "Email invalide.");
            model.addAttribute("user", actualUser);
            return "users/formUpdate";
        }

        redirectAttrs.addFlashAttribute(SUCCESS_MSG, "Profil mis à jour avec succès !");
        return REDIRECT_DASHBOARD;
    }

    @PostMapping("/deleteProfileImage")
    public String deleteProfileImage(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        User user = userService.getUserById(userId);
        imageStorageService.delete(user.getProfileImage());
        user.setProfileImage(null);
        userService.updateUser(user);
        return "redirect:/users/formUpdate";
    }

    @GetMapping("/formLogin")
    public String formLogin(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId != null) {
            return REDIRECT_DASHBOARD;
        }
        return "users/formLogin";
    }

    @PostMapping("/loginUser")
    public String loginUser(@RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        User userLogin = userService.getUserByEmail(email);

        if (userLogin == null || !encoder.matches(password, userLogin.getPassword())) {
            model.addAttribute(ERROR_MSG, "Email ou mot de passe incorrect.");
            return "users/formLogin";
        }

        session.setAttribute(SESSION_USER_ID, userLogin.getId());
        redirectAttrs.addFlashAttribute(SUCCESS_MSG, "Bienvenue, " + userLogin.getFirstName() + " !");
        return REDIRECT_DASHBOARD;
    }

    @GetMapping("/search")
    public String searchUser(@RequestParam(required = false) String query,
                             Model model,
                             HttpSession session) {

        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }

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

    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        session.invalidate();
        return "users/formLogin";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return REDIRECT_LOGIN;
        }

        List<Activity> activities = activityService.getActivitiesByUserId(userId);
        List<Friendship> friendships = friendshipService.getMyFriendships(userId);
        List<User> friends = friendships.stream().map(f -> f.getRequester().getId().equals(userId) ? f.getReceiver() : f.getRequester()).toList();
        int pendingFriendships = friendshipService.getMyPendingFriendships(userId).size();

        model.addAttribute("user", user);
        activities.sort((a1, a2) -> a2.getDate().compareTo(a1.getDate()));
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
        if (currentUserId == null) {
            return REDIRECT_LOGIN;
        }

        User profileUser = userService.getUserById(profileId);
        if (profileUser == null) {
            return "redirect:/users/search";
        }

        List<Activity> userActivities = activityService.getActivitiesByUserId(profileId);
        boolean isOwner = currentUserId.equals(profileId);
        boolean requestSent = friendshipService.requestOrFriendshipExists(currentUserId, profileId);

        model.addAttribute("user", profileUser);
        model.addAttribute("activities", userActivities);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("requestSent", requestSent);
        model.addAttribute("userBadges", badgeService.getUserBadges(profileUser));

        return "users/profile";
    }

    @GetMapping("/consultationPreferences")
    public String consultationPreferences(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
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
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        if (sportId == null || level == null) {
            return "redirect:/users/consultationPreferences";
        }

        Sport sport = sportService.getSportById(sportId);
        User user = userService.getUserById(userId);

        if (userSportService.getUserSportByUserAndSport(user, sport) != null) {
            redirectAttrs.addFlashAttribute(ERROR_MSG, "Ce sport est déjà dans votre liste !");
            return "redirect:/users/consultationPreferences";
        }

        UserSport userSport = new UserSport(user, sport, level);
        userSportService.createUserSport(userSport);
        user.addPreference(userSport);
        userService.updateUser(user);

        return "redirect:/users/consultationPreferences";
    }

    @PostMapping("/updateLevel")
    public String updateLevel(@RequestParam("userSport") Long userSportId,
                              @RequestParam("level") Level level,
                              HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        if (userSportId == null || level == null) {
            return "redirect:/users/consultationPreferences";
        }

        UserSport userSport = userSportService.getUserSportById(userSportId);
        User user = userService.getUserById(userId);
        user.deletePreference(userSport);
        userSport.setLevel(level);
        userSportService.updateUserSport(userSport);
        user.addPreference(userSport);
        userService.updateUser(user);

        return "redirect:/users/consultationPreferences";
    }

    @PostMapping("/deletePreference")
    public String deletePreference(@RequestParam("userSport") Long userSportId, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        if (userSportId == null) {
            return "redirect:/users/consultationPreferences";
        }

        UserSport userSport = userSportService.getUserSportById(userSportId);
        userSportService.deleteUserSport(userSport);
        User user = userService.getUserById(userId);
        user.deletePreference(userSport);
        userService.updateUser(user);

        return "redirect:/users/consultationPreferences";
    }

    @GetMapping("/consultationGoals")
    public String consultationGoals(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        User user = userService.getUserById(userId);
        model.addAttribute("goals", user.getGoals());
        return "goals/listGoals";
    }

    @PostMapping("/addGoal")
    public String addGoal(@RequestParam("goal") String textGoal,
                          @RequestParam("targetDistance") Double distance,
                          HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        if (textGoal == null || distance == null) {
            return "redirect:/users/consultationGoals";
        }

        User user = userService.getUserById(userId);
        Goal goal = new Goal(distance, textGoal, user);
        goalService.createGoal(goal);
        user.addGoal(goal);
        userService.updateUser(user);

        return "redirect:/users/consultationGoals";
    }

    @PostMapping("/updateGoal")
    public String updateGoal(@RequestParam("goalId") Long goalId,
                             @RequestParam("goal") String textGoal,
                             @RequestParam("targetDistance") Double distance,
                             HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        if (textGoal == null || distance == null) {
            return "redirect:/users/consultationGoals";
        }

        Goal goal = goalService.getGoalById(goalId);
        User user = userService.getUserById(userId);
        user.deleteGoal(goal);
        goal.setGoalText(textGoal);
        goal.setTargetDistance(distance);
        goalService.updateGoal(goal);
        user.addGoal(goal);
        userService.updateUser(user);

        return "redirect:/users/consultationGoals";
    }

    @PostMapping("/deleteGoal")
    public String deleteGoal(@RequestParam("goalId") Long goalId, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        if (goalId == null) {
            return "redirect:/users/consultationGoals";
        }

        Goal goal = goalService.getGoalById(goalId);
        goalService.deleteGoal(goal);
        User user = userService.getUserById(userId);
        user.deleteGoal(goal);
        userService.updateUser(user);

        return "redirect:/users/consultationGoals";
    }
}
