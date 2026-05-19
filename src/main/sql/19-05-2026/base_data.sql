-- Données de base minimales pour tester le front - 19-05-2026
-- À exécuter après le reset du schéma/données

-- Nettoyage pour permettre de rejouer le seed sans doublons
TRUNCATE TABLE historique_status_demande, piece_demande, demande, visa_transformable, passeport, individu, administrateur, piece_justificative, type_demande, status, categorie_visa, situation_familiale, nationalite RESTART IDENTITY CASCADE;

-- Référentiels
INSERT INTO nationalite (libelle) VALUES 
('Française'),
('Américaine'),
('Malgache');

INSERT INTO situation_familiale (libelle) VALUES 
('Célibataire'),
('Marié(e)'),
('Divorcé(e)');

INSERT INTO categorie_visa (libelle) VALUES 
('Investisseur'),
('Travailleur');

INSERT INTO status (code, libelle) VALUES 
('CREEE', 'Créée'),
('ATT', 'En attente'),
('PHOTO_PRISE', 'Photo prise'),
('PHOTO_TERMINEE', 'Photo terminée'),
('SCAN_TERMINE', 'Scan terminé'),
('DOSSIER_FINALISE', 'Dossier finalisé'),
('APPROUVEE', 'Approuvée'),
('REJ', 'Rejeté');

INSERT INTO type_demande (libelle) VALUES 
('Duplicata'),
('Transfert'),
('Nouveau titre');

INSERT INTO administrateur (nom, identifiant, mot_de_passe, role) VALUES 
('Admin Principal', 'admin', 'admin', 'ROLE_ADMIN');

INSERT INTO piece_justificative (code, libelle, obligatoire) VALUES 
('COM_PHO', '02 photos d''identité', TRUE),
('COM_NOT', 'Notice de renseignement', TRUE);

-- Demandeur de test
INSERT INTO individu (
    nom, prenom, genre, nom_jeune_fille, date_naissance,
    adresse_mada, contact, email, id_situation_familiale, id_nationalite
) VALUES (
    'Test', 'Marie', 'F', NULL, '1992-06-15',
    'Lot IV 123 Antananarivo', 261234567, 'marie.test@example.com', 1, 3
);

-- Passeport lié au demandeur
INSERT INTO passeport (
    numero_passeport, date_naissance, date_delivrance, date_expiration, id_individu
) VALUES (
    'P12345678', '1992-06-15', '2022-01-10', '2032-01-09', 1
);

-- Visa transformable lié au passeport
INSERT INTO visa_transformable (
    numero_visa, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport
) VALUES (
    'VT0001', '2023-01-01', '2025-01-01', '2023-01-01', '2025-01-01', 1
);

-- Demande de test à ouvrir dans la fiche demande
INSERT INTO demande (
    date_demande, date_traitement, id_visa_transformable, id_categorie_visa,
    id_individu, id_type_demande, qr_token, etat_dossier
) VALUES (
    CURRENT_DATE, CURRENT_DATE, 1, 1,
    1, 3, 'REF-TEST-001', 'BROUILLON'
);

-- Pièces attendues pour la demande
INSERT INTO piece_demande (
    id_demande, id_piece, chemin_fichier, chemin_photo, chemin_scan, etat_piece
) VALUES 
(1, 1, NULL, NULL, NULL, 'BROUILLON'),
(1, 2, NULL, NULL, NULL, 'BROUILLON');

-- Historique initial
INSERT INTO historique_status_demande (
    date_status, id_admin, id_status, id_demande
) VALUES (
    CURRENT_TIMESTAMP, 1, 1, 1
);