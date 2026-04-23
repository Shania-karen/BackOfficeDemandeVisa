package mg.backoffice.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.Demandeur;



@Repository
public interface DemandeurRepository extends JpaRepository<Demandeur, Integer> {
    Optional<Demandeur> findByEmail(String email);
}