package fr.utc.miage.transpitrack.model.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.model.Commentary;

@ExtendWith(MockitoExtension.class)
class CommentaryServiceTest {

    @Mock
    private CommentaryRepository commentaryRepository;

    @InjectMocks
    private CommentaryService commentaryService;

    // ─────────────────────────────────────────────
    // createCommentary
    // ─────────────────────────────────────────────
    @Test
    void createCommentaryShouldSaveAndReturnCommentary() {

        Commentary commentary = new Commentary();

        when(commentaryRepository.save(commentary)).thenReturn(commentary);

        Commentary result = commentaryService.createCommentary(commentary);

        assertEquals(commentary, result);
        verify(commentaryRepository).save(commentary);
    }

    // ─────────────────────────────────────────────
    // getCommentariesByActivityId
    // ─────────────────────────────────────────────
    @Test
    void getCommentariesByActivityIdShouldReturnList() {

        Long activityId = 10L;

        List<Commentary> expected = List.of(new Commentary(), new Commentary());

        when(commentaryRepository.findByActivityIdOrderByIdDesc(activityId))
                .thenReturn(expected);

        List<Commentary> result =
                commentaryService.getCommentariesByActivityId(activityId);

        assertEquals(expected, result);
        verify(commentaryRepository)
                .findByActivityIdOrderByIdDesc(activityId);
    }

    // ─────────────────────────────────────────────
    // getCommentariesByAuthorIdAndActivityId
    // ─────────────────────────────────────────────
    @Test
    void getCommentariesByAuthorIdAndActivityIdShouldReturnList() {

        Long authorId = 1L;
        Long activityId = 10L;

        List<Commentary> expected = List.of(new Commentary());

        when(commentaryRepository.findByAuthorIdAndActivityId(authorId, activityId))
                .thenReturn(expected);

        List<Commentary> result =
                commentaryService.getCommentariesByAuthorIdAndActivityId(authorId, activityId);

        assertEquals(expected, result);

        verify(commentaryRepository)
                .findByAuthorIdAndActivityId(authorId, activityId);
    }

    // ─────────────────────────────────────────────
    // getCommentaryById - found
    // ─────────────────────────────────────────────
    @Test
    void getCommentaryByIdShouldReturnCommentaryWhenFound() {

        Long id = 5L;
        Commentary commentary = new Commentary();

        when(commentaryRepository.findById(id))
                .thenReturn(Optional.of(commentary));

        Commentary result = commentaryService.getCommentaryById(id);

        assertEquals(commentary, result);
    }

    // ─────────────────────────────────────────────
    // getCommentaryById - not found
    // ─────────────────────────────────────────────
    @Test
    void getCommentaryByIdShouldReturnNullWhenNotFound() {

        Long id = 5L;

        when(commentaryRepository.findById(id))
                .thenReturn(Optional.empty());

        Commentary result = commentaryService.getCommentaryById(id);

        assertEquals(null, result);
    }
}