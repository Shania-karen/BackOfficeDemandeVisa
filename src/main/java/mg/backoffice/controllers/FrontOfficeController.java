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

    // Endpoint pour afficher une demande via token QR (JSON)
    @GetMapping("/scan/{token}")
    public ResponseEntity<Demande> scan(@PathVariable("token") String token) {
        return demandeRepository.findByQrToken(token)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint pour afficher une belle page HTML quand on scanne le QR
    @GetMapping("/qr/{token}")
    public ResponseEntity<String> qrPage(@PathVariable("token") String token) {
        return demandeRepository.findByQrToken(token)
                .map(demande -> {
                    String lastStatus = "N/A";
                    String lastStatusDate = "N/A";
                    HistoriqueStatusDemande last = demandeService.findLastHistoriqueForDemande(demande.getId());
                    if (last != null) {
                        lastStatus = last.getStatus() != null ? last.getStatus().getCode() : "N/A";
                        lastStatusDate = last.getDate_status() != null ? last.getDate_status().toString() : "N/A";
                    }

                    String html = "<!DOCTYPE html><html lang=\"fr\"><head>"
                        + "<meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                        + "<title>Demande #" + demande.getId() + "</title>"
                        + "<style>"
                        + "body { font-family: Arial, sans-serif; max-width: 600px; margin: 20px auto; padding: 20px; background: #f5f5f5; }"
                        + ".card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }"
                        + "h1 { color: #333; margin-top: 0; }"
                        + ".field { margin: 15px 0; }"
                        + ".label { font-weight: bold; color: #555; }"
                        + ".value { font-size: 16px; color: #333; margin-top: 5px; }"
                        + ".status { display: inline-block; padding: 8px 12px; border-radius: 4px; font-weight: bold; "
                        + "background: " + ("VAL".equals(lastStatus) ? "#d4edda" : "REJ".equals(lastStatus) ? "#f8d7da" : "#e2e3e5") + "; "
                        + "color: " + ("VAL".equals(lastStatus) ? "#155724" : "REJ".equals(lastStatus) ? "#721c24" : "#383d41") + "; }"
                        + "</style>"
                        + "</head><body>"
                        + "<div class=\"card\">"
                        + "<h1>Demande #" + demande.getId() + "</h1>"
                        + "<div class=\"field\"><span class=\"label\">Type de demande:</span><div class=\"value\">" + (demande.getTypeDemande() != null ? demande.getTypeDemande().getLibelle() : "N/A") + "</div></div>"
                        + "<div class=\"field\"><span class=\"label\">Catégorie:</span><div class=\"value\">" + (demande.getCategorieVisa() != null ? demande.getCategorieVisa().getLibelle() : "N/A") + "</div></div>"
                        + "<div class=\"field\"><span class=\"label\">Date de demande:</span><div class=\"value\">" + (demande.getDateDemande() != null ? demande.getDateDemande() : "N/A") + "</div></div>"
                        + "<div class=\"field\"><span class=\"label\">Statut actuel:</span><div class=\"value\"><span class=\"status\">" + lastStatus + "</span></div></div>"
                        + "<div class=\"field\"><span class=\"label\">Date du dernier jugement:</span><div class=\"value\">" + lastStatusDate + "</div></div>"
                        + "</div>"
                        + "</body></html>";
                    return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8").body(html);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
