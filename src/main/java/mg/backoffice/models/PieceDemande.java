package mg.backoffice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "piece_demande")
@IdClass(PieceDemandeId.class)
public class PieceDemande {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_demande")
    private Demande demande;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_piece")
    private PieceJustificative piece;

    @Column(name = "chemin_fichier")
    private String cheminFichier;
    
    @Column(name = "chemin_photo")
    private String cheminPhoto;
    
    @Column(name = "chemin_scan")
    private String cheminScan;
    
    @Column(name = "date_photo")
    private LocalDateTime datePhoto;
    
    @Column(name = "date_scan")
    private LocalDateTime dateScan;
    
    @Column(name = "etat_piece")
    private String etatPiece = "BROUILLON";
    // BROUILLON, PHOTO_PRISE, PHOTO_TERMINEE, SCAN_TERMINE, FINALISEE

    public Demande getDemande() {
        return demande;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
    }

    public PieceJustificative getPiece() {
        return piece;
    }

    public void setPiece(PieceJustificative piece) {
        this.piece = piece;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }
    
    public String getCheminPhoto() {
        return cheminPhoto;
    }
    
    public void setCheminPhoto(String cheminPhoto) {
        this.cheminPhoto = cheminPhoto;
    }
    
    public String getCheminScan() {
        return cheminScan;
    }
    
    public void setCheminScan(String cheminScan) {
        this.cheminScan = cheminScan;
    }
    
    public LocalDateTime getDatePhoto() {
        return datePhoto;
    }
    
    public void setDatePhoto(LocalDateTime datePhoto) {
        this.datePhoto = datePhoto;
    }
    
    public LocalDateTime getDateScan() {
        return dateScan;
    }
    
    public void setDateScan(LocalDateTime dateScan) {
        this.dateScan = dateScan;
    }
    
    public String getEtatPiece() {
        return etatPiece;
    }
    
    public void setEtatPiece(String etatPiece) {
        this.etatPiece = etatPiece;
    }
}
