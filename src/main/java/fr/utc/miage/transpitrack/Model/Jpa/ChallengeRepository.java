package fr.utc.miage.transpitrack.Model.Jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.Model.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

}