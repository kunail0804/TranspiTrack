package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.model.Sport;

@ExtendWith(MockitoExtension.class)
class SportServiceTest {

    @Mock
    private SportRepository sportRepository;

    @InjectMocks
    private SportService sportService;

    @Test
    void findByNameShouldReturnMatchingSports() {
        Sport sport = new Sport();
        when(sportRepository.findByName("Running")).thenReturn(List.of(sport));

        List<Sport> result = sportService.findByName("Running");

        assertEquals(1, result.size());
        assertEquals(sport, result.get(0));
        verify(sportRepository).findByName("Running");
    }

    @Test
    void findByNameShouldReturnEmptyListWhenNoMatch() {
        when(sportRepository.findByName("Inconnu")).thenReturn(List.of());

        List<Sport> result = sportService.findByName("Inconnu");

        assertEquals(0, result.size());
        verify(sportRepository).findByName("Inconnu");
    }

    @Test
    void getSportByIdShouldReturnSportWhenFound() {
        Sport sport = new Sport();
        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));

        Sport result = sportService.getSportById(1L);

        assertEquals(sport, result);
        verify(sportRepository).findById(1L);
    }

    @Test
    void getSportByIdShouldReturnNullWhenNotFound() {
        when(sportRepository.findById(99L)).thenReturn(Optional.empty());

        Sport result = sportService.getSportById(99L);

        assertNull(result);
        verify(sportRepository).findById(99L);
    }

    @Test
    void saveShouldReturnSavedSport() {
        Sport sport = new Sport();
        when(sportRepository.save(sport)).thenReturn(sport);

        Sport result = sportService.save(sport);

        assertEquals(sport, result);
        verify(sportRepository).save(sport);
    }

    @Test
    void getAllSportsShouldReturnAllSports() {
        Sport s1 = new Sport();
        Sport s2 = new Sport();
        when(sportRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Sport> result = sportService.getAllSports();

        assertEquals(2, result.size());
        verify(sportRepository).findAll();
    }
}
