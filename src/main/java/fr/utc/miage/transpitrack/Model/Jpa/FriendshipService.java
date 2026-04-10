package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.Model.Friendship;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.Enum.FriendshipStatus;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    public Friendship updateFriendship(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    public void deleteFriendship(Long id) {
        friendshipRepository.deleteById(id);
    }

    public List<Friendship> getMyFriendships(Long userId) {
        List<Friendship> friendships = new ArrayList<>();

        friendships.addAll(
                friendshipRepository.findByStatusAndRequesterId(FriendshipStatus.ACCEPTED, userId)
        );

        friendships.addAll(
                friendshipRepository.findByStatusAndReceiverId(FriendshipStatus.ACCEPTED, userId)
        );

        return friendships;
    }

    public List<Friendship> getMyPendingFriendships(Long userId) {
        return friendshipRepository.findByStatusAndReceiverId(FriendshipStatus.PENDING, userId);
    }

    public boolean requestOrFriendshipExists(Long userId1, Long userId2) {
        boolean isFriend1 = friendshipRepository.existsByRequesterIdAndReceiverId(userId1, userId2);
        boolean isFriend2 = friendshipRepository.existsByRequesterIdAndReceiverId(userId2, userId1);
        return isFriend1 || isFriend2;
    }

    public Friendship sendFriendRequest(User requester, User receiver) {

        if (requestOrFriendshipExists(requester.getId(), receiver.getId())) {
            return null; // Une demande d'amitié ou une amitié existe déjà entre ces utilisateurs
        }

        Friendship friendship = new Friendship(requester, receiver);
        return friendshipRepository.save(friendship);
    }

}
