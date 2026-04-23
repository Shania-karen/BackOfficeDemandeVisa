package mg.backoffice.dto;

import java.time.LocalDate;
import java.util.List;

public class DemandeFormDTO {
    // Infos Demandeur
    private String nom;
    private String prenom;
    private String genre;
    private String nomJeuneFille;
    private LocalDate dateNaissance;
    private Integer idSituationFamiliale;
    private Integer idNationalite;
    private String domicile;
    private String adresseMada;
    private Integer contact;
    private String email;

    // Infos Passeport
    private String numeroPasseport;
    private LocalDate dateDelivrancePasseport;
    private LocalDate dateExpirationPasseport;

    // Infos Visa & Demande
    private Integer idCategorieVisa; // 1 = Travailleur, 2 = Investisseur
    private String refVisaTransformable; // Le numéro du visa existant
    
    // Liste des IDs des pièces que l'utilisateur a coché dans le formulaire
    private List<Integer> idsPiecesFournies;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNomJeuneFille() { return nomJeuneFille; }
    public void setNomJeuneFille(String nomJeuneFille) { this.nomJeuneFille = nomJeuneFille; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Integer getIdSituationFamiliale() { return idSituationFamiliale; }
    public void setIdSituationFamiliale(Integer idSituationFamiliale) { this.idSituationFamiliale = idSituationFamiliale; }

    public Integer getIdNationalite() { return idNationalite; }
    public void setIdNationalite(Integer idNationalite) { this.idNationalite = idNationalite; }

    public String getDomicile() { return domicile; }
    public void setDomicile(String domicile) { this.domicile = domicile; }

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

    public Integer getIdCategorieVisa() { return idCategorieVisa; }
    public void setIdCategorieVisa(Integer idCategorieVisa) { this.idCategorieVisa = idCategorieVisa; }

    public String getRefVisaTransformable() { return refVisaTransformable; }
    public void setRefVisaTransformable(String refVisaTransformable) { this.refVisaTransformable = refVisaTransformable; }

    public List<Integer> getIdsPiecesFournies() { return idsPiecesFournies; }
    public void setIdsPiecesFournies(List<Integer> idsPiecesFournies) { this.idsPiecesFournies = idsPiecesFournies; }
}