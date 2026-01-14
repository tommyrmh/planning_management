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

- **Endpoints protégés (JWT requis) :**
  - `/api/profile` - Gestion du profil utilisateur
  - Tous les autres endpoints API

- **Endpoints Admin uniquement :**
  - `/api/auth/register` - Création d'utilisateurs (rôle ADMIN requis)
  - `/register` - Page de création d'utilisateurs

- Le token JWT doit être passé dans le header `Authorization: Bearer <token>`
- Les interfaces web gèrent automatiquement l'authentification via localStorage

## Démarrage

```bash
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`

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
│   └── ProfileController.java      # Endpoints de gestion de profil
│
├── service/
│   ├── AuthService.java           # Logique d'authentification
│   └── UserService.java           # Logique de gestion des utilisateurs
│
├── repository/
│   └── UserRepository.java        # Accès aux données utilisateur
│
├── model/
│   ├── User.java                  # Entité User
│   └── Role.java                  # Énumération des rôles
│
├── dto/
│   ├── RegisterRequest.java       # DTO pour l'inscription
│   ├── LoginRequest.java          # DTO pour la connexion
│   ├── LoginResponse.java         # DTO pour la réponse de connexion
│   ├── ProfileResponse.java       # DTO pour le profil
│   ├── UpdateProfileRequest.java  # DTO pour la mise à jour du profil
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
└── PlanningApplication.java        # Classe principale
```


## Auteur
Tommy RAMIHOATRARIVO

Projet développé dans le cadre d'un test technique de recrutement.

