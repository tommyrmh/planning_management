# Système de Gestion de Planning et Disponibilité d'Équipe

Application complète de gestion de planning et disponibilité des collaborateurs avec API REST back-end en Java/Spring Boot, interface web front-end avec Spring MVC et Thymeleaf, et authentification JWT.

## Table des Matières

- [Technologies Utilisées](#technologies-utilisées)
- [Architecture](#architecture)
- [Fonctionnalités Implémentées](#fonctionnalités-implémentées)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Démarrage](#démarrage)
- [API Endpoints](#api-endpoints)
- [Structure du Projet](#structure-du-projet)

## Technologies Utilisées

- **Java 17**
- **Spring Boot 4.0.1**
- **Spring Security** - Sécurité et authentification
- **JWT (JSON Web Token)** - Authentification stateless
- **Spring Data JPA** - Accès aux données
- **MySQL** - Base de données
- **Thymeleaf** - Moteur de templates
- **Lombok** - Réduction du code boilerplate
- **Maven** - Gestion des dépendances

## Architecture

Le projet suit une architecture en couches :

```
├── controller/      # Contrôleurs REST
├── service/         # Logique métier
├── repository/      # Accès aux données
├── model/           # Entités JPA
├── dto/             # Data Transfer Objects
├── security/        # Configuration de sécurité et JWT
└── exception/       # Gestion des exceptions
```

## Fonctionnalités Implémentées

### 1. Authentification & Autorisation ✅

#### Inscription (Réservée aux Admins)
- Endpoint : `POST /api/auth/register`
- **Nécessite authentification ADMIN**
- Création d'un compte avec : username, email, password, firstName, lastName, department
- Validation des données
- Vérification de l'unicité du username et email
- Hashage du mot de passe avec BCrypt
- Interface web accessible via `/register` (admins uniquement)

#### Connexion
- Endpoint : `POST /api/auth/login`
- Authentification avec username et password
- Génération du token JWT
- Retour des informations de l'utilisateur

#### Gestion des Rôles
Trois rôles disponibles :
- **COLLABORATOR** - Collaborateur standard
- **MANAGER** - Manager d'équipe
- **ADMIN** - Administrateur système

#### Gestion du Profil
- **Consultation** : `GET /api/profile`
  - Affichage des informations du profil
  - Nécessite authentification JWT

- **Modification** : `PUT /api/profile`
  - Modification de : email, password, firstName, lastName, department
  - Validation des données
  - Vérification de l'unicité de l'email

### 2. Gestion des Projets (CRUD) ✅

#### Créer un Projet (MANAGER/ADMIN)
- Endpoint : `POST /api/projects`
- **Nécessite rôle MANAGER ou ADMIN**
- Champs : nom, description, client, dateDebut, dateFin, statut
- Validation : date de fin après date de début
- Interface web accessible via `/projects/new`

#### Lister les Projets avec Filtres et Pagination
- Endpoint : `GET /api/projects`
- Filtres disponibles : statut, période (startDate, endDate)
- Pagination : page, size, sortBy, direction
- Accessible à tous les utilisateurs authentifiés
- Interface web accessible via `/projects`

#### Consulter le Détail d'un Projet
- Endpoint : `GET /api/projects/{id}`
- Affichage de toutes les informations du projet
- Section tâches associées (prête pour future implémentation)
- Interface web accessible via `/projects/{id}`

#### Modifier un Projet (MANAGER/ADMIN)
- Endpoint : `PUT /api/projects/{id}`
- **Nécessite rôle MANAGER ou ADMIN**
- Modification de tous les champs du projet
- Validation des dates
- Interface web accessible via `/projects/{id}/edit`

#### Supprimer un Projet (ADMIN)
- Endpoint : `DELETE /api/projects/{id}`
- **Nécessite rôle ADMIN uniquement**
- Vérification des tâches associées (à implémenter)
- Confirmation requise avant suppression

#### Clôturer un Projet (MANAGER/ADMIN)
- Endpoint : `PUT /api/projects/{id}/close`
- **Nécessite rôle MANAGER ou ADMIN**
- Change le statut à TERMINE
- Enregistre la date de clôture

#### Statuts de Projet
- **EN_PREPARATION** - Projet en préparation
- **EN_COURS** - Projet en cours
- **EN_PAUSE** - Projet en pause
- **TERMINE** - Projet terminé
- **ANNULE** - Projet annulé

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## Installation

1. **Cloner le repository**
```bash
git clone <url-du-repo>
cd planning_management
```

2. **Créer la base de données MySQL**
```sql
CREATE DATABASE planning_management;
```

3. **Configurer la base de données**

Modifier le fichier `src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/planning_management
spring.datasource.username=votre_username
spring.datasource.password=votre_password
```

4. **Installer les dépendances**
```bash
mvn clean install
```

## Configuration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/planning_management
spring.datasource.username=root
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000

# Server
server.port=8080
```

### Sécurité

- **Endpoints publics :**
  - `/api/auth/login` - Connexion accessible à tous
  - Pages statiques (CSS, JS, images)
  - Pages web : `/`, `/login`, `/dashboard`, `/projects/**`, `/profile/**`

- **Endpoints protégés (JWT requis) :**
  - `/api/profile` - Gestion du profil utilisateur
  - `/api/projects` (GET) - Liste des projets
  - `/api/projects/{id}` (GET) - Détail d'un projet
  - Tous les autres endpoints API nécessitent authentification

- **Endpoints Manager/Admin uniquement :**
  - `/api/projects` (POST) - Création de projet
  - `/api/projects/{id}` (PUT) - Modification de projet
  - `/api/projects/{id}/close` (PUT) - Clôture de projet

- **Endpoints Admin uniquement :**
  - `/api/auth/register` - Création d'utilisateurs (rôle ADMIN requis)
  - `/api/projects/{id}` (DELETE) - Suppression de projet
  - `/register` - Page de création d'utilisateurs

- Le token JWT doit être passé dans le header `Authorization: Bearer <token>`
- Les interfaces web gèrent automatiquement l'authentification via localStorage
- Les boutons d'action sont affichés/cachés selon le rôle de l'utilisateur

## Démarrage

```bash
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`

## 🌐 Pages Web Disponibles

### Pages Publiques
- `http://localhost:8080/` - Redirection vers login
- `http://localhost:8080/login` - Page de connexion

### Pages Authentifiées
- `http://localhost:8080/dashboard` - Tableau de bord principal
- `http://localhost:8080/profile` - Consultation du profil
- `http://localhost:8080/profile/edit` - Modification du profil
- `http://localhost:8080/projects` - Liste des projets avec filtres
- `http://localhost:8080/projects/new` - Créer un projet (Manager/Admin)
- `http://localhost:8080/projects/{id}` - Détail d'un projet
- `http://localhost:8080/projects/{id}/edit` - Modifier un projet (Manager/Admin)

### Pages Admin
- `http://localhost:8080/register` - Création d'utilisateurs (Admin uniquement)

## 🔐 Compte Admin par Défaut

Au premier démarrage de l'application, un **compte administrateur** est créé automatiquement :

| Champ | Valeur |
|-------|--------|
| **Username** | `admin` |
| **Password** | `admin123` |
| **Email** | admin@planning.com |
| **Rôle** | ADMIN |

### Connexion Admin

1. Ouvrez votre navigateur sur `http://localhost:8080`
2. Connectez-vous avec :
   - **Username:** `admin`
   - **Password:** `admin123`
3. Vous accédez au dashboard administrateur

### Création d'Utilisateurs

⚠️ **Important :** Seuls les administrateurs peuvent créer de nouveaux utilisateurs.

Pour créer un nouveau compte :
1. Connectez-vous en tant qu'admin
2. Cliquez sur **"Créer un Utilisateur"** dans le dashboard
3. Remplissez le formulaire avec les informations du nouvel utilisateur
4. Sélectionnez le rôle approprié (COLLABORATOR, MANAGER, ADMIN)
5. Cliquez sur **"Créer mon compte"**

## API Endpoints

### Authentification

#### Inscription (Admin uniquement)
```http
POST /api/auth/register
Content-Type: application/json
Authorization: Bearer <admin_token>

{
  "username": "john.doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "department": "IT",
  "role": "COLLABORATOR"
}
```

⚠️ **Note:** Cet endpoint nécessite un token JWT d'un utilisateur avec le rôle ADMIN.

**Réponse (200 OK)** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john.doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "department": "IT",
  "role": "COLLABORATOR"
}
```

#### Connexion
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "password123"
}
```

**Réponse (200 OK)** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john.doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "department": "IT",
  "role": "COLLABORATOR"
}
```

### Profil

#### Consulter son profil
```http
GET /api/profile
Authorization: Bearer <token>
```

**Réponse (200 OK)** :
```json
{
  "id": 1,
  "username": "john.doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "department": "IT",
  "role": "COLLABORATOR"
}
```

#### Modifier son profil
```http
PUT /api/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "new.email@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "department": "IT"
}
```

**Réponse (200 OK)** :
```json
{
  "id": 1,
  "username": "john.doe",
  "email": "new.email@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "department": "IT",
  "role": "COLLABORATOR"
}
```

### Projets

#### Créer un Projet (Manager/Admin uniquement)
```http
POST /api/projects
Authorization: Bearer <token>
Content-Type: application/json

{
  "nom": "Projet Website",
  "description": "Refonte du site web corporate",
  "client": "Acme Corporation",
  "dateDebut": "2026-02-01",
  "dateFin": "2026-06-30",
  "statut": "EN_PREPARATION"
}
```

⚠️ **Note:** Cet endpoint nécessite un token JWT d'un utilisateur avec le rôle MANAGER ou ADMIN.

**Réponse (201 Created)** :
```json
{
  "id": 1,
  "nom": "Projet Website",
  "description": "Refonte du site web corporate",
  "client": "Acme Corporation",
  "dateDebut": "2026-02-01",
  "dateFin": "2026-06-30",
  "statut": "EN_PREPARATION",
  "createdByUsername": "admin",
  "createdAt": "2026-01-14T10:30:00",
  "updatedAt": "2026-01-14T10:30:00",
  "closedAt": null
}
```

#### Lister tous les Projets avec Filtres
```http
GET /api/projects?statut=EN_COURS&startDate=2026-01-01&endDate=2026-12-31&page=0&size=10&sortBy=dateDebut&direction=DESC
Authorization: Bearer <token>
```

**Paramètres de requête** :
- `statut` (optionnel) : EN_PREPARATION, EN_COURS, EN_PAUSE, TERMINE, ANNULE
- `startDate` (optionnel) : Date de début au format ISO (YYYY-MM-DD)
- `endDate` (optionnel) : Date de fin au format ISO (YYYY-MM-DD)
- `page` (défaut: 0) : Numéro de la page
- `size` (défaut: 10) : Nombre d'éléments par page
- `sortBy` (défaut: id) : Champ de tri
- `direction` (défaut: DESC) : ASC ou DESC

**Réponse (200 OK)** :
```json
{
  "content": [
    {
      "id": 1,
      "nom": "Projet Website",
      "description": "Refonte du site web corporate",
      "client": "Acme Corporation",
      "dateDebut": "2026-02-01",
      "dateFin": "2026-06-30",
      "statut": "EN_COURS",
      "createdByUsername": "admin",
      "createdAt": "2026-01-14T10:30:00",
      "updatedAt": "2026-01-14T10:30:00",
      "closedAt": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true
}
```

#### Consulter un Projet
```http
GET /api/projects/1
Authorization: Bearer <token>
```

**Réponse (200 OK)** :
```json
{
  "id": 1,
  "nom": "Projet Website",
  "description": "Refonte du site web corporate",
  "client": "Acme Corporation",
  "dateDebut": "2026-02-01",
  "dateFin": "2026-06-30",
  "statut": "EN_COURS",
  "createdByUsername": "admin",
  "createdAt": "2026-01-14T10:30:00",
  "updatedAt": "2026-01-14T10:30:00",
  "closedAt": null
}
```

#### Modifier un Projet (Manager/Admin uniquement)
```http
PUT /api/projects/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "nom": "Projet Website - Phase 2",
  "description": "Refonte du site web corporate avec module e-commerce",
  "client": "Acme Corporation",
  "dateDebut": "2026-02-01",
  "dateFin": "2026-08-31",
  "statut": "EN_COURS"
}
```

**Réponse (200 OK)** :
```json
{
  "id": 1,
  "nom": "Projet Website - Phase 2",
  "description": "Refonte du site web corporate avec module e-commerce",
  "client": "Acme Corporation",
  "dateDebut": "2026-02-01",
  "dateFin": "2026-08-31",
  "statut": "EN_COURS",
  "createdByUsername": "admin",
  "createdAt": "2026-01-14T10:30:00",
  "updatedAt": "2026-01-14T15:45:00",
  "closedAt": null
}
```

#### Clôturer un Projet (Manager/Admin uniquement)
```http
PUT /api/projects/1/close
Authorization: Bearer <token>
```

**Réponse (200 OK)** :
```json
{
  "id": 1,
  "nom": "Projet Website",
  "description": "Refonte du site web corporate",
  "client": "Acme Corporation",
  "dateDebut": "2026-02-01",
  "dateFin": "2026-06-30",
  "statut": "TERMINE",
  "createdByUsername": "admin",
  "createdAt": "2026-01-14T10:30:00",
  "updatedAt": "2026-01-14T16:00:00",
  "closedAt": "2026-01-14T16:00:00"
}
```

#### Supprimer un Projet (Admin uniquement)
```http
DELETE /api/projects/1
Authorization: Bearer <token>
```

**Réponse (204 No Content)** : Aucun contenu retourné

#### Compter les Projets par Statut
```http
GET /api/projects/stats/count?statut=EN_COURS
Authorization: Bearer <token>
```

**Réponse (200 OK)** :
```json
5
```

### Gestion des Erreurs

#### Erreurs de Validation (400 Bad Request)
```json
{
  "timestamp": "2026-01-14T16:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/auth/register",
  "validationErrors": {
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  }
}
```

#### Authentification Échouée (401 Unauthorized)
```json
{
  "timestamp": "2026-01-14T16:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

#### Ressource Déjà Existante (409 Conflict)
```json
{
  "timestamp": "2026-01-14T16:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Username already exists",
  "path": "/api/auth/register"
}
```

#### Ressource Non Trouvée (404 Not Found)
```json
{
  "timestamp": "2026-01-14T16:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found",
  "path": "/api/profile"
}
```

## Structure du Projet

```
src/main/java/com/gestion/planning/
│
├── controller/
│   ├── AuthController.java         # Endpoints d'authentification
│   ├── ProfileController.java      # Endpoints de gestion de profil
│   ├── ProjectController.java      # Endpoints de gestion des projets
│   └── ViewController.java         # Contrôleur pour les pages Thymeleaf
│
├── service/
│   ├── AuthService.java           # Logique d'authentification
│   ├── UserService.java           # Logique de gestion des utilisateurs
│   └── ProjectService.java        # Logique de gestion des projets
│
├── repository/
│   ├── UserRepository.java        # Accès aux données utilisateur
│   └── ProjectRepository.java     # Accès aux données projets (avec filtres)
│
├── model/
│   ├── User.java                  # Entité User
│   ├── Role.java                  # Énumération des rôles
│   ├── Project.java               # Entité Project
│   └── ProjectStatus.java         # Énumération des statuts de projet
│
├── dto/
│   ├── RegisterRequest.java       # DTO pour l'inscription
│   ├── LoginRequest.java          # DTO pour la connexion
│   ├── LoginResponse.java         # DTO pour la réponse de connexion
│   ├── ProfileResponse.java       # DTO pour le profil
│   ├── UpdateProfileRequest.java  # DTO pour la mise à jour du profil
│   ├── CreateProjectRequest.java  # DTO pour créer un projet
│   ├── UpdateProjectRequest.java  # DTO pour modifier un projet
│   ├── ProjectResponse.java       # DTO pour les projets
│   └── ErrorResponse.java         # DTO pour les erreurs
│
├── security/
│   ├── JwtService.java                  # Service de gestion JWT
│   ├── JwtAuthenticationFilter.java     # Filtre d'authentification JWT
│   ├── CustomUserDetailsService.java    # Service UserDetails personnalisé
│   └── SecurityConfig.java              # Configuration Spring Security
│
├── exception/
│   ├── ResourceNotFoundException.java         # Exception ressource non trouvée
│   ├── ResourceAlreadyExistsException.java   # Exception ressource existante
│   └── GlobalExceptionHandler.java           # Gestionnaire global d'exceptions
│
├── config/
│   └── DataInitializer.java       # Initialisation des données (admin par défaut)
│
└── PlanningApplication.java        # Classe principale
```

```
src/main/resources/
│
├── templates/
│   ├── fragments/
│   │   └── layout.html             # Navbar et layout commun
│   ├── auth/
│   │   ├── login.html              # Page de connexion
│   │   └── register.html           # Page d'inscription (admin)
│   ├── profile/
│   │   ├── view.html               # Page de consultation du profil
│   │   └── edit.html               # Page de modification du profil
│   ├── projects/
│   │   ├── list.html               # Liste des projets avec filtres
│   │   ├── create.html             # Formulaire de création de projet
│   │   ├── detail.html             # Détail d'un projet
│   │   └── edit.html               # Formulaire de modification de projet
│   └── dashboard.html              # Page d'accueil après connexion
│
├── static/
│   ├── css/
│   │   └── slack-theme.css         # Thème Slack personnalisé
│   └── js/
│       └── app.js                  # Fonctions JavaScript (auth, logout)
│
└── application.properties          # Configuration de l'application
```

## Interface Web

L'application dispose d'une interface web complète construite avec Thymeleaf et Bootstrap 5, inspirée du design de Slack.

### Thème Slack Personnalisé

Le thème utilise une palette de couleurs inspirée de Slack :
- **Couleur principale** : Violet aubergine (#4A154B)
- **Couleur secondaire** : Vert (#2EB67D)
- **Couleur d'accent** : Orange (#E01E5A)
- Design moderne et responsive
- Navigation intuitive avec sidebar
- Formulaires stylisés
- Badges de statut colorés

### Fonctionnalités Frontend

**Authentification** :
- Login automatique avec redirection si déjà connecté
- Stockage du token JWT dans localStorage
- Déconnexion avec nettoyage de l'historique
- Protection des pages : redirection si non authentifié

**Gestion des Projets** :
- Liste interactive avec filtres (statut, dates)
- Pagination complète
- Création de projet avec validation en temps réel
- Modification de projet avec données pré-remplies
- Détail de projet avec actions selon le rôle
- Confirmation avant suppression (modal)
- Clôture de projet en un clic

**Permissions Dynamiques** :
- Affichage/masquage des boutons selon le rôle utilisateur
- Vérification côté client avant les actions sensibles
- Messages d'erreur clairs et informatifs

**Navigation** :
- Navbar responsive avec dropdown utilisateur
- Liens actifs selon la page courante
- Icônes Bootstrap pour une meilleure UX
- Footer avec informations de copyright


## Auteur
Tommy RAMIHOATRARIVO


Projet développé dans le cadre d'un test technique de recrutement.

