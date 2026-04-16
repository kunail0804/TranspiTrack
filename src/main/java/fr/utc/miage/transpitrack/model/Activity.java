package fr.utc.miage.transpitrack.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing a physical activity logged by a user.
 * <p>
 * An activity records the sport performed, its duration and distance,
 * the date it took place, the user's personal evaluation, and optional
 * weather data captured at the time of recording.
 * </p>
 */
@Entity
@Table(name="activity")
public class Activity {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_seq")
    private Long id;

    /** Date on which the activity was performed. */
    private LocalDate date;

    /** Duration of the activity in minutes. */
    private int duration;

    /** Distance covered during the activity in kilometres. */
    private double distance;

    /** User's personal evaluation or note about the activity. */
    private String evaluation;

    /** City where the activity took place (used to retrieve weather data). */
    private String city;

    /** Ambient temperature in degrees Celsius at the time of the activity (nullable). */
    private Double temperature;

    /** Human-readable weather condition at the time of the activity (nullable). */
    private String weatherCondition;

    /** Sport associated with this activity. */
    @ManyToOne
    private Sport sport;

    /** User who logged this activity. */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Constructs a fully populated {@code Activity}.
     *
     * @param date       the date of the activity
     * @param duration   the duration in minutes
     * @param distance   the distance covered in kilometres
     * @param evaluation the user's personal evaluation text
     * @param sport      the sport performed
     * @param user       the user who logged the activity
     */
    public Activity(LocalDate date, int duration, double distance, String evaluation, Sport sport, User user) {
        this.date = date;
        this.duration = duration;
        this.distance = distance;
        this.evaluation = evaluation;
        this.sport = sport;
        this.user = user;
    }

    /**
     * No-argument constructor required by JPA.
     */
    public Activity() {
    }

    /**
     * Returns the unique identifier of this activity.
     *
     * @return the activity ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the date on which this activity was performed.
     *
     * @return the activity date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date on which this activity was performed.
     *
     * @param date the activity date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns the duration of this activity.
     *
     * @return the duration in minutes
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the duration of this activity.
     *
     * @param duration the duration in minutes
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Returns the distance covered during this activity.
     *
     * @return the distance in kilometres
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the distance covered during this activity.
     *
     * @param distance the distance in kilometres
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns the user's personal evaluation of this activity.
     *
     * @return the evaluation text
     */
    public String getEvaluation() {
        return evaluation;
    }

    /**
     * Sets the user's personal evaluation of this activity.
     *
     * @param evaluation the evaluation text
     */
    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    /**
     * Returns the sport performed during this activity.
     *
     * @return the associated {@link Sport}
     */
    public Sport getSport() {
        return sport;
    }

    /**
     * Sets the sport performed during this activity.
     *
     * @param sport the associated {@link Sport}
     */
    public void setSport(Sport sport) {
        this.sport = sport;
    }

    /**
     * Returns the user who logged this activity.
     *
     * @return the associated {@link User}
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who logged this activity.
     *
     * @param user the associated {@link User}
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns a placeholder calorie count of {@code 1}.
     * <p>
     * For the actual calorie calculation based on MET value, use {@link #getTotalCaloriesAct()}.
     * </p>
     *
     * @return {@code 1}
     */
    public int getTotalCalories(){
        return 1;
    }

    /**
     * Returns the city associated with this activity.
     *
     * @return the city name, or {@code null} if not set
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns the recorded temperature for this activity.
     *
     * @return the temperature in degrees Celsius, or {@code null} if not recorded
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * Returns the weather condition recorded for this activity.
     *
     * @return the weather condition string, or {@code null} if not recorded
     */
    public String getWeatherCondition() {
        return weatherCondition;
    }

    /**
     * Sets the city associated with this activity.
     *
     * @param city the city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Sets the recorded temperature for this activity.
     *
     * @param temperature the temperature in degrees Celsius
     */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    /**
     * Sets the weather condition for this activity.
     *
     * @param weatherCondition the weather condition string
     */
    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    /**
     * Calculates the total calories burned during this activity using the MET formula:
     * <pre>
     *   calories = MET × weight (kg) × duration (h)
     * </pre>
     * Returns {@code 0} if the sport or user is not set.
     *
     * @return the estimated calories burned, or {@code 0} if data is missing
     */
    public double getTotalCaloriesAct() {
        if (sport == null || user == null) return 0;
        return sport.getMetValue() * user.getWeight() * (duration / 60.0);
    }
}
