package fr.utc.miage.transpitrack.Controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.Jpa.FriendshipService;
import fr.utc.miage.transpitrack.Model.Jpa.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users/friends")
public class FriendshipController {
    
    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserService userService;

    @PostMapping("/addFriend")
    public String addFriend(@RequestParam("friendId") Long friendId, HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);

        if (user == null) {
            return "redirect:/users/login?error=Vous devez être connecté pour ajouter un ami";
        }

        if (friendId == null) {
            return "redirect:/users/profile/" + friendId + "?error=ID de l'ami est requis";
        }

        if(Objects.equals(friendId, userId)) {
            return "redirect:/users/profile/" + friendId + "?error=Vous ne pouvez pas vous ajouter en tant qu'ami";
        }

        User friend = userService.getUserById(friendId);
        Friendship createdFriendship = friendshipService.sendFriendRequest(user, friend);

        if (createdFriendship == null) {
            return "redirect:/users/profile/" + friendId + "?error=Erreur lors de la création de la demande d'amitié";
        }

        return "redirect:/users/profile/" + friendId + "?success=Demande d'amitié envoyée avec succès";

    }

}
