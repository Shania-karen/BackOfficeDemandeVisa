package mg.backoffice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "visa")
public class Visa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "numero_visa", nullable = false)
    private String numeroVisa;

    @Column(name = "date_entree_territoire", nullable = false)
    private LocalDate dateEntreeTerritoire;

    @Column(name = "date_sortie_territoire", nullable = false)
    private LocalDate dateSortieTerritoire;

    @Column(name = "date_delivrance", nullable = false)
    private LocalDate dateDelivrance;

    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    @ManyToOne
    @JoinColumn(name = "id_passeport", nullable = false)
    private Passeport passeport;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroVisa() {
        return numeroVisa;
    }

    public void setNumeroVisa(String numeroVisa) {
        this.numeroVisa = numeroVisa;
    }

    public LocalDate getDateEntreeTerritoire() {
        return dateEntreeTerritoire;
    }

    public void setDateEntreeTerritoire(LocalDate dateEntreeTerritoire) {
        this.dateEntreeTerritoire = dateEntreeTerritoire;
    }

    public LocalDate getDateSortieTerritoire() {
        return dateSortieTerritoire;
    }

    public void setDateSortieTerritoire(LocalDate dateSortieTerritoire) {
        this.dateSortieTerritoire = dateSortieTerritoire;
    }

    public LocalDate getDateDelivrance() {
        return dateDelivrance;
    }

    public void setDateDelivrance(LocalDate dateDelivrance) {
        this.dateDelivrance = dateDelivrance;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Passeport getPasseport() {
        return passeport;
    }

    public void setPasseport(Passeport passeport) {
        this.passeport = passeport;
    }
}
