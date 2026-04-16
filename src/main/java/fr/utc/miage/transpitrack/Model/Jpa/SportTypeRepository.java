package fr.utc.miage.transpitrack.Model.Jpa;

import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.Model.SportType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SportTypeRepository extends JpaRepository<SportType, Long> {
    
    List<SportType> findByName(String name);

    boolean existsByName(String name);
}
