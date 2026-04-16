package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.Model.Goal;
import fr.utc.miage.transpitrack.Model.User;

@Service
public class GoalService {

    @Autowired
    GoalRepository goalRepository;

    public List<Goal> getAllGoals(){
        return goalRepository.findAll();
    }

    public List<Goal> getGoalByUser(User user){
        return goalRepository.findByUser(user);
    }

    public Goal createGoal(Goal goal){
        return goalRepository.save(goal);
    }

    public void updateGoal(Goal goal){
        goalRepository.save(goal);
    }

    public void deleteGoal(Goal goal){
        goalRepository.delete(goal);
    }

    public Goal getGoalById(Long id){
        return goalRepository.findById(id).orElse(null);
    }

}
