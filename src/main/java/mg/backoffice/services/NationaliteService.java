package mg.backoffice.services;

import mg.backoffice.models.Nationalite;
import mg.backoffice.repositories.NationaliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NationaliteService {

    @Autowired
    private NationaliteRepository nationaliteRepository;

    public List<Nationalite> findAll() {
        return nationaliteRepository.findAll();
    }

    public Optional<Nationalite> findById(int id) {
        return nationaliteRepository.findById(id);
    }

    public Nationalite save(Nationalite nationalite) {
        return nationaliteRepository.save(nationalite);
    }

    public void deleteById(int id) {
        nationaliteRepository.deleteById(id);
    }
}
