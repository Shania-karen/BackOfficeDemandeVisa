package mg.backoffice.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.backoffice.dto.DuplicataSearchDTO;
import mg.backoffice.dto.DuplicataSansAnterieurDTO;
import mg.backoffice.models.CarteResident;
import mg.backoffice.models.Demande;
import mg.backoffice.models.Demandeur;
import mg.backoffice.models.HistoriqueStatusDemande;
import mg.backoffice.models.Passeport;
import mg.backoffice.models.Status;
import mg.backoffice.models.TypeDemande;
import mg.backoffice.repositories.CarteResidentRepository;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.repositories.DemandeurRepository;
import mg.backoffice.repositories.HistoriqueStatusDemandeRepository;
import mg.backoffice.repositories.NationaliteRepository;
import mg.backoffice.repositories.PasseportRepository;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.SituationFamilialeRepository;
import mg.backoffice.repositories.StatusRepository;
import mg.backoffice.repositories.TypeDemandeRepository;

@Service
public class DuplicataService {

    @Autowired private CarteResidentRepository carteResidentRepository;
    @Autowired private DemandeRepository demandeRepository;
    @Autowired private HistoriqueStatusDemandeRepository historiqueStatusDemandeRepository;
    @Autowired private DemandeurRepository demandeurRepository;
    @Autowired private PasseportRepository passeportRepository;
    @Autowired private PieceJustificativeRepository pieceRepository;
    @Autowired private SituationFamilialeRepository situationFamilialeRepository;
    @Autowired private NationaliteRepository nationaliteRepository;
    @Autowired private StatusRepository statusRepository;
    @Autowired private TypeDemandeRepository typeDemandeRepository;

    /**
     * Cas 1 : Duplicata avec données antérieures
     * Rechercher une carte résident existante et créer une demande DUPLICATA au statut CREEE
     */
    @Transactional
    public Demande creerDuplicataAvecAnterieur(DuplicataSearchDTO searchDTO) {
        String numeroCarte = searchDTO.getNumeroCarte().trim();
        
        CarteResident carteResident = carteResidentRepository.findByNumeroCarte(numeroCarte)
                .orElseThrow(() -> new RuntimeException("Carte résidente '" + numeroCarte + "' introuvable."));

        // Vérifier que la carte n'a pas expiré
        if (carteResident.getDateExpiration().isBefore(LocalDate.now())) {
            throw new RuntimeException("La carte résidente a expiré. Veuillez utiliser la procédure sans données antérieures.");
        }

        // Récupérer le demandeur via le passeport lié à la carte
        Demandeur demandeur = carteResident.getPasseport().getDemandeur();

        // Créer la demande DUPLICATA
        TypeDemande typeDuplicata = typeDemandeRepository.findById(1)  // ID 1 = Duplicata
                .orElseThrow(() -> new RuntimeException("Type 'Duplicata' introuvable."));

        Demande demandeDuplicata = new Demande();
        demandeDuplicata.setDateDemande(LocalDate.now());
        demandeDuplicata.setDateTraitement(LocalDate.now());
        demandeDuplicata.setTypeDemande(typeDuplicata);
        demandeDuplicata.setDemandeur(demandeur);
        demandeDuplicata = demandeRepository.save(demandeDuplicata);

        // Créer l'historique au statut CREEE
        Status statusCreee = statusRepository.findByCode("CREEE")
                .orElseThrow(() -> new RuntimeException("Statut 'CREEE' introuvable."));

        HistoriqueStatusDemande historique = new HistoriqueStatusDemande();
        historique.setDemande(demandeDuplicata);
        historique.setStatus(statusCreee);
        historique.setDate_status(LocalDateTime.now());
        historique.setAdmin(null);
        historiqueStatusDemandeRepository.save(historique);

        return demandeDuplicata;
    }

