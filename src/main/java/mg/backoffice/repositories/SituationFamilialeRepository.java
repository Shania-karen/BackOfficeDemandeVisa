package mg.backoffice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.SituationFamiliale;

@Repository
public interface SituationFamilialeRepository extends JpaRepository<SituationFamiliale, Integer> {
}
