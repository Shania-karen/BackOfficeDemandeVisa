-- Données de test pour les trois scénarios : Nouveau titre, Duplicata, Transfert
-- À exécuter APRÈS reset.sql et update.sql

-- ============================================
-- SCÉNARIO 1 : NOUVEAU TITRE (Sans antérieur)
-- ============================================
-- Demandeur 1 : Karen shania
INSERT INTO individu (nom, prenom, genre, nom_jeune_fille, date_naissance, adresse_mada, contact, email, id_situation_familiale, id_nationalite)
VALUES ('Karen', 'Shania', 'F', NULL, '2005-04-29', '102 Avenue de France, Antananarivo', 012345678, 'karen.shania@email.com', 1, 3);

-- Passeport pour Karen shania
INSERT INTO passeport (numero_passeport, date_naissance, date_delivrance, date_expiration, id_individu)
VALUES ('NL2567890', '2005-04-29', '2020-01-10', '2030-01-09', 1);

-- Carte résident pour Karen shania (liée à son passeport)
INSERT INTO carte_resident (numero_carte, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport)
VALUES ('CR001', '2020-02-01', '2025-02-01', '2020-01-20', '2030-01-01', 1);

-- Visa transformable pour Karen shania (basé sur sa carte résident existante)

INSERT INTO visa_transformable (numero_visa, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport)
VALUES ('VT001', '2018-04-10', '2023-04-10', '2018-03-25', '2030-01-01', 1);


-- ============================================
-- SCÉNARIO 2 : DUPLICATA (Avec antérieur)
-- ============================================
-- Demandeur 2 : Marie Martin
INSERT INTO individu (nom, prenom, genre, nom_jeune_fille, date_naissance, adresse_mada, contact, email, id_situation_familiale, id_nationalite)
VALUES ('Martin', 'Marie', 'F', 'Dubois', '1985-08-22', '45 Rue de la Paix, Antananarivo', 261234567, 'marie.martin@email.com', 2, 3);

-- Passeport pour Marie Martin
INSERT INTO passeport (numero_passeport, date_naissance, date_delivrance, date_expiration, id_individu)
VALUES ('NL2468901', '1985-08-22', '2018-03-15', '2030-01-01', 2);

-- Carte résident pour Marie Martin (liée à son passeport)
INSERT INTO carte_resident (numero_carte, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport)
VALUES ('CR002', '2018-04-10', '2023-04-10', '2030-01-01', '2030-01-01', 2);

-- Visa transformable pour Marie Martin (basé sur sa carte résident existante)
INSERT INTO visa_transformable (numero_visa, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport)
VALUES ('VT002', '2018-04-10', '2023-04-10', '2030-01-01', '2030-01-01', 2);


-- ============================================
-- SCÉNARIO 3 : TRANSFERT (Avec antérieur)
-- ============================================
-- Demandeur 3 : Pierre Bernard
INSERT INTO individu (nom, prenom, genre, nom_jeune_fille, date_naissance, adresse_mada, contact, email, id_situation_familiale, id_nationalite)
VALUES ('Bernard', 'Pierre', 'M', NULL, '1988-12-10', '78 Boulevard Joffre, Antananarivo', 261344567, 'pierre.bernard@email.com', 1, 3);

-- Passeport pour Pierre Bernard
INSERT INTO passeport (numero_passeport, date_naissance, date_delivrance, date_expiration, id_individu)
VALUES ('NL2579013', '1988-12-10', '2019-06-20', '2029-06-19', 5);

-- Visa existant pour Pierre Bernard (pour test Transfert)
INSERT INTO visa (numero_visa, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport)
VALUES ('V001', '2019-07-15', '2024-07-15', '2030-01-01', '2030-01-01', 7);

-- Visa transformable pour Pierre Bernard (pour le transfert)
INSERT INTO visa_transformable (numero_visa, date_entree_territoire, date_sortie_territoire, date_delivrance, date_expiration, id_passeport)
VALUES ('VT002', '2019-07-15', '2024-07-15', '2030-01-01', '2030-01-01', 7);


-- ============================================
-- SCÉNARIO 4 : DUPLICATA SANS ANTÉRIEUR
-- ============================================
-- Demandeur 4 : Sophie Laurent (SANS carte résident existante)
INSERT INTO individu (nom, prenom, genre, nom_jeune_fille, date_naissance, adresse_mada, contact, email, id_situation_familiale, id_nationalite)
VALUES ('Laurent', 'Sophie', 'F', NULL, '1992-11-03', '89 Rue des Fleurs, Antananarivo', 26134567, 'sophie.laurent@email.com', 1, 3);

-- Passeport pour Sophie Laurent
INSERT INTO passeport (numero_passeport, date_naissance, date_delivrance, date_expiration, id_individu)
VALUES ('NL01', '1992-11-03', '2021-05-12', '2030-01-01', 6);

-- SANS carte résident et SANS visa transformable pour Sophie Laurent (Duplicata sans antérieur)
