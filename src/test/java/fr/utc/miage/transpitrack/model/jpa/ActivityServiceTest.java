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

import fr.utc.miage.transpitrack.model.Activity;
import fr.utc.miage.transpitrack.model.jpa.ActivityRepository;
import fr.utc.miage.transpitrack.model.jpa.ActivityService;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void saveShouldReturnSavedActivity() {
        Activity activity = new Activity();
        when(activityRepository.save(activity)).thenReturn(activity);

        Activity result = activityService.save(activity);

        assertEquals(activity, result);
        verify(activityRepository).save(activity);
    }

    @Test
    void getActivityByIdShouldReturnActivityWhenFound() {
        Activity activity = new Activity();
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));

        Activity result = activityService.getActivityById(1L);

        assertEquals(activity, result);
        verify(activityRepository).findById(1L);
    }

    @Test
    void getActivityByIdShouldReturnNullWhenNotFound() {
        when(activityRepository.findById(99L)).thenReturn(Optional.empty());

        Activity result = activityService.getActivityById(99L);

        assertNull(result);
        verify(activityRepository).findById(99L);
    }

    @Test
    void getAllActivitiesShouldReturnAllActivities() {
        Activity a1 = new Activity();
        Activity a2 = new Activity();
        when(activityRepository.findAll()).thenReturn(List.of(a1, a2));

        List<Activity> result = activityService.getAllActivities();

        assertEquals(2, result.size());
        verify(activityRepository).findAll();
    }

    @Test
    void getActivitiesByUserIdShouldReturnActivitiesForUser() {
        Activity a1 = new Activity();
        Activity a2 = new Activity();
        when(activityRepository.findByUserId(1L)).thenReturn(List.of(a1, a2));

        List<Activity> result = activityService.getActivitiesByUserId(1L);

        assertEquals(2, result.size());
        verify(activityRepository).findByUserId(1L);
    }

    @Test
    void getActivitiesByUserIdShouldReturnEmptyListWhenNoActivities() {
        when(activityRepository.findByUserId(99L)).thenReturn(List.of());

        List<Activity> result = activityService.getActivitiesByUserId(99L);

        assertEquals(0, result.size());
        verify(activityRepository).findByUserId(99L);
    }
}
