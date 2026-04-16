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
import fr.utc.miage.transpitrack.Model.ChallengeScore;
import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.Jpa.ChallengeScoreService;
import fr.utc.miage.transpitrack.Model.Jpa.ChallengeService;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;
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

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    ChallengeScoreService challengeScoreService;

    private static final String REDIRECTFORMLOGIN = "redirect:/users/formLogin";
    private static final String REDIRECTLIST = "redirect:/challenges/list";

    @GetMapping("/formCreate")
    public String formCreateChallenge(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
        }
        model.addAttribute("sports", sportService.getAllSports());
        return "challenge/createChallenge";
    }

    @PostMapping("/create")
    public String createChallenge(@RequestParam("title") String title,@RequestParam("durationDays") int durationDays,@RequestParam("visibility") String visibility,@RequestParam("sportId") Long sportId,HttpSession session,Model model) {
                                      
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
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
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
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
         Long userId = getUserId(session);
         if (userId == null) {
            return REDIRECTFORMLOGIN;
        }

        if(idChallenge==null){
            return REDIRECTLIST;
        }

        User user = userService.getUserById(userId);

        Challenge challenge = challengeService.getChallengeById(idChallenge);

        if(user.isAlreadyJoinChallenge(challenge)){
            model.addAttribute("message", "Vous participer deja a ce challenge !");
            return REDIRECTLIST;
        }

        if(challenge.getCreator()==user){
            model.addAttribute("message", "Vous participer deja a ce challenge car vous l'avez créer !");
            return REDIRECTLIST;
        }

        user.addChallenge(challenge);
        userService.updateUser(user);

        
        model.addAttribute("user", user);
        return "challenge/testSuccessJoin";
    }
   
    @GetMapping("/details/{id}")
    public String showChallengeDetails(@PathVariable("id") Long id, HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
        }
        User currentUser = userService.getUserById(userId);
        Challenge challenge = challengeService.getChallengeById(id);

        boolean canAddScore = currentUser.isTheCreatorOfTheChallenge(challenge)
                || currentUser.isAlreadyJoinChallenge(challenge);

        ChallengeScore userScore = canAddScore
                ? challengeScoreService.getScoreByUserAndChallenge(currentUser, challenge)
                : null;
        List<ChallengeScore> classementTrie = challengeScoreService.getClassementParChallenge(id);

        model.addAttribute("challenge", challenge);
        model.addAttribute("canAddScore", canAddScore);
        model.addAttribute("userScore", userScore);
        model.addAttribute("listeScores", classementTrie);
        return "challenge/detailChallenge";
    }

    @PostMapping("/details/{id}/addScore")
    public String addScore(@PathVariable("id") Long id,
                           @RequestParam("score") double score,
                           HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return REDIRECTFORMLOGIN;
        }
        User currentUser = userService.getUserById(userId);
        Challenge challenge = challengeService.getChallengeById(id);

        boolean canAddScore = currentUser.isTheCreatorOfTheChallenge(challenge)
                || currentUser.isAlreadyJoinChallenge(challenge);

        if (canAddScore) {
            ChallengeScore existing = challengeScoreService.getScoreByUserAndChallenge(currentUser, challenge);
            if (existing != null) {
                existing.setScore(score);
                challengeScoreService.addScore(existing);
            } else {
                challengeScoreService.addScore(new ChallengeScore(currentUser, challenge, score));
            }
        }
        return "redirect:/challenges/details/" + id;
    }

    public Long getUserId(HttpSession session){
        return (Long) session.getAttribute("userId");
    }
}