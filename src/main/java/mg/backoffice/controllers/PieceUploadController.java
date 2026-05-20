package mg.backoffice.controllers;

import mg.backoffice.models.Demande;
import mg.backoffice.models.PieceDemande;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.services.PieceUploadService;
import mg.backoffice.services.PdfLettreReceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/demandes")
public class PieceUploadController {
    
    @Autowired
    private PieceUploadService pieceUploadService;
    
    @Autowired
    private DemandeRepository demandeRepository;
    
    @Autowired
    private PdfLettreReceptionService pdfLettreReceptionService;
    
    /**
     * Upload d'une photo justificatif
     */
    @PostMapping("/upload-photo")
    public String uploadPhoto(
            @RequestParam("idDemande") int idDemande,
            @RequestParam("idPiece") int idPiece,
            @RequestParam("fichier") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Le fichier est vide");
                return "redirect:/demande/" + idDemande;
            }
            
            pieceUploadService.uploadPhotoJustificatif(idDemande, idPiece, file);
            redirectAttributes.addFlashAttribute("success", "Photo uploadée avec succès");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'upload: " + e.getMessage());
        }
        return "redirect:/demande/" + idDemande;
    }
    
    /**
     * Upload d'un scan justificatif
     */
    @PostMapping("/upload-scan")
    public String uploadScan(
            @RequestParam("idDemande") int idDemande,
            @RequestParam("idPiece") int idPiece,
            @RequestParam("fichier") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Le fichier est vide");
                return "redirect:/demande/" + idDemande;
            }
            
            pieceUploadService.uploadScanJustificatif(idDemande, idPiece, file);
            pieceUploadService.updateEtatDossier(idDemande);
            redirectAttributes.addFlashAttribute("success", "Scan uploadé avec succès");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'upload: " + e.getMessage());
        }
        return "redirect:/demande/" + idDemande;
    }
    
    /**
     * Afficher l'aperçu d'un scan PDF ou photo
     */
    @GetMapping("/apercu/{idDemande}/{idPiece}")
    public ResponseEntity<byte[]> afficherApercu(
            @PathVariable int idDemande,
            @PathVariable int idPiece) {
        
        try {
            PieceDemande pieceDemande = pieceUploadService.getPieceDemande(idDemande, idPiece);
            
            if (pieceDemande == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Préférer le scan au fichier original, puis la photo
            String cheminFichier = pieceDemande.getCheminScan() != null ? pieceDemande.getCheminScan() :
                                   pieceDemande.getCheminPhoto() != null ? pieceDemande.getCheminPhoto() :
                                   pieceDemande.getCheminFichier();
            
            if (cheminFichier == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(cheminFichier);
            if (!Files.exists(filePath)) {
                filePath = Paths.get("uploads", cheminFichier);
                if (!Files.exists(filePath)) {
                    return ResponseEntity.notFound().build();
                }
            }
            
            byte[] fileBytes = Files.readAllBytes(filePath);
            String contentType = determineContentType(cheminFichier);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header("Content-Disposition", "inline; filename=\"piece_" + idPiece + ".pdf\"")
                    .body(fileBytes);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Générer et afficher un PDF avec tous les aperçus des pièces
     */
    @GetMapping("/apercu-tous/{idDemande}")
    public ResponseEntity<byte[]> afficherAperçuTous(@PathVariable int idDemande) {
        try {
            List<PieceDemande> pieces = pieceUploadService.getAllPiecesForDemande(idDemande);
            byte[] pdfBytes = pieceUploadService.genererPdfAperçuTous(idDemande, pieces);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "inline; filename=\"apercu_demande_" + idDemande + ".pdf\"")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Générer la lettre de réception avec QR code (PDF)
     * Condition: scan terminé
     */
    @GetMapping("/generer-lettre-reception/{idDemande}")
    public ResponseEntity<byte[]> genererLettreReception(@PathVariable int idDemande) {
        try {
            Demande demande = demandeRepository.findById(idDemande)
                    .orElseThrow(() -> new RuntimeException("Demande introuvable"));
            
            // Vérifier que le scan est terminé
            if (!"FINALISE".equals(demande.getEtatDossier())) {
                return ResponseEntity.badRequest().build();
            }
            
            // Générer le PDF avec lettre et QR code
            byte[] pdfBytes = pdfLettreReceptionService.genererLettreReception(idDemande);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "attachment; filename=\"lettre_reception_" + idDemande + ".pdf\"")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Déterminer le type de contenu en fonction de l'extension
     */
    private String determineContentType(String filePath) {
        String lowerPath = filePath.toLowerCase();
        if (lowerPath.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream";
    }
}
