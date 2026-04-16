package fr.utc.miage.transpitrack.Controller;

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

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Enum.Gender;
import fr.utc.miage.transpitrack.Model.Enum.Level;
import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.Goal;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.Jpa.BadgeService;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;
import fr.utc.miage.transpitrack.Model.Jpa.GoalService;
import fr.utc.miage.transpitrack.Model.Jpa.ImageStorageService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Jpa.UserSportService;
import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.UserSport;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

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

    private String message = "";
    private static final String NEEDCONNEXION = "Il faut être connecte !";

    private static final String REDIRECTFORMLOGIN = "redirect:/users/formLogin";
    private static final String REDIRECTFORMUPDATE = "redirect:/users/formUpdate";
    private static final String REDIRECTFORMCREATE = "redirect:/users/formCreate";
    private static final String REDIRECTDASHBOARD = "redirect:/users/dashboard";
    private static final String REDIRECTCONSULTATIONPREFERENCES = "redirect:/users/consultationPreferences";
    private static final String REDIRECTCONSULTATIONGOALS = "redirect:/users/consultationGoals";

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/formCreate")
    public String formCreate(Model model,
                             HttpSession session) {

        Long userId = getUserId(session);

        if (userId != null) {
            return "users/dashboard";
        }
        model.addAttribute(getViewAttributes(), message);
        message = "";
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
            HttpSession session) {

        if (age < 0) {
            message = "Age ne peut pas être négatif";
            return redirectWithMessage(message, REDIRECTFORMCREATE, model);
        }
        if (height < 0) {
            message = "Taille ne peut pas être négatif";
            return redirectWithMessage(message, REDIRECTFORMCREATE, model);
        }

        if (weight < 0) {
            message = "Poids ne peut pas être négatif";
            return redirectWithMessage(message, REDIRECTFORMCREATE, model);
        }

        User userExist = userService.getUserByEmail(email);

        if (userExist != null) {
            message = "email dejas existant";
            return redirectWithMessage(message, REDIRECTFORMCREATE, model);
        }

        try {
            User newUser = new User(firstName, name, email, encoder.encode(password), age, height, Gender.valueOf(gender), weight, city);

            String filename = imageStorageService.store(profileImageFile);
            newUser.setProfileImage(filename);

            User savedUser = userService.createUser(newUser);
            setSession(session, savedUser.getId());
        } catch (IOException _) {
            message = "Erreur lors de l'upload de l'image";
            model.addAttribute(getViewAttributes(), message);
            return "users/formCreate";
        } catch (Exception _) {
            message = "Email invalide";
            return redirectWithMessage(message, REDIRECTFORMCREATE, model);
        }

        message = "Création compte réussie";
        model.addAttribute(getViewAttributes(), message);
        message = "";
        
        return REDIRECTDASHBOARD;
    }


    @GetMapping("/formUpdate")
    public String formUpdate(Model model, 
                             HttpSession session){

        Long userId = getUserId(session);

        if(userId==null){
            message = NEEDCONNEXION;
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        message = "";

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
                            HttpSession session) {

        String validationError = validateInputs(age, height, weight);
        if (validationError != null) {
            return redirectWithMessage(validationError, REDIRECTFORMUPDATE, model);
        }

        Long actualUserId = getUserId(session);

        User actualUser = userService.getUserById(actualUserId);

        if(!actualUser.getEmail().equals(email)){
            User userExist = userService.getUserByEmail(email);

            if(userExist==null){
                actualUser.setName(name);
                actualUser.setFirstName(firstName);
                actualUser.setAge(age);
                actualUser.setGender(Gender.valueOf(gender));
                actualUser.setEmail(email);
                if(!password.isBlank()){
                    actualUser.setPassword(encoder.encode(password));
                }
                actualUser.setHeight(height);
                actualUser.setWeight(weight);
                actualUser.setCity(city);
            }else{
                message = "email déja existant";
                return redirectWithMessage(message, REDIRECTFORMUPDATE, model);
            }
        }else{
            actualUser.setName(name);
            actualUser.setFirstName(firstName);
            actualUser.setAge(age);
            actualUser.setGender(Gender.valueOf(gender));
            if(!password.isBlank()){
                actualUser.setPassword(encoder.encode(password));
            }
            actualUser.setHeight(height);
            actualUser.setWeight(weight);
            actualUser.setCity(city);
        }

        try {
            String newFilename = imageStorageService.store(profileImageFile);
            if (newFilename != null) {
                imageStorageService.delete(actualUser.getProfileImage());
                actualUser.setProfileImage(newFilename);
            }
            userService.updateUser(actualUser);
        } catch (IOException _) {
            message = "Erreur lors de l'upload de l'image";
            model.addAttribute(getViewAttributes(), message);
            return "users/formUpdate";
        } catch (Exception _) {
            message = "Email invalide";
            return redirectWithMessage(message, REDIRECTFORMUPDATE, model);
        }
        message = "Modification du compte réussie";

        model.addAttribute(getViewAttributes(), message);


        return REDIRECTDASHBOARD;
    }

    @GetMapping("/formLogin")
    public String formLogin(Model model,
                            HttpSession session) {

        Long userId = getUserId(session);

        if (userId != null) {
            return "users/dashboard";
        }
        model.addAttribute(getViewAttributes(), message);
        message = "";

        return "users/formLogin";
    }

    @PostMapping("/loginUser")
    public String loginUser(@RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model,
            HttpSession session) {

        User userLogin = userService.getUserByEmail(email);

        if (userLogin == null) {
            message = "email ou mots de passe incorrect";
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }

        boolean isValid = encoder.matches(password, userLogin.getPassword());

        if (!isValid) {
            message = "email ou mots de passe incorrect";
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }

        setSession(session, userLogin.getId());

        message = "Connexion compte réussie";
        model.addAttribute(getViewAttributes(), message);

        return REDIRECTDASHBOARD;
    }

    @GetMapping("/search")
    public String searchUser(@RequestParam(required = false) String query,
                             Model model,
                             HttpSession session) {

        Long userId = getUserId(session);
        if (userId == null) {
            message = NEEDCONNEXION;
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }

        if (query != null && !query.isBlank()) {
            model.addAttribute("users", userService.searchUsers(query));
        } else {
            model.addAttribute("users", List.of());
        }

        model.addAttribute("query", query);
        return "search/searchUser";
    }

    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        session.invalidate();
        return REDIRECTFORMLOGIN;
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            message = NEEDCONNEXION;
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            message = NEEDCONNEXION;
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
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
    public String viewProfile(@PathVariable("id") Long profileId, @RequestParam(required = false) String msg, Model model, HttpSession session) {
    
    Long currentUserId = getUserId(session);
    if (currentUserId == null) {
        return REDIRECTFORMLOGIN;
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
    model.addAttribute("msg", msg);
    model.addAttribute("userBadges", badgeService.getUserBadges(profileUser));

    return "users/profile";
}

    @GetMapping("/consultationPreferences")
    public String consultationPreferences(Model model,
                                          HttpSession session){
        Long userId = getUserId(session);
        if (userId == null) {
            message = NEEDCONNEXION;
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }
        User user = userService.getUserById(userId);
        List<Sport> sports = sportService.getAllSports();

        model.addAttribute("sportsPreference", user.getSportsPreference());
        model.addAttribute("sports", sports);
        return "users/listPreferences";
    }

    @PostMapping("/addPreference")
    public String addPreference(@RequestParam("sport") Long sportId,
                                @RequestParam("level") Level level,
                                Model model,
                                HttpSession session){
        Long userId = getUserId(session);
        if (userId == null) {
            message = NEEDCONNEXION;
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }

        if(sportId == null || level == null){
            return redirectWithMessage(message, REDIRECTCONSULTATIONPREFERENCES, model);
        }

        Sport sport = sportService.getSportById(sportId);

        User user = userService.getUserById(userId);
       
        UserSport userSportExist = userSportService.getUserSportByUserAndSport(user, sport);
        if(userSportExist!=null){
            message = "Ce sport est dejas dans votre liste !";
            return redirectWithMessage(message, REDIRECTCONSULTATIONPREFERENCES, model);
        }
        

        UserSport userSport = new UserSport(user, sport, level);
        userSportService.createUserSport(userSport);
        user.addPreference(userSport);
        userService.updateUser(user);

        return REDIRECTCONSULTATIONPREFERENCES;
    }

     @PostMapping("/updateLevel")
    public String updateLevel(@RequestParam("userSport") Long userSportId,
                              @RequestParam("level") Level level,
                              Model model,
                              HttpSession session){

        Long userId = getUserId(session);
        if (userId == null) {
            message = NEEDCONNEXION;
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }

         if(userSportId == null || level == null){
            return redirectWithMessage(message, REDIRECTCONSULTATIONPREFERENCES, model);
        }

        UserSport userSport = userSportService.getUserSportById(userSportId);

        User user = userService.getUserById(userId);
        user.deletePreference(userSport);

        userSport.setLevel(level);
        userSportService.updateUserSport(userSport);

        user.addPreference(userSport);
        userService.updateUser(user);

        return REDIRECTCONSULTATIONPREFERENCES;
    }

    @PostMapping("/deletePreference")
    public String deletePreference(@RequestParam("userSport") Long userSportId,
                                   Model model,
                                   HttpSession session){
        Long userId = getUserId(session);
        if (userId == null) {
            message = NEEDCONNEXION;
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }

        if(userSportId == null){
            message = "UserSport non trouvé";
            return redirectWithMessage(message, REDIRECTCONSULTATIONPREFERENCES, model);
        }
        UserSport userSport = userSportService.getUserSportById(userSportId);

        userSportService.deleteUserSport(userSport);

        User user = userService.getUserById(userId);
        user.deletePreference(userSport);
        userService.updateUser(user);

        return REDIRECTCONSULTATIONPREFERENCES;
    }


    @GetMapping("/consultationGoals")
    public String consultationGoals(Model model,
                                          HttpSession session){
        Long userId = getUserId(session);
        if (userId == null) {
            message = NEEDCONNEXION;
            return redirectWithMessage(message, REDIRECTFORMLOGIN, model);
        }
        User user = userService.getUserById(userId);

        model.addAttribute("goals", user.getGoals());
        return "goals/listGoals";
    }

    @PostMapping("/addGoal")
    public String addGoal(@RequestParam("goal") String textGoal,
                          @RequestParam("targetDistance") Double distance,
                          Model model,
                          HttpSession session){
        Long userId = getUserId(session);
        if (userId == null) {
            return redirectWithMessage(NEEDCONNEXION, REDIRECTFORMLOGIN, model);
        }

        if(textGoal == null || distance == null){
            return redirectWithMessage(message, REDIRECTCONSULTATIONGOALS, model);
        }

        User user = userService.getUserById(userId);

        Goal goal = new Goal(distance, textGoal, user);
        
        goalService.createGoal(goal);
        user.addGoal(goal);
        userService.updateUser(user);

        return REDIRECTCONSULTATIONGOALS;
    }

     @PostMapping("/updateGoal")
    public String updateGoal(@RequestParam("goalId") Long goalId,
                             @RequestParam("goal") String textGoal,
                             @RequestParam("targetDistance") Double distance,
                             Model model,
                             HttpSession session){

        Long userId = getUserId(session);
        if (userId == null) {
            return redirectWithMessage(NEEDCONNEXION, REDIRECTFORMLOGIN, model);
        }

        if(textGoal == null || distance == null){
            return redirectWithMessage(message, REDIRECTCONSULTATIONGOALS, model);
        }


        Goal goal = goalService.getGoalById(goalId);

        User user = userService.getUserById(userId);
        user.deleteGoal(goal);

        goal.setGoalText(textGoal);
        goal.setTargetDistance(distance);
        goalService.updateGoal(goal);

        user.addGoal(goal);
        userService.updateUser(user);

        return REDIRECTCONSULTATIONGOALS;
    }

    @PostMapping("/deleteGoal")
    public String deleteGoal(@RequestParam("goalId") Long goalId,
                                   Model model,
                                   HttpSession session){
        Long userId = getUserId(session);
        if (userId == null) {
            return redirectWithMessage(NEEDCONNEXION, REDIRECTFORMLOGIN, model);
        }

        if(goalId == null){
            message = "Goal non trouvé";
            return redirectWithMessage(message, REDIRECTCONSULTATIONPREFERENCES, model);
        }
        Goal goal = goalService.getGoalById(goalId);

        goalService.deleteGoal(goal);

        User user = userService.getUserById(userId);
        user.deleteGoal(goal);
        userService.updateUser(user);

        return REDIRECTCONSULTATIONGOALS;
    }

    public Long getUserId(HttpSession session){
        return (Long) session.getAttribute("userId");
    }

    private String redirectWithMessage(String message, String redirectUrl, Model model) {
        model.addAttribute("message", message);
        return redirectUrl;
    }

    @PostMapping("/deleteProfileImage")
    public String deleteProfileImage(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/users/formLogin";
        }
        User user = userService.getUserById(userId);
        imageStorageService.delete(user.getProfileImage());
        user.setProfileImage(null);
        userService.updateUser(user);
        return "redirect:/users/formUpdate";
    }

    public String getViewAttributes() {
        return "message";
    }

    public void setSession(HttpSession session, Long id){
        session.setAttribute("userId", id);
    }

    private String validateInputs(int age, double height, double weight) {
        if (age < 0) return "Age ne peut pas être négatif";
        if (height < 0) return "Taille ne peut pas être négatif";
        if (weight < 0) return "Poids ne peut pas être négatif";
        return null;
    }
}
