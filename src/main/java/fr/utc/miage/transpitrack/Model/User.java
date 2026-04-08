package fr.utc.miage.transpitrack.Model;

import java.util.List;

import fr.utc.miage.transpitrack.Model.Enum.Gender;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;

    private String firstName;
    private String name;
    private String email;
    private String password;
    private int age;
    private double height;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private double weight;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserSport> sportsPreference;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Activity> activities;

    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Goal> goals;

    @OneToMany(mappedBy = "author")
    private List<Commentary> comments;

    @OneToMany(mappedBy = "creator")
    private List<Challenge> createdChallenges;

    @ManyToMany
    private List<Challenge> joinedChallenges;




    public User(){}

    public User(String firstName, String name, String email,
                String password, int age, double height, Gender gender, double weight){

        this.firstName =firstName;
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.weight=weight;

    }

}
