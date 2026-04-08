package fr.utc.miage.transpitrack.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sport_seq")
    private Long id;

    private String name;
    private String description;

    //@ManyToMany(mappedBy = "sports")
    //private List<Goal> goals;

    @OneToMany(mappedBy = "sport")
    private List<UserSport> users;

    @ManyToOne
    @JoinColumn(name = "sport_type_id", nullable = false)
    private SportType sportType;

    public Sport(Long id, String name, String description, List<Goal> goals) {
        this.id = id;
        this.name = name;
        this.description = description;
        //this.goals = goals;
    }

    
}