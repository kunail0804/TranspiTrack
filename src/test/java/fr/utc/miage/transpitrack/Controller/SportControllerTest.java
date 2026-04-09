package fr.utc.miage.transpitrack.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;
import fr.utc.miage.transpitrack.Model.Jpa.SportTypeService;

@ExtendWith(MockitoExtension.class)
class SportControllerTest {

    @Mock
    private SportService sportService;

    @Mock
    private SportTypeService sportTypeService;

    @Mock
    private Model model;

    @InjectMocks
    private SportController sportController;

    @Test
    void listSportsShouldReturnListView() {
        String view = sportController.listSports(model);

        assertEquals("sports/list", view);
        verify(model).addAttribute(eq("sports"), any());
    }

    @Test
    void addSportShouldReturnAddView() {
        String view = sportController.addSport(model);

        assertEquals("sports/add", view);
        verify(model).addAttribute(eq("sport"), any(Sport.class));
        verify(model).addAttribute(eq("sportTypes"), any());
    }

    @Test
    void saveSportShouldSaveAndRedirect() {
        Sport sport = new Sport();

        String view = sportController.saveSport(sport);

        assertEquals("redirect:/sports", view);
        verify(sportService).save(sport);
    }
}
