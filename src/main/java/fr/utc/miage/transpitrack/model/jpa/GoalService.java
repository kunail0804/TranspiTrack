package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Goal;
import fr.utc.miage.transpitrack.model.User;

/**
 * Service layer for {@link Goal} entities.
 * <p>
 * Provides full CRUD operations for training goals, delegating to
 * {@link GoalRepository}.
 * </p>
 */
@Service
public class GoalService {

    /** Repository used to persist and retrieve goals. */
    @Autowired
    GoalRepository goalRepository;

    /**
     * Returns all goals in the database.
     *
     * @return a list of all {@link Goal} entities
     */
    public List<Goal> getAllGoals(){
        return goalRepository.findAll();
    }

    /**
     * Returns all goals belonging to the given user.
     *
     * @param user the user whose goals to retrieve
     * @return a list of {@link Goal} entries for that user
     */
    public List<Goal> getGoalByUser(User user){
        return goalRepository.findByUser(user);
    }

    /**
     * Persists a new goal.
     *
     * @param goal the {@link Goal} to create
     * @return the saved goal (with generated ID)
     */
    public Goal createGoal(Goal goal){
        return goalRepository.save(goal);
    }

    /**
     * Persists changes to an existing goal.
     *
     * @param goal the {@link Goal} to update
     */
    public void updateGoal(Goal goal){
        goalRepository.save(goal);
    }

    /**
     * Deletes the given goal from the database.
     *
     * @param goal the {@link Goal} to delete
     */
    public void deleteGoal(Goal goal){
        goalRepository.delete(goal);
    }

    /**
     * Returns the goal with the given ID, or {@code null} if not found.
     *
     * @param id the goal ID
     * @return the matching {@link Goal}, or {@code null}
     */
    public Goal getGoalById(Long id){
        return goalRepository.findById(id).orElse(null);
    }
}
