package fr.utc.miage.transpitrack.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import fr.utc.miage.transpitrack.Model.Enum.FriendshipStatus;

@Entity
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User requester;

    @ManyToOne
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    public Friendship() {}

    public Friendship(User requester, User receiver) {
        this.requester = requester;
        this.receiver = receiver;
        this.status = FriendshipStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public User getRequester() {
        return requester;
    }

    public User getReceiver() {
        return receiver;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}
