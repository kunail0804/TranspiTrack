package fr.utc.miage.transpitrack.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class SportType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sportType_seq")
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "sportType")
    private List<Sport> sports;
    
    public SportType() {
    
    }

    public SportType(Long id, String name, String description, List<Sport> sports) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sports = sports;
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

    public List<Sport> getSports() {
        return sports;
    }

    public void setSports(List<Sport> sports) {
        this.sports = sports;
    }
}




