package fr.utc.miage.transpitrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.utc.miage.transpitrack.model.Sport;
import fr.utc.miage.transpitrack.model.jpa.SportService;
import fr.utc.miage.transpitrack.model.jpa.SportTypeService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Spring MVC controller handling sport management under {@code /sports}.
 * <p>
 * Allows administrators to list all sports and add new sports associated with
 * an existing {@link fr.utc.miage.transpitrack.model.SportType}.
 * </p>
 */
@Controller
@RequestMapping("/sports")
public class SportController {

    /** Service for sport CRUD operations. */
    @Autowired
    private SportService sportService;

    /** Service for retrieving all sport types (used to populate the add-sport form). */
    @Autowired
    private SportTypeService sportTypeService;

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public SportController() {
        // Spring-managed bean.
    }

    /**
     * Displays the list of all available sports.
     *
     * @param model the Spring MVC model
     * @return the {@code sports/list} view
     */
    @GetMapping("")
    public String listSports(Model model) {
        model.addAttribute("sports", sportService.getAllSports());
        return "sports/list";
    }

    /**
     * Displays the form for adding a new sport.
     * <p>
     * Populates the model with an empty {@link Sport} instance and the list of
     * all sport types so the user can select the appropriate category.
     * </p>
     *
     * @param model the Spring MVC model
     * @return the {@code sports/add} view
     */
    @GetMapping("/add")
    public String addSport(Model model) {
        model.addAttribute("sport", new Sport());
        model.addAttribute("sportTypes", sportTypeService.getAllSportTypes());
        return "sports/add";
    }

    /**
     * Processes the add-sport form submission and persists the new sport.
     *
     * @param sport the {@link Sport} instance populated by Spring MVC from the form data
     * @return a redirect to the sport list
     */
    @PostMapping("/add")
    public String saveSport(Sport sport) {
        sportService.save(sport);
        return "redirect:/sports";
    }

}
