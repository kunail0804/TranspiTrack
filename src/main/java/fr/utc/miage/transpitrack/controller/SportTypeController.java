package fr.utc.miage.transpitrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.utc.miage.transpitrack.model.SportType;
import fr.utc.miage.transpitrack.model.jpa.SportTypeService;

/**
 * Spring MVC controller handling sport-type management under {@code /sport-types}.
 * <p>
 * Sport types are top-level categories (e.g. "Endurance", "Strength") that group
 * individual {@link fr.utc.miage.transpitrack.model.Sport} entries. This controller
 * allows administrators to list existing sport types and create new ones.
 * </p>
 */
@Controller
@RequestMapping("/sport-types")
public class SportTypeController {

    /** Service for sport-type CRUD operations. */
    @Autowired
    private SportTypeService sportTypeService;

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public SportTypeController() {
        // Spring-managed bean.
    }

    /**
     * Displays the list of all sport types.
     *
     * @param model the Spring MVC model
     * @return the {@code sport-types/list} view
     */
    @RequestMapping("")
    public String listSportTypes(Model model) {
        model.addAttribute("sportTypes", sportTypeService.getAllSportTypes());
        return "sport-types/list";
    }

    /**
     * Displays the form for adding a new sport type.
     * <p>
     * Populates the model with an empty {@link SportType} instance bound to the form.
     * </p>
     *
     * @param model the Spring MVC model
     * @return the {@code sport-types/add} view
     */
    @GetMapping("/add")
    public String addSportType(Model model) {
        model.addAttribute("sportType", new SportType());
        return "sport-types/add";
    }

    /**
     * Processes the add-sport-type form submission and persists the new sport type.
     *
     * @param sportType the {@link SportType} instance populated by Spring MVC from the form data
     * @return a redirect to the sport-type list
     */
    @PostMapping("/add")
    public String saveSportType(SportType sportType) {
        sportTypeService.save(sportType);
        return "redirect:/sport-types";
    }
}
