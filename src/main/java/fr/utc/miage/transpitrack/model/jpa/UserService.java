package fr.utc.miage.transpitrack.model.jpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.User;

/**
 * Service layer for {@link User} entities.
 * <p>
 * Provides CRUD operations, user search, and challenge participation queries,
 * delegating to {@link UserRepository}.
 * </p>
 */
@Service
public class UserService {

    /** Repository used to persist and retrieve users. */
    @Autowired
    UserRepository userRepository;

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public UserService() {
        // Spring-managed bean.
    }

    /**
     * Returns all users whose first name exactly matches the given value.
     *
     * @param firstName the first name to search for
     * @return a list of matching users
     */
    public List<User> getUserByFirstName(String firstName){
        return userRepository.findUserByFirstName(firstName);
    }

    /**
     * Returns all users whose last name exactly matches the given value.
     *
     * @param name the last name to search for
     * @return a list of matching users
     */
    public List<User> getUserByName(String name){
        return userRepository.findUserByName(name);
    }

    /**
     * Returns all users whose full name contains the given query string
     * (case-insensitive).
     *
     * @param query the search term
     * @return a list of users whose name partially matches the query
     */
    public List<User> searchUsers(String query){
        return userRepository.searchByFullName(query);
    }

    /**
     * Returns the user with the given email address, or {@code null} if not found.
     *
     * @param email the email address to look up
     * @return the matching {@link User}, or {@code null}
     */
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    /**
     * Persists a new user.
     *
     * @param user the {@link User} to create
     * @return the saved user (with generated ID)
     */
    public User createUser(User user){
        return userRepository.save(user);
    }

    /**
     * Returns all users in the database.
     *
     * @return a list of all {@link User} entities
     */
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * Deletes the user with the given ID.
     *
     * @param id the ID of the user to delete
     */
    public void deleteUserById(Long id){
        userRepository.deleteById(id);
    }

    /**
     * Persists changes to an existing user.
     *
     * @param user the {@link User} to update
     * @return the saved user
     */
    public User updateUser(User user){
        return userRepository.save(user);
    }

    /**
     * Returns the user with the given ID, or {@code null} if not found.
     *
     * @param id the user ID
     * @return the matching {@link User}, or {@code null}
     */
    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Returns the combined set of challenge IDs in which the given user participates,
     * either as creator or as a joined member.
     *
     * @param userId the ID of the user
     * @return a set of challenge IDs
     */
    public Set<Long> getParticipatingChallengeIds(Long userId) {
        Set<Long> ids = new HashSet<>();
        ids.addAll(userRepository.findCreatedChallengeIdsByUserId(userId));
        ids.addAll(userRepository.findJoinedChallengeIdsByUserId(userId));
        return ids;
    }

    /**
     * Returns whether the given user has joined the given challenge.
     *
     * @param userId      the ID of the user
     * @param challengeId the ID of the challenge
     * @return {@code true} if the user has joined the challenge
     */
    public boolean hasJoinedChallenge(Long userId, Long challengeId) {
        return userRepository.hasJoinedChallenge(userId, challengeId);
    }
}
