package mg.backoffice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.HistoriqueStatusDemande;

@Repository
public interface HistoriqueStatusDemandeRepository extends JpaRepository<HistoriqueStatusDemande, Integer> {
}