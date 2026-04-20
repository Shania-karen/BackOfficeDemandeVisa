package mg.backoffice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
}
