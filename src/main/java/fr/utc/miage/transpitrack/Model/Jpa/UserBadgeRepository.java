package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.model.Badge;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserBadge;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findByUser(User user);

    boolean existsByUserAndBadge(User user, Badge badge);
}
