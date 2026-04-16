package fr.utc.miage.transpitrack.model.jpa;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.enumer.FriendshipStatus;

/**
 * Service layer for managing friendships between users.
 * <p>
 * Handles sending, accepting, rejecting, and querying friend requests and
 * established friendships. A friendship is directional in the database
 * (requester → receiver), but both directions are considered when building
 * the effective friend list.
 * </p>
 */
@Service
public class FriendshipService {

    /** Repository used to persist and retrieve friendship records. */
    @Autowired
    private FriendshipRepository friendshipRepository;

    /**
     * Persists an updated friendship record.
     *
     * @param friendship the {@link Friendship} to save
     * @return the saved friendship
     */
    public Friendship updateFriendship(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    /**
     * Deletes the friendship with the given ID.
     *
     * @param id the ID of the friendship to delete
     */
    public void deleteFriendship(Long id) {
        friendshipRepository.deleteById(id);
    }

    /**
     * Returns all accepted friendships involving the given user, regardless of direction.
     *
     * @param userId the ID of the user
     * @return a list of accepted {@link Friendship} records where the user is requester or receiver
     */
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

    /**
     * Returns all pending friend requests received by the given user (not yet accepted).
     *
     * @param userId the ID of the receiver user
     * @return a list of pending inbound {@link Friendship} records
     */
    public List<Friendship> getMyPendingFriendships(Long userId) {
        return friendshipRepository.findByStatusAndReceiverId(FriendshipStatus.PENDING, userId);
    }

    /**
     * Returns all pending friend requests sent by the given user (not yet accepted).
     *
     * @param userId the ID of the requester user
     * @return a list of pending outbound {@link Friendship} records
     */
    public List<Friendship> getMySentPendingFriendships(Long userId) {
        return friendshipRepository.findByStatusAndRequesterId(FriendshipStatus.PENDING, userId);
    }

    /**
     * Returns whether any friendship or friend request already exists between the two users,
     * in either direction.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @return {@code true} if a record exists in either direction
     */
    public boolean requestOrFriendshipExists(Long userId1, Long userId2) {
        boolean isFriend1 = friendshipRepository.existsByRequesterIdAndReceiverId(userId1, userId2);
        boolean isFriend2 = friendshipRepository.existsByRequesterIdAndReceiverId(userId2, userId1);
        return isFriend1 || isFriend2;
    }

    /**
     * Sends a friend request from {@code requester} to {@code receiver}.
     * Returns {@code null} without creating a record if any friendship or request
     * already exists between the two users.
     *
     * @param requester the user sending the request
     * @param receiver  the user receiving the request
     * @return the created {@link Friendship} with status {@code PENDING},
     *         or {@code null} if a relationship already exists
     */
    public Friendship sendFriendRequest(User requester, User receiver) {
        if (requestOrFriendshipExists(requester.getId(), receiver.getId())) {
            return null;
        }
        Friendship friendship = new Friendship(requester, receiver);
        return friendshipRepository.save(friendship);
    }

    /**
     * Returns the friendship with the given ID, or {@code null} if not found.
     *
     * @param friendshipId the friendship ID
     * @return the matching {@link Friendship}, or {@code null}
     */
    public Friendship getFriendshipById(Long friendshipId) {
        return friendshipRepository.findById(friendshipId).orElse(null);
    }

    /**
     * Accepts the given friend request by setting its status to {@link FriendshipStatus#ACCEPTED}.
     *
     * @param friendship the pending {@link Friendship} to accept
     */
    public void acceptFriendRequest(Friendship friendship) {
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
    }

    /**
     * Rejects the given friend request by deleting the record from the database.
     *
     * @param friendship the pending {@link Friendship} to reject
     */
    public void rejectFriendRequest(Friendship friendship) {
        friendshipRepository.delete(friendship);
    }
}
