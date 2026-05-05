# Données Requises en Base de Données

## 📋 DUPLICATA

### Avec antérieur (Visa/Carte résident existant)
- **Demandeur**
  - nom
  - prénom
  - genre
  - date_naissance
  - adresse_mada
  - contact
  - email
  - situation_familiale (FK)
  - nationalité (FK)

- **Passeport**
  - numéro_passeport
  - date_naissance
  - date_delivrance
  - date_expiration

- **Visa transformable existant**
  - numero_visa
  - date_entree_territoire
  - date_sortie_territoire
  - date_delivrance
  - date_expiration

- **Pièces justificatives**
  - Liste des pièces fournies (FK piece_justificative)

### Sans antérieur (Saisie complète)
- **Demandeur** (voir ci-dessus)
- **Passeport** (voir ci-dessus)
- **Carte résident**
  - numero_carte
  - date_entree_territoire
  - date_sortie_territoire
  - date_delivrance
  - date_expiration

- **Pièces justificatives** (voir ci-dessus)
- **Type demande** : Duplicata (id=1)

---

## 🔄 TRANSFERT

### Avec antérieur (Visa existant)
- **Demandeur** (identique à Duplicata)
- **Passeport** (identique à Duplicata)
- **Visa transformable existant** (identique à Duplicata)
- **Pièces justificatives** (identique à Duplicata)

### Sans antérieur (Saisie complète)
- **Demandeur** (identique à Duplicata)
- **Passeport** (identique à Duplicata)

- **Visa transformable** (à créer)
  - numero_visa
  - date_entree_territoire
  - date_sortie_territoire
  - date_delivrance
  - date_expiration

- **Visa** (à créer)
  - numero_visa
  - date_entree_territoire
  - date_sortie_territoire
  - date_delivrance
  - date_expiration

- **Pièces justificatives** (identique à Duplicata)
- **Type demande** : Transfert (id=2)

---

## ✨ NOUVEAU TITRE

- **Demandeur**
  - nom
  - prénom
  - genre
  - date_naissance
  - adresse_mada
  - contact
  - email
  - situation_familiale (FK)
  - nationalité (FK)

- **Passeport**
  - numéro_passeport
  - date_naissance
  - date_delivrance
  - date_expiration

- **Catégorie visa** (FK)
  - Travailleur (id=1)
  - Investisseur (id=2)

- **Visa transformable** (optionnel)
  - Si demande liée à changement de titre

- **Pièces justificatives**
  - Liste des pièces fournies (FK piece_justificative)

- **Type demande** : Nouveau titre (id=3)

---

## 🗂️ Tables de Référence (Obligatoires)

### Nationalité
- Française
- Américaine
- Malgache

### Situation Familiale
- Célibataire
- Marié(e)
- Divorcé(e)

### Catégorie Visa
- Investisseur
- Travailleur

### Status
- ATT (En attente)
- VAL (Validé)
- REJ (Rejeté)
- CREEE (Créée)
- APPROUVEE (Approuvée)

### Type Demande
- Duplicata
- Transfert
- Nouveau titre

### Pièces Justificatives
| Code | Libelle | Obligatoire |
|------|---------|-------------|
| COM_PHO | 02 photos d'identité | ✓ |
| COM_NOT | Notice de renseignement | ✓ |
| COM_DEM | Demande adressée à Mr le Ministère | ✓ |
| COM_VIS | Photocopie certifiée du visa en cours | ✓ |
| COM_PAS | Photocopie certifiée du passeport | ✓ |
| COM_CAR | Photocopie certifiée de la carte résident | ✓ |
| COM_RES | Certificat de résidence à Madagascar | ✓ |
| COM_CAS | Extrait de casier judiciaire < 3 mois | ✓ |
| INV_STA | Statut de la Société | ✓ |
| INV_REG | Extrait d'inscription au registre | ✓ |
| INV_FIS | Carte fiscale | ✓ |
| TRA_AUT | Autorisation emploi délivrée à Madagascar | ✓ |
| TRA_EMP | Attestation d'emploi délivré par l'employeur | ✓ |

---

## 📊 Schéma des Relations

```
Demandeur
├── Passeport
│   ├── Visa
│   └── CarteResident
├── SituationFamiliale
└── Nationalite

Demande
├── Demandeur
├── TypeDemande (Duplicata, Transfert, Nouveau titre)
├── CategorieVisa (Travailleur, Investisseur)
├── VisaTransformable (optionnel)
└── PiecesDemande
    └── PieceJustificative
```
