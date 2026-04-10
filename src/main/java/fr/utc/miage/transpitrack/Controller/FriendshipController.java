package fr.utc.miage.transpitrack.Controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/addFriend/{id}")
    public String addFriend(@PathVariable(value="id") Long friendId, HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);

        if (user == null) {
            return "redirect:/users/login?msg=Vous devez etre connecte pour ajouter un ami";
        }

        if (friendId == null) {
            return "redirect:/users/profile/" + friendId + "?msg=ID de l'ami est requis";
        }

        if(Objects.equals(friendId, userId)) {
            return "redirect:/users/profile/" + friendId + "?msg=Vous ne pouvez pas vous ajouter en tant qu'ami";
        }

        User friend = userService.getUserById(friendId);
        Friendship createdFriendship = friendshipService.sendFriendRequest(user, friend);

        if (createdFriendship == null) {
            return "redirect:/users/profile/" + friendId + "?msg=Erreur lors de la creation de la demande d'amitie";
        }

        return "redirect:/users/profile/" + friendId + "?msg=Demande d'amitie envoyee avec succes";

    }

}
