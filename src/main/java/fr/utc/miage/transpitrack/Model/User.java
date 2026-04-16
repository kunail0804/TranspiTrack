package fr.utc.miage.transpitrack.model;

import java.util.ArrayList;
import java.util.List;

import fr.utc.miage.transpitrack.model.enumer.Gender;
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
import jakarta.validation.constraints.Email;

@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;

    private String firstName;
    private String name;

    @Email
    private String email;
    
    private String password;
    private int age;
    private double height;
    private String city;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private double weight;

    private String profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserSport> sportsPreference = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Goal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Commentary> comments = new ArrayList<>();

    @OneToMany(mappedBy = "creator")
    private List<Challenge> createdChallenges = new ArrayList<>();

    @ManyToMany
    private List<Challenge> joinedChallenges = new ArrayList<>();

    public User(){
        // Cette méthode est intentionnellement vide
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void addGoal(Goal goal){
        this.goals.add(goal);
    }

    public void deleteGoal(Goal goal){
        this.goals.remove(goal);
    }
    public void addPreference(UserSport userSport){
        this.sportsPreference.add(userSport);
    }

    public void deletePreference(UserSport userSport){
        this.sportsPreference.remove(userSport);
    }

    public void addChallenge(Challenge challenge){
        this.joinedChallenges.add(challenge);
    }

    public boolean isAlreadyJoinChallenge(Challenge challenge){
        return joinedChallenges.contains(challenge);
    }

    public boolean isTheCreatorOfTheChallenge(Challenge challenge){
        return createdChallenges.contains(challenge);
    }

}
