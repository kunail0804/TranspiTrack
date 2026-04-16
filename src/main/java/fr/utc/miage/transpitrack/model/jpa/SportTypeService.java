package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.SportType;

/**
 * Service layer for {@link SportType} entities.
 * <p>
 * Provides persistence and retrieval operations for sport type categories,
 * delegating to {@link SportTypeRepository}.
 * </p>
 */
@Service
public class SportTypeService {

    /** Repository used to persist and retrieve sport type categories. */
    @Autowired
    private SportTypeRepository sportTypeRepository;

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public SportTypeService() {
        // Spring-managed bean.
    }

    /**
     * Persists a new or updated sport type category.
     *
     * @param sportType the {@link SportType} to save
     * @return the saved sport type (with generated ID if new)
     */
    public SportType save(SportType sportType) {
        return sportTypeRepository.save(sportType);
    }

    /**
     * Returns all sport type categories in the database.
     *
     * @return a list of all {@link SportType} entities
     */
    public List<SportType> getAllSportTypes() {
        return sportTypeRepository.findAll();
    }
}
