# Mise à Jour 19-05-2026 : Gestion des Photos et Scans

## Vue d'ensemble
Cette mise à jour ajoute une gestion complète des photos et scans des pièces justificatives, avec génération de lettres de réception en PDF avec QR codes.

## Changements Principaux

### 1. Base de Données
**Fichiers SQL créés dans `/src/main/sql/19-05-2026/`:**
- `update.sql` : Ajoute les colonnes et tables nécessaires
- `reset.sql` : Réinitialise les données

**Nouvelles colonnes dans `piece_demande`:**
- `chemin_photo` : Chemin du fichier photo
- `chemin_scan` : Chemin du fichier scan
- `date_photo` : Date d'upload de la photo
- `date_scan` : Date d'upload du scan
- `etat_piece` : État de la pièce (BROUILLON, PHOTO_PRISE, PHOTO_TERMINEE, SCAN_TERMINE, FINALISEE)

**Nouvelles colonnes dans `demande`:**
- `etat_dossier` : État global du dossier (BROUILLON, PHOTO_EN_COURS, SCAN_EN_COURS, FINALISE)
- `date_qr_genere` : Date de génération du QR code

**Nouvelle table `historique_fichier`:**
- Historise tous les uploads de fichiers (photos, scans, PDFs)

**Nouveaux statuts ajoutés:**
- PHOTO_PRISE
- PHOTO_TERMINEE
- SCAN_TERMINE
- DOSSIER_FINALISE

### 2. Modèles Java
**Modifiés:**
- `Demande.java` : Ajout de `etatDossier` et `dateQrGenere`
- `PieceDemande.java` : Ajout des colonnes pour photo, scan, dates et état

**Créés:**
- `HistoriqueFichier.java` : Nouveau modèle pour tracer les uploads

### 3. Services
**Créés:**
- `PieceUploadService.java` : Gestion des uploads de photos et scans
- `PdfLettreReceptionService.java` : Génération de PDF avec lettre et QR code

**Méthodes principales de PieceUploadService:**
- `uploadPhotoJustificatif()` : Upload une photo
- `uploadScanJustificatif()` : Upload un scan
- `updateEtatDossier()` : Met à jour l'état du dossier
- `getAllPiecesForDemande()` : Récupère toutes les pièces
- `getHistoriqueFichiers()` : Récupère l'historique

### 4. Contrôleurs
**Créé:**
- `PieceUploadController.java` avec endpoints:
  - `POST /demandes/upload-photo` : Upload photo
  - `POST /demandes/upload-scan` : Upload scan
  - `GET /demandes/apercu/{idDemande}/{idPiece}` : Affiche aperçu
  - `GET /demandes/apercu-tous/{idDemande}` : Affiche tous les aperçus
  - `GET /demandes/generer-lettre-reception/{idDemande}` : Génère PDF

### 5. Templates HTML
**Modifiés:**
- `detail-demande.html` : Ajout de boutons pour photos, scans, aperçu, lettre

**Créés:**
- `apercu-pieces.html` : Page d'aperçu de tous les pièces

### 6. Dépendances Maven
**Ajoutées:**
- `itext7-core:7.2.4` : Génération de PDF
- `zxing:core:3.5.1` : Génération de QR codes
- `zxing:javase:3.5.1` : Support Java pour ZXing

### 7. Configuration
**Modifications dans `application.properties`:**
- Ajout de `upload.dir=uploads` : Répertoire des uploads

## Workflow d'utilisation

### État des Pièces Justificatives
```
BROUILLON → PHOTO_PRISE → PHOTO_TERMINEE → SCAN_TERMINE → FINALISEE
```

### État du Dossier
```
BROUILLON → PHOTO_EN_COURS → SCAN_EN_COURS → FINALISE
```

## Processus d'Upload

1. **Upload Photo** : 
   - L'utilisateur clique sur le bouton "📷 Photo"
   - Sélectionne ou prend une photo
   - La pièce passe à l'état "PHOTO_PRISE"

2. **Upload Scan** :
   - L'utilisateur clique sur le bouton "📄 Scan"
   - Upload un fichier PDF ou image
   - La pièce passe à l'état "SCAN_TERMINE"

3. **Aperçu** :
   - L'utilisateur clique sur "Aperçu"
   - Affiche la photo ou le scan dans le navigateur
   - Non téléchargeable, juste pour visualiser

4. **Génération Lettre** :
   - Quand TOUS les scans sont terminés, le dossier passe à "FINALISE"
   - L'utilisateur peut cliquer sur "📥 Télécharger lettre de réception"
   - Génère un PDF avec:
     - Lettre de confirmation
     - Numéro de référence unique
     - QR code contenant le numéro de référence
     - Informations du demandeur

## Détails Techniques

### Upload de Fichiers
- Les fichiers sont sauvegardés dans `uploads/demande_{id}/photos/` ou `uploads/demande_{id}/scans/`
- Noms des fichiers: `photo_piece_{id}_{timestamp}.{ext}` ou `scan_piece_{id}_{timestamp}.{ext}`
- Tous les uploads sont enregistrés dans la table `historique_fichier`

### Génération du QR Code
- Format: `DOSSIER_{idDemande}_{timestamp}`
- Utilisé pour la lettre de réception
- Généré avec la bibliothèque ZXing

### Génération du PDF
- Utilise iText7
- Contient:
  - En-tête "LETTRE DE RÉCEPTION DU DOSSIER"
  - Informations du demandeur
  - Numéro de référence unique
  - QR code de suivi
  - Message de conclusion

## Prérequis pour Utiliser

1. **Exécuter les scripts SQL** :
   ```sql
   -- Dans votre base de données visa_db
   -- Exécuter d'abord reset.sql pour réinitialiser les données
   -- Puis exécuter update.sql pour ajouter les nouvelles colonnes
   ```

2. **Créer le répertoire uploads** :
   ```bash
   mkdir -p uploads
   ```

3. **Relancer l'application** :
   - Maven recompilera le projet avec les nouvelles dépendances
   - Les classes mappées à la BD seront créées

## Endpoints Disponibles

### Upload
- `POST /demandes/upload-photo?idDemande=1&idPiece=1` → Upload photo
- `POST /demandes/upload-scan?idDemande=1&idPiece=1` → Upload scan

### Aperçu
- `GET /demandes/apercu/1/1` → Aperçu d'une pièce (navigateur)
- `GET /demandes/apercu-tous/1` → Tous les aperçus (page HTML)

### Génération
- `GET /demandes/generer-lettre-reception/1` → PDF lettre + QR code

## Validation du Dossier

Le dossier est considéré comme finalisé quand:
- ✅ Toutes les pièces obligatoires ont un scan
- ✅ L'état de toutes les pièces est "SCAN_TERMINE"
- ✅ L'état du dossier est "FINALISE"

Seulement à ce moment:
- ✅ La lettre de réception peut être générée
- ✅ Le PDF peut être téléchargé

## Limitations Actuelles

- La photo ne peut pas être modifiée entre photo et scan (ce qui est attendu selon la spec)
- Les aperçus ne sont pas téléchargeables (juste visualisation)
- Le QR code est généré avec la référence et timestamp

## Prochaines Étapes Possibles

- Intégration OCR pour extraction d'informations
- Signature électronique sur la lettre
- Archivage automatique des dossiers finalisés
- Système de notifications
- Authentification de l'utilisateur pour signature
