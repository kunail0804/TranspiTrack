package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.Model.User;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<User> getUserByFirstName(String firstName){
        return userRepository.findUserByFirstName(firstName);
    }

    public List<User> getUserByName(String name){
        return userRepository.findUserByName(name);
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void deleteUserById(Long id){
        userRepository.deleteById(id);
    }

    public User updateUser(User user){
        return userRepository.save(user);
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }
}
