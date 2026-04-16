package fr.utc.miage.transpitrack.controller;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.UserSport;
import fr.utc.miage.transpitrack.model.enumer.Level;
import fr.utc.miage.transpitrack.model.jpa.SportService;
import fr.utc.miage.transpitrack.model.jpa.UserService;
import fr.utc.miage.transpitrack.model.jpa.UserSportService;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class PreferenceControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private UserSportService userSportService;
    @Mock
    private SportService sportService;
    @Mock
    private Model model;
    @Mock
    private HttpSession session;
    @Mock
    private RedirectAttributes redirectAttrs;

    @InjectMocks
    private PreferenceController preferenceController;

    @Test
    void consultationPreferencesShouldRedirectToLoginWhenNotLoggedIn() {
        String view = preferenceController.consultationPreferences(model, session);

        assertEquals("redirect:/users/formLogin", view);
        verify(model).addAttribute("message", "Il faut être connecte !");
    }

    @Test
    void consultationPreferencesShouldReturnPreferencesViewWhenLoggedIn() {
        User user = new User();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(sportService.getAllSports()).thenReturn(List.of(new Sport()));

        String view = preferenceController.consultationPreferences(model, session);

        assertEquals("users/listPreferences", view);
        verify(model).addAttribute("sportsPreference", user.getSportsPreference());
        verify(model).addAttribute(eq("sports"), any());
    }

    @Test
    void addPreferenceShouldRedirectToLoginWhenNotLoggedIn() {
        String view = preferenceController.addPreference(1L, Level.BEGINNER, session, redirectAttrs);

        assertEquals("redirect:/users/formLogin", view);
    }

    @ParameterizedTest
    @MethodSource("provideNullAddPreferenceParams")
    void addPreferenceShouldRedirectWhenRequiredParamIsNull(Long sportId, Level level) {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = preferenceController.addPreference(sportId, level, session, redirectAttrs);

        assertEquals("redirect:/users/consultationPreferences", view);
    }

    static Stream<Arguments> provideNullAddPreferenceParams() {
        return Stream.of(
            Arguments.of(null, Level.BEGINNER),
            Arguments.of(1L, null)
        );
    }

    @Test
    void addPreferenceShouldFlashErrorWhenSportAlreadyInList() {
        User user = new User();
        Sport sport = new Sport();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(sportService.getSportById(1L)).thenReturn(sport);
        when(userSportService.getUserSportByUserAndSport(user, sport))
                .thenReturn(new UserSport(user, sport, Level.BEGINNER));

        String view = preferenceController.addPreference(1L, Level.BEGINNER, session, redirectAttrs);

        assertEquals("redirect:/users/consultationPreferences", view);
        verify(redirectAttrs).addFlashAttribute("errorMessage", "Ce sport est déjà dans votre liste !");
    }

    @Test
    void addPreferenceShouldSaveAndRedirectWhenValid() {
        User user = new User();
        Sport sport = new Sport();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(sportService.getSportById(1L)).thenReturn(sport);
        when(userSportService.getUserSportByUserAndSport(user, sport)).thenReturn(null);

        String view = preferenceController.addPreference(1L, Level.INTERMEDIATE, session, redirectAttrs);

        assertEquals("redirect:/users/consultationPreferences", view);
        verify(userSportService).createUserSport(any(UserSport.class));
        verify(userService).updateUser(user);
    }

    @Test
    void updateLevelShouldRedirectToLoginWhenNotLoggedIn() {
        String view = preferenceController.updateLevel(1L, Level.ADVANCED, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @ParameterizedTest
    @MethodSource("provideNullUpdateLevelParams")
    void updateLevelShouldRedirectWhenRequiredParamIsNull(Long userSportId, Level level) {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = preferenceController.updateLevel(userSportId, level, session);

        assertEquals("redirect:/users/consultationPreferences", view);
    }

    static Stream<Arguments> provideNullUpdateLevelParams() {
        return Stream.of(
            Arguments.of(null, Level.ADVANCED),
            Arguments.of(1L, null)
        );
    }

    @Test
    void updateLevelShouldUpdateLevelAndRedirectWhenValid() {
        User user = new User();
        UserSport us = new UserSport(user, new Sport(), Level.BEGINNER);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(userSportService.getUserSportById(1L)).thenReturn(us);

        String view = preferenceController.updateLevel(1L, Level.ADVANCED, session);

        assertEquals("redirect:/users/consultationPreferences", view);
        verify(userSportService).updateUserSport(us);
        verify(userService).updateUser(user);
        assertEquals(Level.ADVANCED, us.getLevel());
    }

    @Test
    void deletePreferenceShouldRedirectToLoginWhenNotLoggedIn() {
        String view = preferenceController.deletePreference(1L, session);

        assertEquals("redirect:/users/formLogin", view);
    }

    @Test
    void deletePreferenceShouldRedirectWhenUserSportIdIsNull() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String view = preferenceController.deletePreference(null, session);

        assertEquals("redirect:/users/consultationPreferences", view);
    }

    @Test
    void deletePreferenceShouldDeleteAndRedirectWhenValid() {
        User user = new User();
        UserSport us = new UserSport(user, new Sport(), Level.BEGINNER);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(userSportService.getUserSportById(1L)).thenReturn(us);

        String view = preferenceController.deletePreference(1L, session);

        assertEquals("redirect:/users/consultationPreferences", view);
        verify(userSportService).deleteUserSport(us);
        verify(userService).updateUser(user);
    }
}
