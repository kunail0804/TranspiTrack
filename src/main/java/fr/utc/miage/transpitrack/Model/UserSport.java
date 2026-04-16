package fr.utc.miage.transpitrack.model;

import fr.utc.miage.transpitrack.model.enumer.Level;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="userSport")
public class UserSport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "userSport_seq")
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Sport sport;

    @Enumerated(EnumType.STRING)
    private Level level;

    public UserSport(User user, Sport sport, Level level) {
        this.user = user;
        this.sport = sport;
        this.level = level;
    } 

    public UserSport() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Sport getSport() {
        return sport;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }   

    
}
