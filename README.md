# freelanceapp-back

---

## 🏗️ Structure du Projet (Architecture Hexagonale)

Le projet suit les principes de la **Clean Architecture** (Ports & Adaptateurs) pour garantir un découplage total entre la logique métier et les détails techniques.

```text
src/main/java/com/example/myapp
│
├── 📂 application                      # --- ADAPTATEURS D'ENTRÉE ---
│   └── 📂 rest                         # Exposition API REST
│       ├── 📂 dto                      # Data Transfer Objects (Request/Response)
│       │   ├── UserRequest.java
│       │   └── UserResponse.java
│       └── UserController.java         # Point d'entrée de l'API
│
├── 📂 domain                           # --- LE CŒUR MÉTIER ---
│   ├── 📂 model                        # Objets métier (POJO purs, sans framework)
│   │   └── User.java
│   │
│   ├── 📂 ports                        # Les contrats (Interfaces)
│   │   ├── 📂 in                       # Ports d'entrée (Actions utilisateur)
│   │   │   ├── CreateUserUseCase.java
│   │   │   ├── UpdateUserUseCase.java
│   │   │   └── ...
│   │   │
│   │   └── 📂 out                      # Ports de sortie (Besoins techniques)
│   │       └── UserRepository.java
│   │
│   └── 📂 service                      # Implémentations des UseCases (Interactors)
│       ├── CreateUserService.java      # Logique de création
│       ├── UpdateUserService.java      # Logique de mise à jour
│       └── ...
│
└── 📂 infrastructure                   # --- ADAPTATEURS DE SORTIE ---
    └── 📂 persistence                  # Couche de persistance des données
        ├── 📂 entity                   # Modèles de données (JPA, MongoDB, etc.)
        │   └── UserEntity.java
        └── 📂 adapter                  # Implémentations concrètes des ports out
            └── BDDUserRepository.java

```

---

### 📝 Légende des couches

* **Domain** : Contient la logique métier pure. Cette couche ne doit dépendre d'aucune autre couche ni d'aucun framework (Spring, Hibernate, etc.).
* **Application** : Gère l'interaction avec le monde extérieur (ici en REST). Elle transforme les requêtes externes en appels vers les Use Cases du domaine.
* **Infrastructure** : Contient les détails d'implémentation technique (Base de données, appels API externes, envoi de mails). Elle implémente les interfaces définies dans les ports de sortie.

---

## Local dev notes

Demo credentials (H2 seed):
- username: demo
- password: demo1234