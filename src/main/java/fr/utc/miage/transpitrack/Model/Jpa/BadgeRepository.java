package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.Model.Badge;
import fr.utc.miage.transpitrack.Model.Enum.BadgeType;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    List<Badge> findByBadgeType(BadgeType badgeType);

    boolean existsByTitle(String title);
}
