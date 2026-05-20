package mg.backoffice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.backoffice.dto.NouveauTitreDTO;
import mg.backoffice.models.Demande;
import mg.backoffice.models.Demandeur;
import mg.backoffice.repositories.CategorieVisaRepository;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.repositories.DemandeurRepository;
import mg.backoffice.repositories.NationaliteRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.SituationFamilialeRepository;
import mg.backoffice.services.NouveauTitreService;
import mg.backoffice.services.NouveauTitreService.NouveauTitreResponse;

@Controller
@RequestMapping("/nouveau-titre")
public class NouveauTitreController {

    @Autowired private NouveauTitreService nouveauTitreService;
    @Autowired private DemandeRepository demandeRepository;
    @Autowired private DemandeurRepository demandeurRepository;
    @Autowired private NationaliteRepository nationaliteRepository;
    @Autowired private SituationFamilialeRepository situationFamilialeRepository;
    @Autowired private PieceJustificativeRepository pieceRepository;
    @Autowired private CategorieVisaRepository categorieVisaRepository;

    /**
     * Page d'accueil - Affiche la liste de tous les individus à sélectionner
     */
    @GetMapping
    public String accueil(Model model) {
        List<Demandeur> individus = nouveauTitreService.obtenirTousLesIndividus();
        model.addAttribute("individus", individus);
        return "nouveau-titre/accueil";
    }

    /**
     * Formulaire de saisie pré-rempli avec les données d'un individu sélectionné
     */
    @GetMapping("/saisie/{idIndividu}")
    public String formulairePrerempli(@PathVariable Integer idIndividu, Model model) {
        Demandeur individu = nouveauTitreService.obtenirIndividuParId(idIndividu);
        
        // Préremplir le DTO avec les données de l'individu
        NouveauTitreDTO form = new NouveauTitreDTO();
        form.setIdIndividuExistant(idIndividu);  // Pour réutiliser l'individu et éviter duplicate email
        form.setNom(individu.getNom());
        form.setPrenom(individu.getPrenom());
        form.setGenre(individu.getGenre());
        form.setNomJeuneFille(individu.getNomJeuneFille());
        form.setDateNaissance(individu.getDateNaissance());
        form.setAdresseMada(individu.getAdresseMada());
        form.setContact(individu.getContact());
        form.setEmail(individu.getEmail());
        
        if (individu.getSituationFamiliale() != null) {
            form.setIdSituationFamiliale(individu.getSituationFamiliale().getId());
        }
        if (individu.getNationalite() != null) {
            form.setIdNationalite(individu.getNationalite().getId());
        }

        model.addAttribute("form", form);
        model.addAttribute("individu", individu);
        model.addAttribute("nationalites", nationaliteRepository.findAll());
        model.addAttribute("situations", situationFamilialeRepository.findAll());
        model.addAttribute("pieces", pieceRepository.findAll());
        model.addAttribute("categories", categorieVisaRepository.findAll());
        return "nouveau-titre/saisie";
    }

    /**
     * Soumettre le formulaire du Nouveau Titre
     */
    @PostMapping("/saisie")
    public String soumettreSaisie(@ModelAttribute NouveauTitreDTO form, Model model) {
        try {
            NouveauTitreResponse response = nouveauTitreService.creerNouveauTitre(form);
            // Redirection vers la fiche récapitulative de la demande
            return "redirect:/nouveau-titre/recap/" + response.getDemandeNouveauTitre().getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("nationalites", nationaliteRepository.findAll());
            model.addAttribute("situations", situationFamilialeRepository.findAll());
            model.addAttribute("pieces", pieceRepository.findAll());
            return "nouveau-titre/saisie";
        }
    }

    /**
     * Fiche récapitulative du Nouveau Titre créé
     */
    @GetMapping("/recap/{id}")
    public String afficherRecapitulatif(@PathVariable Integer id, Model model) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée."));
        model.addAttribute("demande", demande);
        return "nouveau-titre/recap";
    }

    /**
     * API REST pour récupérer les données d'un individu (JSON)
     * Utile pour le pré-remplissage JavaScript
     */
    @GetMapping("/api/individu/{id}")
    @ResponseBody
    public ResponseEntity<Demandeur> obtenirIndividuJson(@PathVariable Integer id) {
        try {
            Demandeur individu = nouveauTitreService.obtenirIndividuParId(id);
            return ResponseEntity.ok(individu);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
