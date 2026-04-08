package fr.utc.miage.transpitrack.Model;

import java.time.Duration;
import java.util.Date;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activiter_seq")
    private Long id;

    private Date date;
    private Duration duration;
    private double distance;

    @ManyToOne
    private Sport sport;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Activity(Date date, Duration duration, double distance, Sport sport, User user) {
        this.date = date;
        this.duration = duration;
        this.distance = distance;
        this.sport = sport;
        this.user = user;
    }


}

