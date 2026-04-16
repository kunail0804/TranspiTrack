package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.model.Goal;
import fr.utc.miage.transpitrack.model.User;

public interface GoalRepository extends JpaRepository<Goal, Long>{
    
    List<Goal> findByUser(User user);


}
