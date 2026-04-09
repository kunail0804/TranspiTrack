package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.Model.SportType;

@ExtendWith(MockitoExtension.class)
class SportTypeServiceTest {

    @Mock
    private SportTypeRepository sportTypeRepository;

    @InjectMocks
    private SportTypeService sportTypeService;

    @Test
    void saveShouldReturnSavedSportType() {
        SportType sportType = new SportType();
        when(sportTypeRepository.save(sportType)).thenReturn(sportType);

        SportType result = sportTypeService.save(sportType);

        assertEquals(sportType, result);
        verify(sportTypeRepository).save(sportType);
    }

    @Test
    void getAllSportTypesShouldReturnAllSportTypes() {
        SportType st1 = new SportType();
        SportType st2 = new SportType();
        when(sportTypeRepository.findAll()).thenReturn(List.of(st1, st2));

        List<SportType> result = sportTypeService.getAllSportTypes();

        assertEquals(2, result.size());
        verify(sportTypeRepository).findAll();
    }
}
