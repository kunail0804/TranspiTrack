package fr.utc.miage.transpitrack.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.Model.Enum.Gender;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.User;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

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

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$")) {
            model.addAttribute("message", "Email n'est pas au bon format");
            return "users/formCreate";
        }

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

        User newUser = new User(firstName, name, email, encoder.encode(password), age, height, Gender.valueOf(gender), weight, city);

        User savedUser = userService.createUser(newUser);

        session.setAttribute("userId", savedUser.getId());

        model.addAttribute("message", "Création compte réussie");

        //TODO : à modifier à l'avenir quand la page sera définie
        return "users/dashboard";
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
        return "users/dashboard";
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
            return "users/formLogin";
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return "users/formLogin";
        }
        model.addAttribute("user", user);
        return "users/profile";
    }
}
