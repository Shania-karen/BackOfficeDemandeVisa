# Prompt rectifié pour l'IA (implémentation Sprint 2)

## Contexte
Application Spring Boot + Thymeleaf de gestion de demandes de titre.
Types de demande concernés dans ce sprint :
- `DUPLICATA`
- `TRANSFERT`

Le workflow `NOUVEAU_TITRE` existe déjà et doit être réutilisé dans certains cas.

## Objectif
Implémenter les parcours métier complets pour `DUPLICATA` et `TRANSFERT`, chacun avec deux cas :
1. avec données antérieures
2. sans données antérieures

## Règles métier obligatoires

### A. Duplicata
- Le duplicata concerne la **carte de résident**.
- Cas avec données antérieures :
  - Rechercher la carte de résident existante.
  - Créer une demande `DUPLICATA` au statut `CREEE` à partir de cette carte.
- Cas sans données antérieures :
  - Faire une saisie complète comme `NOUVEAU_TITRE`.
  - Créer la demande `NOUVEAU_TITRE` au statut `APPROUVEE`.
  - En parallèle, créer une demande `DUPLICATA` au statut `CREEE`.
  - Les données de la demande duplicata reprennent les données saisies dans le parcours nouveau titre.

### B. Transfert de VISA
- Le transfert concerne le **visa**.
- Le **passeport est obligatoire** pour le transfert.
- Cas avec données antérieures :
  - Rechercher le visa existant.
  - Associer/valider le passeport obligatoire.
  - Créer la demande `TRANSFERT` au statut `CREEE`.
- Cas sans données antérieures :
  - Même logique que duplicata sans antérieur.
  - Créer une demande `NOUVEAU_TITRE` au statut `APPROUVEE`.
  - En parallèle, créer une demande `TRANSFERT` au statut `CREEE`.
  - Réutiliser les mêmes données saisies.
  - Inclure la saisie des informations de **visa transformable**.

## Exigences UI/UX
- Les workflows duplicata et transfert ont les mêmes étapes, mais des pages séparées.
- Éviter les écrans vides : mise en page compacte et claire.
- Afficher un `*` rouge pour tous les champs obligatoires.
- Dans l'étape finale (`Demande & Pièces`) :
  - afficher les pièces justificatives spécifiques au type de demande,
  - afficher `date_demande` avec la date du jour par défaut,
  - afficher les informations nécessaires sur la carte/visa créés,
  - si sans données antérieures : afficher aussi les informations du nouveau titre et du visa transformable saisis.
- Après création : afficher une **fiche récapitulative** (pas redirection immédiate vers la liste).
  - Ajouter un bouton pour aller ensuite vers la liste.

## Exigences techniques
- Conserver la cohérence transactionnelle : une création parallèle doit être atomique (rollback total si échec).
- Référence de chaque demande générée automatiquement et unique.
- Contrôler les cas non trouvés en recherche et proposer la bascule vers "sans données antérieures".
- Prévoir les vues détail/liste pour distinguer clairement `DUPLICATA` et `TRANSFERT`.

## Livrables attendus
1. Endpoints + services pour les 4 parcours :
   - duplicata avec antérieur
   - duplicata sans antérieur
   - transfert avec antérieur
   - transfert sans antérieur
2. Templates Thymeleaf complets pour chaque étape.
3. Page récapitulative finale par demande créée.
4. Mise à jour des listes/détails de demandes.
5. Tests d'intégration couvrant les 4 parcours.

## Points de vigilance
- Ne pas confondre carte de résident (duplicata) et visa (transfert).
- Ne pas oublier le passeport obligatoire pour transfert.
- Ne pas oublier la saisie du visa transformable dans le parcours sans antérieur.