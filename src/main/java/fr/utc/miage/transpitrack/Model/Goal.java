package fr.utc.miage.transpitrack.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "goal_seq")
    private Long id;

    private Double targetDistance;

    private String goalText;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Goal(){}

    public Goal(Double targetDistance, String goalText, User user) {
        this.targetDistance = targetDistance;
        this.user = user;
        this.goalText = goalText;
    }

    public Double getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(Double targetDistance) {
        this.targetDistance = targetDistance;
    }

    public String getGoalText() {
        return goalText;
    }

    public void setGoalText(String goalText) {
        this.goalText = goalText;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }    
}