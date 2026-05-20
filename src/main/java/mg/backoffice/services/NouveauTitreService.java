package mg.backoffice.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.backoffice.dto.NouveauTitreDTO;
import mg.backoffice.models.CarteResident;
import mg.backoffice.models.CategorieVisa;
import mg.backoffice.models.Demande;
import mg.backoffice.models.Demandeur;
import mg.backoffice.models.HistoriqueStatusDemande;
import mg.backoffice.models.Passeport;
import mg.backoffice.models.PieceDemande;
import mg.backoffice.models.Status;
import mg.backoffice.models.TypeDemande;
import mg.backoffice.repositories.CarteResidentRepository;
import mg.backoffice.repositories.CategorieVisaRepository;
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

@Service
public class NouveauTitreService {

    @Autowired private CarteResidentRepository carteResidentRepository;
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
    @Autowired private CategorieVisaRepository categorieVisaRepository;

    /**
     * Récupérer tous les individus disponibles
     */
    public List<Demandeur> obtenirTousLesIndividus() {
        return demandeurRepository.findAll();
    }

    /**
     * Obtenir les détails d'un individu
     */
    public Demandeur obtenirIndividuParId(Integer id) {
        return demandeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Individu avec l'ID " + id + " introuvable."));
    }

    /**
     * Créer une demande de Nouveau Titre
     * - Réutilise un individu existant ou en crée un nouveau
     * - Crée un passeport
     * - Crée une carte résident
     * - Crée une demande Nouveau Titre au statut APPROUVEE
     */
    @Transactional
    public NouveauTitreResponse creerNouveauTitre(NouveauTitreDTO form) {
        Demandeur demandeur;
        
        // Si un individu existant est fourni, le réutiliser
        if (form.getIdIndividuExistant() != null && form.getIdIndividuExistant() > 0) {
            demandeur = demandeurRepository.findById(form.getIdIndividuExistant())
                    .orElseThrow(() -> new RuntimeException("Individu avec l'ID " + form.getIdIndividuExistant() + " introuvable."));
            
            // Mettre à jour ses données si elles ont changé
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
        } else {
            // Créer un nouvel individu
            demandeur = new Demandeur();
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

        // CRÉER DEMANDE NOUVEAU_TITRE au statut APPROUVEE (VAL)
        TypeDemande typeNouveau = typeDemandeRepository.findById(3)  // ID 3 = Nouveau titre
                .orElseThrow(() -> new RuntimeException("Type 'Nouveau titre' introuvable."));

        Demande demandeNouveauTitre = new Demande();
        demandeNouveauTitre.setDateDemande(LocalDate.now());
        demandeNouveauTitre.setDateTraitement(LocalDate.now());
        demandeNouveauTitre.setTypeDemande(typeNouveau);
        demandeNouveauTitre.setDemandeur(demandeur);
        
        // Définir la catégorie de visa
        CategorieVisa categorieVisa = categorieVisaRepository.findById(form.getIdCategorieVisa())
                .orElseThrow(() -> new RuntimeException("Catégorie de visa non trouvée."));
        demandeNouveauTitre.setCategorieVisa(categorieVisa);
        
        demandeNouveauTitre = demandeRepository.save(demandeNouveauTitre);

        Status statusApprouvee = statusRepository.findByCode("VAL")
                .orElseThrow(() -> new RuntimeException("Statut 'VAL' (Validé) introuvable."));

        HistoriqueStatusDemande historiqueNouveau = new HistoriqueStatusDemande();
        historiqueNouveau.setDemande(demandeNouveauTitre);
        historiqueNouveau.setStatus(statusApprouvee);
        historiqueNouveau.setDate_status(LocalDateTime.now());
        historiqueNouveau.setAdmin(null);
        historiqueStatusDemandeRepository.save(historiqueNouveau);

        // Enregistrer les pièces justificatives fournies
        if (form.getIdsPiecesFournies() != null && !form.getIdsPiecesFournies().isEmpty()) {
            for (Integer idPiece : form.getIdsPiecesFournies()) {
                PieceDemande pieceDemande = new PieceDemande();
                pieceDemande.setDemande(demandeNouveauTitre);
                pieceDemande.setPiece(pieceRepository.findById(idPiece)
                        .orElseThrow(() -> new RuntimeException("Pièce justificative avec ID " + idPiece + " introuvable")));
                pieceDemandeRepository.save(pieceDemande);
            }
        }

        // Générer un token QR unique pour cette demande
        String tokenQR = UUID.randomUUID().toString();
        demandeNouveauTitre.setQrToken(tokenQR);
        demandeNouveauTitre.setDateQrGenere(LocalDateTime.now());
        demandeNouveauTitre = demandeRepository.save(demandeNouveauTitre);

        // Retourner les données créées
        return new NouveauTitreResponse(demandeNouveauTitre, carteResident);
    }

    /**
     * DTO de réponse pour Nouveau Titre
     */
    public static class NouveauTitreResponse {
        private Demande demandeNouveauTitre;
        private CarteResident carteResident;

        public NouveauTitreResponse(Demande demandeNouveauTitre, CarteResident carteResident) {
            this.demandeNouveauTitre = demandeNouveauTitre;
            this.carteResident = carteResident;
        }

        public Demande getDemandeNouveauTitre() { return demandeNouveauTitre; }
        public CarteResident getCarteResident() { return carteResident; }
    }
}
