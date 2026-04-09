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

    private String city;

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
                String password, int age, double height, Gender gender, double weight, String city){

        this.firstName =firstName;
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.weight=weight;
        this.city = city;

    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public double getHeight() {
        return height;
    }

    public String getCity() {
        return city;
    }

    public Gender getGender() {
        return gender;
    }

    public double getWeight() {
        return weight;
    }

    public List<UserSport> getSportsPreference() {
        return sportsPreference;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public List<User> getFriends() {
        return friends;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public List<Commentary> getComments() {
        return comments;
    }

    public List<Challenge> getCreatedChallenges() {
        return createdChallenges;
    }

    public List<Challenge> getJoinedChallenges() {
        return joinedChallenges;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public void setId(Long id){
        this.id=id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
