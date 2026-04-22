package mg.backoffice.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.Demande;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Integer> {
    
}