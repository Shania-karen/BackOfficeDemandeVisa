package mg.backoffice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.CarteResident;
import java.util.Optional;

@Repository
public interface CarteResidentRepository extends JpaRepository<CarteResident, Integer> {
    Optional<CarteResident> findByNumeroCarte(String numeroCarte);
}
