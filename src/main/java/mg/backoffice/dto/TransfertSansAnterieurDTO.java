package mg.backoffice.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour Transfert SansAntérieur : saisie complète avec visa transformable
 */
public class TransfertSansAnterieurDTO {
    // Infos Demandeur
    private String nom;
    private String prenom;
    private String genre;
    private String nomJeuneFille;
    private LocalDate dateNaissance;
    private Integer idSituationFamiliale;
    private Integer idNationalite;
    private String adresseMada;
    private Integer contact;
    private String email;

    // Infos Passeport
    private String numeroPasseport;
    private LocalDate dateDelivrancePasseport;
    private LocalDate dateExpirationPasseport;

    // Infos Visa Transformable à créer
    private String numeroVisaTransformable;
    private LocalDate dateEntreeVisaTransformable;
    private LocalDate dateSortieVisaTransformable;
    private LocalDate dateDelivranceVisaTransformable;
    private LocalDate dateExpirationVisaTransformable;

    // Infos Visa à créer
    private String numeroVisa;
    private LocalDate dateEntreeVisa;
    private LocalDate dateSortieVisa;
    private LocalDate dateDelivranceVisa;
    private LocalDate dateExpirationVisa;

    // Catégorie visa
    private Integer idCategorieVisa;

    // Pièces justificatives
    private List<Integer> idsPiecesFournies;

    // Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getNomJeuneFille() { return nomJeuneFille; }
    public void setNomJeuneFille(String nomJeuneFille) { this.nomJeuneFille = nomJeuneFille; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Integer getIdSituationFamiliale() { return idSituationFamiliale; }
    public void setIdSituationFamiliale(Integer idSituationFamiliale) { this.idSituationFamiliale = idSituationFamiliale; }

    public Integer getIdNationalite() { return idNationalite; }
    public void setIdNationalite(Integer idNationalite) { this.idNationalite = idNationalite; }

    public String getAdresseMada() { return adresseMada; }
    public void setAdresseMada(String adresseMada) { this.adresseMada = adresseMada; }

    public Integer getContact() { return contact; }
    public void setContact(Integer contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumeroPasseport() { return numeroPasseport; }
    public void setNumeroPasseport(String numeroPasseport) { this.numeroPasseport = numeroPasseport; }

    public LocalDate getDateDelivrancePasseport() { return dateDelivrancePasseport; }
    public void setDateDelivrancePasseport(LocalDate dateDelivrancePasseport) { this.dateDelivrancePasseport = dateDelivrancePasseport; }

    public LocalDate getDateExpirationPasseport() { return dateExpirationPasseport; }
    public void setDateExpirationPasseport(LocalDate dateExpirationPasseport) { this.dateExpirationPasseport = dateExpirationPasseport; }

    public String getNumeroVisaTransformable() { return numeroVisaTransformable; }
    public void setNumeroVisaTransformable(String numeroVisaTransformable) { this.numeroVisaTransformable = numeroVisaTransformable; }

    public LocalDate getDateEntreeVisaTransformable() { return dateEntreeVisaTransformable; }
    public void setDateEntreeVisaTransformable(LocalDate dateEntreeVisaTransformable) { this.dateEntreeVisaTransformable = dateEntreeVisaTransformable; }

    public LocalDate getDateSortieVisaTransformable() { return dateSortieVisaTransformable; }
    public void setDateSortieVisaTransformable(LocalDate dateSortieVisaTransformable) { this.dateSortieVisaTransformable = dateSortieVisaTransformable; }

    public LocalDate getDateDelivranceVisaTransformable() { return dateDelivranceVisaTransformable; }
    public void setDateDelivranceVisaTransformable(LocalDate dateDelivranceVisaTransformable) { this.dateDelivranceVisaTransformable = dateDelivranceVisaTransformable; }

    public LocalDate getDateExpirationVisaTransformable() { return dateExpirationVisaTransformable; }
    public void setDateExpirationVisaTransformable(LocalDate dateExpirationVisaTransformable) { this.dateExpirationVisaTransformable = dateExpirationVisaTransformable; }

    public String getNumeroVisa() { return numeroVisa; }
    public void setNumeroVisa(String numeroVisa) { this.numeroVisa = numeroVisa; }

    public LocalDate getDateEntreeVisa() { return dateEntreeVisa; }
    public void setDateEntreeVisa(LocalDate dateEntreeVisa) { this.dateEntreeVisa = dateEntreeVisa; }

    public LocalDate getDateSortieVisa() { return dateSortieVisa; }
    public void setDateSortieVisa(LocalDate dateSortieVisa) { this.dateSortieVisa = dateSortieVisa; }

    public LocalDate getDateDelivranceVisa() { return dateDelivranceVisa; }
    public void setDateDelivranceVisa(LocalDate dateDelivranceVisa) { this.dateDelivranceVisa = dateDelivranceVisa; }

    public LocalDate getDateExpirationVisa() { return dateExpirationVisa; }
    public void setDateExpirationVisa(LocalDate dateExpirationVisa) { this.dateExpirationVisa = dateExpirationVisa; }

    public Integer getIdCategorieVisa() { return idCategorieVisa; }
    public void setIdCategorieVisa(Integer idCategorieVisa) { this.idCategorieVisa = idCategorieVisa; }

    public List<Integer> getIdsPiecesFournies() { return idsPiecesFournies; }
    public void setIdsPiecesFournies(List<Integer> idsPiecesFournies) { this.idsPiecesFournies = idsPiecesFournies; }
}
