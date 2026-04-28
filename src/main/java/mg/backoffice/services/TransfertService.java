package mg.backoffice.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.backoffice.dto.TransfertSearchDTO;
import mg.backoffice.dto.TransfertSansAnterieurDTO;
import mg.backoffice.models.Demande;
import mg.backoffice.models.Demandeur;
import mg.backoffice.models.HistoriqueStatusDemande;
import mg.backoffice.models.Passeport;
import mg.backoffice.models.PieceDemande;
import mg.backoffice.models.PieceDemandeId;
import mg.backoffice.models.Status;
import mg.backoffice.models.TypeDemande;
import mg.backoffice.models.Visa;
import mg.backoffice.models.VisaTransformable;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.repositories.DemandeurRepository;
import mg.backoffice.repositories.HistoriqueStatusDemandeRepository;
import mg.backoffice.repositories.NationaliteRepository;
import mg.backoffice.repositories.PasseportRepository;
import mg.backoffice.repositories.PieceDemandeRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.SituationFamilialeRepository;
import mg.backoffice.repositories.StatusRepository;
import mg.backoffice.repositories.TypeDemandeRepository;
import mg.backoffice.repositories.VisaRepository;
import mg.backoffice.repositories.VisaTransformableRepository;

@Service
public class TransfertService {

    @Autowired private VisaRepository visaRepository;
    @Autowired private DemandeRepository demandeRepository;
    @Autowired private HistoriqueStatusDemandeRepository historiqueStatusDemandeRepository;
    @Autowired private DemandeurRepository demandeurRepository;
    @Autowired private PasseportRepository passeportRepository;
    @Autowired private PieceJustificativeRepository pieceRepository;
    @Autowired private PieceDemandeRepository pieceDemandeRepository;
    @Autowired private SituationFamilialeRepository situationFamilialeRepository;
    @Autowired private NationaliteRepository nationaliteRepository;
    @Autowired private StatusRepository statusRepository;
    @Autowired private TypeDemandeRepository typeDemandeRepository;
    @Autowired private VisaTransformableRepository visaTransformableRepository;

    /**
     * Cas 1 : Transfert avec données antérieures
     * Rechercher un visa existant + valider que le passeport correspond
     * Créer une demande TRANSFERT au statut CREEE
     */
    @Transactional
    public Demande creerTransfertAvecAnterieur(TransfertSearchDTO searchDTO) {
        String numeroVisa = searchDTO.getNumeroVisa().trim();
        String numeroPasseport = searchDTO.getNumeroPasseport().trim();

        // Rechercher le visa
        Visa visa = visaRepository.findByNumeroVisa(numeroVisa)
                .orElseThrow(() -> new RuntimeException("Visa '" + numeroVisa + "' introuvable."));

        // Vérifier que le passeport correspond
        if (!visa.getPasseport().getNumeroPasseport().equalsIgnoreCase(numeroPasseport)) {
            throw new RuntimeException("Le passeport saisi ne correspond pas au visa.");
        }

        // Vérifier que le visa n'a pas expiré
        if (visa.getDateExpiration().isBefore(LocalDate.now())) {
            throw new RuntimeException("Le visa a expiré. Veuillez utiliser la procédure sans données antérieures.");
        }

        // Récupérer le demandeur via le passeport
        Demandeur demandeur = visa.getPasseport().getDemandeur();

        // Créer la demande TRANSFERT
        TypeDemande typeTransfert = typeDemandeRepository.findById(2)  // ID 2 = Transfert
                .orElseThrow(() -> new RuntimeException("Type 'Transfert' introuvable."));

        Demande demandeTransfert = new Demande();
        demandeTransfert.setDateDemande(LocalDate.now());
        demandeTransfert.setDateTraitement(LocalDate.now());
        demandeTransfert.setTypeDemande(typeTransfert);
        demandeTransfert.setDemandeur(demandeur);
        demandeTransfert = demandeRepository.save(demandeTransfert);

        // Créer l'historique au statut ATT (En attente)
        Status statusCreee = statusRepository.findByCode("ATT")
                .orElseThrow(() -> new RuntimeException("Statut 'ATT' (En attente) introuvable."));

        HistoriqueStatusDemande historique = new HistoriqueStatusDemande();
        historique.setDemande(demandeTransfert);
        historique.setStatus(statusCreee);
        historique.setDate_status(LocalDateTime.now());
        historique.setAdmin(null);
        historiqueStatusDemandeRepository.save(historique);

        return demandeTransfert;
    }

