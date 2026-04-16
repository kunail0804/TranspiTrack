package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Sport;

@Service
public class SportService {

    @Autowired
    private SportRepository sportRepository;

    public List<Sport> findByName(String name) {
        return sportRepository.findByName(name);
    }

    public Sport getSportById(Long id) {
        return sportRepository.findById(id).orElse(null);
    }

    public Sport save(Sport sport) {
        return sportRepository.save(sport);
    }

    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }
    
}
