package fr.utc.miage.transpitrack;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import fr.utc.miage.transpitrack.BadgeInitializer;
import fr.utc.miage.transpitrack.model.Badge;
import fr.utc.miage.transpitrack.model.jpa.BadgeRepository;

@ExtendWith(MockitoExtension.class)
class BadgeInitializerTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private ApplicationArguments args;

    @InjectMocks
    private BadgeInitializer badgeInitializer;

    @Test
    void runShouldInsertAllBadgesWhenNoneExist() {
        when(badgeRepository.existsByTitle(any())).thenReturn(false);

        badgeInitializer.run(args);

        verify(badgeRepository, times(14)).save(any(Badge.class));
    }

    @Test
    void runShouldSkipBadgesAlreadyPresent() {
        when(badgeRepository.existsByTitle(any())).thenReturn(true);

        badgeInitializer.run(args);

        verify(badgeRepository, never()).save(any());
    }
}
