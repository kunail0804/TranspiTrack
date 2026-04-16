package fr.utc.miage.transpitrack.Model;

import java.util.List;
import java.util.ArrayList;

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
    private double metValue;

    @OneToMany(mappedBy = "sport")
    private List<UserSport> users = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "sport_type_id", nullable = false)
    private SportType sportType;

    public Sport() {
    }

    public Sport(String name, String description){
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserSport> getUsers() {
        return users;
    }

    public void setUsers(List<UserSport> users) {
        this.users = users;
    }

    public double getMetValue() {
        return metValue;
    }

    public void setMetValue(double metValue) {
        this.metValue = metValue;
    }

    public SportType getSportType() {
        return sportType;
    }

    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }


    
}