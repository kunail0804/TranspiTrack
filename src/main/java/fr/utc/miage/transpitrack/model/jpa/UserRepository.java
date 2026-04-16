package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.utc.miage.transpitrack.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.firstName = ?1 ")
    List<User> findUserByFirstName(String firstName);

    @Query("SELECT u FROM User u WHERE u.name = ?1 ")
    List<User> findUserByName(String name);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(CONCAT(u.firstName, ' ', u.name)) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<User> searchByFullName(String query);

    @Query("SELECT u FROM User u WHERE u.email = ?1 ")
    User findByEmail(String email);

    @Query("SELECT c.id FROM User u JOIN u.joinedChallenges c WHERE u.id = :userId")
    Set<Long> findJoinedChallengeIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT c.id FROM Challenge c WHERE c.creator.id = :userId")
    Set<Long> findCreatedChallengeIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.joinedChallenges c WHERE u.id = :userId AND c.id = :challengeId")
    boolean hasJoinedChallenge(@Param("userId") Long userId, @Param("challengeId") Long challengeId);
}