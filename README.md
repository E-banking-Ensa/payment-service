# Payment Service - eBanking Platform

Service de paiement pour la plateforme eBanking gérant les virements bancaires et les recharges téléphoniques.

## 🚀 Fonctionnalités

### 1. Virement Bancaire
- **Endpoint**: `POST /api/payments/virement`
- **Description**: Effectue un virement d'un compte à un autre
- **Requête**:
```json
{
  "ribSource": "123456789012345678901234",
  "ribDestination": "987654321098765432109876",
  "amount": 1000.00,
  "motif": "Paiement facture"
}
```

### 2. Recharge Téléphonique
- **Endpoint**: `POST /api/payments/recharge`
- **Description**: Effectue une recharge mobile
- **Requête**:
```json
{
  "rib": "123456789012345678901234",
  "phoneNumber": "0612345678",
  "amount": 50.00
}
```

## 🛠️ Technologies

- **Spring Boot** 3.3.0
- **Spring Cloud** 2023.0.2
- **OpenFeign** - Communication inter-services
- **Eureka Client** - Service Discovery
- **Spring Data JPA** - Persistance
- **PostgreSQL** - Base de données
- **Lombok** - Réduction du code boilerplate
- **Java** 17

## 📦 Dépendances

Le service communique avec:
- **Account Service** - Vérification des comptes et soldes
- **Legacy Adapter Service** - Exécution des transactions SOAP
- **Eureka Discovery** - Enregistrement du service

## ⚙️ Configuration

### Application Properties
```properties
server.port=8082
spring.application.name=payment-service

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5433/payment_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## 🚀 Démarrage

### Prérequis
1. Java 17+
2. PostgreSQL en cours d'exécution
3. Eureka Discovery Server actif (port 8761)
4. Account Service actif
5. Legacy Adapter Service actif

### Compilation et exécution
```bash
# Compilation
./mvnw clean install

# Exécution
./mvnw spring-boot:run
```

Le service sera disponible sur `http://localhost:8082`

## 🗄️ Base de Données

### Créer la base de données
```sql
CREATE DATABASE payment_db;
```

### Structure automatique
Les tables seront créées automatiquement grâce à `spring.jpa.hibernate.ddl-auto=update`

## 📊 Architecture

```
Frontend
    ↓
Payment Service :8082
    ↓
    ├── Account Service (Validation)
    └── Legacy Adapter (Exécution SOAP)
            ↓
        Core Banking (SOAP)
```

## 🔍 Flux de Transaction

### Virement:
1. Réception de la requête
2. Validation des données
3. Vérification compte source (Account Service)
4. Vérification du solde
5. Vérification compte destination (Account Service)
6. Exécution via Legacy Adapter (SOAP)
7. Mise à jour du statut
8. Réponse au client

### Recharge Mobile:
1. Réception de la requête
2. Validation (montant 10-500 DH)
3. Vérification du compte (Account Service)
4. Vérification du solde
5. Exécution via Legacy Adapter (SOAP)
6. Mise à jour du statut
7. Réponse au client

## 📝 Logging

Les logs sont configurés avec le niveau DEBUG pour le suivi détaillé des transactions.

## 🧪 Tests

```bash
# Exécuter les tests
./mvnw test
```

## 📈 Monitoring

- **Health Check**: `GET /api/payments/health`

## 🔒 Sécurité

À implémenter:
- Spring Security
- JWT Authentication
- Rate Limiting
- Transaction Encryption
