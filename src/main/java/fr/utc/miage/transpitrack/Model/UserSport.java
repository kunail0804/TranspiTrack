package fr.utc.miage.transpitrack.Model;

import fr.utc.miage.transpitrack.Model.Enum.Level;
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
