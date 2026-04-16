package fr.utc.miage.transpitrack.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_seq")
    private Long id;

    private LocalDate date;
    private int duration;
    private double distance;
    private String evaluation;

    private String city;
    private Double temperature; 
    private String weatherCondition;

    @ManyToOne
    private Sport sport;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Activity(LocalDate date, int duration, double distance, String evaluation, Sport sport, User user) {
        this.date = date;
        this.duration = duration;
        this.distance = distance;
        this.evaluation = evaluation;
        this.sport = sport;
        this.user = user;
    }

    public Activity() {
        
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTotalCalories(){
        return 1;
    }

    public String getCity() {
        return city;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public double getTotalCaloriesAct() {
        if (sport == null || user == null) return 0;
        return sport.getMetValue() * user.getWeight() * (duration / 60.0);
    }

}

