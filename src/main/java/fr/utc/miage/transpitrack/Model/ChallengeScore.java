package fr.utc.miage.transpitrack.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "challenge_score")
public class ChallengeScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "challenge_score_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    private double score;

    public ChallengeScore() {}

    public ChallengeScore(User user, Challenge challenge, double score) {
        this.user = user;
        this.challenge = challenge;
        this.score = score;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Challenge getChallenge() { return challenge; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}
