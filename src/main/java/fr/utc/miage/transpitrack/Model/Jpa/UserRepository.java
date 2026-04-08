package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.utc.miage.transpitrack.Model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.firstName = ?1 ")
    List<User> findUserByFirstName(String firstName);

    @Query("SELECT u FROM User u WHERE u.name = ?1 ")
    List<User> findUserByName(String name);

    @Query("SELECT u FROM User u WHERE u.email = ?1 ")
    User findByEmail(String email);
}