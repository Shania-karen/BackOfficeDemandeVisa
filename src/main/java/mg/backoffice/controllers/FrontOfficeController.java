package mg.backoffice.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mg.backoffice.dto.DemandeHistoryDTO;
import mg.backoffice.models.Demande;
import mg.backoffice.models.HistoriqueStatusDemande;
import mg.backoffice.repositories.DemandeRepository;
import mg.backoffice.services.DemandeService;

@RestController
@RequestMapping("/api/frontoffice")
public class FrontOfficeController {

    @Autowired
    private DemandeService demandeService;

    @Autowired
    private DemandeRepository demandeRepository;

    // Recherche par passeport ou par numéro de demande
    @GetMapping("/search")
    public ResponseEntity<List<DemandeHistoryDTO>> search(@RequestParam("by") String by, @RequestParam("value") String value) {
        List<DemandeHistoryDTO> result = new ArrayList<>();

        if ("passport".equalsIgnoreCase(by) || "passeport".equalsIgnoreCase(by)) {
            List<Demande> demandes = demandeService.findDemandesByPassport(value.trim());
            for (Demande d : demandes) {
                DemandeHistoryDTO dto = new DemandeHistoryDTO();
                dto.setDemandeId(d.getId());
                dto.setTypeDemande(d.getTypeDemande() != null ? d.getTypeDemande().getLibelle() : null);
                dto.setDateDemande(d.getDateDemande());
                HistoriqueStatusDemande last = demandeService.findLastHistoriqueForDemande(d.getId());
                if (last != null) {
                    dto.setLastStatusCode(last.getStatus() != null ? last.getStatus().getCode() : null);
                    dto.setLastStatusDate(last.getDate_status());
                }
                String token = d.getQrToken();
                if (token != null) dto.setQrUrl("/api/frontoffice/scan/" + token);

                result.add(dto);
            }
            return ResponseEntity.ok(result);
        }

        if ("demande".equalsIgnoreCase(by)) {
            try {
                Integer id = Integer.valueOf(value.trim());
                // d'abord la demande ciblée
                Demande cible = demandeRepository.findByIdWithRelations(id).orElse(null);
                if (cible != null) {
                    DemandeHistoryDTO dto = new DemandeHistoryDTO();
                    dto.setDemandeId(cible.getId());
                    dto.setTypeDemande(cible.getTypeDemande() != null ? cible.getTypeDemande().getLibelle() : null);
                    dto.setDateDemande(cible.getDateDemande());
                    HistoriqueStatusDemande last = demandeService.findLastHistoriqueForDemande(cible.getId());
                    if (last != null) {
                        dto.setLastStatusCode(last.getStatus() != null ? last.getStatus().getCode() : null);
                        dto.setLastStatusDate(last.getDate_status());
                    }
                    if (cible.getQrToken() != null) dto.setQrUrl("/api/frontoffice/scan/" + cible.getQrToken());
                    result.add(dto);

                    // puis le reste des demandes liées à la même personne
                    List<Demande> autres = demandeService.findDemandesByDemandeNumber(id);
                    for (Demande d : autres) {
                        if (d.getId() == id) continue; // skip duplicate
                        DemandeHistoryDTO dto2 = new DemandeHistoryDTO();
                        dto2.setDemandeId(d.getId());
                        dto2.setTypeDemande(d.getTypeDemande() != null ? d.getTypeDemande().getLibelle() : null);
                        dto2.setDateDemande(d.getDateDemande());
                        HistoriqueStatusDemande last2 = demandeService.findLastHistoriqueForDemande(d.getId());
                        if (last2 != null) {
                            dto2.setLastStatusCode(last2.getStatus() != null ? last2.getStatus().getCode() : null);
                            dto2.setLastStatusDate(last2.getDate_status());
                        }
                        if (d.getQrToken() != null) dto2.setQrUrl("/api/frontoffice/scan/" + d.getQrToken());
                        result.add(dto2);
                    }
                }
            } catch (NumberFormatException ex) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.badRequest().build();
    }

    // Endpoint pour afficher une demande via token QR
    @GetMapping("/scan/{token}")
    public ResponseEntity<Demande> scan(@PathVariable("token") String token) {
        return demandeRepository.findByQrToken(token)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
