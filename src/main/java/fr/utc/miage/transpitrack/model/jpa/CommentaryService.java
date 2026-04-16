package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utc.miage.transpitrack.model.Commentary;

/**
 * Service layer for {@link Commentary} entities.
 * <p>
 * Provides creation and retrieval operations for activity commentaries,
 * delegating to {@link CommentaryRepository}.
 * </p>
 */
@Service
public class CommentaryService {

    /** Repository used to persist and retrieve commentaries. */
    @Autowired
    CommentaryRepository commentaryRepository;

    /**
     * Persists a new or updated commentary.
     *
     * @param commentary the {@link Commentary} to save
     * @return the saved commentary (with generated ID if new)
     */
    public Commentary createCommentary(Commentary commentary) {
       return commentaryRepository.save(commentary);
    }

    /**
     * Returns all commentaries for the given activity, ordered by ID descending.
     *
     * @param activityId the ID of the activity
     * @return a list of commentaries ordered newest first
     */
    public List<Commentary> getCommentariesByActivityId(Long activityId) {
        return commentaryRepository.findByActivityIdOrderByIdDesc(activityId);
    }

    /**
     * Returns all commentaries written by the given author on the given activity.
     * Due to the unique constraint, this list contains at most one entry.
     *
     * @param authorId   the ID of the author user
     * @param activityId the ID of the activity
     * @return a list of matching commentaries (0 or 1 element)
     */
    public List<Commentary> getCommentariesByAuthorIdAndActivityId(Long authorId, Long activityId) {
        return commentaryRepository.findByAuthorIdAndActivityId(authorId, activityId);
    }

    /**
     * Returns the commentary with the given ID, or {@code null} if not found.
     *
     * @param commentId the commentary ID
     * @return the matching {@link Commentary}, or {@code null}
     */
    public Commentary getCommentaryById(Long commentId) {
        return commentaryRepository.findById(commentId).orElse(null);
    }
}
