package fr.utc.miage.transpitrack;

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
        insertIfAbsent("Premier pas", "Complète ta 1ère activité", 1, BadgeType.ACTIVITY_COUNT, "/images/badges/premier_pas.png");
        insertIfAbsent("Régulier", "Complète 5 activités", 5, BadgeType.ACTIVITY_COUNT, "/images/badges/regulier.png");
        insertIfAbsent("Habitué", "Complète 10 activités", 10, BadgeType.ACTIVITY_COUNT, "/images/badges/habitue.png");
        insertIfAbsent("Assidu", "Complète 50 activités", 50, BadgeType.ACTIVITY_COUNT, "/images/badges/assidu.png");
        insertIfAbsent("Centurion", "Complète 100 activités", 100, BadgeType.ACTIVITY_COUNT, "/images/badges/centurion.png");

        // Distance
        insertIfAbsent("Premiers kilomètres", "Parcours 10 km au total", 10, BadgeType.DISTANCE, "/images/badges/snail.png");
        insertIfAbsent("Marcheur", "Parcours 50 km au total", 50, BadgeType.DISTANCE, "/images/badges/turtle.png");
        insertIfAbsent("Coureur", "Parcours 100 km au total", 100, BadgeType.DISTANCE, "/images/badges/dog.png");
        insertIfAbsent("Marathonien", "Parcours 500 km au total", 500, BadgeType.DISTANCE, "/images/badges/horse.png");
        insertIfAbsent("Ultra", "Parcours 1000 km au total", 1000, BadgeType.DISTANCE, "/images/badges/speed.png");

        // Durée
        insertIfAbsent("Première heure", "Cumule 60 min d'entraînement", 60, BadgeType.DURATION, "/images/badges/premiere_heure.png");
        insertIfAbsent("Endurant", "Cumule 5h d'entraînement", 300, BadgeType.DURATION, "/images/badges/endurant.png");
        insertIfAbsent("Infatigable", "Cumule 16h d'entraînement", 1000, BadgeType.DURATION, "/images/badges/infatigable.png");
        insertIfAbsent("Titan", "Cumule 83h d'entraînement", 5000, BadgeType.DURATION, "/images/badges/titan.png");
    }

    private void insertIfAbsent(String title, String description, double threshold, BadgeType type, String url) {
        if (!badgeRepository.existsByTitle(title)) {
            badgeRepository.save(new Badge(title, description, threshold, type,url));
        }
    }
}
