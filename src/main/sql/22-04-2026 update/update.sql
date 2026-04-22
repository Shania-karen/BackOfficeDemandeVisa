-- 1. Ajout du champ manquant dans la table demandeur
ALTER TABLE demandeur
ADD COLUMN nom_jeune_fille VARCHAR(255),
ADD COLUMN date_naissance DATE,
ADD COLUMN genre VARCHAR(1) NOT NULL DEFAULT 'M';

-- 2. Insertion des catégories de visa
INSERT INTO categorie_visa (libelle) VALUES 
('Investisseur'), 
('Travailleur'); 

-- Insertion des situations familiales
INSERT INTO situation_familiale (libelle) VALUES 
('Célibataire'),
('Marié(e)'),
('Divorcé(e)');


-- 3. Insertion des pièces justificatives communes (toutes obligatoires pour l'exemple)
INSERT INTO piece_justificative (code, libelle, obligatoire) VALUES 
('COM_PHO', '02 photos d''identité', TRUE),
('COM_NOT', 'Notice de renseignement', TRUE),
('COM_DEM', 'Demande adressée à Mr le Ministère', TRUE),
('COM_VIS', 'Photocopie certifiée du visa en cours', TRUE),
('COM_PAS', 'Photocopie certifiée de la 1ère page du passeport', TRUE),
('COM_RES', 'Photocopie certifiée de la carte résident', TRUE),
('COM_CER', 'Certificat de résidence à Madagascar', TRUE),
('COM_CAS', 'Extrait de casier judiciaire moins de 3 mois', TRUE);

-- 4. Insertion des pièces spécifiques
-- Investisseur
INSERT INTO piece_justificative (code, libelle, obligatoire) VALUES 
('INV_STA', 'Statut de la Société', TRUE),
('INV_REG', 'Extrait d''inscription au registre de commerce', TRUE),
('INV_FIS', 'Carte fiscale', TRUE);

-- Travailleur
INSERT INTO piece_justificative (code, libelle, obligatoire) VALUES 
('TRA_AUT', 'Autorisation emploi délivrée à Madagascar', TRUE),
('TRA_ATT', 'Attestation d''emploi délivré par l''employeur', TRUE);

-- 5. Modifications de structure
-- Suppression constraint id_demandeur dans visa_transformable 
ALTER TABLE visa_transformable DROP COLUMN id_demandeur;

-- Ajout du lien entre un visa transformable et le passeport sur lequel il se trouve
ALTER TABLE visa_transformable 
ADD COLUMN id_passeport INTEGER,
ADD CONSTRAINT fk_visa_transform_passeport FOREIGN KEY (id_passeport) REFERENCES passeport(id);

-- Ajout du demandeur dans la table demande (pour savoir qui fait la demande)
ALTER TABLE demande 
ADD COLUMN id_demandeur INTEGER,
ADD CONSTRAINT fk_demande_demandeur FOREIGN KEY (id_demandeur) REFERENCES demandeur(id);

-- 6. Données de test (jeu de test complet)

-- Nationalités
INSERT INTO nationalite (libelle) VALUES 
('Française'), 
('Américaine'), 
('Canadienne');

-- Status de demande
INSERT INTO status (code, libelle) VALUES 
('ATT', 'En attente'), 
('VAL', 'Validé'), 
('REJ', 'Rejeté');

-- Demandeurs
-- (Supposons id_situation=1 pour Célibataire, id_nationalite=1 pour Français)
INSERT INTO demandeur (nom, prenom, adresse_mada, contact, email, id_situation_familiale, id_nationalite, nom_jeune_fille, date_naissance, genre) 
VALUES ('Dupont', 'Jean', 'Lot 123 Antananarivo', 340000001, 'jean@example.com', 1, 1, '', '1990-05-14', 'M');

-- (Supposons id_situation=2 pour Marié(e), id_nationalite=2 pour Américaine)
INSERT INTO demandeur (nom, prenom, adresse_mada, contact, email, id_situation_familiale, id_nationalite, nom_jeune_fille, date_naissance, genre) 
VALUES ('Smith', 'Alice', 'Lot 456 Majunga', 320000002, 'alice@example.com', 2, 1, 'Johnson', '1985-11-20', 'F');

-- Passeports (Liés aux demandeurs via ID)
INSERT INTO passeport (numero_passeport, date_naissance, date_delivrance, date_expiration, id_demandeur) VALUES 
('PASS-FR-001', '1990-05-14', '2020-01-01', '2030-01-01', 1),
('PASS-US-002', '1985-11-20', '2018-05-10', '2028-05-10', 3);

-- Visas transformables (Liés au passeport)
INSERT INTO visa_transformable (numero_visa, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport) VALUES 
('VT-2026-001', '2026-01-15', '2026-02-15', '2026-01-10', '2026-04-10', 5),
('VT-2026-002', '2026-03-10', '2026-04-10', '2026-03-05', '2026-06-05', 6);

-- Demandes (Liées au visa transformable, à la catégorie de visa et au demandeur)
INSERT INTO demande (date_demande, date_traitement, id_visa_transformable, id_categorie_visa, id_demandeur) VALUES 
('2026-04-20', '2026-04-22',8, 1, 1), -- Dupont demande Investisseur (ID 1)
('2026-04-21', '2026-04-22', 9, 2, 3); -- Smith demande Travailleur (ID 2)
