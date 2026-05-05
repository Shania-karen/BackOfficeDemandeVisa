ALTER TABLE piece_demande ADD COLUMN chemin_fichier VARCHAR(255);

-- Rendre id_visa_transformable nullable pour les demandes sans antérieur (Nouveau titre, Duplicata/Transfert sans antérieur)
ALTER TABLE demande ALTER COLUMN id_visa_transformable DROP NOT NULL;

-- Rendre id_categorie_visa nullable car elle ne s'applique pas à tous les types de demande
ALTER TABLE demande ALTER COLUMN id_categorie_visa DROP NOT NULL;