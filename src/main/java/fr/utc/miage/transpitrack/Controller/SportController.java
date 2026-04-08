package fr.utc.miage.transpitrack.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import fr.utc.miage.transpitrack.Model.Sport;
import fr.utc.miage.transpitrack.Model.Jpa.SportTypeService;
import fr.utc.miage.transpitrack.Model.Jpa.SportService;

@Controller
@RequestMapping("/sports")
public class SportController {
    
    @Autowired
    private SportService sportService;

    @Autowired
    private SportTypeService sportTypeService;

    @GetMapping("")
    public String listSports(Model model) {
        model.addAttribute("sports", sportService.getAllSports());
        return "sports/list";
    }

    @GetMapping("/add")
    public String addSport(Model model) {
        model.addAttribute("sport", new Sport());
        model.addAttribute("sportTypes", sportTypeService.getAllSportTypes());
        return "sports/add";
    }

    @PostMapping("/add")
    public String saveSport(Sport sport) {
        sportService.save(sport);
        return "redirect:/sports";
    }
    
}
