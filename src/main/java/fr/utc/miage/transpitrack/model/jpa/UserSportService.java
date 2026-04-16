package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserSport;

/**
 * Service layer for {@link UserSport} entities.
 * <p>
 * Provides full CRUD operations for user sport preferences,
 * delegating to {@link UserSportRepository}.
 * </p>
 */
@Service
public class UserSportService {

    /** Repository used to persist and retrieve user sport preferences. */
    @Autowired
    UserSportRepository userSportRepository;

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public UserSportService() {
        // Spring-managed bean.
    }

    /**
     * Returns all user sport preference records in the database.
     *
     * @return a list of all {@link UserSport} entities
     */
    public List<UserSport> getAllUserSport(){
        return userSportRepository.findAll();
    }

    /**
     * Returns all sport preferences declared by the given user.
     *
     * @param user the user whose preferences to retrieve
     * @return a list of {@link UserSport} entries for that user
     */
    public List<UserSport> getUserSportByUser(User user){
        return userSportRepository.findByUser(user);
    }

    /**
     * Returns the preference record for the given user and sport, or {@code null}
     * if none exists.
     *
     * @param user  the user to look up
     * @param sport the sport to look up
     * @return the matching {@link UserSport}, or {@code null}
     */
    public UserSport getUserSportByUserAndSport(User user, Sport sport){
        return userSportRepository.findByUserAndSport(user, sport);
    }

    /**
     * Persists a new user sport preference.
     *
     * @param userSport the {@link UserSport} to create
     * @return the saved preference (with generated ID)
     */
    public UserSport createUserSport(UserSport userSport){
        return userSportRepository.save(userSport);
    }

    /**
     * Persists changes to an existing user sport preference.
     *
     * @param userSport the {@link UserSport} to update
     */
    public void updateUserSport(UserSport userSport){
        userSportRepository.save(userSport);
    }

    /**
     * Deletes the given user sport preference from the database.
     *
     * @param userSport the {@link UserSport} to delete
     */
    public void deleteUserSport(UserSport userSport){
        userSportRepository.delete(userSport);
    }

    /**
     * Returns the user sport preference with the given ID, or {@code null} if not found.
     *
     * @param id the preference record ID
     * @return the matching {@link UserSport}, or {@code null}
     */
    public UserSport getUserSportById(Long id){
        return userSportRepository.findById(id).orElse(null);
    }
}
