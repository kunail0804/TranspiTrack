package fr.utc.miage.transpitrack.Model.Jpa;

import fr.utc.miage.transpitrack.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
}