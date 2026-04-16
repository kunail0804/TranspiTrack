package fr.utc.miage.transpitrack;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.SportType;
import fr.utc.miage.transpitrack.model.jpa.SportRepository;
import fr.utc.miage.transpitrack.model.jpa.SportTypeRepository;

@ExtendWith(MockitoExtension.class)
class SportDataInitializerTest {

    @Mock
    private SportTypeRepository sportTypeRepository;

    @Mock
    private SportRepository sportRepository;

    @Mock
    private ApplicationArguments args;

    @InjectMocks
    private SportDataInitializer sportDataInitializer;

    @Test
    void runShouldInsertSportTypesAndSportsWhenNoneExist() {
        when(sportTypeRepository.existsByName(any())).thenReturn(false);
        when(sportTypeRepository.save(any(SportType.class))).thenReturn(new SportType());
        when(sportRepository.existsByName(any())).thenReturn(false);

        sportDataInitializer.run(args);

        verify(sportTypeRepository, atLeastOnce()).save(any(SportType.class));
        verify(sportRepository, atLeastOnce()).save(any(Sport.class));
    }

    @Test
    void runShouldSkipWhenAllSportTypesAndSportsAlreadyExist() {
        SportType existing = new SportType();
        when(sportTypeRepository.existsByName(any())).thenReturn(true);
        when(sportTypeRepository.findByName(any())).thenReturn(List.of(existing));
        when(sportRepository.existsByName(any())).thenReturn(true);

        sportDataInitializer.run(args);

        verify(sportTypeRepository, never()).save(any());
        verify(sportRepository, never()).save(any());
    }
}
