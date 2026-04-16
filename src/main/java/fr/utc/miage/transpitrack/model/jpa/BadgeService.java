package fr.utc.miage.transpitrack.model.jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Activity;
import fr.utc.miage.transpitrack.model.Badge;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserBadge;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    public List<UserBadge> getUserBadges(User user) {
        return userBadgeRepository.findByUser(user);
    }

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
