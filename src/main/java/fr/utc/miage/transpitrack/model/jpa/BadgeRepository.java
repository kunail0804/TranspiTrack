package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.model.Badge;
import fr.utc.miage.transpitrack.model.enumer.BadgeType;

/**
 * Spring Data JPA repository for {@link Badge} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} plus queries
 * for looking up badges by type or by their unique title.
 * </p>
 */
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    /**
     * Returns all badges of the specified type.
     *
     * @param badgeType the {@link BadgeType} to filter on
     * @return a list of matching badges
     */
    List<Badge> findByBadgeType(BadgeType badgeType);

    /**
     * Returns whether a badge with the given title already exists in the database.
     *
     * @param title the badge title to check
     * @return {@code true} if at least one badge with that title exists
     */
    boolean existsByTitle(String title);

    /**
     * Returns the badge with the given title, if one exists.
     *
     * @param title the badge title to look up
     * @return an {@link Optional} containing the badge, or empty if not found
     */
    Optional<Badge> findByTitle(String title);
}
