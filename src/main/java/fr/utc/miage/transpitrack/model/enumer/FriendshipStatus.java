package fr.utc.miage.transpitrack.model.enumer;

/**
 * Represents the current state of a {@link fr.utc.miage.transpitrack.model.Friendship}
 * between two users.
 */
public enum FriendshipStatus {

    /** The friend request has been sent but not yet accepted. */
    PENDING,

    /** The friend request has been accepted; both users are now friends. */
    ACCEPTED
}
