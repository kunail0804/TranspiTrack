package fr.utc.miage.transpitrack.model.jpa;

import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.SportType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link SportType} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus queries
 * for finding sport type categories by name and checking name uniqueness.
 * </p>
 */
@Repository
public interface SportTypeRepository extends JpaRepository<SportType, Long> {

    /**
     * Returns all sport type categories with the given name.
     *
     * @param name the category name to look up
     * @return a list of matching sport types (typically 0 or 1 element)
     */
    List<SportType> findByName(String name);

    /**
     * Returns whether a sport type category with the given name already exists.
     *
     * @param name the category name to check
     * @return {@code true} if at least one sport type with that name exists
     */
    boolean existsByName(String name);
}
