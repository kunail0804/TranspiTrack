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
    private String needConnexion = "Il faut être connecte !";

    private final String redirectFormLogin = "redirect:/users/formLogin";
    private final String redirectFormUpdate = "redirect:/users/formUpdate";
    private final String redirectFormCreate = "redirect:/users/formCreate";
    private final String redirectDashboard = "redirect:/users/dashboard";
    private final String redirectConsultationPreferences = "redirect:/users/consultationPreferences";
    private final String redirectConsultationGoals = "redirect:/users/consultationGoals";

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/formCreate")
    public String formCreate(Model model,
                             HttpSession session) {

        Long userId = getUserId(session);

        if (userId != null) {
            return "users/dashboard";
        }
        model.addAttribute("message", message);
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
            return redirectWithMessage(message, redirectFormCreate, model);
        }
        if (height < 0) {
            message = "Taille ne peut pas être négatif";
            return redirectWithMessage(message, redirectFormCreate, model);
        }

        if (weight < 0) {
            message = "Poids ne peut pas être négatif";
            return redirectWithMessage(message, redirectFormCreate, model);
        }

        User userExist = userService.getUserByEmail(email);

        if (userExist != null) {
            message = "email dejas existant";
            return redirectWithMessage(message, redirectFormCreate, model);
        }

        try {
            User newUser = new User(firstName, name, email, encoder.encode(password), age, height, Gender.valueOf(gender), weight, city);

            String filename = imageStorageService.store(profileImageFile);
            newUser.setProfileImage(filename);

            User savedUser = userService.createUser(newUser);
            session.setAttribute("userId", savedUser.getId());
        } catch (IOException e) {
            message = "Erreur lors de l'upload de l'image";
            model.addAttribute("message", message);
            return "users/formCreate";
        } catch (Exception e) {
            message = "Email invalide";
            return redirectWithMessage(message, redirectFormCreate, model);
        }

        message = "Création compte réussie";
        model.addAttribute("message", message);
        message = "";
        
        return redirectDashboard;
    }


    @GetMapping("/formUpdate")
    public String formUpdate(Model model, 
                             HttpSession session){

        Long userId = getUserId(session);

        if(userId==null){
            message = needConnexion;
            return redirectWithMessage(message, redirectFormLogin, model);
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

        if(age<0){
            message = "Age ne peut pas être négatif";
            return redirectWithMessage(message, redirectFormUpdate, model);
        }
        if(height<0){
            message = "Taille ne peut pas être négatif";
            return redirectWithMessage(message, redirectFormUpdate, model);
        }

        if(weight<0){
            message = "Poids ne peut pas être négatif";
            return redirectWithMessage(message, redirectFormUpdate, model);
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
                return redirectWithMessage(message, redirectFormUpdate, model);
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
        } catch (IOException e) {
            message = "Erreur lors de l'upload de l'image";
            model.addAttribute("message", message);
            return "users/formUpdate";
        } catch (Exception e) {
            message = "Email invalide";
            return redirectWithMessage(message, redirectFormUpdate, model);
        }
        message = "Modification du compte réussie";

        model.addAttribute("message", message);


        return redirectDashboard;
    }

    @GetMapping("/formLogin")
    public String formLogin(Model model,
                            HttpSession session) {

        Long userId = getUserId(session);

        if (userId != null) {
            return "users/dashboard";
        }
        model.addAttribute("message", message);
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
            return redirectWithMessage(message, redirectFormLogin, model);
        }

        boolean isValid = encoder.matches(password, userLogin.getPassword());

        if (!isValid) {
            message = "email ou mots de passe incorrect";
            return redirectWithMessage(message, redirectFormLogin, model);
        }

        session.setAttribute("userId", userLogin.getId());

        message = "Connexion compte réussie";
        model.addAttribute("message", message);

        return redirectDashboard;
    }

    @GetMapping("/search")
    public String searchUser(@RequestParam(required = false) String query,
                             Model model,
                             HttpSession session) {

        Long userId = getUserId(session);
        if (userId == null) {
            message = needConnexion;
            return redirectWithMessage(message, redirectFormLogin, model);
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
        return redirectFormLogin;
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            message = needConnexion;
            return redirectWithMessage(message, redirectFormLogin, model);
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            message = needConnexion;
            return redirectWithMessage(message, redirectFormLogin, model);
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
        return redirectFormLogin;
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
            message = needConnexion;
            return redirectWithMessage(message, redirectFormLogin, model);
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
            message = needConnexion;
            return redirectWithMessage(message, redirectFormLogin, model);
        }

        if(sportId == null || level == null){
            return redirectWithMessage(message, redirectConsultationPreferences, model);
        }

        Sport sport = sportService.getSportById(sportId);

        User user = userService.getUserById(userId);
       
        UserSport userSportExist = userSportService.getUserSportByUserAndSport(user, sport);
        if(userSportExist!=null){
            message = "Ce sport est dejas dans votre liste !";
            return redirectWithMessage(message, redirectConsultationPreferences, model);
        }
        

        UserSport userSport = new UserSport(user, sport, level);
        userSportService.createUserSport(userSport);
        user.addPreference(userSport);
        userService.updateUser(user);

        return redirectConsultationPreferences;
    }

     @PostMapping("/updateLevel")
    public String updateLevel(@RequestParam("userSport") Long userSportId,
                              @RequestParam("level") Level level,
                              Model model,
                              HttpSession session){

        Long userId = getUserId(session);
        if (userId == null) {
            message = needConnexion;
            return redirectWithMessage(message, redirectFormLogin, model);
        }

         if(userSportId == null || level == null){
            return redirectWithMessage(message, redirectConsultationPreferences, model);
        }

        UserSport userSport = userSportService.getUserSportById(userSportId);

        User user = userService.getUserById(userId);
        user.deletePreference(userSport);

        userSport.setLevel(level);
        userSportService.updateUserSport(userSport);

        user.addPreference(userSport);
        userService.updateUser(user);

        return redirectConsultationPreferences;
    }

    @PostMapping("/deletePreference")
    public String deletePreference(@RequestParam("userSport") Long userSportId,
                                   Model model,
                                   HttpSession session){
        Long userId = getUserId(session);
        if (userId == null) {
            message = needConnexion;
            return redirectWithMessage(message, redirectFormLogin, model);
        }

        if(userSportId == null){
            message = "UserSport non trouvé";
            return redirectWithMessage(message, redirectConsultationPreferences, model);
        }
        UserSport userSport = userSportService.getUserSportById(userSportId);

        userSportService.deleteUserSport(userSport);

        User user = userService.getUserById(userId);
        user.deletePreference(userSport);
        userService.updateUser(user);

        return redirectConsultationPreferences;
    }


    @GetMapping("/consultationGoals")
    public String consultationGoals(Model model,
                                          HttpSession session){
        Long userId = getUserId(session);
        if (userId == null) {
            message = needConnexion;
            return redirectWithMessage(message, redirectFormLogin, model);
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
            return redirectWithMessage(needConnexion, redirectFormLogin, model);
        }

        if(textGoal == null || distance == null){
            return redirectWithMessage(message, redirectConsultationGoals, model);
        }

        User user = userService.getUserById(userId);

        Goal goal = new Goal(distance, textGoal, user);
        
        goalService.createGoal(goal);
        user.addGoal(goal);
        userService.updateUser(user);

        return redirectConsultationGoals;
    }

     @PostMapping("/updateGoal")
    public String updateGoal(@RequestParam("goalId") Long goalId,
                             @RequestParam("goal") String textGoal,
                             @RequestParam("targetDistance") Double distance,
                             Model model,
                             HttpSession session){

        Long userId = getUserId(session);
        if (userId == null) {
            return redirectWithMessage(needConnexion, redirectFormLogin, model);
        }

        if(textGoal == null || distance == null){
            return redirectWithMessage(message, redirectConsultationGoals, model);
        }


        Goal goal = goalService.getGoalById(goalId);

        User user = userService.getUserById(userId);
        user.deleteGoal(goal);

        goal.setGoalText(textGoal);
        goal.setTargetDistance(distance);
        goalService.updateGoal(goal);

        user.addGoal(goal);
        userService.updateUser(user);

        return redirectConsultationGoals;
    }

    @PostMapping("/deleteGoal")
    public String deleteGoal(@RequestParam("goalId") Long goalId,
                                   Model model,
                                   HttpSession session){
        Long userId = getUserId(session);
        if (userId == null) {
            return redirectWithMessage(needConnexion, redirectFormLogin, model);
        }

        if(goalId == null){
            message = "Goal non trouvé";
            return redirectWithMessage(message, redirectConsultationPreferences, model);
        }
        Goal goal = goalService.getGoalById(goalId);

        goalService.deleteGoal(goal);

        User user = userService.getUserById(userId);
        user.deleteGoal(goal);
        userService.updateUser(user);

        return redirectConsultationGoals;
    }

    public Long getUserId(HttpSession session){
        return (Long) session.getAttribute("userId");
    }

    private String redirectWithMessage(String message, String redirectUrl, Model model) {
        model.addAttribute("message", message);
        return redirectUrl;
    }
}
