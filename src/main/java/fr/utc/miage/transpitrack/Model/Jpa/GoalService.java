package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.Model.Goal;
import fr.utc.miage.transpitrack.Model.User;

@Service
public class GoalService {

    @Autowired
    GoalRepository GoalRepository;

    public List<Goal> getAllGoals(){
        return GoalRepository.findAll();
    }

    public List<Goal> getGoalByUser(User user){
        return GoalRepository.findByUser(user);
    }

    public Goal createGoal(Goal goal){
        return GoalRepository.save(goal);
    }

    public void updateGoal(Goal goal){
        GoalRepository.save(goal);
    }

    public void deleteGoal(Goal goal){
        GoalRepository.delete(goal);
    }

    public Goal getGoalById(Long id){
        return GoalRepository.findById(id).orElse(null);
    }

}
