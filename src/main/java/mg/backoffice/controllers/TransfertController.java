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

import mg.backoffice.dto.TransfertSearchDTO;
import mg.backoffice.dto.TransfertSansAnterieurDTO;
import mg.backoffice.models.Demande;
import mg.backoffice.models.Visa;
import mg.backoffice.repositories.CategorieVisaRepository;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.repositories.DemandeurRepository;
import mg.backoffice.repositories.NationaliteRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.SituationFamilialeRepository;
import mg.backoffice.repositories.VisaRepository;
import mg.backoffice.services.TransfertService;
import mg.backoffice.services.TransfertService.TransfertSansAnterieurResponse;

@Controller
@RequestMapping("/transfert")
public class TransfertController {

    @Autowired private TransfertService transfertService;
    @Autowired private DemandeRepository demandeRepository;
    @Autowired private VisaRepository visaRepository;
    @Autowired private NationaliteRepository nationaliteRepository;
    @Autowired private SituationFamilialeRepository situationFamilialeRepository;
    @Autowired private CategorieVisaRepository categorieRepository;
    @Autowired private PieceJustificativeRepository pieceRepository;
    @Autowired private DemandeurRepository demandeurRepository;

    /**
     * Page de choix : avec antérieur ou sans antérieur
     */
    @GetMapping
    public String accueil() {
        return "transfert/accueil";
    }

    /**
     * Formulaire de recherche - Transfert avec antérieur
     */
    @GetMapping("/avec-anterieur/recherche")
    public String rechercheVisa(Model model) {
        model.addAttribute("searchDTO", new TransfertSearchDTO());
        return "transfert/recherche";
    }

    /**
     * Traiter la recherche de visa
     */
    @PostMapping("/avec-anterieur/recherche")
    public String rechercherVisa(@ModelAttribute TransfertSearchDTO searchDTO, Model model) {
        Optional<Visa> visa = visaRepository.findByNumeroVisa(searchDTO.getNumeroVisa().trim());
        
        if (visa.isEmpty()) {
            model.addAttribute("errorMessage", "Visa introuvable. Voulez-vous utiliser la procédure sans données antérieures ?");
            model.addAttribute("searchDTO", searchDTO);
            return "transfert/recherche";
        }

        try {
            Demande demande = transfertService.creerTransfertAvecAnterieur(searchDTO);
            return "redirect:/transfert/recap/" + demande.getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("searchDTO", searchDTO);
            return "transfert/recherche";
        }
    }

    /**
     * Formulaire de saisie - Transfert sans antérieur
     */
    @GetMapping("/sans-anterieur/saisie")
    public String formulaireSansAnterieur(Model model) {
        model.addAttribute("form", new TransfertSansAnterieurDTO());
        model.addAttribute("nationalites", nationaliteRepository.findAll());
        model.addAttribute("situations", situationFamilialeRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("pieces", pieceRepository.findAll());
        return "transfert/saisie";
    }

    /**
     * Soumettre le formulaire sans antérieur
     */
    @PostMapping("/sans-anterieur/saisie")
    public String soumettreSansAnterieur(@ModelAttribute TransfertSansAnterieurDTO form, Model model) {
        try {
            TransfertSansAnterieurResponse response = transfertService.creerTransfertSansAnterieur(form);
            // Redirection vers la fiche récapitulative de la demande transfert
            return "redirect:/transfert/recap/" + response.getDemandeTransfert().getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("nationalites", nationaliteRepository.findAll());
            model.addAttribute("situations", situationFamilialeRepository.findAll());
            model.addAttribute("categories", categorieRepository.findAll());
            model.addAttribute("pieces", pieceRepository.findAll());
            return "transfert/saisie";
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
        return "transfert/recap";
    }
}