    /**
     * Cas 2 : Transfert sans données antérieures
     * Créer un NOUVEAU_TITRE au statut APPROUVEE + une demande TRANSFERT au statut CREEE
     * Création atomique avec visa transformable
     */
    @Transactional
    public TransfertSansAnterieurResponse creerTransfertSansAnterieur(TransfertSansAnterieurDTO form) {
        // Créer ou récupérer le demandeur
        Demandeur demandeur = new Demandeur();
        demandeur.setNom(form.getNom());
        demandeur.setPrenom(form.getPrenom());
        demandeur.setGenre(form.getGenre());
        demandeur.setNomJeuneFille(form.getNomJeuneFille());
        demandeur.setDateNaissance(form.getDateNaissance());
        demandeur.setAdresseMada(form.getAdresseMada());
        demandeur.setContact(form.getContact());
        demandeur.setEmail(form.getEmail());

        if (form.getIdSituationFamiliale() != null) {
            demandeur.setSituationFamiliale(
                situationFamilialeRepository.findById(form.getIdSituationFamiliale()).orElse(null)
            );
        }
        if (form.getIdNationalite() != null) {
            demandeur.setNationalite(
                nationaliteRepository.findById(form.getIdNationalite()).orElse(null)
            );
        }
        demandeur = demandeurRepository.save(demandeur);

        // Créer le passeport (OBLIGATOIRE pour transfert)
        Passeport passeport = new Passeport();
        passeport.setNumeroPasseport(form.getNumeroPasseport());
        passeport.setDateNaissance(form.getDateNaissance());
        passeport.setDateDelivrance(form.getDateDelivrancePasseport());
        passeport.setDateExpiration(form.getDateExpirationPasseport());
        passeport.setDemandeur(demandeur);
        passeport = passeportRepository.save(passeport);

        // Créer le visa transformable
        VisaTransformable visaTransformable = new VisaTransformable();
        visaTransformable.setNumeroVisa(form.getNumeroVisaTransformable());
        visaTransformable.setDateEntreeTerritoire(form.getDateEntreeVisaTransformable());
        visaTransformable.setDateSortieTerritoire(form.getDateSortieVisaTransformable());
        visaTransformable.setDateDelivrance(form.getDateDelivranceVisaTransformable());
        visaTransformable.setDateExpiration(form.getDateExpirationVisaTransformable());
        visaTransformable.setPasseport(passeport);
        visaTransformable = visaTransformableRepository.save(visaTransformable);

        // Créer le visa à transformer
        Visa visa = new Visa();
        visa.setNumeroVisa(form.getNumeroVisa());
        visa.setDateEntreeTerritoire(form.getDateEntreeVisa());
        visa.setDateSortieTerritoire(form.getDateSortieVisa());
        visa.setDateDelivrance(form.getDateDelivranceVisa());
        visa.setDateExpiration(form.getDateExpirationVisa());
        visa.setPasseport(passeport);
        visa = visaRepository.save(visa);

        // CRÉER DEMANDE NOUVEAU_TITRE au statut APPROUVEE
        TypeDemande typeNouveau = typeDemandeRepository.findById(3)  // ID 3 = Nouveau titre
                .orElseThrow(() -> new RuntimeException("Type 'Nouveau titre' introuvable."));

        Demande demandeNouveauTitre = new Demande();
        demandeNouveauTitre.setDateDemande(LocalDate.now());
        demandeNouveauTitre.setDateTraitement(LocalDate.now());
        demandeNouveauTitre.setTypeDemande(typeNouveau);
        demandeNouveauTitre.setDemandeur(demandeur);
        demandeNouveauTitre.setVisaTransformable(visaTransformable);
        demandeNouveauTitre = demandeRepository.save(demandeNouveauTitre);

        Status statusApprouvee = statusRepository.findByCode("VAL")
                .orElseThrow(() -> new RuntimeException("Statut 'VAL' (Validé) introuvable."));

        HistoriqueStatusDemande historiqueNouveau = new HistoriqueStatusDemande();
        historiqueNouveau.setDemande(demandeNouveauTitre);
        historiqueNouveau.setStatus(statusApprouvee);
        historiqueNouveau.setDate_status(LocalDateTime.now());
        historiqueNouveau.setAdmin(null);
        historiqueStatusDemandeRepository.save(historiqueNouveau);

        // CRÉER DEMANDE TRANSFERT au statut ATT (En attente)
        TypeDemande typeTransfert = typeDemandeRepository.findById(2)  // ID 2 = Transfert
                .orElseThrow(() -> new RuntimeException("Type 'Transfert' introuvable."));

        Demande demandeTransfert = new Demande();
        demandeTransfert.setDateDemande(LocalDate.now());
        demandeTransfert.setDateTraitement(LocalDate.now());
        demandeTransfert.setTypeDemande(typeTransfert);
        demandeTransfert.setDemandeur(demandeur);
        demandeTransfert.setVisaTransformable(visaTransformable);
        demandeTransfert = demandeRepository.save(demandeTransfert);

        Status statusCreee = statusRepository.findByCode("ATT")
                .orElseThrow(() -> new RuntimeException("Statut 'ATT' (En attente) introuvable."));

        HistoriqueStatusDemande historiqueTransfert = new HistoriqueStatusDemande();
        historiqueTransfert.setDemande(demandeTransfert);
        historiqueTransfert.setStatus(statusCreee);
        historiqueTransfert.setDate_status(LocalDateTime.now());
        historiqueTransfert.setAdmin(null);
        historiqueStatusDemandeRepository.save(historiqueTransfert);

        // Enregistrer les pièces justificatives fournies
        if (form.getIdsPiecesFournies() != null && !form.getIdsPiecesFournies().isEmpty()) {
            for (Integer idPiece : form.getIdsPiecesFournies()) {
                PieceDemande pieceDemande = new PieceDemande();
                pieceDemande.setDemande(demandeTransfert);
                pieceDemande.setPiece(pieceRepository.findById(idPiece)
                        .orElseThrow(() -> new RuntimeException("Pièce justificative avec ID " + idPiece + " introuvable")));
                pieceDemandeRepository.save(pieceDemande);
            }
        }

        // Retourner les deux demandes créées
        return new TransfertSansAnterieurResponse(demandeNouveauTitre, demandeTransfert, visa, visaTransformable);
    }

    /**
     * DTO de réponse pour Transfert sans antérieur
     */
    public static class TransfertSansAnterieurResponse {
        private Demande demandeNouveauTitre;
        private Demande demandeTransfert;
        private Visa visa;
        private VisaTransformable visaTransformable;

        public TransfertSansAnterieurResponse(Demande demandeNouveauTitre, Demande demandeTransfert, Visa visa, VisaTransformable visaTransformable) {
            this.demandeNouveauTitre = demandeNouveauTitre;
            this.demandeTransfert = demandeTransfert;
            this.visa = visa;
            this.visaTransformable = visaTransformable;
        }

        public Demande getDemandeNouveauTitre() { return demandeNouveauTitre; }
        public Demande getDemandeTransfert() { return demandeTransfert; }
        public Visa getVisa() { return visa; }
        public VisaTransformable getVisaTransformable() { return visaTransformable; }
    }
}
