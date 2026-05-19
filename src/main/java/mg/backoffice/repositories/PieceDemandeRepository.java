package mg.backoffice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.PieceDemande;
import mg.backoffice.models.PieceDemandeId;

@Repository
public interface PieceDemandeRepository extends JpaRepository<PieceDemande, PieceDemandeId> {

	long countByDemande_IdAndCheminFichierIsNull(Integer demandeId);

	long countByDemande_Id(Integer demandeId);
	
	@Query("SELECT pd FROM PieceDemande pd WHERE pd.demande.id = :demandeId AND pd.piece.id = :pieceId")
	PieceDemande findByDemandeIdAndPieceId(@Param("demandeId") int demandeId, @Param("pieceId") int pieceId);

}