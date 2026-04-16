package fr.utc.miage.transpitrack.Model;

import fr.utc.miage.transpitrack.Model.Enum.Temporality;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @ManyToOne
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    @Enumerated(EnumType.STRING)
    private Temporality temporality;

    public Goal(){}

    public Goal(Double targetDistance, String goalText, User user) {
        this.targetDistance = targetDistance;
        this.user = user;
        this.goalText = goalText;
    }


    public Goal(Double targetDistance, String goalText, User user, Sport sport, Temporality temporality) {
        this.targetDistance = targetDistance;
        this.goalText = goalText;
        this.user = user;
        this.sport = sport;
        this.temporality = temporality;
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

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public void setTemporality(Temporality temporality) {
        this.temporality = temporality;
    }

    public Sport getSport() {
        return sport;
    }

    public Temporality getTemporality() {
        return temporality;
    }   
    
    
}