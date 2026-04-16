package fr.utc.miage.transpitrack.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.jpa.FriendshipService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import jakarta.servlet.http.HttpSession;

/**
 * Spring MVC controller handling friend requests under {@code /users/friends}.
 * <p>
 * Allows authenticated users to send friend requests, view incoming and outgoing
 * pending requests, and accept or reject incoming requests.
 * </p>
 */
@Controller
@RequestMapping("/users/friends")
public class FriendshipController {

    /** Service for friendship CRUD operations. */
    @Autowired
    private FriendshipService friendshipService;

    /** Service for user retrieval. */
    @Autowired
    private UserService userService;

    /** Redirect prefix for the profile page of a specific user. */
    private static final String REDIRECTPROFILE = "redirect:/users/profile/";

    /**
     * Sends a friend request from the currently authenticated user to the user with
     * the given ID.
     * <p>
     * Redirects with an error message if the user tries to add themselves, or if a
     * friendship or request already exists.
     * </p>
     *
     * @param friendId the ID of the user to send a request to
     * @param session  the current HTTP session
     * @param model    the Spring MVC model (unused but required by Spring MVC)
     * @return a redirect to the target user's profile page with a status message
     */
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
            return REDIRECTPROFILE + friendId + "?msg=ID de l'ami est requis";
        }

        if (Objects.equals(friendId, userId)) {
            return REDIRECTPROFILE + friendId + "?msg=Vous ne pouvez pas vous ajouter en tant qu'ami";
        }

        User friend = userService.getUserById(friendId);
        Friendship createdFriendship = friendshipService.sendFriendRequest(user, friend);

        if (createdFriendship == null) {
            return REDIRECTPROFILE + friendId + "?msg=Erreur lors de la creation de la demande d'amitie";
        }

        return REDIRECTPROFILE + friendId + "?msg=Demande d'amitie envoyee avec succes";
    }

    /**
     * Displays all pending friend requests received by and sent by the authenticated user.
     *
     * @param msg     optional status message to display (may be {@code null})
     * @param session the current HTTP session
     * @param model   the Spring MVC model
     * @return the {@code users/friendInvites} view, or a redirect to login
     */
    @GetMapping("/invites")
    public String showInvites(@RequestParam(required=false) String msg, HttpSession session, Model model) {
        Long userId = getUserId(session);

        if (userId == null) {
            return "redirect:/users/login?msg=Vous devez etre connecte pour voir vos invitations";
        }

        model.addAttribute("friendInvites", friendshipService.getMyPendingFriendships(userId));
        model.addAttribute("sentInvites", friendshipService.getMySentPendingFriendships(userId));
        model.addAttribute("msg", msg);
        return "users/friendInvites";
    }

    /**
     * Accepts a pending friend request received by the authenticated user.
     * The friendship status is updated to {@code ACCEPTED}.
     *
     * @param friendshipId the ID of the {@link Friendship} record to accept
     * @param session      the current HTTP session
     * @return a redirect to the invites page with a status message
     */
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

    /**
     * Rejects a pending friend request by deleting the friendship record.
     *
     * @param friendshipId the ID of the {@link Friendship} record to reject
     * @param session      the current HTTP session
     * @return a redirect to the invites page with a status message
     */
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

    /**
     * Returns the ID of the currently authenticated user from the session,
     * or {@code null} if not logged in.
     *
     * @param session the current HTTP session
     * @return the user ID, or {@code null}
     */
    public Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }
}
