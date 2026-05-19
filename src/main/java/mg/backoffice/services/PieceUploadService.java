package mg.backoffice.services;

import mg.backoffice.models.*;
import mg.backoffice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PieceUploadService {
    
    @Value("${upload.dir:uploads}")
    private String uploadDir;
    
    @Autowired
    private DemandeRepository demandeRepository;
    
    @Autowired
    private PieceDemandeRepository pieceDemandeRepository;
    
    @Autowired
    private HistoriqueFichierRepository historiqueFichierRepository;
    
    @Autowired
    private PieceJustificativeRepository pieceJustificativeRepository;
    
    @Transactional
    public String uploadPhotoJustificatif(int idDemande, int idPiece, MultipartFile file) throws IOException {
        // Récupérer la demande et la pièce
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
        
        PieceJustificative piece = pieceJustificativeRepository.findById(idPiece)
                .orElseThrow(() -> new RuntimeException("Pièce justificative introuvable"));
        
        // Créer le répertoire de la demande si inexistant
        String demandeDirPath = uploadDir + "/demande_" + idDemande + "/photos";
        Files.createDirectories(Paths.get(demandeDirPath));
        
        // Générer le nom du fichier
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : ".jpg";
        String savedFileName = "photo_piece_" + idPiece + "_" + timestamp + fileExtension;
        
        // Sauvegarder le fichier
        Path filePath = Paths.get(demandeDirPath, savedFileName);
        Files.write(filePath, file.getBytes());
        
        // Enregistrer dans PieceDemande
        PieceDemande pieceDemande = pieceDemandeRepository.findByDemandeIdAndPieceId(idDemande, idPiece);
        if (pieceDemande == null) {
            pieceDemande = new PieceDemande();
            pieceDemande.setDemande(demande);
            pieceDemande.setPiece(piece);
        }
        
        pieceDemande.setCheminPhoto(filePath.toString());
        pieceDemande.setDatePhoto(LocalDateTime.now());
        pieceDemande.setEtatPiece("PHOTO_PRISE");
        pieceDemandeRepository.save(pieceDemande);
        
        // Enregistrer dans l'historique
        HistoriqueFichier historique = new HistoriqueFichier(demande, piece, "PHOTO", 
                                                            filePath.toString(), file.getSize());
        historiqueFichierRepository.save(historique);
        
        return filePath.toString();
    }
    
    @Transactional
    public String uploadScanJustificatif(int idDemande, int idPiece, MultipartFile file) throws IOException {
        // Récupérer la demande et la pièce
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
        
        PieceJustificative piece = pieceJustificativeRepository.findById(idPiece)
                .orElseThrow(() -> new RuntimeException("Pièce justificative introuvable"));
        
        // Créer le répertoire de la demande si inexistant
        String demandeDirPath = uploadDir + "/demande_" + idDemande + "/scans";
        Files.createDirectories(Paths.get(demandeDirPath));
        
        // Générer le nom du fichier
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : ".pdf";
        String savedFileName = "scan_piece_" + idPiece + "_" + timestamp + fileExtension;
        
        // Sauvegarder le fichier
        Path filePath = Paths.get(demandeDirPath, savedFileName);
        Files.write(filePath, file.getBytes());
        
        // Enregistrer dans PieceDemande
        PieceDemande pieceDemande = pieceDemandeRepository.findByDemandeIdAndPieceId(idDemande, idPiece);
        if (pieceDemande == null) {
            pieceDemande = new PieceDemande();
            pieceDemande.setDemande(demande);
            pieceDemande.setPiece(piece);
        }
        
        pieceDemande.setCheminScan(filePath.toString());
        pieceDemande.setDateScan(LocalDateTime.now());
        pieceDemande.setEtatPiece("SCAN_TERMINE");
        pieceDemandeRepository.save(pieceDemande);
        
        // Enregistrer dans l'historique
        HistoriqueFichier historique = new HistoriqueFichier(demande, piece, "SCAN", 
                                                            filePath.toString(), file.getSize());
        historiqueFichierRepository.save(historique);
        
        // Mettre à jour l'état du dossier si tous les scans sont terminés
        updateEtatDossier(idDemande);
        
        return filePath.toString();
    }
    
    @Transactional
    public void updateEtatDossier(int idDemande) {
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
        
        List<PieceDemande> pieces = demande.getPieces();
        
        if (pieces == null || pieces.isEmpty()) {
            return;
        }
        
        // Compter les pièces complétées (qu'elles soient sous forme de scan, photo ou fichier standard)
        long piecesComplete = pieces.stream()
            .filter(p -> p.getCheminScan() != null || p.getCheminPhoto() != null || p.getCheminFichier() != null)
            .count();
        
        if (piecesComplete == pieces.size()) {
            demande.setEtatDossier("FINALISE");
            demandeRepository.save(demande);
        }
    }
    
    public List<PieceDemande> getAllPiecesForDemande(int idDemande) {
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
        
        return demande.getPieces() != null ? demande.getPieces() : new ArrayList<>();
    }
    
    public PieceDemande getPieceDemande(int idDemande, int idPiece) {
        return pieceDemandeRepository.findByDemandeIdAndPieceId(idDemande, idPiece);
    }
    
    public List<HistoriqueFichier> getHistoriqueFichiers(int idDemande) {
        return historiqueFichierRepository.findByDemandeId(idDemande);
    }
}
