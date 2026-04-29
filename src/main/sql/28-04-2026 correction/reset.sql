TRUNCATE TABLE piece_demande, historique_status_demande, demande, type_demande, carte_resident, visa, visa_transformable, passeport, individu, administrateur, status, categorie_visa, situation_familiale, nationalite, piece_justificative RESTART IDENTITY CASCADE;

INSERT INTO nationalite (libelle) VALUES ('Française'), ('Américaine'), ('Malgache');
INSERT INTO situation_familiale (libelle) VALUES ('Célibataire'), ('Marié(e)'), ('Divorcé(e)');
INSERT INTO categorie_visa (libelle) VALUES ('Investisseur'), ('Travailleur');
INSERT INTO status (code, libelle) VALUES ('ATT', 'En attente'), ('VAL', 'Validé'), ('REJ', 'Rejeté'), ('CREEE', 'Créée'), ('APPROUVEE', 'Approuvée');
INSERT INTO type_demande (libelle) VALUES ('Duplicata'), ('Transfert'), ('Nouveau titre');

INSERT INTO administrateur (nom, identifiant, mot_de_passe, role) 
VALUES ('Admin Principal', 'admin', 'admin', 'ROLE_ADMIN');


INSERT INTO piece_justificative (code, libelle, obligatoire) VALUES 
('COM_PHO', '02 photos d''identité', TRUE),
('COM_NOT', 'Notice de renseignement', TRUE);
