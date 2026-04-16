package fr.utc.miage.transpitrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.enumer.FriendshipStatus;

class FriendshipTest {

    // ── Constructeur & getters ──────────────────────────────────────

    @Test
    void constructorShouldSetAllFields() {
        User requester = new User();
        User receiver = new User();

        Friendship friendship = new Friendship(requester, receiver);

        assertEquals(requester, friendship.getRequester());
        assertEquals(receiver, friendship.getReceiver());
        assertEquals(FriendshipStatus.PENDING, friendship.getStatus());
    }

    @Test
    void getIdShouldReturnNullWhenNotPersisted() {
        assertNull(new Friendship().getId());
    }

    // ── Setters ──────────────────────────────────────

    @Test
    void setStatusShouldUpdateStatus() {
        Friendship friendship = new Friendship();
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        assertEquals(FriendshipStatus.ACCEPTED, friendship.getStatus());
    }
    
}
