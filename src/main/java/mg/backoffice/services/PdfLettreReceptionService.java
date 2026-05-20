package mg.backoffice.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import mg.backoffice.models.Demande;
import mg.backoffice.models.Demandeur;
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
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, pdfStream);
        
        document.open();
        
        // Titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("LETTRE DE RÉCEPTION DU DOSSIER", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Date
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
        Paragraph dateP = new Paragraph(
            "Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            normalFont);
        dateP.setAlignment(Element.ALIGN_RIGHT);
        dateP.setSpacingAfter(30);
        document.add(dateP);
        
        // Salutation
        Paragraph salutation = new Paragraph("Monsieur, Madame,", normalFont);
        salutation.setSpacingAfter(15);
        document.add(salutation);
        
        // Contenu principal
        String contenuTexte = "Nous confirmons la réception de votre dossier de demande de visa. " +
                "Votre dossier a été enregistré sous la référence suivante:";
        Paragraph contenu = new Paragraph(contenuTexte, normalFont);
        contenu.setSpacingAfter(20);
        document.add(contenu);
        
        // Référence en gros
        Font refFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph refP = new Paragraph(reference, refFont);
        refP.setAlignment(Element.ALIGN_CENTER);
        refP.setSpacingAfter(30);
        document.add(refP);
        
        // Section informations
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Paragraph infoTitle = new Paragraph("Informations de la demande et du demandeur:", boldFont);
        infoTitle.setSpacingAfter(10);
        document.add(infoTitle);
        
        // Tableau d'informations
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(30);
        
        table.addCell(new PdfPCell(new Phrase("Numéro de Demande:", boldFont)));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(idDemande), normalFont)));

        table.addCell(new PdfPCell(new Phrase("Date de Demande:", boldFont)));
        table.addCell(new PdfPCell(new Phrase(dateDemande, normalFont)));

        table.addCell(new PdfPCell(new Phrase("Type de Demande:", boldFont)));
        table.addCell(new PdfPCell(new Phrase(typeDemande, normalFont)));

        table.addCell(new PdfPCell(new Phrase("Catégorie Visa:", boldFont)));
        table.addCell(new PdfPCell(new Phrase(categorieVisa, normalFont)));

        table.addCell(new PdfPCell(new Phrase("État du Dossier:", boldFont)));
        table.addCell(new PdfPCell(new Phrase(etatDossier, normalFont)));

        PdfPCell cell1 = new PdfPCell(new Phrase("Nom et Prénom:", boldFont));
        PdfPCell cell2 = new PdfPCell(new Phrase(nomDemandeur, normalFont));
        table.addCell(cell1);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Phrase("Genre:", boldFont));
        PdfPCell cell4 = new PdfPCell(new Phrase(demandeur != null && demandeur.getGenre() != null ? demandeur.getGenre() : "Non spécifié", normalFont));
        table.addCell(cell3);
        table.addCell(cell4);

        PdfPCell cell5 = new PdfPCell(new Phrase("Date de naissance:", boldFont));
        PdfPCell cell6 = new PdfPCell(new Phrase(
            demandeur != null && demandeur.getDateNaissance() != null
                ? demandeur.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "Non spécifiée",
            normalFont));
        table.addCell(cell5);
        table.addCell(cell6);

        PdfPCell cell7 = new PdfPCell(new Phrase("Nationalité:", boldFont));
        PdfPCell cell8 = new PdfPCell(new Phrase(
            demandeur != null && demandeur.getNationalite() != null ? demandeur.getNationalite().getLibelle() : "Non spécifiée",
            normalFont));
        table.addCell(cell7);
        table.addCell(cell8);

        PdfPCell cell9 = new PdfPCell(new Phrase("Situation familiale:", boldFont));
        PdfPCell cell10 = new PdfPCell(new Phrase(
            demandeur != null && demandeur.getSituationFamiliale() != null ? demandeur.getSituationFamiliale().getLibelle() : "Non spécifiée",
            normalFont));
        table.addCell(cell9);
        table.addCell(cell10);

        PdfPCell cell11 = new PdfPCell(new Phrase("Adresse:", boldFont));
        PdfPCell cell12 = new PdfPCell(new Phrase(
            demandeur != null && demandeur.getAdresseMada() != null ? demandeur.getAdresseMada() : "Non spécifiée",
            normalFont));
        table.addCell(cell11);
        table.addCell(cell12);

        PdfPCell cell13 = new PdfPCell(new Phrase("Contact:", boldFont));
        PdfPCell cell14 = new PdfPCell(new Phrase(
            demandeur != null ? String.valueOf(demandeur.getContact()) : "Non spécifié",
            normalFont));
        table.addCell(cell13);
        table.addCell(cell14);

        PdfPCell cell15 = new PdfPCell(new Phrase("Email:", boldFont));
        PdfPCell cell16 = new PdfPCell(new Phrase(
            demandeur != null && demandeur.getEmail() != null ? demandeur.getEmail() : "Non spécifié",
            normalFont));
        table.addCell(cell15);
        table.addCell(cell16);
        
        document.add(table);
        
        // QR Code
        try {
            Paragraph qrTitle = new Paragraph("Code de suivi (QR Code):", boldFont);
            qrTitle.setSpacingBefore(20);
            qrTitle.setSpacingAfter(15);
            document.add(qrTitle);
            
            // Générer le QR code
            ByteArrayOutputStream qrStream = generateQRCode(reference);
            Image qrImage = Image.getInstance(qrStream.toByteArray());
            qrImage.scaleAbsolute(100f, 100f);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrImage);
        } catch (Exception e) {
            Paragraph qrError = new Paragraph("(Code QR non disponible)", normalFont);
            document.add(qrError);
        }
        
        // Message de conclusion
        document.add(new Paragraph(" "));
        
        Paragraph conclusion1 = new Paragraph(
            "Veuillez conserver cette lettre pour toute correspondance future.",
            normalFont);
        conclusion1.setSpacingBefore(20);
        conclusion1.setSpacingAfter(10);
        document.add(conclusion1);
        
        Paragraph conclusion2 = new Paragraph(
            "Vous pouvez nous contacter en cas de question concernant votre dossier.",
            normalFont);
        conclusion2.setSpacingAfter(30);
        document.add(conclusion2);
        
        Paragraph signature = new Paragraph("Cordialement,", normalFont);
        signature.setSpacingBefore(30);
        document.add(signature);
        
        Paragraph ministry = new Paragraph("Le Ministère de l'Intérieur", boldFont);
        document.add(ministry);
        
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
