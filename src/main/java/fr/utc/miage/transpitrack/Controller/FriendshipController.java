package fr.utc.miage.transpitrack.Controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import fr.utc.miage.transpitrack.Model.User;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users/friends")
public class FriendshipController {
    
    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserService userService;

    private final String redirectProfile = "redirect:/users/profile/";

    @GetMapping("/addFriend/{id}")
    public String addFriend(@PathVariable(value="id") Long friendId, HttpSession session, Model model) {

        Long userId = getUserId(session);

        if (userId == null) {
            return "redirect:/users/login?msg=Vous devez etre connecte pour ajouter un ami";
        }

        User user = userService.getUserById(userId);

        if (user == null) {
            return "redirect:/users/login?msg=Vous devez etre connecte pour ajouter un ami";
        }

        if (friendId == null) {
            return redirectProfile + friendId + "?msg=ID de l'ami est requis";
        }

        if(Objects.equals(friendId, userId)) {
            return redirectProfile + friendId + "?msg=Vous ne pouvez pas vous ajouter en tant qu'ami";
        }

        User friend = userService.getUserById(friendId);
        Friendship createdFriendship = friendshipService.sendFriendRequest(user, friend);

        if (createdFriendship == null) {
            return redirectProfile + friendId + "?msg=Erreur lors de la creation de la demande d'amitie";
        }

        return redirectProfile + friendId + "?msg=Demande d'amitie envoyee avec succes";
    }

    @GetMapping("/invites")
    public String showInvites(@RequestParam(required=false) String msg, HttpSession session, Model model){
        Long userId = getUserId(session);

        if (userId == null) {
            return "redirect:/users/login?msg=Vous devez etre connecte pour voir vos invitations";
        }

        model.addAttribute("friendInvites", friendshipService.getMyPendingFriendships(userId));
        model.addAttribute("msg", msg);
        return "users/friendInvites";
    }

    @PostMapping("/accept/{id}")
    public String acceptInvite(@PathVariable(value="id") Long friendshipId, HttpSession session) {
        Long userId = getUserId(session);

        if (userId == null) {
            return "redirect:/users/login?msg=Vous devez etre connecte pour accepter une invitation";
        }

        Friendship friendship = friendshipService.getFriendshipById(friendshipId);

        if (friendship == null || !friendship.getReceiver().getId().equals(userId)) {
            return "redirect:/users/friends/invites?msg=Invitation non trouvee";
        }

        friendshipService.acceptFriendRequest(friendship);

        return "redirect:/users/friends/invites?msg=Vous etes maintenant amis avec " + friendship.getRequester().getName();
    }

    @PostMapping("/reject/{id}")
    public String refuseInvite(@PathVariable(value="id") Long friendshipId, HttpSession session) {
        Long userId = getUserId(session);

        if (userId == null) {
            return "redirect:/users/login?msg=Vous devez etre connecte pour refuser une invitation";
        }

        Friendship friendship = friendshipService.getFriendshipById(friendshipId);

        if (friendship == null) {
            return "redirect:/users/friends/invites?msg=Invitation non trouvee";
        }

        friendshipService.rejectFriendRequest(friendship);

        return "redirect:/users/friends/invites?msg=Invitation refusee";
    }

    public Long getUserId(HttpSession session){
        return (Long) session.getAttribute("userId");
    }

}
