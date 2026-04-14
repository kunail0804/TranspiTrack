package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.Model.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    
    Challenge findChallengeById(Long id);

    @Query("SELECT c FROM Challenge c WHERE c.visibility = :visibility")
    List<Challenge> findChallengesByVisibility(String visibility);

}