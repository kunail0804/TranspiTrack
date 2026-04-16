package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.Sport;

/**
 * Spring Data JPA repository for {@link Sport} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus queries
 * for finding sports by name and checking name uniqueness.
 * </p>
 */
@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {

    /**
     * Returns all sports with the given name.
     *
     * @param name the sport name to look up
     * @return a list of matching sports (typically 0 or 1 element)
     */
    List<Sport> findByName(String name);

    /**
     * Returns whether a sport with the given name already exists in the database.
     *
     * @param name the sport name to check
     * @return {@code true} if at least one sport with that name exists
     */
    boolean existsByName(String name);
}
