package mg.backoffice.dto;

import java.time.LocalDate;

/**
 * DTO pour la recherche de carte résident existante dans le parcours Duplicata
 */
public class DuplicataSearchDTO {
    private String numeroCarte;

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }
}
