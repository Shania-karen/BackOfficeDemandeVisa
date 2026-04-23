package mg.backoffice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.backoffice.models.CategorieVisa;

@Repository
public interface CategorieVisaRepository extends JpaRepository<CategorieVisa, Integer> {
}
