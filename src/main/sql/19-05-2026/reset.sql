-- Reset et initialisation des données - 19-05-2026

-- Vider les tables dans l'ordre des dépendances
TRUNCATE TABLE historique_fichier CASCADE;
TRUNCATE TABLE piece_demande CASCADE;
TRUNCATE TABLE historique_status_demande CASCADE;
TRUNCATE TABLE demande CASCADE;
TRUNCATE TABLE carte_resident CASCADE;
TRUNCATE TABLE visa CASCADE;
TRUNCATE TABLE visa_transformable CASCADE;
TRUNCATE TABLE passeport CASCADE;
TRUNCATE TABLE individu CASCADE;
TRUNCATE TABLE piece_justificative CASCADE;
TRUNCATE TABLE administrateur CASCADE;
TRUNCATE TABLE status CASCADE;
TRUNCATE TABLE type_demande CASCADE;
TRUNCATE TABLE categorie_visa CASCADE;
TRUNCATE TABLE situation_familiale CASCADE;
TRUNCATE TABLE nationalite CASCADE;

-- Réinitialiser les séquences
ALTER SEQUENCE nationalite_id_seq RESTART;
ALTER SEQUENCE situation_familiale_id_seq RESTART;
ALTER SEQUENCE categorie_visa_id_seq RESTART;
ALTER SEQUENCE status_id_seq RESTART;
ALTER SEQUENCE administrateur_id_seq RESTART;
ALTER SEQUENCE piece_justificative_id_seq RESTART;
ALTER SEQUENCE individu_id_seq RESTART;
ALTER SEQUENCE passeport_id_seq RESTART;
ALTER SEQUENCE visa_transformable_id_seq RESTART;
ALTER SEQUENCE visa_id_seq RESTART;
ALTER SEQUENCE carte_resident_id_seq RESTART;
ALTER SEQUENCE type_demande_id_seq RESTART;
ALTER SEQUENCE demande_id_seq RESTART;
ALTER SEQUENCE historique_status_demande_id_seq RESTART;
ALTER SEQUENCE historique_fichier_id_seq RESTART;

-- Insertion des données de base
INSERT INTO nationalite (libelle) VALUES ('Française'), ('Américaine'), ('Malgache');
INSERT INTO situation_familiale (libelle) VALUES ('Célibataire'), ('Marié(e)'), ('Divorcé(e)');
INSERT INTO categorie_visa (libelle) VALUES ('Investisseur'), ('Travailleur');

-- Statuts complets incluant les nouveaux pour photos et scans
INSERT INTO status (code, libelle) VALUES 
('CREEE', 'Créée'),
('PHOTO_PRISE', 'Photo prise'),
('PHOTO_TERMINEE', 'Photo terminée'),
('SCAN_TERMINE', 'Scan terminé'),
('DOSSIER_FINALISE', 'Dossier finalisé'),
('ATT', 'En attente'),
('VAL', 'Validé'),
('REJ', 'Rejeté'),
('APPROUVEE', 'Approuvée');

INSERT INTO type_demande (libelle) VALUES ('Duplicata'), ('Transfert'), ('Nouveau titre');

INSERT INTO administrateur (nom, identifiant, mot_de_passe, role) 
VALUES ('Admin Principal', 'admin', 'admin', 'ROLE_ADMIN');

-- Pièces justificatives
INSERT INTO piece_justificative (code, libelle, obligatoire) VALUES 
('COM_PHO', '02 photos d''identité', TRUE),
('COM_NOT', 'Notice de renseignement', TRUE),
('COM_CRT', 'Certificat de travail', FALSE),
('COM_DIP', 'Diplôme', FALSE),
('COM_BAN', 'Relevé bancaire', FALSE);
