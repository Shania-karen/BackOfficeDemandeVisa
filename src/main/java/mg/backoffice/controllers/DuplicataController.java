package mg.backoffice.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.backoffice.dto.DuplicataSearchDTO;
import mg.backoffice.dto.DuplicataSansAnterieurDTO;
import mg.backoffice.models.CarteResident;
import mg.backoffice.models.Demande;
import mg.backoffice.repositories.CategorieVisaRepository;
import mg.backoffice.repositories.CarteResidentRepository;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.repositories.DemandeurRepository;
import mg.backoffice.repositories.NationaliteRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.SituationFamilialeRepository;
import mg.backoffice.services.DuplicataService;
import mg.backoffice.services.DuplicataService.DuplicataSansAnterieurResponse;

@Controller
@RequestMapping("/duplicata")
public class DuplicataController {

    @Autowired private DuplicataService duplicataService;
    @Autowired private CarteResidentRepository carteResidentRepository;
    @Autowired private DemandeRepository demandeRepository;
    @Autowired private NationaliteRepository nationaliteRepository;
    @Autowired private SituationFamilialeRepository situationFamilialeRepository;
    @Autowired private PieceJustificativeRepository pieceRepository;
    @Autowired private DemandeurRepository demandeurRepository;

    /**
     * Page de choix : avec antérieur ou sans antérieur
     */
    @GetMapping
    public String accueil() {
        return "duplicata/accueil";
    }

    /**
     * Formulaire de recherche - Duplicata avec antérieur
     */
    @GetMapping("/avec-anterieur/recherche")
    public String rechercheCarteResident(Model model) {
        model.addAttribute("searchDTO", new DuplicataSearchDTO());
        return "duplicata/recherche";
    }

    /**
     * Traiter la recherche de carte résident
     */
    @PostMapping("/avec-anterieur/recherche")
    public String rechercherCarteResident(@ModelAttribute DuplicataSearchDTO searchDTO, Model model) {
        Optional<CarteResident> carte = carteResidentRepository.findByNumeroCarte(searchDTO.getNumeroCarte().trim());
        
        if (carte.isEmpty()) {
            model.addAttribute("errorMessage", "Carte résident introuvable. Voulez-vous utiliser la procédure sans données antérieures ?");
            model.addAttribute("searchDTO", searchDTO);
            return "duplicata/recherche";
        }

        try {
            Demande demande = duplicataService.creerDuplicataAvecAnterieur(searchDTO);
            return "redirect:/duplicata/recap/" + demande.getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("searchDTO", searchDTO);
            return "duplicata/recherche";
        }
    }

    /**
     * Formulaire de saisie - Duplicata sans antérieur
     */
    @GetMapping("/sans-anterieur/saisie")
    public String formulaireSansAnterieur(Model model) {
        model.addAttribute("form", new DuplicataSansAnterieurDTO());
        model.addAttribute("nationalites", nationaliteRepository.findAll());
        model.addAttribute("situations", situationFamilialeRepository.findAll());
        model.addAttribute("pieces", pieceRepository.findAll());
        return "duplicata/saisie";
    }

    /**
     * Soumettre le formulaire sans antérieur
     */
    @PostMapping("/sans-anterieur/saisie")
    public String soumettreSansAnterieur(@ModelAttribute DuplicataSansAnterieurDTO form, Model model) {
        try {
            DuplicataSansAnterieurResponse response = duplicataService.creerDuplicataSansAnterieur(form);
            // Redirection vers la fiche récapitulative de la demande duplicata
            return "redirect:/duplicata/recap/" + response.getDemandeDuplicata().getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("nationalites", nationaliteRepository.findAll());
            model.addAttribute("situations", situationFamilialeRepository.findAll());
            model.addAttribute("pieces", pieceRepository.findAll());
            return "duplicata/saisie";
        }
    }

    /**
     * Fiche récapitulative
     */
    @GetMapping("/recap/{id}")
    public String afficherRecapitulatif(@PathVariable("id") Integer id, Model model) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée."));
        model.addAttribute("demande", demande);
        return "duplicata/recap";
    }
}
