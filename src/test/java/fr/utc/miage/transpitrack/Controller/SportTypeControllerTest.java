package fr.utc.miage.transpitrack.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import fr.utc.miage.transpitrack.controller.SportTypeController;
import fr.utc.miage.transpitrack.model.SportType;
import fr.utc.miage.transpitrack.model.jpa.SportTypeService;

@ExtendWith(MockitoExtension.class)
class SportTypeControllerTest {

    @Mock
    private SportTypeService sportTypeService;

    @Mock
    private Model model;

    @InjectMocks
    private SportTypeController sportTypeController;

    @Test
    void listSportTypesShouldReturnListView() {
        String view = sportTypeController.listSportTypes(model);

        assertEquals("sport-types/list", view);
        verify(model).addAttribute(eq("sportTypes"), any());
    }

    @Test
    void listSportTypesShouldReturnListViewWhenEmpty() {
        when(sportTypeService.getAllSportTypes()).thenReturn(new java.util.ArrayList<>());

        String view = sportTypeController.listSportTypes(model);

        assertEquals("sport-types/list", view);
        verify(model).addAttribute(eq("sportTypes"), any());
    }

    @Test
    void addSportTypeShouldReturnAddView() {
        String view = sportTypeController.addSportType(model);

        assertEquals("sport-types/add", view);
        verify(model).addAttribute(eq("sportType"), any(SportType.class));
    }

    @Test
    void saveSportTypeShouldSaveAndRedirect() {
        SportType sportType = new SportType();

        String view = sportTypeController.saveSportType(sportType);

        assertEquals("redirect:/sport-types", view);
        verify(sportTypeService).save(sportType);
    }
}
