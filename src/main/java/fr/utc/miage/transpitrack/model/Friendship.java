package fr.utc.miage.transpitrack.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import fr.utc.miage.transpitrack.model.enumer.FriendshipStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * JPA entity representing a friend request or an established friendship between two users.
 * <p>
 * When created, the status is set to {@link FriendshipStatus#PENDING}. The receiver can then
 * accept (changing the status to {@link FriendshipStatus#ACCEPTED}) or reject (deleting
 * the record).
 * </p>
 */
@Entity
public class Friendship {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who initiated the friend request. */
    @ManyToOne
    private User requester;

    /** The user who received the friend request. */
    @ManyToOne
    private User receiver;

    /** Current status of the friendship ({@code PENDING} or {@code ACCEPTED}). */
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    /**
     * No-argument constructor required by JPA.
     */
    public Friendship() {}

    /**
     * Constructs a new friend request from {@code requester} to {@code receiver}.
     * The initial status is {@link FriendshipStatus#PENDING}.
     *
     * @param requester the user sending the request
     * @param receiver  the user receiving the request
     */
    public Friendship(User requester, User receiver) {
        this.requester = requester;
        this.receiver = receiver;
        this.status = FriendshipStatus.PENDING;
    }

    /**
     * Returns the unique identifier of this friendship.
     *
     * @return the friendship ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the user who initiated the friend request.
     *
     * @return the requester {@link User}
     */
    public User getRequester() {
        return requester;
    }

    /**
     * Returns the user who received the friend request.
     *
     * @return the receiver {@link User}
     */
    public User getReceiver() {
        return receiver;
    }

    /**
     * Returns the current status of this friendship.
     *
     * @return {@link FriendshipStatus#PENDING} or {@link FriendshipStatus#ACCEPTED}
     */
    public FriendshipStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of this friendship.
     *
     * @param status the new {@link FriendshipStatus}
     */
    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}
