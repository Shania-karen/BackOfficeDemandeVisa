package mg.backoffice.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.Demande;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Integer> {
    
    // Requête qui récupère la dernière ligne de statut pour chaque demande, 
    // et ne garde que celles dont le dernier statut est 'VAL' (Validé)
    @Query(value = "SELECT d.* FROM demande d " +
                   "JOIN historique_status_demande h ON d.id = h.id_demande " +
                   "JOIN status s ON h.id_status = s.id " +
                   "WHERE s.code = 'VAL' " +
                   "AND h.date_status = (SELECT MAX(h2.date_status) FROM historique_status_demande h2 WHERE h2.id_demande = d.id)", 
           nativeQuery = true)
    List<Demande> findDemandesAcceptees();

    // Et pour les demandes en attente (statut 'ATT')
    @Query(value = "SELECT d.* FROM demande d " +
                   "JOIN historique_status_demande h ON d.id = h.id_demande " +
                   "JOIN status s ON h.id_status = s.id " +
                   "WHERE s.code = 'ATT' " +
                   "AND h.date_status = (SELECT MAX(h2.date_status) FROM historique_status_demande h2 WHERE h2.id_demande = d.id)", 
           nativeQuery = true)
    List<Demande> findDemandesEnAttente();
}