package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.Model.SportType;

@Service
public class SportTypeService {
    
    @Autowired
    private SportTypeRepository sportTypeRepository;

    public SportType save(SportType sportType) {
        return sportTypeRepository.save(sportType);
    }

    public List<SportType> getAllSportTypes() {
        return sportTypeRepository.findAll();
    }
}
