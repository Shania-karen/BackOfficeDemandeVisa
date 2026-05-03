package mg.backoffice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.HistoriqueStatusDemande;

@Repository
public interface HistoriqueStatusDemandeRepository extends JpaRepository<HistoriqueStatusDemande, Integer> {

	@Query(value = "SELECT * FROM historique_status_demande WHERE id_demande = :demandeId ORDER BY date_status DESC LIMIT 1", nativeQuery = true)
	HistoriqueStatusDemande findLastByDemandeId(Integer demandeId);
}