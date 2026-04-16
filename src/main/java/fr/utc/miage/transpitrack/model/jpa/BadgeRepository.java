package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.model.Badge;
import fr.utc.miage.transpitrack.model.enumer.BadgeType;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    List<Badge> findByBadgeType(BadgeType badgeType);

    boolean existsByTitle(String title);

    Optional<Badge> findByTitle(String title);
}
