package mg.backoffice.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import mg.backoffice.models.Demande;
import mg.backoffice.models.Demandeur;
import mg.backoffice.models.PieceDemande;
import mg.backoffice.repositories.DemandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfLettreReceptionService {
    
    @Autowired
    private DemandeRepository demandeRepository;
    
    public byte[] genererLettreReception(int idDemande) throws Exception {
        Demande demande = demandeRepository.findByIdWithRelations(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
        
        // Vérifier que le dossier est finalisé
        if (!"FINALISE".equals(demande.getEtatDossier())) {
            throw new RuntimeException("Le dossier n'est pas finalisé. Veuillez compléter tous les scans.");
        }
        
        Demandeur demandeur = demande.getDemandeur();
        String nomDemandeur = demandeur != null ? demandeur.getNom() + " " + demandeur.getPrenom() : "Non spécifié";
        String typeDemande = demande.getTypeDemande() != null ? demande.getTypeDemande().getLibelle() : "Non spécifié";
        String categorieVisa = demande.getCategorieVisa() != null ? demande.getCategorieVisa().getLibelle() : "Non spécifiée";
        String dateDemande = demande.getDateDemande() != null
            ? demande.getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            : "Non spécifiée";
        String etatDossier = demande.getEtatDossier() != null ? demande.getEtatDossier() : "Non spécifié";
        
        // Générer la référence et QR code
        String reference = "DOSSIER_" + idDemande + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        // Créer le document PDF
        Document document = new Document(PageSize.A4, 45, 45, 45, 45);
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, pdfStream);
        
        document.open();
        
        // Couleurs de base de Madagascar et design
        BaseColor redFlag = new BaseColor(219, 48, 34);
        BaseColor greenFlag = new BaseColor(0, 126, 75);
        BaseColor darkSlate = new BaseColor(15, 23, 42);
        BaseColor borderCellColor = new BaseColor(226, 232, 240);
        BaseColor bgCellColor = new BaseColor(248, 250, 252);
        
        // Polices
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, darkSlate);
        Font refOfficialFont = new Font(Font.FontFamily.HELVETICA, 10.5f, Font.BOLD, redFlag);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 9.5f, Font.NORMAL, darkSlate);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, darkSlate);
        
        // --- 1. EN-TÊTE DOUBLE COLONNE ---
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1.3f, 1f});
        headerTable.setSpacingAfter(10);
        
        // Cellule Gauche : Hiérarchie ministérielle de Madagascar
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        
        Font minBold = new Font(Font.FontFamily.HELVETICA, 7.5f, Font.BOLD, darkSlate);
        Font minNormal = new Font(Font.FontFamily.HELVETICA, 6.5f, Font.NORMAL, darkSlate);
        
        leftCell.addElement(new Paragraph("MINISTÈRE DE L'INTÉRIEUR", minBold));
        leftCell.addElement(new Paragraph("ET DE LA DÉCENTRALISATION", minBold));
        
        Paragraph sep1 = new Paragraph("-----------------------------------------------------", minNormal);
        sep1.setSpacingBefore(-2);
        sep1.setSpacingAfter(-2);
        leftCell.addElement(sep1);
        
        leftCell.addElement(new Paragraph("SECRÉTARIAT GÉNÉRAL", minBold));
        
        Paragraph sep2 = new Paragraph("-----------------------------------------------------", minNormal);
        sep2.setSpacingBefore(-2);
        sep2.setSpacingAfter(-2);
        leftCell.addElement(sep2);
        
        leftCell.addElement(new Paragraph("DIRECTION GÉNÉRALE DE L'ADMINISTRATION", minNormal));
        leftCell.addElement(new Paragraph("TERRITORIALE", minNormal));
        
        Paragraph sep3 = new Paragraph("-----------------------------------------------------", minNormal);
        sep3.setSpacingBefore(-2);
        sep3.setSpacingAfter(-2);
        leftCell.addElement(sep3);
        
        leftCell.addElement(new Paragraph("DIRECTION DE L'IMMIGRATION ET DE L'ÉMIGRATION", minNormal));
        
        Paragraph sep4 = new Paragraph("-----------------------------------------------------", minNormal);
        sep4.setSpacingBefore(-2);
        sep4.setSpacingAfter(-2);
        leftCell.addElement(sep4);
        
        leftCell.addElement(new Paragraph("SERVICE DES VISAS ET DES TITRES DE SÉJOUR", minBold));
        
        headerTable.addCell(leftCell);
        
        // Cellule Droite : République de Madagascar
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Font repBold = new Font(Font.FontFamily.HELVETICA, 9.5f, Font.BOLD, darkSlate);
        Font mottoItalic = new Font(Font.FontFamily.HELVETICA, 7.5f, Font.ITALIC, darkSlate);
        
        Paragraph repTitle = new Paragraph("REPOBLIKAN'I MADAGASIKARA", repBold);
        repTitle.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(repTitle);
        
        Paragraph motto = new Paragraph("Fitiavana - Tanindrazana - Fandrosoana", mottoItalic);
        motto.setAlignment(Element.ALIGN_CENTER);
        motto.setSpacingAfter(10);
        rightCell.addElement(motto);
        
        Paragraph repLine = new Paragraph("* * * * *", mottoItalic);
        repLine.setAlignment(Element.ALIGN_CENTER);
        repLine.setSpacingAfter(10);
        rightCell.addElement(repLine);
        
        Paragraph dateLoc = new Paragraph("Antananarivo, le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont);
        dateLoc.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(dateLoc);
        
        headerTable.addCell(rightCell);
        document.add(headerTable);
        
        // --- 2. BARRE AUX COULEURS NATIONALES (DRAPEAU MALGACHE) ---
        PdfPTable flagBar = new PdfPTable(3);
        flagBar.setWidthPercentage(100);
        flagBar.setWidths(new float[]{1f, 1f, 1f});
        flagBar.setSpacingBefore(5);
        flagBar.setSpacingAfter(20);
        
        PdfPCell whiteCell = new PdfPCell();
        whiteCell.setBackgroundColor(new BaseColor(245, 245, 245));
        whiteCell.setBorder(Rectangle.NO_BORDER);
        whiteCell.setFixedHeight(2.5f);
        
        PdfPCell redCell = new PdfPCell();
        redCell.setBackgroundColor(redFlag);
        redCell.setBorder(Rectangle.NO_BORDER);
        redCell.setFixedHeight(2.5f);
        
        PdfPCell greenCell = new PdfPCell();
        greenCell.setBackgroundColor(greenFlag);
        greenCell.setBorder(Rectangle.NO_BORDER);
        greenCell.setFixedHeight(2.5f);
        
        flagBar.addCell(whiteCell);
        flagBar.addCell(redCell);
        flagBar.addCell(greenCell);
        document.add(flagBar);
        
        // --- 3. TITRE DE L'ATTESTATION ---
        PdfPTable titleBox = new PdfPTable(1);
        titleBox.setWidthPercentage(100);
        titleBox.setSpacingAfter(15);
        
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.BOX);
        titleCell.setBorderWidth(1.2f);
        titleCell.setBorderColor(greenFlag);
        titleCell.setPadding(10);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setBackgroundColor(new BaseColor(248, 252, 249));
        
        Paragraph titleP = new Paragraph("ATTESTATION DE DÉPÔT DE DEMANDE DE VISA", titleFont);
        titleP.setAlignment(Element.ALIGN_CENTER);
        titleP.setSpacingAfter(4);
        titleCell.addElement(titleP);
        
        String officialRefNum = "N° " + String.format("%04d", idDemande) + "/" + LocalDateTime.now().getYear() + "/MID/SG/DGAT/DIE/SVTS";
        Paragraph refP = new Paragraph(officialRefNum, refOfficialFont);
        refP.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(refP);
        
        titleBox.addCell(titleCell);
        document.add(titleBox);
        
        // --- 4. TEXTE D'INTRODUCTION SOLENNEL ---
        Paragraph intro = new Paragraph("Le Secrétariat Général du Ministère de l'Intérieur et de la Décentralisation de la République de Madagascar atteste par la présente avoir reçu le dossier de demande de visa de séjour ci-après spécifié et finalisé :", normalFont);
        intro.setSpacingAfter(15);
        intro.setLeading(13f);
        document.add(intro);
        
        // --- 5. RECHERCHE DE LA PHOTO D'IDENTITÉ ("COM_PHO") ---
        String pathPhoto = null;
        if (demande.getPieces() != null) {
            for (PieceDemande pd : demande.getPieces()) {
                if (pd.getPiece() != null && "COM_PHO".equals(pd.getPiece().getCode())) {
                    pathPhoto = pd.getCheminPhoto() != null ? pd.getCheminPhoto() :
                                pd.getCheminScan() != null ? pd.getCheminScan() :
                                pd.getCheminFichier();
                    break;
                }
            }
        }
        
        Image photoImg = null;
        if (pathPhoto != null) {
            try {
                java.nio.file.Path filePath = java.nio.file.Paths.get(pathPhoto);
                if (!java.nio.file.Files.exists(filePath)) {
                    filePath = java.nio.file.Paths.get("uploads", pathPhoto);
                }
                if (java.nio.file.Files.exists(filePath)) {
                    photoImg = Image.getInstance(filePath.toString());
                    photoImg.scaleAbsolute(70f, 90f); // Ratio standard photo d'identité
                    photoImg.setBorder(Rectangle.BOX);
                    photoImg.setBorderWidth(1f);
                    photoImg.setBorderColor(darkSlate);
                }
            } catch (Exception e) {
                // En cas d'erreur de chargement, on garde photoImg = null
            }
        }
        
        // Cadre de placement pour photo d'identité (si non fournie)
        PdfPTable photoPlaceholder = new PdfPTable(1);
        photoPlaceholder.setWidthPercentage(100);
        PdfPCell pCell = new PdfPCell();
        pCell.setBorder(Rectangle.BOX);
        pCell.setBorderWidth(0.8f);
        pCell.setBorderColor(BaseColor.GRAY);
        pCell.setFixedHeight(90f);
        pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        Font placeholderFont = new Font(Font.FontFamily.HELVETICA, 6f, Font.ITALIC, BaseColor.GRAY);
        Paragraph phP1 = new Paragraph("PHOTO", boldFont);
        phP1.setAlignment(Element.ALIGN_CENTER);
        Paragraph phP2 = new Paragraph("D'IDENTITÉ", placeholderFont);
        phP2.setAlignment(Element.ALIGN_CENTER);
        pCell.addElement(phP1);
        pCell.addElement(phP2);
        photoPlaceholder.addCell(pCell);
        
        // --- 6. CONSTRUCTION DU TABLEAU DES INFORMATIONS DU DOSSIER ---
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.35f, 0.65f});
        
        java.util.function.BiConsumer<String, String> addRow = (key, value) -> {
            PdfPCell keyCell = new PdfPCell(new Phrase(key, boldFont));
            keyCell.setPadding(6f);
            keyCell.setBorderColor(borderCellColor);
            keyCell.setBorderWidth(0.5f);
            keyCell.setBackgroundColor(bgCellColor);
            
            PdfPCell valCell = new PdfPCell(new Phrase(value != null ? value : "Non spécifié", normalFont));
            valCell.setPadding(6f);
            valCell.setBorderColor(borderCellColor);
            valCell.setBorderWidth(0.5f);
            
            table.addCell(keyCell);
            table.addCell(valCell);
        };
        
        addRow.accept("Référence de Suivi :", reference);
        addRow.accept("Type de Demande :", typeDemande);
        addRow.accept("Catégorie de Visa :", categorieVisa);
        addRow.accept("Nom et Prénoms :", nomDemandeur);
        addRow.accept("Nationalité :", demandeur != null && demandeur.getNationalite() != null ? demandeur.getNationalite().getLibelle() : "Non spécifiée");
        
        String passportNum = "Non spécifié";
        if (demande.getVisaTransformable() != null && demande.getVisaTransformable().getPasseport() != null) {
            passportNum = demande.getVisaTransformable().getPasseport().getNumeroPasseport();
        } else if (demandeur != null && demandeur.getPasseports() != null && !demandeur.getPasseports().isEmpty()) {
            passportNum = demandeur.getPasseports().get(0).getNumeroPasseport();
        }
        addRow.accept("Numéro de Passeport :", passportNum);
        
        addRow.accept("Genre :", demandeur != null && demandeur.getGenre() != null ? ("F".equals(demandeur.getGenre()) ? "Féminin" : "Masculin") : "Non spécifié");
        addRow.accept("Date de Naissance :", demandeur != null && demandeur.getDateNaissance() != null ? demandeur.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");
        addRow.accept("Situation Familiale :", demandeur != null && demandeur.getSituationFamiliale() != null ? demandeur.getSituationFamiliale().getLibelle() : "Non spécifiée");
        addRow.accept("Adresse à Madagascar :", demandeur != null && demandeur.getAdresseMada() != null ? demandeur.getAdresseMada() : "Non spécifiée");
        addRow.accept("Contact Téléphonique :", demandeur != null ? String.valueOf(demandeur.getContact()) : "Non spécifié");
        addRow.accept("Adresse Électronique :", demandeur != null && demandeur.getEmail() != null ? demandeur.getEmail() : "Non spécifié");
        addRow.accept("Date du Dépôt :", dateDemande);
        addRow.accept("État du Dossier :", etatDossier);
        
        // --- 7. ZONE DE CONTENU PRINCIPALE (TABLEAU + PHOTO CÔTE-À-CÔTE) ---
        PdfPTable mainContentTable = new PdfPTable(2);
        mainContentTable.setWidthPercentage(100);
        mainContentTable.setWidths(new float[]{0.80f, 0.20f});
        mainContentTable.setSpacingAfter(20);
        
        // Cellule Gauche : Le tableau des informations
        PdfPCell tableContainerCell = new PdfPCell();
        tableContainerCell.setBorder(Rectangle.NO_BORDER);
        tableContainerCell.setPaddingRight(10f);
        tableContainerCell.addElement(table);
        mainContentTable.addCell(tableContainerCell);
        
        // Cellule Droite : La photo d'identité
        PdfPCell photoContainerCell = new PdfPCell();
        photoContainerCell.setBorder(Rectangle.NO_BORDER);
        photoContainerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        photoContainerCell.setVerticalAlignment(Element.ALIGN_TOP);
        photoContainerCell.setPaddingLeft(5f);
        
        if (photoImg != null) {
            photoContainerCell.addElement(photoImg);
            
            Font photoLabelFont = new Font(Font.FontFamily.HELVETICA, 6f, Font.ITALIC, darkSlate);
            Paragraph labelPhoto = new Paragraph("Photo du titulaire", photoLabelFont);
            labelPhoto.setAlignment(Element.ALIGN_CENTER);
            labelPhoto.setSpacingBefore(4);
            photoContainerCell.addElement(labelPhoto);
        } else {
            photoPlaceholder.setHorizontalAlignment(Element.ALIGN_CENTER);
            photoContainerCell.addElement(photoPlaceholder);
            
            Font photoLabelFont = new Font(Font.FontFamily.HELVETICA, 6f, Font.ITALIC, BaseColor.GRAY);
            Paragraph labelPhoto = new Paragraph("(Non fournie)", photoLabelFont);
            labelPhoto.setAlignment(Element.ALIGN_CENTER);
            labelPhoto.setSpacingBefore(4);
            photoContainerCell.addElement(labelPhoto);
        }
        
        mainContentTable.addCell(photoContainerCell);
        document.add(mainContentTable);
        
        // --- 6. SECU & SIGNATURES DOUBLE COLONNE ---
        PdfPTable bottomTable = new PdfPTable(2);
        bottomTable.setWidthPercentage(100);
        bottomTable.setWidths(new float[]{0.45f, 0.55f});
        bottomTable.setSpacingBefore(10);
        
        // Colonne Gauche : QR Code de suivi sécurisé
        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Paragraph qrTitleP = new Paragraph("CONTRÔLE & VÉRIFICATION", boldFont);
        qrTitleP.setAlignment(Element.ALIGN_CENTER);
        qrTitleP.setSpacingAfter(6);
        qrCell.addElement(qrTitleP);
        
        try {
            ByteArrayOutputStream qrStream = generateQRCode(reference);
            Image qrImage = Image.getInstance(qrStream.toByteArray());
            qrImage.scaleAbsolute(90f, 90f);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrCell.addElement(qrImage);
        } catch (Exception e) {
            Paragraph qrError = new Paragraph("(QR Code non disponible)", normalFont);
            qrError.setAlignment(Element.ALIGN_CENTER);
            qrCell.addElement(qrError);
        }
        
        Font qrNoteFont = new Font(Font.FontFamily.HELVETICA, 7f, Font.ITALIC, BaseColor.GRAY);
        Paragraph qrNote = new Paragraph("Cette attestation fait foi du dépôt de votre demande de visa. Scannez ce code QR officiel pour vérifier le statut et l'avancement en temps réel sur la plateforme gouvernementale.", qrNoteFont);
        qrNote.setAlignment(Element.ALIGN_CENTER);
        qrNote.setSpacingBefore(6);
        qrCell.addElement(qrNote);
        
        bottomTable.addCell(qrCell);
        
        // Colonne Droite : Signature & Cachet Officiel
        PdfPCell sigCell = new PdfPCell();
        sigCell.setBorder(Rectangle.NO_BORDER);
        sigCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        Paragraph sigTitle1 = new Paragraph("Pour le Ministre et par délégation,", mottoItalic);
        sigTitle1.setAlignment(Element.ALIGN_RIGHT);
        sigCell.addElement(sigTitle1);
        
        Paragraph sigTitle2 = new Paragraph("Le Directeur de l'Immigration et de l'Émigration", boldFont);
        sigTitle2.setAlignment(Element.ALIGN_RIGHT);
        sigTitle2.setSpacingAfter(10);
        sigCell.addElement(sigTitle2);
        
        // Cachet Officiel Dessiné en Rouge
        PdfPTable cachetTable = new PdfPTable(1);
        cachetTable.setWidthPercentage(75);
        cachetTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell cachetCell = new PdfPCell();
        cachetCell.setBorder(Rectangle.BOX);
        cachetCell.setBorderWidth(1.2f);
        cachetCell.setBorderColor(redFlag);
        cachetCell.setPadding(6);
        cachetCell.setBackgroundColor(new BaseColor(255, 245, 245));
        cachetCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Font stampFont = new Font(Font.FontFamily.HELVETICA, 6.5f, Font.BOLD, redFlag);
        
        Paragraph st1 = new Paragraph("MINISTÈRE DE L'INTÉRIEUR", stampFont);
        st1.setAlignment(Element.ALIGN_CENTER);
        cachetCell.addElement(st1);
        
        Paragraph st2 = new Paragraph("* DIRECTION DE L'IMMIGRATION *", stampFont);
        st2.setAlignment(Element.ALIGN_CENTER);
        cachetCell.addElement(st2);
        
        Paragraph st3 = new Paragraph("CACHET DE SÉCURITÉ DE L'ÉTAT", stampFont);
        st3.setAlignment(Element.ALIGN_CENTER);
        cachetCell.addElement(st3);
        
        Paragraph st4 = new Paragraph("RÉPUBLIQUE DE MADAGASIKARA", stampFont);
        st4.setAlignment(Element.ALIGN_CENTER);
        cachetCell.addElement(st4);
        
        cachetTable.addCell(cachetCell);
        sigCell.addElement(cachetTable);
        
        bottomTable.addCell(sigCell);
        document.add(bottomTable);
        
        document.close();
        
        return pdfStream.toByteArray();
    }
    
    /**
     * Générer une image QR code
     */
    private ByteArrayOutputStream generateQRCode(String text) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        QRCode.from(text)
                .to(ImageType.PNG)
                .withSize(200, 200)
                .writeTo(stream);
        
        stream.close();
        return stream;
    }
}
