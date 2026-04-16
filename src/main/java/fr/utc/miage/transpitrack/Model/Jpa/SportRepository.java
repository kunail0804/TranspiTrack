package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.Model.Sport;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {
    
    List<Sport> findByName(String name);

    boolean existsByName(String name);
}
