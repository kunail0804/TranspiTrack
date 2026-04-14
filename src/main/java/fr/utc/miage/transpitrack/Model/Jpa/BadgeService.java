package fr.utc.miage.transpitrack.Model.Jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.Model.Activity;
import fr.utc.miage.transpitrack.Model.Badge;
import fr.utc.miage.transpitrack.Model.Enum.BadgeType;
import fr.utc.miage.transpitrack.Model.User;
import fr.utc.miage.transpitrack.Model.UserBadge;

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

            if (badge.getBadgeType() == BadgeType.DISTANCE) {
                earned = totalDistance >= badge.getThresholdValue();
            } else if (badge.getBadgeType() == BadgeType.ACTIVITY_COUNT) {
                earned = totalCount >= badge.getThresholdValue();
            } else if (badge.getBadgeType() == BadgeType.DURATION) {
                earned = totalDuration >= badge.getThresholdValue();
            }

            if (earned) {
                userBadgeRepository.save(new UserBadge(user, badge, LocalDate.now()));
            }
        }
    }
}
