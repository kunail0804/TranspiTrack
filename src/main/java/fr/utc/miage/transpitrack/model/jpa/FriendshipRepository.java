package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.enumer.FriendshipStatus;

/**
 * Spring Data JPA repository for {@link Friendship} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus queries
 * for retrieving, filtering, and checking the existence of friendship records.
 * </p>
 */
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    /**
     * Returns all friendships where the given user is the requester.
     *
     * @param requesterId the ID of the requester user
     * @return a list of friendships initiated by that user
     */
    List<Friendship> findByRequesterId(Long requesterId);

    /**
     * Returns all friendships where the given user is the receiver.
     *
     * @param receiverId the ID of the receiver user
     * @return a list of friendships received by that user
     */
    List<Friendship> findByReceiverId(Long receiverId);

    /**
     * Returns all friendships with the given status where the user is the requester.
     *
     * @param status      the friendship status to filter on
     * @param requesterId the ID of the requester user
     * @return a list of matching friendships
     */
    List<Friendship> findByStatusAndRequesterId(FriendshipStatus status, Long requesterId);

    /**
     * Returns all friendships with the given status where the user is the receiver.
     *
     * @param status     the friendship status to filter on
     * @param receiverId the ID of the receiver user
     * @return a list of matching friendships
     */
    List<Friendship> findByStatusAndReceiverId(FriendshipStatus status, Long receiverId);

    /**
     * Returns the friendship from the given requester to the given receiver, or {@code null}.
     *
     * @param requesterId the ID of the requester user
     * @param receiverId  the ID of the receiver user
     * @return the matching {@link Friendship}, or {@code null}
     */
    Friendship findByRequesterIdAndReceiverId(Long requesterId, Long receiverId);

    /**
     * Returns whether a friendship or friend request already exists from requester to receiver.
     *
     * @param requesterId the ID of the requester user
     * @param receiverId  the ID of the receiver user
     * @return {@code true} if such a record exists
     */
    boolean existsByRequesterIdAndReceiverId(Long requesterId, Long receiverId);

    /**
     * Returns whether a friendship with the given status exists between requester and receiver.
     *
     * @param requesterId the ID of the requester user
     * @param receiverId  the ID of the receiver user
     * @param status      the friendship status to check
     * @return {@code true} if a matching record exists
     */
    boolean existsByRequesterIdAndReceiverIdAndStatus(Long requesterId, Long receiverId, FriendshipStatus status);
}
