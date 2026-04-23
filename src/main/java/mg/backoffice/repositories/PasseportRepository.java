package mg.backoffice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.Passeport;

@Repository
public interface PasseportRepository extends JpaRepository<Passeport, Integer> {
 
    Optional<Passeport> findByNumeroPasseport(String numeroPasseport);
}