package fr.utc.miage.transpitrack.Model.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.utc.miage.transpitrack.Model.Commentary;

public interface CommentaryRepository extends JpaRepository<Commentary, Long> {
    
    List<Commentary> findByAuthorIdAndActivityId(Long authorId, Long activityId);

    List<Commentary> findByActivityIdOrderByIdDesc(Long activityId);
}