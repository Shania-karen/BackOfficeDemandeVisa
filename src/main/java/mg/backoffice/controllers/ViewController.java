package mg.backoffice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import mg.backoffice.dto.DemandeFormDTO;
import mg.backoffice.repositories.CategorieVisaRepository;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.repositories.NationaliteRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.SituationFamilialeRepository;
import mg.backoffice.repositories.HistoriqueStatusDemandeRepository;
import mg.backoffice.repositories.StatusRepository;
import mg.backoffice.repositories.AdministrateurRepository;
import mg.backoffice.models.HistoriqueStatusDemande;
import mg.backoffice.models.Demande;
import mg.backoffice.models.Status;
import mg.backoffice.models.Administrateur;
import java.time.LocalDateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
public class ViewController {

    @Autowired private NationaliteRepository nationaliteRepo;
    @Autowired private SituationFamilialeRepository situationRepo;
    @Autowired private CategorieVisaRepository categorieRepo;
    @Autowired private PieceJustificativeRepository pieceRepo;
    @Autowired private DemandeRepository demandeRepo;
    @Autowired private HistoriqueStatusDemandeRepository historiqueRepo;
    @Autowired private StatusRepository statusRepo;
    @Autowired private AdministrateurRepository adminRepo;

    @GetMapping("/demandes/acceptees")
    public String listerDemandesAcceptees(Model model) {
        model.addAttribute("demandes", demandeRepo.findDemandesAcceptees());
        return "liste-demandes-acceptees";
    }

    @GetMapping("/demandes/attente")
    public String listerDemandesAttente(Model model) {
        model.addAttribute("demandes", demandeRepo.findDemandesEnAttente());
        return "liste-demandes-attente";
    }

    @GetMapping("/demande/{id}")
    public String detailDemande(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("demande", demandeRepo.findById(id).orElse(null));
        return "detail-demande";
    }

    private void changerStatutDemande(Integer idDemande, String codeStatut) {
        Demande demande = demandeRepo.findById(idDemande).orElseThrow();
        Status status = statusRepo.findByCode(codeStatut).orElseThrow();
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        Administrateur admin = adminRepo.findByIdentifiant(username).orElseThrow();

        HistoriqueStatusDemande historique = new HistoriqueStatusDemande();
        historique.setDemande(demande);
        historique.setStatus(status);
        historique.setAdmin(admin);
        historique.setDate_status(LocalDateTime.now());
        
        historiqueRepo.save(historique);
    }

    @PostMapping("/demande/{id}/valider")
    public String validerDemande(@PathVariable("id") Integer id) {
        changerStatutDemande(id, "VAL");
        return "redirect:/demandes/attente?success=valider";
    }

    @PostMapping("/demande/{id}/refuser")
    public String refuserDemande(@PathVariable("id") Integer id) {
        changerStatutDemande(id, "REJ");
        return "redirect:/demandes/attente?success=refuser";
    }

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