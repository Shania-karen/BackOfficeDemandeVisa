package mg.backoffice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.PieceDemande;
import mg.backoffice.models.PieceDemandeId;

@Repository
public interface PieceDemandeRepository extends JpaRepository<PieceDemande, PieceDemandeId> {

}