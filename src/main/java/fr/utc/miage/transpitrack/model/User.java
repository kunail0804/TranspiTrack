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

/**
 * JPA entity representing an application user.
 * <p>
 * A user holds personal information (name, age, weight, city, etc.), authentication
 * credentials (email + BCrypt-hashed password), and relationships to their activities,
 * goals, sport preferences, badges, and challenges.
 * </p>
 */
@Entity
@Table(name="user")
public class User {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;

    /** User's first name. */
    private String firstName;

    /** User's last name. */
    private String name;

    /** User's email address, used as the login identifier. Must be a valid email format. */
    @Email
    private String email;

    /** BCrypt-hashed password. Never stored in plain text. */
    private String password;

    /** User's age in years. */
    private int age;

    /** User's height in centimetres. */
    private double height;

    /** City where the user is based (used for weather lookups). */
    private String city;

    /** User's biological gender. */
    @Enumerated(EnumType.STRING)
    private Gender gender;

    /** User's weight in kilograms (used in calorie calculations). */
    private double weight;

    /** Filename of the user's profile picture, or {@code null} for the placeholder. */
    private String profileImage;

    /** Sports the user has declared as preferences, each with a proficiency level. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserSport> sportsPreference = new ArrayList<>();

    /** Bidirectional many-to-many friendship list (legacy; friendships are tracked via {@link Friendship}). */
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends = new ArrayList<>();

    /** Training goals created by this user. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Goal> goals = new ArrayList<>();

    /** Commentaries written by this user on activities. */
    @OneToMany(mappedBy = "author")
    private List<Commentary> comments = new ArrayList<>();

    /** Challenges created by this user. */
    @OneToMany(mappedBy = "creator")
    private List<Challenge> createdChallenges = new ArrayList<>();

    /** Challenges this user has joined (but did not create). */
    @ManyToMany
    private List<Challenge> joinedChallenges = new ArrayList<>();

    /**
     * No-argument constructor required by JPA.
     */
    public User(){
        // This constructor is intentionally empty
    }

    /**
     * Returns the unique identifier of this user.
     *
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the user's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the user's last name.
     *
     * @return the last name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user's BCrypt-hashed password.
     *
     * @return the hashed password string
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user's age.
     *
     * @return the age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the user's height.
     *
     * @return the height in centimetres
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the user's city.
     *
     * @return the city name, or {@code null} if not set
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns the user's gender.
     *
     * @return the {@link Gender}
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Returns the user's weight.
     *
     * @return the weight in kilograms
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the list of sport preferences declared by this user.
     *
     * @return the list of {@link UserSport} entries
     */
    public List<UserSport> getSportsPreference() {
        return sportsPreference;
    }

    /**
     * Returns the user's friend list (legacy association table).
     *
     * @return the list of friend {@link User} entries
     */
    public List<User> getFriends() {
        return friends;
    }

    /**
     * Returns the list of training goals created by this user.
     *
     * @return the list of {@link Goal} entries
     */
    public List<Goal> getGoals() {
        return goals;
    }

    /**
     * Returns the commentaries written by this user.
     *
     * @return the list of {@link Commentary} entries
     */
    public List<Commentary> getComments() {
        return comments;
    }

    /**
     * Returns the challenges created by this user.
     *
     * @return the list of created {@link Challenge} entries
     */
    public List<Challenge> getCreatedChallenges() {
        return createdChallenges;
    }

    /**
     * Returns the challenges this user has joined (not created).
     *
     * @return the list of joined {@link Challenge} entries
     */
    public List<Challenge> getJoinedChallenges() {
        return joinedChallenges;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the user's last name.
     *
     * @param name the last name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's BCrypt-hashed password.
     *
     * @param password the hashed password string
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the user's age.
     *
     * @param age the age in years
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets the user's height.
     *
     * @param height the height in centimetres
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Sets the user's city.
     *
     * @param city the city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Sets the user's gender.
     *
     * @param gender the {@link Gender}
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Sets the user's weight.
     *
     * @param weight the weight in kilograms
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Returns the filename of the user's profile image.
     *
     * @return the image filename, or {@code null} if using the placeholder
     */
    public String getProfileImage() {
        return profileImage;
    }

    /**
     * Sets the filename of the user's profile image.
     *
     * @param profileImage the image filename, or {@code null}
     */
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * Adds a goal to this user's goal list.
     *
     * @param goal the {@link Goal} to add
     */
    public void addGoal(Goal goal){
        this.goals.add(goal);
    }

    /**
     * Removes a goal from this user's goal list.
     *
     * @param goal the {@link Goal} to remove
     */
    public void deleteGoal(Goal goal){
        this.goals.remove(goal);
    }

    /**
     * Adds a sport preference to this user's preference list.
     *
     * @param userSport the {@link UserSport} preference to add
     */
    public void addPreference(UserSport userSport){
        this.sportsPreference.add(userSport);
    }

    /**
     * Removes a sport preference from this user's preference list.
     *
     * @param userSport the {@link UserSport} preference to remove
     */
    public void deletePreference(UserSport userSport){
        this.sportsPreference.remove(userSport);
    }

    /**
     * Adds a challenge to this user's list of joined challenges.
     *
     * @param challenge the {@link Challenge} to join
     */
    public void addChallenge(Challenge challenge){
        this.joinedChallenges.add(challenge);
    }

    /**
     * Returns whether this user has already joined the given challenge.
     *
     * @param challenge the {@link Challenge} to check
     * @return {@code true} if the user has joined the challenge
     */
    public boolean isAlreadyJoinChallenge(Challenge challenge){
        return joinedChallenges.contains(challenge);
    }

    /**
     * Returns whether this user is the creator of the given challenge.
     *
     * @param challenge the {@link Challenge} to check
     * @return {@code true} if the user created the challenge
     */
    public boolean isTheCreatorOfTheChallenge(Challenge challenge){
        return createdChallenges.contains(challenge);
    }
}
