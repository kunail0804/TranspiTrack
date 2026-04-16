package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserSport;

@Service
public class UserSportService {

    @Autowired
    UserSportRepository userSportRepository;

    public List<UserSport> getAllUserSport(){
        return userSportRepository.findAll();
    }

    public List<UserSport> getUserSportByUser(User user){
        return userSportRepository.findByUser(user);
    }

    public UserSport getUserSportByUserAndSport(User user, Sport sport){
        return userSportRepository.findByUserAndSport(user, sport);
    }

    public UserSport createUserSport(UserSport userSport){
        return userSportRepository.save(userSport);
    }

    public void updateUserSport(UserSport userSport){
        userSportRepository.save(userSport);
    }

    public void deleteUserSport(UserSport userSport){
        userSportRepository.delete(userSport);
    }

    public UserSport getUserSportById(Long id){
        return userSportRepository.findById(id).orElse(null);
    }

}
