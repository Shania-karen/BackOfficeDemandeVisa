package mg.backoffice.controllers;

import mg.backoffice.models.Nationalite;
import mg.backoffice.services.NationaliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/nationalites")
public class NationaliteController {

    @Autowired
    private NationaliteService nationaliteService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("nationalites", nationaliteService.findAll());
        return "nationalites/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("nationalite", new Nationalite());
        return "nationalites/form";
    }

    @PostMapping
    public String save(@ModelAttribute("nationalite") Nationalite nationalite) {
        nationaliteService.save(nationalite);
        return "redirect:/nationalites";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Nationalite nationalite = nationaliteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid nationalite Id:" + id));
        model.addAttribute("nationalite", nationalite);
        return "nationalites/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        nationaliteService.deleteById(id);
        return "redirect:/nationalites";
    }
}
