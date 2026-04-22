package mg.backoffice.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.backoffice.dto.DemandeFormDTO;
import mg.backoffice.models.VisaTransformable;
import mg.backoffice.repositories.PieceJustificativeRepository;
import mg.backoffice.repositories.VisaTransformableRepository;

@Service
public class DemandeService {

    @Autowired
    private VisaTransformableRepository visaTransformableRepository;

    @Autowired
    private PieceJustificativeRepository pieceRepository;

@Transactional
    public void soumettreDemande(DemandeFormDTO form) {
        
        // --- NOUVELLE RÈGLE MÉTIER : Gestion du nom de jeune fille ---
        if ("M".equalsIgnoreCase(form.getGenre())) {
            // Si c'est un homme, on force le nom de jeune fille à null par sécurité
            // même si le frontend a envoyé quelque chose par erreur.
            form.setNomJeuneFille(null);
        } else if ("F".equalsIgnoreCase(form.getGenre())) {
            // Optionnel : tu pourrais rajouter une vérification ici selon la situation familiale
            // (ex: si mariée, le nom de jeune fille pourrait être obligatoire).
        } else {
            throw new RuntimeException("Demande Invalide : Le genre doit être 'M' ou 'F'.");
        }

        // 1. Vérification du Visa Transformable existant (en ignorant les espaces vides)
        String numeroVisaForm = form.getRefVisaTransformable() != null ? form.getRefVisaTransformable().trim() : "";
        VisaTransformable visaActuel = visaTransformableRepository.findByNumeroVisa(numeroVisaForm)
                .orElseThrow(() -> new RuntimeException("Demande Invalide : Référence de visa transformable au numero '" + numeroVisaForm + "' introuvable."));

        if (visaActuel.getDateExpiration().isBefore(LocalDate.now())) {
            throw new RuntimeException("Demande Invalide : Le visa transformable a expiré.");
        }

        // 2. Vérification des pièces justificatives obligatoires
        List<Integer> idsPiecesObligatoires = pieceRepository.findIdsPiecesObligatoires(form.getIdCategorieVisa());
        boolean tousLesDossiersPresents = form.getIdsPiecesFournies().containsAll(idsPiecesObligatoires);
        
        if (!tousLesDossiersPresents) {
            throw new RuntimeException("Demande Invalide : Un ou plusieurs dossiers obligatoires sont manquants.");
        }

        // 3. Sauvegarde en base de données
        System.out.println("La demande est valide ! Prêt pour l'insertion en base.");
    }
}