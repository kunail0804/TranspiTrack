package fr.utc.miage.transpitrack.Model;

import java.time.Duration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "challenge_seq")
    private Long id;

    private String title;
    private String visibility;
    private Duration duration;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    public Challenge() {}

    public Challenge(String title, String visibility, Duration duration, User creator) {
        this.title = title;
        this.visibility = visibility;
        this.duration = duration;
        this.creator = creator;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getVisibility() { return visibility; }
    public Duration getDuration() { return duration; }
    public User getCreator() { return creator; }

    public void setTitle(String title) { this.title = title; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public void setDuration(Duration duration) { this.duration = duration; }
    public void setCreator(User creator) { this.creator = creator; }
}


