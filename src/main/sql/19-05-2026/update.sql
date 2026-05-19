-- Mise à jour 19-05-2026: Gestion des photos et scans de pièces justificatives

-- 1. Ajouter les nouveaux statuts pour photo et scan
INSERT INTO status (code, libelle) VALUES 
('PHOTO_PRISE', 'Photo prise'),
('PHOTO_TERMINEE', 'Photo terminée'),
('SCAN_TERMINE', 'Scan terminé'),
('DOSSIER_FINALISE', 'Dossier finalisé');

-- 2. Modifier la table piece_demande pour supporter les photos et scans
-- Ajouter colonnes pour gérer l'état des pièces (photo, scan, etc.)
ALTER TABLE piece_demande ADD COLUMN IF NOT EXISTS chemin_photo VARCHAR(255);
ALTER TABLE piece_demande ADD COLUMN IF NOT EXISTS chemin_scan VARCHAR(255);
ALTER TABLE piece_demande ADD COLUMN IF NOT EXISTS date_photo TIMESTAMP;
ALTER TABLE piece_demande ADD COLUMN IF NOT EXISTS date_scan TIMESTAMP;
ALTER TABLE piece_demande ADD COLUMN IF NOT EXISTS etat_piece VARCHAR(50) DEFAULT 'BROUILLON';

-- etat_piece peut être: BROUILLON, PHOTO_PRISE, PHOTO_TERMINEE, SCAN_TERMINE, FINALISEE

-- 3. Ajouter une colonne pour tracker l'état global du dossier dans demande
ALTER TABLE demande ADD COLUMN IF NOT EXISTS etat_dossier VARCHAR(50) DEFAULT 'BROUILLON';
-- etat_dossier peut être: BROUILLON, PHOTO_EN_COURS, SCAN_EN_COURS, FINALISE

-- 4. Ajouter colonne pour le QR token de réception (si pas déjà existante)
-- Utilisée pour la génération du PDF de lettre de réception
ALTER TABLE demande ADD COLUMN IF NOT EXISTS date_qr_genere TIMESTAMP;

-- 5. Créer table pour tracker les historiques d'upload
CREATE TABLE IF NOT EXISTS historique_fichier (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER NOT NULL,
    id_piece INTEGER,
    type_fichier VARCHAR(50) NOT NULL, -- PHOTO, SCAN, PDF
    chemin_fichier VARCHAR(255) NOT NULL,
    date_upload TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    taille_fichier INTEGER,
    FOREIGN KEY(id_demande) REFERENCES demande(id) ON DELETE CASCADE,
    FOREIGN KEY(id_piece) REFERENCES piece_justificative(id)
);

-- Index pour performances
CREATE INDEX IF NOT EXISTS idx_historique_fichier_demande ON historique_fichier(id_demande);
CREATE INDEX IF NOT EXISTS idx_piece_demande_etat ON piece_demande(etat_piece);
CREATE INDEX IF NOT EXISTS idx_demande_etat_dossier ON demande(etat_dossier);
