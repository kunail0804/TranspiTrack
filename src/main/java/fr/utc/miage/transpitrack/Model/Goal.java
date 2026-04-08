package fr.utc.miage.transpitrack.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sport_seq")
    private Long id;

    private Double targetDistance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Goal(Long id, Double targetDistance, User user) {
        this.id = id;
        this.targetDistance = targetDistance;
        this.user = user;
    }

    
}