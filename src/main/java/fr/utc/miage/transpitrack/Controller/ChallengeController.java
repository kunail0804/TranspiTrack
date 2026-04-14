package fr.utc.miage.transpitrack.Controller;

import java.time.Duration;
import java.util.ArrayList;
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
import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.Jpa.ChallengeService;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
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

    @Autowired
    FriendshipService friendshipService;

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

        List<Friendship> friendships = friendshipService.getMyFriendships(userId);
        List<User> friends = friendships.stream().map(f -> f.getRequester().getId().equals(userId) ? f.getReceiver() : f.getRequester()).toList();
        List<Challenge> friendsChallenges = new ArrayList<>();
        for(User friend : friends){
            for(Challenge challenge : friend.getCreatedChallenges()){
                friendsChallenges.add(challenge);
            }
        }
        List<Challenge> challenges = challengeService.getChallengesByVisibility("PUBLIC");
        
        model.addAttribute("challenges", challenges);
        model.addAttribute("friendsChallenges", friendsChallenges);

        return "challenge/listChallenges"; 
    }

    @PostMapping("/joinChallenge")
    public String getMethodName(@RequestParam("challengeId") Long idChallenge,
                                Model model,
                                HttpSession session) {
         Long userId = (Long) session.getAttribute("userId");
         if (userId == null) {
            return "redirect:/users/formLogin";
        }

        if(idChallenge==null){
            return "redirect:/challenges/list";
        }

        User user = userService.getUserById(userId);

        Challenge challenge = challengeService.getChallengeById(idChallenge);

        if(user.isAlreadyJoinChallenge(challenge)){
            model.addAttribute("message", "Vous participer deja a ce challenge !");
            return"redirect:/challenges/list";
        }

        if(challenge.getCreator()==user){
            model.addAttribute("message", "Vous participer deja a ce challenge car vous l'avez créer !");
            return"redirect:/challenges/list";
        }

        user.addChallenge(challenge);
        userService.updateUser(user);

        
        model.addAttribute("user", user);
        return "challenge/testSuccessJoin";
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