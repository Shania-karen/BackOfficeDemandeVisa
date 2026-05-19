package mg.backoffice.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_fichier")
public class HistoriqueFichier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "id_demande", nullable = false)
    private Demande demande;
    
    @ManyToOne
    @JoinColumn(name = "id_piece")
    private PieceJustificative piece;
    
    @Column(name = "type_fichier", nullable = false)
    private String typeFichier; // PHOTO, SCAN, PDF
    
    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;
    
    @Column(name = "date_upload", nullable = false)
    private LocalDateTime dateUpload = LocalDateTime.now();
    
    @Column(name = "taille_fichier")
    private Long tailleFichier;
    
    // Constructeurs
    public HistoriqueFichier() {}
    
    public HistoriqueFichier(Demande demande, PieceJustificative piece, String typeFichier, 
                            String cheminFichier, Long tailleFichier) {
        this.demande = demande;
        this.piece = piece;
        this.typeFichier = typeFichier;
        this.cheminFichier = cheminFichier;
        this.tailleFichier = tailleFichier;
        this.dateUpload = LocalDateTime.now();
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
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
    
    public String getTypeFichier() {
        return typeFichier;
    }
    
    public void setTypeFichier(String typeFichier) {
        this.typeFichier = typeFichier;
    }
    
    public String getCheminFichier() {
        return cheminFichier;
    }
    
    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }
    
    public LocalDateTime getDateUpload() {
        return dateUpload;
    }
    
    public void setDateUpload(LocalDateTime dateUpload) {
        this.dateUpload = dateUpload;
    }
    
    public Long getTailleFichier() {
        return tailleFichier;
    }
    
    public void setTailleFichier(Long tailleFichier) {
        this.tailleFichier = tailleFichier;
    }
}
