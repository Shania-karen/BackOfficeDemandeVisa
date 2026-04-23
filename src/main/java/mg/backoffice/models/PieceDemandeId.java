package mg.backoffice.models;

import java.io.Serializable;
import java.util.Objects;

public class PieceDemandeId implements Serializable {
    private int demande;
    private int piece;

    public PieceDemandeId() {}

    public PieceDemandeId(int demande, int piece) {
        this.demande = demande;
        this.piece = piece;
    }

    public int getDemande() {
        return demande;
    }

    public void setDemande(int demande) {
        this.demande = demande;
    }

    public int getPiece() {
        return piece;
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceDemandeId that = (PieceDemandeId) o;
        return demande == that.demande && piece == that.piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(demande, piece);
    }
}
