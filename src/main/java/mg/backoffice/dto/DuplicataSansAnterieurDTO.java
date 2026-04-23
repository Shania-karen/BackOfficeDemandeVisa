package mg.backoffice.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour Duplicata SansAntérieur : saisie complète comme un NOUVEAU_TITRE
 */
public class DuplicataSansAnterieurDTO {
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

    // Infos Carte Résident à créer
    private String numeroCarte;
    private LocalDate dateEntreeCarteResident;
    private LocalDate dateSortieCarteResident;
    private LocalDate dateDelivranceCarteResident;
    private LocalDate dateExpirationCarteResident;

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

    public String getNumeroCarte() { return numeroCarte; }
    public void setNumeroCarte(String numeroCarte) { this.numeroCarte = numeroCarte; }

    public LocalDate getDateEntreeCarteResident() { return dateEntreeCarteResident; }
    public void setDateEntreeCarteResident(LocalDate dateEntreeCarteResident) { this.dateEntreeCarteResident = dateEntreeCarteResident; }

    public LocalDate getDateSortieCarteResident() { return dateSortieCarteResident; }
    public void setDateSortieCarteResident(LocalDate dateSortieCarteResident) { this.dateSortieCarteResident = dateSortieCarteResident; }

    public LocalDate getDateDelivranceCarteResident() { return dateDelivranceCarteResident; }
    public void setDateDelivranceCarteResident(LocalDate dateDelivranceCarteResident) { this.dateDelivranceCarteResident = dateDelivranceCarteResident; }

    public LocalDate getDateExpirationCarteResident() { return dateExpirationCarteResident; }
    public void setDateExpirationCarteResident(LocalDate dateExpirationCarteResident) { this.dateExpirationCarteResident = dateExpirationCarteResident; }

    public List<Integer> getIdsPiecesFournies() { return idsPiecesFournies; }
    public void setIdsPiecesFournies(List<Integer> idsPiecesFournies) { this.idsPiecesFournies = idsPiecesFournies; }
}
