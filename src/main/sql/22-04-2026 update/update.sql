-- Active: 1749037938113@@127.0.0.1@5432@visa_db

DROP TABLE IF EXISTS piece_demande CASCADE;
DROP TABLE IF EXISTS type_demande CASCADE;
DROP TABLE IF EXISTS historique_status_demande CASCADE;
DROP TABLE IF EXISTS demande CASCADE;
DROP TABLE IF EXISTS carte_resident CASCADE;
DROP TABLE IF EXISTS visa CASCADE;
DROP TABLE IF EXISTS visa_transformable CASCADE;
DROP TABLE IF EXISTS passeport CASCADE;
DROP TABLE IF EXISTS individu CASCADE;
DROP TABLE IF EXISTS piece_justificative CASCADE;
DROP TABLE IF EXISTS administrateur CASCADE;
DROP TABLE IF EXISTS status CASCADE;
DROP TABLE IF EXISTS categorie_visa CASCADE;
DROP TABLE IF EXISTS situation_familiale CASCADE;
DROP TABLE IF EXISTS nationalite CASCADE;

CREATE TABLE nationalite(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(255) NOT NULL
);

CREATE TABLE situation_familiale(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(255) NOT NULL
);

CREATE TABLE categorie_visa(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(255) NOT NULL
);

CREATE TABLE status(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(255) NOT NULL,
   code VARCHAR(50) NOT NULL
);

CREATE TABLE administrateur(
   id SERIAL PRIMARY KEY,
   nom VARCHAR(255) NOT NULL,
   identifiant VARCHAR(255) NOT NULL,
   mot_de_passe VARCHAR(255) NOT NULL,
   role VARCHAR(50) DEFAULT 'ROLE_ADMIN'
);

CREATE TABLE piece_justificative(
   id SERIAL PRIMARY KEY,
   code VARCHAR(255) NOT NULL,
   libelle VARCHAR(255) NOT NULL,
   obligatoire BOOLEAN NOT NULL
);

CREATE TABLE individu(
   id SERIAL PRIMARY KEY,
   nom VARCHAR(255) NOT NULL,
   prenom VARCHAR(255) NOT NULL,
   genre VARCHAR(1) NOT NULL DEFAULT 'M',
   nom_jeune_fille VARCHAR(255),
   date_naissance DATE NOT NULL,
   adresse_mada VARCHAR(255) NOT NULL,
   contact INTEGER NOT NULL,
   email VARCHAR(255) NOT NULL UNIQUE, 
   id_situation_familiale INTEGER NOT NULL,
   id_nationalite INTEGER NOT NULL,
   FOREIGN KEY(id_situation_familiale) REFERENCES situation_familiale(id),
   FOREIGN KEY(id_nationalite) REFERENCES nationalite(id)
);

CREATE TABLE passeport(
   id SERIAL PRIMARY KEY,
   numero_passeport VARCHAR(255) NOT NULL,
   date_naissance DATE NOT NULL,
   date_delivrance DATE NOT NULL,
   date_expiration DATE NOT NULL,
   id_individu INTEGER NOT NULL,
   FOREIGN KEY(id_individu) REFERENCES individu(id)
);

CREATE TABLE visa_transformable(
   id SERIAL PRIMARY KEY,
   numero_visa VARCHAR(255) NOT NULL,
   date_entree_territoire DATE NOT NULL,
   date_sortie_territoire DATE NOT NULL,
   date_delivrance DATE NOT NULL,
   date_expiration DATE NOT NULL,
   id_passeport INTEGER NOT NULL,
   FOREIGN KEY(id_passeport) REFERENCES passeport(id)
);


CREATE TABLE visa(
   id SERIAL PRIMARY KEY,
   numero_visa VARCHAR(255) NOT NULL,
   date_entree_territoire DATE NOT NULL,
   date_sortie_territoire DATE NOT NULL,
   date_delivrance DATE NOT NULL,
   date_expiration DATE NOT NULL,
   id_passeport INTEGER NOT NULL,
   FOREIGN KEY(id_passeport) REFERENCES passeport(id)
);

CREATE TABLE carte_resident(
   id SERIAL PRIMARY KEY,
   numero_carte VARCHAR(255) NOT NULL,
   date_entree_territoire DATE NOT NULL,
   date_sortie_territoire DATE NOT NULL,
   date_delivrance DATE NOT NULL,
   date_expiration DATE NOT NULL,
   id_passeport INTEGER NOT NULL,
   FOREIGN KEY(id_passeport) REFERENCES passeport(id)
);


CREATE TABLE type_demande(
   id SERIAL PRIMARY KEY,
   libelle VARCHAR(255) NOT NULL
);

CREATE TABLE demande(
   id SERIAL PRIMARY KEY,
   date_demande DATE NOT NULL,
   date_traitement DATE NOT NULL,
   id_visa_transformable INTEGER,
   id_categorie_visa INTEGER,
   id_individu INTEGER NOT NULL,
   id_type_demande INTEGER NOT NULL,
   FOREIGN KEY(id_visa_transformable) REFERENCES visa_transformable(id),
   FOREIGN KEY(id_categorie_visa) REFERENCES categorie_visa(id),
   FOREIGN KEY(id_individu) REFERENCES individu(id),
   FOREIGN KEY(id_type_demande) REFERENCES type_demande(id)
);

CREATE TABLE historique_status_demande(
   id SERIAL PRIMARY KEY,
   date_status TIMESTAMP NOT NULL,
   id_admin INTEGER,
   id_status INTEGER NOT NULL,
   id_demande INTEGER NOT NULL,
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