package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Activity;

/**
 * Service layer for {@link Activity} entities.
 * <p>
 * Delegates to {@link ActivityRepository} to provide CRUD operations and
 * user-specific activity retrieval.
 * </p>
 */
@Service
public class ActivityService {

    /** Repository used to persist and retrieve activities. */
    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Persists a new or updated activity.
     *
     * @param activity the activity to save
     * @return the saved activity (with generated ID if new)
     */
    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }

    /**
     * Returns the activity with the given ID, or {@code null} if not found.
     *
     * @param id the activity ID
     * @return the matching {@link Activity}, or {@code null}
     */
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElse(null);
    }

    /**
     * Returns all activities in the database.
     *
     * @return a list of all {@link Activity} entities
     */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /**
     * Returns all activities logged by the specified user.
     *
     * @param userId the ID of the user
     * @return a list of that user's activities
     */
    public List<Activity> getActivitiesByUserId(Long userId) {
        return activityRepository.findByUserId(userId);
    }
}
