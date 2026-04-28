package mg.backoffice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.Visa;
import java.util.Optional;

@Repository
public interface VisaRepository extends JpaRepository<Visa, Integer> {
    Optional<Visa> findByNumeroVisa(String numeroVisa);
}
