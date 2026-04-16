package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.model.Commentary;

public interface CommentaryRepository extends JpaRepository<Commentary, Long> {
    
    List<Commentary> findByAuthorIdAndActivityId(Long authorId, Long activityId);

    List<Commentary> findByActivityIdOrderByIdDesc(Long activityId);
}