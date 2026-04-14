package fr.utc.miage.transpitrack.Model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Badge badge;

    private LocalDate earnedAt;

    public UserBadge() {}

    public UserBadge(User user, Badge badge, LocalDate earnedAt) {
        this.user = user;
        this.badge = badge;
        this.earnedAt = earnedAt;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Badge getBadge() {
        return badge;
    }

    public LocalDate getEarnedAt() {
        return earnedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public void setEarnedAt(LocalDate earnedAt) {
        this.earnedAt = earnedAt;
    }
}
