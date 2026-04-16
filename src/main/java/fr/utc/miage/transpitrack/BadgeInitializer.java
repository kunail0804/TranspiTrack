package fr.utc.miage.transpitrack;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import fr.utc.miage.transpitrack.model.Badge;
import fr.utc.miage.transpitrack.model.enumer.BadgeType;
import fr.utc.miage.transpitrack.model.jpa.BadgeRepository;

@Component
public class BadgeInitializer implements ApplicationRunner {

    @Autowired
    private BadgeRepository badgeRepository;

    @Override
    public void run(ApplicationArguments args) {
        insertIfAbsent("Premier pas", "Complète ta 1ère activité", 1, BadgeType.ACTIVITY_COUNT, null);
        insertIfAbsent("Régulier", "Complète 5 activités", 5, BadgeType.ACTIVITY_COUNT, null);
        insertIfAbsent("Habitué", "Complète 10 activités", 10, BadgeType.ACTIVITY_COUNT, null);
        insertIfAbsent("Assidu", "Complète 50 activités", 50, BadgeType.ACTIVITY_COUNT, null);
        insertIfAbsent("Centurion", "Complète 100 activités", 100, BadgeType.ACTIVITY_COUNT, null);

        // Distance
        insertIfAbsent("Premiers kilomètres", "Parcours 10 km au total", 10, BadgeType.DISTANCE, "snail.png");
        insertIfAbsent("Marcheur", "Parcours 50 km au total", 50, BadgeType.DISTANCE, "turtle.png");
        insertIfAbsent("Coureur", "Parcours 100 km au total", 100, BadgeType.DISTANCE, "dog.png");
        insertIfAbsent("Marathonien", "Parcours 500 km au total", 500, BadgeType.DISTANCE, "horse.png");
        insertIfAbsent("Ultra", "Parcours 1000 km au total", 1000, BadgeType.DISTANCE, "speed.png");

        // Durée
        insertIfAbsent("Première heure", "Cumule 60 min d'entraînement", 60, BadgeType.DURATION, null);
        insertIfAbsent("Endurant", "Cumule 5h d'entraînement", 300, BadgeType.DURATION, null);
        insertIfAbsent("Infatigable", "Cumule 16h d'entraînement", 1000, BadgeType.DURATION, null);
        insertIfAbsent("Titan", "Cumule 83h d'entraînement", 5000, BadgeType.DURATION, null);
    }

    private void insertIfAbsent(String title, String description, double threshold, BadgeType type, String url) {
        badgeRepository.findByTitle(title).ifPresentOrElse(
            existing -> {
                if (!Objects.equals(existing.getUrlImage(), url)) {
                    existing.setUrlImage(url);
                    badgeRepository.save(existing);
                }
            },
            () -> badgeRepository.save(new Badge(title, description, threshold, type, url))
        );
    }
}
