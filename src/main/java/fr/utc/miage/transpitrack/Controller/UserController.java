package fr.utc.miage.transpitrack.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

import fr.utc.miage.transpitrack.Model.Enum.Gender;


@Controller
@RequestMapping("/user")
public class UserController {
   
    @Autowired
    UserService userService;

    @GetMapping("/formCreate")
    public String formCreate(@RequestParam(required=false) String message,
        Model model, 
        HttpSession session){

        String email = (String) session.getAttribute("userEmail");

        if(email!=null){
            //TODO : à modifier à l'avenir quand la page sera définie
           return "dashboard";
        }
        model.addAttribute("message", message);

        return "formCreate";
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

        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$")){
            model.addAttribute("message", "Email n'est pas au bon format");
            return "formCreate";
        }
                                
        if(age<0){
            model.addAttribute("message", "Age ne peut pas être négatif");
            return "formCreate";
        }

        if(height<0){
            model.addAttribute("message", "Taille ne peut pas être négatif");
            return "formCreate";
        }

        if(weight<0){
            model.addAttribute("message", "Poids ne peut pas être négatif");
            return "formCreate";
        }

        User userExist = userService.getUserByEmail(email);

        if(userExist!=null){
            model.addAttribute("message", "email dejas existant");
            return "formCreate";
        }

        User newUser = new User(firstName, name, email, password, age, height, Gender.valueOf(gender), weight, city); 

        userService.createUser(newUser);

        session.setAttribute("userEmail", email);

        model.addAttribute("message", "Création compte réussie");

        //TODO : à modifier à l'avenir quand la page sera définie
        return "dashboard";
    }
    
}
