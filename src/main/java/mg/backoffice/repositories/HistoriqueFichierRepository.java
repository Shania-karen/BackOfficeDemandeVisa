package mg.backoffice.repositories;

import mg.backoffice.models.HistoriqueFichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueFichierRepository extends JpaRepository<HistoriqueFichier, Integer> {
    
    List<HistoriqueFichier> findByDemandeId(int demandeId);
    
    @Query("SELECT h FROM HistoriqueFichier h WHERE h.demande.id = :demandeId AND h.typeFichier = :type")
    List<HistoriqueFichier> findByDemandeIdAndType(@Param("demandeId") int demandeId, @Param("type") String type);
    
    @Query("SELECT h FROM HistoriqueFichier h WHERE h.demande.id = :demandeId AND h.piece.id = :pieceId")
    List<HistoriqueFichier> findByDemandeIdAndPieceId(@Param("demandeId") int demandeId, @Param("pieceId") int pieceId);
}
