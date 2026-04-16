package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Commentary;

@Service
public class CommentaryService {
    
    @Autowired
    CommentaryRepository commentaryRepository;

    public Commentary createCommentary(Commentary commentary) {
       return commentaryRepository.save(commentary);
    }

    public List<Commentary> getCommentariesByActivityId(Long activityId) {
        return commentaryRepository.findByActivityIdOrderByIdDesc(activityId);
    }

    public List<Commentary> getCommentariesByAuthorIdAndActivityId(Long authorId, Long activityId) {
        return commentaryRepository.findByAuthorIdAndActivityId(authorId, activityId);
    }

    public Commentary getCommentaryById(Long commentId) {
        return commentaryRepository.findById(commentId).orElse(null);
    }
}
