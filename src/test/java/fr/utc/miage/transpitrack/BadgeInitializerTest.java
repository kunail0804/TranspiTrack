package fr.utc.miage.transpitrack;

import java.util.Map;
import java.util.Optional;

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
        // findByTitle returns empty Optional by default → save called for each badge
        badgeInitializer.run(args);

        verify(badgeRepository, times(14)).save(any(Badge.class));
    }

    @Test
    void runShouldUpdateUrlWhenBadgeExistsWithDifferentUrl() {
        // Return a badge with null urlImage for all titles.
        // The 5 DISTANCE badges (snail.png, turtle.png, dog.png, horse.png, speed.png)
        // differ from null → save is called to update the url.
        // The 9 other badges have null url → no update needed.
        when(badgeRepository.findByTitle(any())).thenAnswer(inv -> {
            Badge existing = new Badge();
            existing.setUrlImage(null);
            return Optional.of(existing);
        });

        badgeInitializer.run(args);

        verify(badgeRepository, times(5)).save(any(Badge.class));
    }

    @Test
    void runShouldSkipBadgesAlreadyPresent() {
        // Map of badges that have a non-null urlImage in the initializer
        Map<String, String> urlMap = Map.of(
            "Premiers kilomètres", "snail.png",
            "Marcheur", "turtle.png",
            "Coureur", "dog.png",
            "Marathonien", "horse.png",
            "Ultra", "speed.png"
        );

        // Return an existing badge with the SAME url as the initializer would set → no update needed
        when(badgeRepository.findByTitle(any())).thenAnswer(inv -> {
            String title = inv.getArgument(0);
            Badge existing = new Badge();
            existing.setUrlImage(urlMap.get(title)); // null for badges without an image
            return Optional.of(existing);
        });

        badgeInitializer.run(args);

        verify(badgeRepository, never()).save(any());
    }
}
