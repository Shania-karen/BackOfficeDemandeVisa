package mg.backoffice.dto;

/**
 * DTO pour la recherche de visa existant dans le parcours Transfert
 */
public class TransfertSearchDTO {
    private String numeroVisa;
    private String numeroPasseport;

    public String getNumeroVisa() {
        return numeroVisa;
    }

    public void setNumeroVisa(String numeroVisa) {
        this.numeroVisa = numeroVisa;
    }

    public String getNumeroPasseport() {
        return numeroPasseport;
    }

    public void setNumeroPasseport(String numeroPasseport) {
        this.numeroPasseport = numeroPasseport;
    }
}
