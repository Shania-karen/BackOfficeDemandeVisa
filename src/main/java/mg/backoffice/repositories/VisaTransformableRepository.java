package mg.backoffice.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.VisaTransformable;

@Repository

public interface VisaTransformableRepository extends JpaRepository<VisaTransformable, Integer> {
    
    Optional<VisaTransformable> findByNumeroVisa(String numeroVisa);
}