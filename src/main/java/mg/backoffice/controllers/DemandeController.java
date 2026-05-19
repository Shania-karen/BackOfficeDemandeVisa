package mg.backoffice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.backoffice.dto.DemandeFormDTO;
import mg.backoffice.services.DemandeService;

@RestController
@RequestMapping("/api/demandes")
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    @PostMapping("/soumettre")
    public ResponseEntity<String> soumettreDemande(@RequestBody DemandeFormDTO form) {
        try {
            String token = demandeService.soumettreDemande(form);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
           
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}