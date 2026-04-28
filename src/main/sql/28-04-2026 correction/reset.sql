TRUNCATE TABLE piece_demande, historique_status_demande, demande, type_demande, carte_resident, visa, visa_transformable, passeport, demandeur, individu, administrateur, status, categorie_visa, situation_familiale, nationalite, piece_justificative RESTART IDENTITY CASCADE;

INSERT INTO nationalite (libelle) VALUES ('Française'), ('Américaine'), ('Malgache');
INSERT INTO situation_familiale (libelle) VALUES ('Célibataire'), ('Marié(e)'), ('Divorcé(e)');
INSERT INTO categorie_visa (libelle) VALUES ('Investisseur'), ('Travailleur');
INSERT INTO status (code, libelle) VALUES ('ATT', 'En attente'), ('VAL', 'Validé'), ('REJ', 'Rejeté'), ('CREEE', 'Créée'), ('APPROUVEE', 'Approuvée');
INSERT INTO type_demande (libelle) VALUES ('Duplicata'), ('Transfert'), ('Nouveau titre');

INSERT INTO administrateur (nom, identifiant, mot_de_passe, role) 
VALUES ('Admin Principal', 'admin', 'admin', 'ROLE_ADMIN');

INSERT INTO individu (nom, prenom, genre, date_naissance, adresse_mada, contact, email, id_situation_familiale, id_nationalite) 
VALUES ('Ralijaona', 'Shania', 'F', '2000-01-01', 'Lot 123 Antananarivo', 344521178, 'ralijaonashania@gmail.com', 1, 1);

INSERT INTO passeport (numero_passeport, date_naissance, date_delivrance, date_expiration, id_individu) 
VALUES ('PASS-001', '2000-01-01', '2020-01-01', '2030-01-01', 1);

INSERT INTO visa_transformable (numero_visa, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport) 
VALUES ('VT-2026-002', '2026-01-01', '2026-02-01', '2026-01-01', '2027-01-01', 1);

INSERT INTO piece_justificative (code, libelle, obligatoire) VALUES 
('COM_PHO', '02 photos d''identité', TRUE),
('COM_NOT', 'Notice de renseignement', TRUE),
('COM_DEM', 'Demande adressée à Mr le Ministère de l''Intérieur et de la Décentralisation', TRUE),
('COM_VIS', 'Photocopie certifiée du visa en cours de validité', TRUE),
('COM_PAS', 'Photocopie certifiée de la première page du passeport', TRUE),
('COM_CAR', 'Photocopie certifiée de la carte résident en cours de validité', TRUE),
('COM_RES', 'Certificat de résidence à Madagascar', TRUE),
('COM_CAS', 'Extrait de casier judiciaire moins de 3 mois', TRUE),
('INV_STA', 'Statut de la Société', TRUE),
('INV_REG', 'Extrait d''inscription au registre de commerce', TRUE),
('INV_FIS', 'Carte fiscale', TRUE),
('TRA_AUT', 'Autorisation emploi délivrée à Madagascar par le Ministère de la Fonction publique', TRUE),
('TRA_EMP', 'Attestation d''emploi délivré par l''employeur (Original)', TRUE);
