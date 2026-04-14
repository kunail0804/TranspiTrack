package fr.utc.miage.transpitrack.Controller;

import fr.utc.miage.transpitrack.Model.Jpa.UserSportService;

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

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.Enum.Gender;
import fr.utc.miage.transpitrack.Model.Enum.Level;
import fr.utc.miage.transpitrack.Model.Jpa.ActivityService;
import fr.utc.miage.transpitrack.Model.User;

import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;

import fr.utc.miage.transpitrack.Model.Jpa.SportService;

import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Sport;
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
    SportService sportService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/formCreate")
    public String formCreate(@RequestParam(required = false) String message,
            Model model,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            //TODO : à modifier à l'avenir quand la page sera définie
            return "users/dashboard";
        }
        model.addAttribute("message", message);

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
            Model model,
            HttpSession session) {

        if (age < 0) {
            model.addAttribute("message", "Age ne peut pas être négatif");
            return "users/formCreate";
        }
        if (height < 0) {
            model.addAttribute("message", "Taille ne peut pas être négatif");
            return "users/formCreate";
        }

        if (weight < 0) {
            model.addAttribute("message", "Poids ne peut pas être négatif");
            return "users/formCreate";
        }

        User userExist = userService.getUserByEmail(email);

        if (userExist != null) {
            model.addAttribute("message", "email dejas existant");
            return "users/formCreate";
        }

        try{
            User newUser = new User(firstName, name, email, encoder.encode(password), age, height, Gender.valueOf(gender), weight, city);

            User savedUser = userService.createUser(newUser);
            session.setAttribute("userId", savedUser.getId());
        } catch (Exception e) {
            model.addAttribute("message", "Email invalide");
            return "users/formCreate";
        }

        model.addAttribute("message", "Création compte réussie");

        //TODO : à modifier à l'avenir quand la page sera définie
        return "redirect:/users/dashboard";
    }


    @GetMapping("/formUpdate")
    public String formUpdate(@RequestParam(required = false) String message,
        Model model, 
        HttpSession session){

        Long userId = (Long) session.getAttribute("userId");

        if(userId==null){
            return "users/formLogin";
        }
        User user = userService.getUserById(userId);
        model.addAttribute("message", message);
        model.addAttribute("user", user);

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
                            Model model,
                            HttpSession session) {
                                
        if(age<0){
            model.addAttribute("message", "Age ne peut pas être négatif");
            return "users/formUpdate";
        }
        if(height<0){
            model.addAttribute("message", "Taille ne peut pas être négatif");
            return "users/formUpdate";
        }

        if(weight<0){
            model.addAttribute("message", "Poids ne peut pas être négatif");
            return "users/formUpdate";
        }

        Long actualUserId = (Long) session.getAttribute("userId");

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
                model.addAttribute("message", "email déja existant");
                return "users/formUpdate";
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
        try{
            userService.updateUser(actualUser);
        } catch (Exception e) {
            model.addAttribute("message", "Email invalide");
            return "users/formUpdate";
        }
        model.addAttribute("message", "Modification du compte réussie");

        //TODO : à modifier à l'avenir quand la page "profil" sera définie
        return "redirect:/users/dashboard";
    }


    @GetMapping("/formLogin")
    public String formLogin(@RequestParam(required = false) String message,
            Model model,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            //TODO : à modifier à l'avenir quand la page sera définie
            return "users/dashboard";
        }
        model.addAttribute("message", message);

        return "users/formLogin";
    }

    @PostMapping("/loginUser")
    public String loginUser(@RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model,
            HttpSession session) {

        User userLogin = userService.getUserByEmail(email);

        if (userLogin == null) {
            model.addAttribute("message", "email ou mots de passe incorrect");
            return "users/formLogin";
        }

        boolean isValid = encoder.matches(password, userLogin.getPassword());

        if (!isValid) {
            model.addAttribute("message", "email ou mots de passe incorrect");
            return "users/formLogin";
        }

        session.setAttribute("userId", userLogin.getId());

        model.addAttribute("message", "Connexion compte réussie");

        //TODO : à modifier à l'avenir quand la page sera définie
        return "redirect:/users/dashboard";
    }

    @GetMapping("/search")
    public String searchUser(@RequestParam(required = false) String query,
                             Model model,
                             HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/formLogin";
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
        return "users/formLogin";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("message", "Il faut êtres connecter !");
            return "users/formLogin";
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return "users/formLogin";
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

        return "users/profile";
    }


    @GetMapping("/profile/{id}")
    public String viewProfile(@PathVariable("id") Long profileId, @RequestParam(required = false) String msg, Model model, HttpSession session) {
    
    Long currentUserId = (Long) session.getAttribute("userId");
    if (currentUserId == null) {
        return "redirect:/users/formLogin";
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

    return "users/profile";
}

    @GetMapping("/consultationPreferences")
    public String consultationPreferences(Model model,
                                          HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("message", "Il faut êtres connecter !");
            return "users/formLogin";
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
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("message", "Il faut êtres connecter !");
            return "users/formLogin";
        }

        if(sportId == null || level == null){
            return "redirect:/users/consultationPreferences";
        }

        Sport sport = sportService.getSportById(sportId);

        User user = userService.getUserById(userId);
       
        UserSport userSportExist = userSportService.getUserSportByUserAndSport(user, sport);
        if(userSportExist!=null){
            model.addAttribute("message", "Ce sport est dejas dans votre liste !");
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
                              Model model,
                              HttpSession session){

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("message", "Il faut êtres connecter !");
            return "users/formLogin";
        }

         if(userSportId == null || level == null){
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
    public String deletePreference(@RequestParam("userSport") Long userSportId,
                                   Model model,
                                   HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("message", "Il faut êtres connecter !");
            return "users/formLogin";
        }

        if(userSportId == null){
            return "redirect:/users/consultationPreferences";
        }
        UserSport userSport = userSportService.getUserSportById(userSportId);

        userSportService.deleteUserSport(userSport);

        User user = userService.getUserById(userId);
        user.deletePreference(userSport);
        userService.updateUser(user);

        return "redirect:/users/consultationPreferences";
    }
}
