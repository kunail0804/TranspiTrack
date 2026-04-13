package fr.utc.miage.transpitrack.Controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.Model.Challenge;
import fr.utc.miage.transpitrack.Model.Jpa.ChallengeService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.User;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/challenges")
public class ChallengeController {

    @Autowired
    ChallengeService challengeService;

    @Autowired
    UserService userService;

    @GetMapping("/formCreate")
    public String formCreateChallenge(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/formLogin";
        }
        return "challenge/createChallenge";
    }

    @PostMapping("/create")
    public String createChallenge(@RequestParam("title") String title,@RequestParam("durationDays") int durationDays,@RequestParam("visibility") String visibility,HttpSession session,Model model) {
                                      
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/formLogin";
        }

        if (durationDays < 0) {
            model.addAttribute("errorMessage", "Durée négative, veuillez insérer une durée valide");
            return "challenge/createChallenge"; 
        }

        User creator = userService.getUserById(userId);

        Duration duration = Duration.ofDays(durationDays);

        Challenge newChallenge = new Challenge(title, visibility, duration, creator);
        challengeService.createChallenge(newChallenge);

        return "redirect:/users/dashboard"; 
    }
}