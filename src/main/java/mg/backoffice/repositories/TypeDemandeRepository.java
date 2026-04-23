package mg.backoffice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.TypeDemande;

@Repository
public interface TypeDemandeRepository extends JpaRepository<TypeDemande, Integer> {
}
