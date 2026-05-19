-- Données de test pour les fonctionnalités de photos et scans - 19-05-2026
-- À exécuter APRÈS reset.sql et update.sql

-- ============================================
-- Insertion des données de base
-- ============================================

-- Demandeur 1 : Test Photo/Scan
INSERT INTO individu (nom, prenom, genre, date_naissance, adresse_mada, contact, email, id_situation_familiale, id_nationalite)
VALUES ('Dupont', 'Jean', 'M', '1990-05-15', '123 Rue de Test, Antananarivo', 261234567, 'jean.dupont@test.com', 1, 1);

-- Passeport pour Jean Dupont
INSERT INTO passeport (numero_passeport, date_naissance, date_delivrance, date_expiration, id_individu)
VALUES ('FR0001234', '1990-05-15', '2020-01-10', '2030-01-09', 1);

-- Visa transformable pour Jean Dupont
INSERT INTO visa_transformable (numero_visa, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport)
VALUES ('VT_TEST_001', '2022-03-01', '2027-03-01', '2022-02-15', '2027-02-15', 1);

-- Demande pour Jean Dupont (Nouveau titre)
INSERT INTO demande (date_demande, date_traitement, id_visa_transformable, id_categorie_visa, id_individu, id_type_demande, etat_dossier)
VALUES (CURRENT_DATE, CURRENT_DATE, 1, 1, 1, 3, 'BROUILLON');

-- Ajouter les pièces justificatives à la demande
INSERT INTO piece_demande (id_demande, id_piece, etat_piece)
VALUES 
(1, 1, 'BROUILLON'),  -- Photos d'identité
(1, 2, 'BROUILLON');  -- Notice de renseignement

-- ============================================
-- Demandeur 2 : Test avec dossier finalisé
-- ============================================

INSERT INTO individu (nom, prenom, genre, date_naissance, adresse_mada, contact, email, id_situation_familiale, id_nationalite)
VALUES ('Martin', 'Claire', 'F', '1988-08-22', '456 Avenue Test, Antananarivo', 261234568, 'claire.martin@test.com', 2, 3);

INSERT INTO passeport (numero_passeport, date_naissance, date_delivrance, date_expiration, id_individu)
VALUES ('MG0005678', '1988-08-22', '2019-06-20', '2029-06-19', 2);

INSERT INTO visa_transformable (numero_visa, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport)
VALUES ('VT_TEST_002', '2020-07-01', '2025-07-01', '2020-06-15', '2025-06-15', 2);

INSERT INTO demande (date_demande, date_traitement, id_visa_transformable, id_categorie_visa, id_individu, id_type_demande, etat_dossier)
VALUES (CURRENT_DATE, CURRENT_DATE, 2, 2, 2, 1, 'FINALISE');

INSERT INTO piece_demande (id_demande, id_piece, etat_piece, chemin_scan, date_scan)
VALUES 
(2, 1, 'SCAN_TERMINE', 'uploads/demande_2/scans/scan_piece_1_20260519_120000.pdf', CURRENT_TIMESTAMP),
(2, 2, 'SCAN_TERMINE', 'uploads/demande_2/scans/scan_piece_2_20260519_120100.pdf', CURRENT_TIMESTAMP);

-- Enregistrer dans l'historique
INSERT INTO historique_fichier (id_demande, id_piece, type_fichier, chemin_fichier, date_upload, taille_fichier)
VALUES 
(2, 1, 'SCAN', 'uploads/demande_2/scans/scan_piece_1_20260519_120000.pdf', CURRENT_TIMESTAMP, 245000),
(2, 2, 'SCAN', 'uploads/demande_2/scans/scan_piece_2_20260519_120100.pdf', CURRENT_TIMESTAMP, 120000);

-- ============================================
-- Historique de statut
-- ============================================

INSERT INTO historique_status_demande (date_status, id_admin, id_status, id_demande)
VALUES 
(CURRENT_TIMESTAMP, 1, 1, 1),  -- Créée
(CURRENT_TIMESTAMP, 1, 1, 2);  -- Créée
