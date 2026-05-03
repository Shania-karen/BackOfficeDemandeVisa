package mg.backoffice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DemandeHistoryDTO {
    private Integer demandeId;
    private String typeDemande;
    private LocalDate dateDemande;
    private String lastStatusCode;
    private LocalDateTime lastStatusDate;
    private String qrUrl;

    public Integer getDemandeId() {
        return demandeId;
    }

    public void setDemandeId(Integer demandeId) {
        this.demandeId = demandeId;
    }

    public String getTypeDemande() {
        return typeDemande;
    }

    public void setTypeDemande(String typeDemande) {
        this.typeDemande = typeDemande;
    }

    public LocalDate getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDate dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getLastStatusCode() {
        return lastStatusCode;
    }

    public void setLastStatusCode(String lastStatusCode) {
        this.lastStatusCode = lastStatusCode;
    }

    public LocalDateTime getLastStatusDate() {
        return lastStatusDate;
    }

    public void setLastStatusDate(LocalDateTime lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }
}