    /**
     * Cas 2 : Duplicata sans données antérieures
     * Créer un NOUVEAU_TITRE au statut APPROUVEE + une demande DUPLICATA au statut CREEE
     * Création atomique (transactionnelle)
     */
    @Transactional
    public DuplicataSansAnterieurResponse creerDuplicataSansAnterieur(DuplicataSansAnterieurDTO form) {
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

        // Créer le passeport
        Passeport passeport = new Passeport();
        passeport.setNumeroPasseport(form.getNumeroPasseport());
        passeport.setDateNaissance(form.getDateNaissance());
        passeport.setDateDelivrance(form.getDateDelivrancePasseport());
        passeport.setDateExpiration(form.getDateExpirationPasseport());
        passeport.setDemandeur(demandeur);
        passeport = passeportRepository.save(passeport);

        // Créer la carte résident
        CarteResident carteResident = new CarteResident();
        carteResident.setNumeroCarte(form.getNumeroCarte());
        carteResident.setDateEntreeTerritoire(form.getDateEntreeCarteResident());
        carteResident.setDateSortieTerritoire(form.getDateSortieCarteResident());
        carteResident.setDateDelivrance(form.getDateDelivranceCarteResident());
        carteResident.setDateExpiration(form.getDateExpirationCarteResident());
        carteResident.setPasseport(passeport);
        carteResident = carteResidentRepository.save(carteResident);

        // CRÉER DEMANDE NOUVEAU_TITRE au statut APPROUVEE
        TypeDemande typeNouveau = typeDemandeRepository.findById(3)  // ID 3 = Nouveau titre
                .orElseThrow(() -> new RuntimeException("Type 'Nouveau titre' introuvable."));

        Demande demandeNouveauTitre = new Demande();
        demandeNouveauTitre.setDateDemande(LocalDate.now());
        demandeNouveauTitre.setDateTraitement(LocalDate.now());
        demandeNouveauTitre.setTypeDemande(typeNouveau);
        demandeNouveauTitre.setDemandeur(demandeur);
        demandeNouveauTitre = demandeRepository.save(demandeNouveauTitre);

        Status statusApprouvee = statusRepository.findByCode("APPROUVEE")
                .orElseThrow(() -> new RuntimeException("Statut 'APPROUVEE' introuvable."));

        HistoriqueStatusDemande historiqueNouveau = new HistoriqueStatusDemande();
        historiqueNouveau.setDemande(demandeNouveauTitre);
        historiqueNouveau.setStatus(statusApprouvee);
        historiqueNouveau.setDate_status(LocalDateTime.now());
        historiqueNouveau.setAdmin(null);
        historiqueStatusDemandeRepository.save(historiqueNouveau);

        // CRÉER DEMANDE DUPLICATA au statut CREEE
        TypeDemande typeDuplicata = typeDemandeRepository.findById(1)  // ID 1 = Duplicata
                .orElseThrow(() -> new RuntimeException("Type 'Duplicata' introuvable."));

        Demande demandeDuplicata = new Demande();
        demandeDuplicata.setDateDemande(LocalDate.now());
        demandeDuplicata.setDateTraitement(LocalDate.now());
        demandeDuplicata.setTypeDemande(typeDuplicata);
        demandeDuplicata.setDemandeur(demandeur);
        demandeDuplicata = demandeRepository.save(demandeDuplicata);

        Status statusCreee = statusRepository.findByCode("CREEE")
                .orElseThrow(() -> new RuntimeException("Statut 'CREEE' introuvable."));

        HistoriqueStatusDemande historiqueDuplicata = new HistoriqueStatusDemande();
        historiqueDuplicata.setDemande(demandeDuplicata);
        historiqueDuplicata.setStatus(statusCreee);
        historiqueDuplicata.setDate_status(LocalDateTime.now());
        historiqueDuplicata.setAdmin(null);
        historiqueStatusDemandeRepository.save(historiqueDuplicata);

        // Retourner les deux demandes créées
        return new DuplicataSansAnterieurResponse(demandeNouveauTitre, demandeDuplicata, carteResident);
    }

    /**
     * DTO de réponse pour Duplicata sans antérieur
     */
    public static class DuplicataSansAnterieurResponse {
        private Demande demandeNouveauTitre;
        private Demande demandeDuplicata;
        private CarteResident carteResident;

        public DuplicataSansAnterieurResponse(Demande demandeNouveauTitre, Demande demandeDuplicata, CarteResident carteResident) {
            this.demandeNouveauTitre = demandeNouveauTitre;
            this.demandeDuplicata = demandeDuplicata;
            this.carteResident = carteResident;
        }

        public Demande getDemandeNouveauTitre() { return demandeNouveauTitre; }
        public Demande getDemandeDuplicata() { return demandeDuplicata; }
        public CarteResident getCarteResident() { return carteResident; }
    }
}
