package mg.backoffice.services;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.backoffice.dto.DemandeFormDTO;
import mg.backoffice.models.VisaTransformable;
import mg.backoffice.models.*;
import mg.backoffice.repositories.AdministrateurRepository;
import mg.backoffice.repositories.CategorieVisaRepository;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.repositories.HistoriqueStatusDemandeRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.StatusRepository;
import mg.backoffice.repositories.VisaTransformableRepository;
@Service
public class DemandeService {

    @Autowired
    private VisaTransformableRepository visaTransformableRepository;

    @Autowired
    private PieceJustificativeRepository pieceRepository;

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private HistoriqueStatusDemandeRepository historiqueStatusDemandeRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private CategorieVisaRepository categorieVisaRepository;

    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Transactional
    public void soumettreDemande(DemandeFormDTO form) {

        // 1. Vérification du genre
        if ("M".equalsIgnoreCase(form.getGenre())) {
            form.setNomJeuneFille(null);
        } else if ("F".equalsIgnoreCase(form.getGenre())) {
            // Logique spécifique femme si nécessaire
        } else {
            throw new RuntimeException("Demande Invalide : Le genre doit être 'M' ou 'F'.");
        }

        // 2. Vérification du Visa
        String numeroVisaForm = form.getRefVisaTransformable() != null ? form.getRefVisaTransformable().trim() : "";
        VisaTransformable visaActuel = visaTransformableRepository.findByNumeroVisa(numeroVisaForm)
                .orElseThrow(() -> new RuntimeException("Demande Invalide : Référence de visa transformable au numero '" + numeroVisaForm + "' introuvable."));

        if (visaActuel.getDateExpiration().isBefore(LocalDate.now())) {
            throw new RuntimeException("Demande Invalide : Le visa transformable a expiré.");
        }

        if (visaActuel.getPasseport() == null) {
            throw new RuntimeException("Demande Invalide : Ce visa n'est lié à aucun passeport en base de données.");
        }
        
        String numeroPasseportSaisi = form.getNumeroPasseport() != null ? form.getNumeroPasseport().trim() : "";
        if (!visaActuel.getPasseport().getNumeroPasseport().equalsIgnoreCase(numeroPasseportSaisi)) {
            throw new RuntimeException("Demande Invalide : Le numéro de passeport saisi (" + numeroPasseportSaisi + 
                                       ") ne correspond pas au passeport associé à ce visa transformable (" + 
                                       visaActuel.getPasseport().getNumeroPasseport() + ").");
        }

        // 3. Vérification des pièces
        List<Integer> idsPiecesObligatoires = pieceRepository.findIdsPiecesObligatoires(form.getIdCategorieVisa());
        boolean tousLesDossiersPresents = form.getIdsPiecesFournies().containsAll(idsPiecesObligatoires);
        
        if (!tousLesDossiersPresents) {
            throw new RuntimeException("Demande Invalide : Un ou plusieurs dossiers obligatoires sont manquants.");
        }

        // 4. SAUVEGARDE EN BASE DE DONNÉES
        CategorieVisa categorie = categorieVisaRepository.findById(form.getIdCategorieVisa())
                .orElseThrow(() -> new RuntimeException("Catégorie de visa introuvable."));

        Demande nouvelleDemande = new Demande();
        nouvelleDemande.setDateDemande(LocalDate.now()); 
        nouvelleDemande.setDateTraitement(LocalDate.now()); 
        nouvelleDemande.setVisaTransformable(visaActuel);
        nouvelleDemande.setCategorieVisa(categorie);
        
        nouvelleDemande = demandeRepository.save(nouvelleDemande);

        Status statutAttente = statusRepository.findByCode("ATT")
                .orElseThrow(() -> new RuntimeException("Statut 'ATT' introuvable. Veuillez l'insérer dans la table status."));

        HistoriqueStatusDemande historique = new HistoriqueStatusDemande();
        historique.setDemande(nouvelleDemande);
        historique.setStatus(statutAttente);
        historique.setDate_status(LocalDateTime.now());
        historique.setAdmin(null); // Géré par le système lors de la soumission
        
        historiqueStatusDemandeRepository.save(historique);
    }
}