package fr.utc.miage.transpitrack.model.jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Activity;
import fr.utc.miage.transpitrack.model.Badge;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserBadge;

/**
 * Service responsible for checking and awarding gamification badges to users.
 * <p>
 * After each activity submission, {@link #checkAndAwardBadges} iterates over all
 * badges in the database and awards any whose threshold the user has now exceeded,
 * provided the user does not already hold that badge.
 * </p>
 */
@Service
public class BadgeService {

    /** Repository used to retrieve all defined badges. */
    @Autowired
    private BadgeRepository badgeRepository;

    /** Repository used to check and persist awarded user-badge records. */
    @Autowired
    private UserBadgeRepository userBadgeRepository;

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public BadgeService() {
        // Spring-managed bean.
    }

    /**
     * Returns the list of badges already earned by the given user.
     *
     * @param user the user whose badges to retrieve
     * @return a list of {@link UserBadge} records
     */
    public List<UserBadge> getUserBadges(User user) {
        return userBadgeRepository.findByUser(user);
    }

    /**
     * Evaluates all badges against the user's current activity statistics and awards
     * any newly earned badges.
     * <p>
     * For each badge not yet held by the user, the method checks whether the
     * corresponding metric (total distance, activity count, or total duration) meets
     * or exceeds the badge's threshold value. If so, a {@link UserBadge} record is
     * created and persisted.
     * </p>
     *
     * @param user       the user to evaluate
     * @param activities the full list of the user's activities used to compute statistics
     * @throws IllegalStateException if a badge has a {@code null} badge type
     */
    public void checkAndAwardBadges(User user, List<Activity> activities) {
        double totalDistance = activities.stream().mapToDouble(Activity::getDistance).sum();
        int totalCount = activities.size();
        int totalDuration = activities.stream().mapToInt(Activity::getDuration).sum();

        for (Badge badge : badgeRepository.findAll()) {
            if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
                continue;
            }

            boolean earned = false;

            switch (badge.getBadgeType()) {
                case null ->
                    throw new IllegalStateException("Badge type cannot be null");
                case DISTANCE ->
                    earned = totalDistance >= badge.getThresholdValue();
                case ACTIVITY_COUNT ->
                    earned = totalCount >= badge.getThresholdValue();
                case DURATION ->
                    earned = totalDuration >= badge.getThresholdValue();
            }

            if (earned) {
                userBadgeRepository.save(new UserBadge(user, badge, LocalDate.now()));
            }
        }
    }
}
