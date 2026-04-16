package fr.utc.miage.transpitrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.utc.miage.transpitrack.model.SportType;
import fr.utc.miage.transpitrack.model.jpa.SportTypeService;

@Controller
@RequestMapping("/sport-types")
public class SportTypeController {

    @Autowired
    private SportTypeService sportTypeService;

    @RequestMapping("")
    public String listSportTypes(Model model) {
        model.addAttribute("sportTypes", sportTypeService.getAllSportTypes());
        return "sport-types/list";
    }

    @GetMapping("/add")
    public String addSportType(Model model) {
        model.addAttribute("sportType", new SportType());
        return "sport-types/add";
    }

    @PostMapping("/add")
    public String saveSportType(SportType sportType) {
        sportTypeService.save(sportType);
        return "redirect:/sport-types";
    }
}
