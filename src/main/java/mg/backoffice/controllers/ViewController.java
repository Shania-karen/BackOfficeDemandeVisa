package mg.backoffice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import mg.backoffice.dto.DemandeFormDTO;
import mg.backoffice.repositories.CategorieVisaRepository;
import mg.backoffice.repositories.NationaliteRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.SituationFamilialeRepository;

@Controller
public class ViewController {

    @Autowired private NationaliteRepository nationaliteRepo;
    @Autowired private SituationFamilialeRepository situationRepo;
    @Autowired private CategorieVisaRepository categorieRepo;
    @Autowired private PieceJustificativeRepository pieceRepo;

    @GetMapping("/demande-visa")
    public String afficherFormulaire(Model model) {
        model.addAttribute("nationalites", nationaliteRepo.findAll());
        model.addAttribute("situations", situationRepo.findAll());
        model.addAttribute("categories", categorieRepo.findAll());
        model.addAttribute("pieces", pieceRepo.findAll());
        model.addAttribute("demandeForm", new DemandeFormDTO());
        return "demande-form";
    }

    @PostMapping("/demande-visa")
    public String soumettreFormulaire(@ModelAttribute DemandeFormDTO demandeForm, Model model) {
        // Logique de traitement du formulaire pour le moment (à compléter)
        System.out.println("Formulaire soumis : " + demandeForm);
        
        // Redirection vers une page de succès ou rechargement
        return "redirect:/demande-visa?success=true";
    }
}