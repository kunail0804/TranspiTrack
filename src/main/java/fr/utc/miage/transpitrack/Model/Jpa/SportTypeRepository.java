package fr.utc.miage.transpitrack.model.jpa;

import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.SportType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SportTypeRepository extends JpaRepository<SportType, Long> {
    
    List<SportType> findByName(String name);

    boolean existsByName(String name);
}
