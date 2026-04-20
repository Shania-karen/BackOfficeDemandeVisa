-- Active: 1749037938113@@127.0.0.1@5432@visa_db
-- Script SQL pour la création de la base de données de gestion des visas
--nom de la base visa_db

CREATE TABLE nationalite(
   id SERIAL,
   libelle VARCHAR(255)  NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE situation_familiale(
   id SERIAL,
   libelle VARCHAR(255)  NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE categorie_visa(
   id SERIAL,
   libelle VARCHAR(255)  NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE status(
   id SERIAL,
   libelle VARCHAR(255)  NOT NULL,
   code VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE administrateur(
   id SERIAL,
   nom VARCHAR(255)  NOT NULL,
   identifiant VARCHAR(255)  NOT NULL,
   mot_de_passe VARCHAR(255)  NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE piece_justificative(
   id SERIAL,
   code VARCHAR(255)  NOT NULL,
   libelle VARCHAR(255)  NOT NULL,
   obligatoire BOOLEAN NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE demandeur(
   id SERIAL,
   nom VARCHAR(255)  NOT NULL,
   prenom VARCHAR(255)  NOT NULL,
   adresse_mada VARCHAR(255)  NOT NULL,
   contact INTEGER NOT NULL,
   email VARCHAR(255)  NOT NULL,
   id_situation_familiale INTEGER NOT NULL,
   id_nationalite INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_situation_familiale) REFERENCES situation_familiale(id),
   FOREIGN KEY(id_nationalite) REFERENCES nationalite(id)
);

CREATE TABLE passeport(
   id SERIAL,
   numero_passeport VARCHAR(255)  NOT NULL,
   date_naissance DATE NOT NULL,
   date_delivrance DATE NOT NULL,
   date_expiration DATE NOT NULL,
   id_demandeur INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_demandeur) REFERENCES demandeur(id)
);

CREATE TABLE visa(
   id SERIAL,
   numero_visa VARCHAR(255)  NOT NULL,
   date_entree_territoire DATE NOT NULL,
   date_sortie_territoire DATE NOT NULL,
   date_delivrance DATE NOT NULL,
   date_expiration DATE NOT NULL,
   id_passeport INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_passeport) REFERENCES passeport(id)
);

CREATE TABLE visa_transformable(
   id SERIAL,
   numero_visa VARCHAR(255)  NOT NULL,
   date_entree_territoire DATE NOT NULL,
   date_sortie_territoire DATE NOT NULL,
   date_delivrance DATE NOT NULL,
   date_expiration DATE NOT NULL,
   id_demandeur INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_demandeur) REFERENCES demandeur(id)
);

CREATE TABLE carte_resident(
   id SERIAL,
   numero_carte VARCHAR(255)  NOT NULL,
   date_entree_territoire DATE NOT NULL,
   date_sortie_territoire DATE NOT NULL,
   date_delivrance DATE NOT NULL,
   date_expiration DATE NOT NULL,
   id_passeport INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_passeport) REFERENCES passeport(id)
);

CREATE TABLE demande(
   id SERIAL,
   date_demande DATE NOT NULL,
   date_traitement DATE NOT NULL,
   id_visa_transformable INTEGER NOT NULL,
   id_categorie_visa INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_visa_transformable) REFERENCES visa_transformable(id),
   FOREIGN KEY(id_categorie_visa) REFERENCES categorie_visa(id)
);

CREATE TABLE historique_status_demande(
   id SERIAL,
   date_status TIMESTAMP NOT NULL,
   id_admin INTEGER NOT NULL,
   id_status INTEGER NOT NULL,
   id_demande INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_admin) REFERENCES administrateur(id),
   FOREIGN KEY(id_status) REFERENCES status(id),
   FOREIGN KEY(id_demande) REFERENCES demande(id)
);

CREATE TABLE piece_demande(
   id_demande INTEGER,
   id_piece INTEGER,
   PRIMARY KEY(id_demande, id_piece),
   FOREIGN KEY(id_demande) REFERENCES demande(id),
   FOREIGN KEY(id_piece) REFERENCES piece_justificative(id)
);
