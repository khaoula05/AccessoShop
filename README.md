# 🛍️ AccessoShop — E-Commerce d'Accessoires

> Projet de fin de module — Site de vente en ligne d'accessoires (Montres, Sacs, Bijoux, Lunettes)
> **BAC+3 Licence Informatique**

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
![Java](https://img.shields.io/badge/Java-17-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-teal)

---

## 📋 Description

**AccessoShop** est un site e-commerce complet développé avec Spring Boot et Thymeleaf. Il permet aux utilisateurs de parcourir et d'acheter des accessoires de mode (montres, sacs à main, bijoux, lunettes).

---

## ✨ Fonctionnalités

| Fonctionnalité | Statut |
|---|---|
| 🏠 Page d'accueil avec Hero section | ✅ |
| 🛒 Catalogue de produits avec filtres par catégorie | ✅ |
| 🔍 Recherche de produits | ✅ |
| 📄 Page détail produit | ✅ |
| 🛍️ Panier d'achats | ✅ |
| 👤 Inscription / Connexion | ✅ |
| 🔒 Spring Security (auth complète) | ✅ |
| 📱 Design responsive (mobile-friendly) | ✅ |
| 🎨 Interface professionnelle (style premium) | ✅ |
| 🗄️ Base de données MySQL | ✅ |

---

## 🛠️ Technologies utilisées

- **Backend** : Java 17, Spring Boot 3.2.5, Spring Security, Spring Data JPA
- **Frontend** : Thymeleaf, CSS3, HTML5 (pas de framework JS externe)
- **Base de données** : MySQL 8
- **Build** : Maven
- **Sécurité** : BCrypt password encoding, Spring Security

---

## 🚀 Lancer le projet

### Prérequis
- Java 17+
- MySQL 8+
- Maven 3.8+

### Étapes

1. **Cloner le dépôt**
```bash
git clone https://github.com/votre-username/AccessoShop.git
cd AccessoShop
```

2. **Créer la base de données MySQL**
```sql
CREATE DATABASE ecommerce;
```

3. **Configurer `application.properties`**
```properties
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

4. **Lancer l'application**
```bash
./mvnw spring-boot:run
```

5. **Accéder au site**
```
https://accessoshop-production.up.railway.app/
```

### Comptes de test
| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Admin | admin@accesso.com | admin123 |
| Client | test@accesso.com | test123 |

---

## 🗂️ Architecture du projet

```
src/main/java/com/ecommerce/
├── config/
│   ├── SecurityConfig.java        # Configuration Spring Security
│   ├── UserDetailsServiceImpl.java # Auth service
│   └── DataInitializer.java       # Données de test
├── controller/
│   ├── HomeController.java        # Page d'accueil
│   ├── ProductController.java     # Catalogue produits
│   ├── CartController.java        # Panier
│   ├── AuthController.java        # Login / Register
│   ├── OrderController.java       # Commandes (REST)
│   └── PaymentController.java     # Paiements (REST)
├── entity/
│   ├── Product.java
│   ├── User.java
│   ├── Cart.java
│   ├── Order.java
│   └── Payment.java
├── repository/                    # Spring Data JPA
├── service/                       # Logique métier
└── ProjectEcommerceApplication.java

src/main/resources/
├── templates/
│   ├── index.html                 # Page d'accueil
│   ├── products/list.html         # Catalogue
│   ├── products/detail.html       # Détail produit
│   ├── cart/view.html             # Panier
│   ├── auth/login.html            # Connexion
│   ├── auth/register.html         # Inscription
│   └── fragments/                 # Navbar, Footer
└── static/css/main.css            # Styles CSS
```

---

## 👥 Auteurs

Projet réalisé dans le cadre du cours de développement web — Licence 3 Informatique.

---

## 📸 Aperçu

Site accessible sur `https://accessoshop-production.up.railway.app/` après lancement.
