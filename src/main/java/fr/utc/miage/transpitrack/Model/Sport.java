package fr.utc.miage.transpitrack.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    //@ManyToMany(mappedBy = "sports")
    //private List<Goal> goals;

    @OneToMany(mappedBy = "sport")
    private List<UserSport> users;

    public Sport(Long id, String name, String description, List<Goal> goals) {
        this.id = id;
        this.name = name;
        this.description = description;
        //this.goals = goals;
    }

    
}