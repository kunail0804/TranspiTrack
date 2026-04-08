package fr.utc.miage.transpitrack.Model;

import java.time.LocalDate;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.date);
        hash = 41 * hash + Objects.hashCode(this.duration);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.distance) ^ (Double.doubleToLongBits(this.distance) >>> 32));
        hash = 41 * hash + Objects.hashCode(this.evaluation);
        hash = 41 * hash + Objects.hashCode(this.sport);
        hash = 41 * hash + Objects.hashCode(this.user);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Activity other = (Activity) obj;
        if (Double.doubleToLongBits(this.distance) != Double.doubleToLongBits(other.distance)) {
            return false;
        }
        if (!Objects.equals(this.evaluation, other.evaluation)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.duration, other.duration)) {
            return false;
        }
        if (!Objects.equals(this.sport, other.sport)) {
            return false;
        }
        return Objects.equals(this.user, other.user);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Activity{");
        sb.append("id=").append(id);
        sb.append(", date=").append(date);
        sb.append(", duration=").append(duration);
        sb.append(", distance=").append(distance);
        sb.append(", evaluation=").append(evaluation);
        sb.append(", sport=").append(sport);
        sb.append(", user=").append(user);
        sb.append('}');
        return sb.toString();
    }



}

