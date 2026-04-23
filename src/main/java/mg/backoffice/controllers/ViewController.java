package mg.backoffice.controllers;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import mg.backoffice.dto.DemandeFormDTO;
import mg.backoffice.models.Administrateur;
import mg.backoffice.models.Demande;
import mg.backoffice.models.HistoriqueStatusDemande;
import mg.backoffice.models.Status;
import mg.backoffice.repositories.AdministrateurRepository;
import mg.backoffice.repositories.CategorieVisaRepository;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.repositories.HistoriqueStatusDemandeRepository;
import mg.backoffice.repositories.NationaliteRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.SituationFamilialeRepository;
import mg.backoffice.repositories.StatusRepository;
import mg.backoffice.repositories.TypeDemandeRepository;

@Controller
public class ViewController {

    private static final Logger logger = LoggerFactory.getLogger(ViewController.class);

    @Autowired private NationaliteRepository nationaliteRepo;
    @Autowired private SituationFamilialeRepository situationRepo;
    @Autowired private CategorieVisaRepository categorieRepo;
    @Autowired private PieceJustificativeRepository pieceRepo;
    @Autowired private TypeDemandeRepository typeDemandeRepo;
    @Autowired private DemandeRepository demandeRepo;
    @Autowired private HistoriqueStatusDemandeRepository historiqueRepo;
    @Autowired private StatusRepository statusRepo;
    @Autowired private AdministrateurRepository adminRepo;

    /**
     * Page d'accueil pour les demandes
     */
    @GetMapping("/demandes")
    public String accueilDemandes() {
        return "demandes-accueil";
    }

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
        logger.info("=== DETAIL DEMANDE - ID: {} ===", id);
        Optional<Demande> demande = demandeRepo.findByIdWithRelations(id);
        
        if (demande.isPresent()) {
            Demande d = demande.get();
            logger.info("Demande trouvée - ID: {}", d.getId());
            logger.info("Demandeur: {}", d.getDemandeur() != null ? d.getDemandeur().getNom() + " " + d.getDemandeur().getPrenom() : "NULL");
            logger.info("Email: {}", d.getDemandeur() != null ? d.getDemandeur().getEmail() : "NULL");
            logger.info("Contact: {}", d.getDemandeur() != null ? d.getDemandeur().getContact() : "NULL");
            logger.info("Visa Transformable: {}", d.getVisaTransformable() != null ? d.getVisaTransformable().getNumeroVisa() : "NULL");
            logger.info("Categorie: {}", d.getCategorieVisa() != null ? d.getCategorieVisa().getLibelle() : "NULL");
            model.addAttribute("demande", d);
        } else {
            logger.warn("Demande NOT FOUND - ID: {}", id);
            model.addAttribute("demande", null);
        }
        return "detail-demande";
    }

    private void changerStatutDemande(Integer idDemande, String codeStatut) {
        logger.info("=== CHANGER STATUT - ID: {}, Code: {} ===", idDemande, codeStatut);
        
        Demande demande = demandeRepo.findById(idDemande).orElseThrow();
        logger.info("Demande: id={}, dateDemande={}", demande.getId(), demande.getDateDemande());
        
        Status status = statusRepo.findByCode(codeStatut).orElseThrow();
        logger.info("Status trouvé: code={}, libelle={}", status.getCode(), status.getLibelle());
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        logger.info("Admin: {}", username);
        
        Administrateur admin = adminRepo.findByIdentifiant(username).orElseThrow();

        HistoriqueStatusDemande historique = new HistoriqueStatusDemande();
        historique.setDemande(demande);
        historique.setStatus(status);
        historique.setAdmin(admin);
        historique.setDate_status(LocalDateTime.now());
        
        historiqueRepo.save(historique);
        logger.info("✓ Historique sauvegardé avec succès");
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
        model.addAttribute("typesDemande", typeDemandeRepo.findAll());
        model.addAttribute("pieces", pieceRepo.findAll());
        model.addAttribute("demandeForm", new DemandeFormDTO());
        return "demande-form";
    }

    @PostMapping("/demande-visa")
    public String soumettreFormulaire(@ModelAttribute DemandeFormDTO demandeForm, Model model) {

        System.out.println("Formulaire soumis : " + demandeForm);
        

        return "redirect:/demande-visa?success=true";
    }
}