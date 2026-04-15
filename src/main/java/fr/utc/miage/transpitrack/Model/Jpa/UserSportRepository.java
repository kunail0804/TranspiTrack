package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.UserSport;


public interface UserSportRepository extends JpaRepository<UserSport, Long> {

    List<UserSport> findByUser(User user);

    @Query("SELECT u FROM UserSport u WHERE u.user = :user AND u.sport = :sport")
    UserSport findByUserAndSport(User user, Sport sport);
}
