package mg.backoffice.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.backoffice.dto.DemandeFormDTO;
import mg.backoffice.models.CategorieVisa;
import mg.backoffice.models.Demande;
import mg.backoffice.models.Demandeur;
import mg.backoffice.models.HistoriqueStatusDemande;
import mg.backoffice.models.Passeport;
import mg.backoffice.models.Status;
import mg.backoffice.models.TypeDemande;
import mg.backoffice.models.VisaTransformable;
import mg.backoffice.repositories.CategorieVisaRepository;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.repositories.DemandeurRepository;
import mg.backoffice.repositories.HistoriqueStatusDemandeRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.SituationFamilialeRepository;
import mg.backoffice.repositories.StatusRepository;
import mg.backoffice.repositories.TypeDemandeRepository;
import mg.backoffice.repositories.VisaTransformableRepository;

@Service
public class DemandeService {

    @Autowired private VisaTransformableRepository visaTransformableRepository;
    @Autowired private PieceJustificativeRepository pieceRepository;
    @Autowired private DemandeRepository demandeRepository;
    @Autowired private HistoriqueStatusDemandeRepository historiqueStatusDemandeRepository;
    @Autowired private CategorieVisaRepository categorieVisaRepository;
    @Autowired private SituationFamilialeRepository situationFamilialeRepository;
    @Autowired private StatusRepository statusRepository;
    @Autowired private TypeDemandeRepository typeDemandeRepository;
    @Autowired private DemandeurRepository demandeurRepository; // C'est ta table 'individu'

    @Transactional
  
    @SuppressWarnings("BoxingBoxedValue")
    public void soumettreDemande(DemandeFormDTO form) {

       
        String refVisa = form.getRefVisaTransformable() != null ? form.getRefVisaTransformable().trim() : "";
        VisaTransformable visaActuel = visaTransformableRepository.findByNumeroVisa(refVisa)
                .orElseThrow(() -> new RuntimeException("Demande Invalide : Le visa transformable '" + refVisa + "' est introuvable."));

        if (visaActuel.getDateExpiration().isBefore(LocalDate.now())) {
            throw new RuntimeException("Demande Invalide : Le visa transformable a expiré. Vous êtes en situation irrégulière.");
        }

        Passeport passeportEnBase = visaActuel.getPasseport();

        Demandeur demandeurEnBase = passeportEnBase.getDemandeur(); 

        if (!passeportEnBase.getNumeroPasseport().equalsIgnoreCase(form.getNumeroPasseport().trim())) {
            throw new RuntimeException("Demande Invalide : Le passeport saisi ne correspond pas à ce visa.");
        }

        if (!demandeurEnBase.getNom().equalsIgnoreCase(form.getNom().trim()) || !demandeurEnBase.getPrenom().equalsIgnoreCase(form.getPrenom().trim())) {
            throw new RuntimeException("Demande Invalide : Le nom saisi ne correspond pas au titulaire de ce visa enregistré.");
        }
        if (!demandeurEnBase.getDateNaissance().equals(form.getDateNaissance())) {
            throw new RuntimeException("Demande Invalide : La date de naissance ne correspond pas au titulaire de ce visa enregistré.");
        }
        if (form.getIdNationalite() != null && demandeurEnBase.getNationalite() != null 
            && demandeurEnBase.getNationalite().getId() != form.getIdNationalite()) {
            throw new RuntimeException("Demande Invalide : La nationalité ne correspond pas au titulaire de ce visa enregistré.");
        }

        demandeurEnBase.setAdresseMada(form.getAdresseMada());
        demandeurEnBase.setContact(form.getContact());
        demandeurEnBase.setEmail(form.getEmail().trim());

        if (form.getIdSituationFamiliale() != null) {
            demandeurEnBase.setSituationFamiliale(
                situationFamilialeRepository.findById(form.getIdSituationFamiliale())
                .orElse(demandeurEnBase.getSituationFamiliale())
            );
        }

        demandeurRepository.save(demandeurEnBase);

        List<Integer> idsPiecesObligatoires = pieceRepository.findIdsPiecesObligatoires(form.getIdCategorieVisa());
        if (form.getIdsPiecesFournies() == null || !form.getIdsPiecesFournies().containsAll(idsPiecesObligatoires)) {
            throw new RuntimeException("Demande Invalide : Un ou plusieurs dossiers obligatoires sont manquants.");
        }

        CategorieVisa categorie = categorieVisaRepository.findById(form.getIdCategorieVisa())
                .orElseThrow(() -> new RuntimeException("Catégorie de visa introuvable."));
TypeDemande typeDemande = typeDemandeRepository.findById(form.getIdTypeDemande())
                .orElseThrow(() -> new RuntimeException("Type de demande introuvable."));

        Demande nouvelleDemande = new Demande();
        nouvelleDemande.setDateDemande(LocalDate.now()); 
        nouvelleDemande.setDateTraitement(LocalDate.now()); 
        nouvelleDemande.setVisaTransformable(visaActuel);
        nouvelleDemande.setCategorieVisa(categorie);
        nouvelleDemande.setTypeDemande(typeDemande);
        nouvelleDemande.setDemandeur(demandeurEnBase); 
        
        nouvelleDemande = demandeRepository.save(nouvelleDemande);

        Status statutAttente = statusRepository.findByCode("ATT")
                .orElseThrow(() -> new RuntimeException("Statut 'ATT' introuvable en base."));

        HistoriqueStatusDemande historique = new HistoriqueStatusDemande();
        historique.setDemande(nouvelleDemande);
        historique.setStatus(statutAttente);
        historique.setDate_status(LocalDateTime.now());
        historique.setAdmin(null); 
        
        historiqueStatusDemandeRepository.save(historique);
    }
}