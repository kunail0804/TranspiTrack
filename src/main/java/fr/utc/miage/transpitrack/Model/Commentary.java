package fr.utc.miage.transpitrack.model;

import fr.utc.miage.transpitrack.model.enumer.ReactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="commentary", uniqueConstraints = @UniqueConstraint(columnNames = {"author_id", "activity_id"}))
public class Commentary {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commentary_seq")
    private Long id;

    private String message; // nullable

    @Enumerated(EnumType.STRING)
    private ReactionType reaction; // nullable

    @ManyToOne
    private User author;

    @OneToOne
    private Activity activity;

    public Commentary() {
    }

    public Commentary(String message, ReactionType reaction, User author, Activity activity) {
        this.message = message;
        this.reaction = reaction;
        this.author = author;
        this.activity = activity;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public ReactionType getReaction() {
        return reaction;
    }

    public void setReaction(ReactionType reaction) {
        this.reaction = reaction;
    }

    public User getAuthor() {
        return author;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}