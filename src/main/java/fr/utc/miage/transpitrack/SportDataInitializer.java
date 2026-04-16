package fr.utc.miage.transpitrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.SportType;
import fr.utc.miage.transpitrack.model.jpa.SportRepository;
import fr.utc.miage.transpitrack.model.jpa.SportTypeRepository;

/**
 * Application startup component that seeds sport type categories and individual sports
 * into the database.
 * <p>
 * Runs at startup with {@link Order}(1), before other initializers that may depend on
 * sport data. Uses idempotent helper methods so re-running on an already-populated
 * database is safe — existing records are never overwritten.
 * </p>
 */
@Component
@Order(1)
public class SportDataInitializer implements ApplicationRunner {

    /** Repository used to check for and persist sport type categories. */
    @Autowired
    private SportTypeRepository sportTypeRepository;

    /** Repository used to check for and persist individual sports. */
    @Autowired
    private SportRepository sportRepository;

    /**
     * Seeds all predefined sport types and sports on application startup.
     *
     * @param args the application arguments (not used)
     */
    @Override
    public void run(ApplicationArguments args) {
        SportType courseAPied    = insertTypeIfAbsent("Course à pied",       "Activités de course et marche sportive");
        SportType cyclisme       = insertTypeIfAbsent("Cyclisme",            "Vélo de route, VTT et disciplines associées");
        SportType natation       = insertTypeIfAbsent("Natation",            "Sports aquatiques en piscine");
        SportType combat         = insertTypeIfAbsent("Sports de combat",    "Arts martiaux et sports de contact");
        SportType collectifs     = insertTypeIfAbsent("Sports collectifs",   "Sports pratiqués en équipe");
        SportType fitness        = insertTypeIfAbsent("Musculation & Fitness", "Renforcement musculaire et fitness en salle");
        SportType raquette       = insertTypeIfAbsent("Sports de raquette",  "Tennis, badminton et sports de raquette");
        SportType hiver          = insertTypeIfAbsent("Sports d'hiver",      "Ski, snowboard et sports sur neige");
        SportType nautiques      = insertTypeIfAbsent("Sports nautiques",    "Kayak, aviron et sports sur eau");
        SportType pleinAir       = insertTypeIfAbsent("Randonnée & Plein air", "Randonnée, escalade et activités outdoor");

        // --- Running ---
        insertSportIfAbsent("Marche sportive",  "Marche rapide à allure soutenue",                     4.5,  courseAPied);
        insertSportIfAbsent("Jogging",          "Course à allure modérée",                             7.0,  courseAPied);
        insertSportIfAbsent("Course lente",     "Course d'endurance à faible vitesse",                 8.0,  courseAPied);
        insertSportIfAbsent("Course rapide",    "Course à haute intensité",                           11.0,  courseAPied);
        insertSportIfAbsent("Trail",            "Course hors-piste en nature",                         9.0,  courseAPied);
        insertSportIfAbsent("Sprint",           "Effort maximal sur courte distance",                 13.5,  courseAPied);

        // --- Cycling ---
        insertSportIfAbsent("Vélo de ville",    "Déplacements urbains à vélo",                         6.0,  cyclisme);
        insertSportIfAbsent("Vélo de route",    "Cyclisme sur route à allure modérée",                  8.0,  cyclisme);
        insertSportIfAbsent("Cyclisme intense", "Vélo de route à haute intensité",                    12.0,  cyclisme);
        insertSportIfAbsent("VTT",              "Vélo tout terrain, descente et cross-country",        10.0,  cyclisme);
        insertSportIfAbsent("Vélo elliptique",  "Cardio sur appareil elliptique",                       5.0,  cyclisme);

        // --- Swimming ---
        insertSportIfAbsent("Natation lente",   "Nage à allure tranquille",                            6.0,  natation);
        insertSportIfAbsent("Natation rapide",  "Nage à allure soutenue",                             10.0,  natation);
        insertSportIfAbsent("Brasse",           "Nage brasse à allure modérée",                        5.5,  natation);
        insertSportIfAbsent("Crawl",            "Nage crawl à allure modérée",                         8.0,  natation);
        insertSportIfAbsent("Aquagym",          "Gymnastique aquatique en groupe",                      5.5,  natation);

        // --- Combat Sports ---
        insertSportIfAbsent("Boxe",             "Boxe anglaise, entraînement et sparring",              7.8,  combat);
        insertSportIfAbsent("Judo",             "Art martial japonais au sol et en debout",            10.0,  combat);
        insertSportIfAbsent("Karaté",           "Art martial avec frappes pieds-poings",               10.0,  combat);
        insertSportIfAbsent("MMA",              "Arts martiaux mixtes, entraînement complet",          12.0,  combat);
        insertSportIfAbsent("Lutte",            "Sport de combat au sol",                              10.0,  combat);
        insertSportIfAbsent("Taekwondo",        "Art martial coréen axé sur les coups de pied",         9.0,  combat);

        // --- Team Sports ---
        insertSportIfAbsent("Football",         "Football à 11 joueurs",                               7.0,  collectifs);
        insertSportIfAbsent("Basketball",       "Basketball à 5 joueurs par équipe",                   6.5,  collectifs);
        insertSportIfAbsent("Volleyball",       "Volleyball en salle ou en plage",                     4.0,  collectifs);
        insertSportIfAbsent("Rugby",            "Rugby à XV ou à VII",                                 8.3,  collectifs);
        insertSportIfAbsent("Handball",         "Handball en salle",                                   8.0,  collectifs);
        insertSportIfAbsent("Hockey sur gazon", "Hockey joué sur terrain naturel ou synthétique",      8.0,  collectifs);

        // --- Strength Training & Fitness ---
        insertSportIfAbsent("Musculation",      "Entraînement avec charges libres et machines",        6.0,  fitness);
        insertSportIfAbsent("CrossFit",         "Entraînement fonctionnel à haute intensité",         12.0,  fitness);
        insertSportIfAbsent("HIIT",             "Entraînement fractionné haute intensité",             8.0,  fitness);
        insertSportIfAbsent("Yoga",             "Pratique de postures et respiration",                  3.0,  fitness);
        insertSportIfAbsent("Pilates",          "Renforcement du gainage et de la posture",             3.5,  fitness);
        insertSportIfAbsent("Corde à sauter",   "Cardio à la corde à sauter",                         11.0,  fitness);

        // --- Racquet Sports ---
        insertSportIfAbsent("Tennis",           "Tennis simple ou double",                             7.0,  raquette);
        insertSportIfAbsent("Badminton",        "Badminton en salle",                                   5.5,  raquette);
        insertSportIfAbsent("Squash",           "Squash en salle",                                    12.0,  raquette);
        insertSportIfAbsent("Tennis de table",  "Ping-pong en compétition ou loisir",                   4.0,  raquette);
        insertSportIfAbsent("Padel",            "Sport de raquette sur court fermé",                    6.0,  raquette);

        // --- Winter Sports ---
        insertSportIfAbsent("Ski alpin",        "Descente sur pistes",                                  5.3,  hiver);
        insertSportIfAbsent("Ski de fond",      "Ski nordique en plaine ou montagne",                   9.0,  hiver);
        insertSportIfAbsent("Snowboard",        "Descente sur neige avec une planche",                  5.3,  hiver);
        insertSportIfAbsent("Patinage sur glace","Patinage artistique ou de vitesse",                   5.5,  hiver);
        insertSportIfAbsent("Raquettes à neige","Randonnée hivernale en raquettes",                     6.5,  hiver);

        // --- Water Sports ---
        insertSportIfAbsent("Kayak",            "Pagayage en kayak sur eau calme ou vive",              5.0,  nautiques);
        insertSportIfAbsent("Surf",             "Surf en mer",                                          3.0,  nautiques);
        insertSportIfAbsent("Aviron",           "Aviron en équipe ou en skiff",                         8.5,  nautiques);
        insertSportIfAbsent("Planche à voile",  "Windsurf sur mer ou lac",                              5.7,  nautiques);
        insertSportIfAbsent("Paddle",           "Stand-up paddle sur eau calme",                        4.0,  nautiques);

        // --- Hiking & Outdoor ---
        insertSportIfAbsent("Randonnée",        "Marche en nature sur sentiers balisés",                6.0,  pleinAir);
        insertSportIfAbsent("Escalade",         "Escalade en salle ou en falaise",                      8.0,  pleinAir);
        insertSportIfAbsent("Marche nordique",  "Marche avec bâtons de Nordic Walking",                 6.5,  pleinAir);
        insertSportIfAbsent("Course d'orientation", "Navigation en terrain naturel",                    9.0,  pleinAir);
    }

    /**
     * Returns the sport type category with the given name, creating and persisting it
     * if it does not yet exist.
     *
     * @param name        the unique display name of the category
     * @param description a short description of the category
     * @return the existing or newly created {@link SportType}
     */
    private SportType insertTypeIfAbsent(String name, String description) {
        if (!sportTypeRepository.existsByName(name)) {
            SportType sportType = new SportType();
            sportType.setName(name);
            sportType.setDescription(description);
            return sportTypeRepository.save(sportType);
        }
        return sportTypeRepository.findByName(name).get(0);
    }

    /**
     * Inserts a new sport if no sport with the given name already exists.
     *
     * @param name        the unique display name of the sport
     * @param description a short description of the sport
     * @param metValue    the MET value used for calorie calculations
     * @param sportType   the category this sport belongs to
     */
    private void insertSportIfAbsent(String name, String description, double metValue, SportType sportType) {
        if (!sportRepository.existsByName(name)) {
            Sport sport = new Sport();
            sport.setName(name);
            sport.setDescription(description);
            sport.setMetValue(metValue);
            sport.setSportType(sportType);
            sportRepository.save(sport);
        }
    }
}
