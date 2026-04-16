package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Sport;

/**
 * Service layer for {@link Sport} entities.
 * <p>
 * Provides retrieval and persistence operations for sports,
 * delegating to {@link SportRepository}.
 * </p>
 */
@Service
public class SportService {

    /** Repository used to persist and retrieve sports. */
    @Autowired
    private SportRepository sportRepository;

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public SportService() {
        // Spring-managed bean.
    }

    /**
     * Returns all sports with the given name.
     *
     * @param name the sport name to look up
     * @return a list of matching sports
     */
    public List<Sport> findByName(String name) {
        return sportRepository.findByName(name);
    }

    /**
     * Returns the sport with the given ID, or {@code null} if not found.
     *
     * @param id the sport ID
     * @return the matching {@link Sport}, or {@code null}
     */
    public Sport getSportById(Long id) {
        return sportRepository.findById(id).orElse(null);
    }

    /**
     * Persists a new or updated sport.
     *
     * @param sport the {@link Sport} to save
     * @return the saved sport (with generated ID if new)
     */
    public Sport save(Sport sport) {
        return sportRepository.save(sport);
    }

    /**
     * Returns all sports in the database.
     *
     * @return a list of all {@link Sport} entities
     */
    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }
}
