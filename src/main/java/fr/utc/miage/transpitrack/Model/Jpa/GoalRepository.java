package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.Model.Goal;
import fr.utc.miage.transpitrack.Model.User;

public interface GoalRepository extends JpaRepository<Goal, Long>{
    
    List<Goal> findByUser(User user);


}
