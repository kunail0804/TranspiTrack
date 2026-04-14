package fr.utc.miage.transpitrack.Controller;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.Model.Challenge;
import fr.utc.miage.transpitrack.Model.Jpa.ChallengeService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.User;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/challenges")
public class ChallengeController {

    @Autowired
    ChallengeService challengeService;

    @Autowired
    UserService userService;

    @Autowired
    SportService sportService;

    @GetMapping("/formCreate")
    public String formCreateChallenge(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/formLogin";
        }
        model.addAttribute("sports", sportService.getAllSports());
        return "challenge/createChallenge";
    }

    @PostMapping("/create")
    public String createChallenge(@RequestParam("title") String title,@RequestParam("durationDays") int durationDays,@RequestParam("visibility") String visibility,@RequestParam("sportId") Long sportId,HttpSession session,Model model) {
                                      
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/formLogin";
        }

        if (durationDays < 0) {
            model.addAttribute("errorMessage", "Durée négative, veuillez insérer une durée valide");
            return "challenge/createChallenge"; 
        }

        User creator = userService.getUserById(userId);
        Sport sport = sportService.getSportById(sportId);
        Duration duration = Duration.ofDays(durationDays);

        Challenge newChallenge = new Challenge(title, visibility, duration, creator, sport);
        challengeService.createChallenge(newChallenge);

        return "redirect:/users/dashboard"; 
    }

    @GetMapping("/list")
    public String listChallenges(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/formLogin";
        }
        List<Challenge> challenges = challengeService.getAllChallenges();
        
        model.addAttribute("challenges", challenges);

        return "challenge/listChallenges"; 
    }

    @GetMapping("/details/{id}")
    public String showChallengeDetails(@PathVariable("id") Long id, HttpSession session, Model model) {
    if (session.getAttribute("userId") == null) {
        return "redirect:/users/formLogin";
    }
    Challenge challenge = challengeService.getChallengeById(id);
    model.addAttribute("challenge", challenge);
    return "challenge/detailChallenge"; 
}
}