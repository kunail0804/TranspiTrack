package fr.utc.miage.transpitrack.model;

import fr.utc.miage.transpitrack.model.enumer.BadgeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private double thresholdValue;

    @Enumerated(EnumType.STRING)
    private BadgeType badgeType;

    private String urlImage;

    public Badge() {}

    public Badge(String title, String description, double thresholdValue, BadgeType badgeType) {
        this.title = title;
        this.description = description;
        this.thresholdValue = thresholdValue;
        this.badgeType = badgeType;
    }

    public Badge(String title, String description, double thresholdValue, BadgeType badgeType, String url) {
        this.title = title;
        this.description = description;
        this.thresholdValue = thresholdValue;
        this.badgeType = badgeType;
        this.urlImage=url;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getThresholdValue() {
        return thresholdValue;
    }

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThresholdValue(double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }
}
