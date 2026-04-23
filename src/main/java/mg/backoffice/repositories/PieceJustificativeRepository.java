package mg.backoffice.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.PieceJustificative;

@Repository
public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Integer> {

    @Query(value = "SELECT p.id FROM piece_justificative p " +
                   "WHERE p.obligatoire = true " +
                   "AND (p.code LIKE 'COM_%' " +
                   "OR (:idCategorie = 1 AND p.code LIKE 'INV_%') " +
                   "OR (:idCategorie = 2 AND p.code LIKE 'TRA_%'))", 
           nativeQuery = true)
    List<Integer> findIdsPiecesObligatoires(@Param("idCategorie") Integer idCategorie);
}