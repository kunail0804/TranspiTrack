package fr.utc.miage.transpitrack.Model;

import java.util.logging.Level;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;

@Entity
public class UserSport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    
}
