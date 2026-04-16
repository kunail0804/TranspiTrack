package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    
    Challenge findChallengeById(Long id);

    @Query("SELECT c FROM Challenge c WHERE c.visibility = :visibility")
    List<Challenge> findChallengesByVisibility(String visibility);

    List<Challenge> findByCreatorId(Long creatorId);
}